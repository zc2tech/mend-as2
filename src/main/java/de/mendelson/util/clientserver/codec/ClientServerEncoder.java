package de.mendelson.util.clientserver.codec;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Deflater;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Encodes a passed object to send it to the client/server. The structure is: 4
 * bytes: 32 bit value that contains the [object length] [object length] bytes:
 * Object that is transmitted
 *
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class ClientServerEncoder implements ProtocolEncoder {

    public ClientServerEncoder() {
    }

    /**
     * Returns a 32 bit value as byte array, fixed length 4 bytes
     */
    private byte[] encodeLengthHeader32Bit(int length) {
        //BIG_ENDIAN is the standard oder under java - just to ensure this
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(length).array();
    }

    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput encoderOutput) throws Exception {
        try (ByteArrayOutputStream objectBuffer = new ByteArrayOutputStream()) {
            try (ObjectOutput objectOut = new ObjectOutputStream(objectBuffer)) {
                objectOut.writeObject(message);
                objectOut.flush();
            }
            byte[] compressedObjectArray = this.compress(objectBuffer.toByteArray());
            byte[] objectHeader = this.encodeLengthHeader32Bit(compressedObjectArray.length);
            // Enable buffer pooling to improve performance by reusing buffers 
            // and reducing allocation overhead for frequent I/O operations.
            IoBuffer buffer = IoBuffer.allocate(objectHeader.length + compressedObjectArray.length, true);
            try {
                buffer.put(objectHeader);
                buffer.put(compressedObjectArray);
                buffer.flip();
                encoderOutput.write(buffer);
            } finally {
                buffer.free();
            }
        }
    }

    private byte[] compress(byte[] data) throws Exception {
        Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        deflater.setInput(data);
        deflater.finish();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            byte[] buffer = new byte[2048];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } finally {
            deflater.end();
        }
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        // nothing to dispose
    }

}
