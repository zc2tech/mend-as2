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
import de.mendelson.util.clientserver.messages.ClientServerResponse;

import java.util.List;

/**
 * Response for inbound authentication credential operations
 *
 * @author Julian Xu
 */
public class InboundAuthCredentialResponse extends ClientServerResponse {

    private List<InboundAuthCredential> credentials;
    private boolean success;

    public InboundAuthCredentialResponse(InboundAuthCredentialRequest request) {
        super(request);
        this.success = true;
    }

    @Override
    public String toString() {
        return "Inbound auth credential response";
    }

    public List<InboundAuthCredential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<InboundAuthCredential> credentials) {
        this.credentials = credentials;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
