package de.mendelson.util.clientserver.codec;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.ClientSessionHandlerCallback;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.Inflater;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Decodes a command from the line
 *
 * @author S.Heller
 * @version $Revision: 21 $
 */
public class ClientServerDecoder extends CumulativeProtocolDecoder {

    private final static MecResourceBundle rb;
    static{
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    de.mendelson.util.clientserver.codec.ResourceBundleServerDecoder.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    private final ClientSessionHandlerCallback clientCallback;
    private final List<String> allowedClientServerClassList = Collections.synchronizedList(new ArrayList<String>());

    /**
     *
     * @param clientCallback This may be null if there is no callback or this is
     * not a client instance
     */
    public ClientServerDecoder(ClientSessionHandlerCallback clientCallback) {
        super();
        this.clientCallback = clientCallback;        
    }

    private int decodeLengthHeader32Bit(byte[] header32Bit) {
        //BIG_ENDIAN is the standard oder under java - just to ensure this
        return( ByteBuffer.wrap(header32Bit).order(ByteOrder.BIG_ENDIAN).getInt());
    }

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput decoderOutput) throws Exception {
        //store tha current position to rewind later
        int position = in.position();
        byte[] headerData = new byte[4];
        if (in.remaining() >= 4) {
            in.get(headerData);
        } else {
            //buffer has not been consumed: return false
            return (false);
        }
        int contentLength = this.decodeLengthHeader32Bit(headerData);
        //bail out and write back the buffer if the buffer does not contain enough bytes
        if (in.remaining() < contentLength) {
            //rewind the input buffer, next attempt will read the data again
            in.position(position);
            //buffer has not been consumed: return false
            return (false);
        }
        //read the full object into a byte array
        byte[] compressedObjectBuffer = new byte[contentLength];
        in.get(compressedObjectBuffer);
        byte[] objectBuffer = this.decompress(compressedObjectBuffer);
        try (ByteArrayInputStream objectInStream = new ByteArrayInputStream(objectBuffer)) {
            try (ObjectInputStream objectInput = new ObjectInputStream(objectInStream)) {
                //serialization filtering deserialization vulnerability protection, 
                //see https://docs.oracle.com/javase/10/core/serialization-filtering1.htm            
                objectInput.setObjectInputFilter(this::clientServerMessageFilter);
                Object object = objectInput.readObject();
                //at this point it must be a de.mendelson.util.clientserver.messages.ClientServerMessage
                //-every REJECT results in a InvalidClassException which will result in informing the callback if set
                decoderOutput.write(object);
            } catch (InvalidClassException ex) {
                ex.printStackTrace();
                if (this.clientCallback != null) {
                    this.clientCallback.clientIsIncompatible(rb.getResourceString("client.incompatible"));
                }
            }
        }
        //buffer has been consumed: return true
        return (true);
    }

    private byte[] decompress(byte[] data) throws Exception {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            byte[] buffer = new byte[2048];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } finally {
            inflater.end();
        }
    }

    /**
     * The danger in serialization is that classes are implementing the private
     * readObject method to execute custom code: "An object is deserialized when
     * its serialized form is converted to a copy of the object. It is important
     * to ensure the security of this conversion. Deserialization is code
     * execution, because the readObject method of the class that is being
     * deserialized can contain custom code. Serializable classes, also known as
     * "gadget classes", can do arbitrary reflective actions such as create
     * classes and invoke methods on them. If your application deserializes
     * these classes, they can cause a denial of service or remote code
     * execution."
     *
     * @param filterInfo
     * @return
     */
    private ObjectInputFilter.Status clientServerMessageFilter(ObjectInputFilter.FilterInfo filterInfo) {
        Class serialClass = filterInfo.serialClass();
        if (serialClass == null) {
            return (ObjectInputFilter.Status.ALLOWED);
        }
        if (serialClass.isPrimitive()) {
            return (ObjectInputFilter.Status.ALLOWED);
        }
        //allow all embedded objects
        if (filterInfo.depth() > 1) {
            return (ObjectInputFilter.Status.ALLOWED);
        }
        synchronized (this.allowedClientServerClassList) {
            if (this.allowedClientServerClassList.contains(serialClass.getName())) {
                return ObjectInputFilter.Status.ALLOWED;
            }
        }
        //is it a subclass of ClientServerMessage? Then allow the deserialization
        if (de.mendelson.util.clientserver.messages.ClientServerMessage.class.isAssignableFrom(serialClass)) {
            synchronized (this.allowedClientServerClassList) {
                if (!this.allowedClientServerClassList.contains(serialClass.getName())) {
                    this.allowedClientServerClassList.add(serialClass.getName());
                }
            }
            return ObjectInputFilter.Status.ALLOWED;
        }
        return ObjectInputFilter.Status.REJECTED;
    }

}
