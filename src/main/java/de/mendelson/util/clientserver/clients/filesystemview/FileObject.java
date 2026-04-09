package de.mendelson.util.clientserver.clients.filesystemview;

import java.io.Serializable;
import java.net.URI;
import java.nio.file.Paths;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public abstract class FileObject implements Serializable, Comparable<FileObject> {

    private static final long serialVersionUID = 1L;
    private final URI fileURI;
    private final String absolutePathDisplayOnServerSide;

    protected FileObject(URI fileURI) {
        this.fileURI = fileURI;
        this.absolutePathDisplayOnServerSide = Paths.get(fileURI).toAbsolutePath().toString();
    }

    /**
     * @return the file
     */
    public URI getFileURI() {
        return this.fileURI;
    }


    @Override
    public int compareTo(FileObject otherFileObject) {
        if (otherFileObject == null || this.fileURI == null) {
            return (0);
        }
        return (Paths.get(this.fileURI).getFileName().toString()
                .compareTo(Paths.get(otherFileObject.getFileURI()).getFileName().toString()));
    }

    /**
     * @return the absolutePathDisplayOnServerSide
     */
    public String getAbsolutePathDisplayOnServerSide() {
        return absolutePathDisplayOnServerSide;
    }

}
