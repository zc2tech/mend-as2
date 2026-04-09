/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.mendelson.comm.as2.clientserver.message;

import de.mendelson.comm.as2.preferences.InboundAuthCredential;
import de.mendelson.util.clientserver.messages.ClientServerMessage;

import java.util.List;

/**
 * Message to request inbound authentication credential operations
 *
 * @author Julian Xu
 */
public class InboundAuthCredentialRequest extends ClientServerMessage{

    public static final int OPERATION_GET = 1;
    public static final int OPERATION_SAVE = 2;
    public static final int OPERATION_DELETE_ALL = 3;

    private int operation;
    private int authType;  // InboundAuthCredential.TYPE_BASIC or TYPE_CERTIFICATE
    private List<InboundAuthCredential> credentials;  // For SAVE operation

    public InboundAuthCredentialRequest(int operation, int authType) {
        this.operation = operation;
        this.authType = authType;
    }

    @Override
    public String toString() {
        return "Inbound auth credential request";
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public List<InboundAuthCredential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<InboundAuthCredential> credentials) {
        this.credentials = credentials;
    }
}
