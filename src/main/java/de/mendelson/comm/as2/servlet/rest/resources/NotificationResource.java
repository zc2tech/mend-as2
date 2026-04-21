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

package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.util.systemevents.notification.NotificationData;
import de.mendelson.util.systemevents.notification.NotificationDataImplAS2;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetRequest;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetResponse;
import de.mendelson.util.systemevents.notification.clientserver.NotificationSetMessage;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * REST API for email notification settings
 */
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    /**
     * Get notification settings
     */
    @GET
    public Response getNotificationSettings() {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        NotificationGetRequest request = new NotificationGetRequest();
        NotificationGetResponse response = processing.processNotificationGetRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        NotificationData notificationData = response.getData();
        NotificationDTO dto = new NotificationDTO();

        if (notificationData instanceof NotificationDataImplAS2 impl) {
            dto.setMailServer(impl.getMailServer());
            dto.setMailServerPort(impl.getMailServerPort());
            dto.setConnectionSecurity(impl.getConnectionSecurity());
            dto.setNotificationMail(impl.getNotificationMail());
            dto.setReplyTo(impl.getReplyTo());
            dto.setUseSMTPAuthCredentials(impl.usesSMTPAuthCredentials());
            dto.setSmtpUser(impl.getSMTPUser());
            // Don't send password in GET response
            dto.setMaxNotificationsPerMin(impl.getMaxNotificationsPerMin());
            dto.setNotifyCertExpire(impl.notifyCertExpire());
            dto.setNotifyTransactionError(impl.notifyTransactionError());
            dto.setNotifySystemFailure(impl.notifySystemFailure());
            dto.setNotifyCEM(impl.notifyCEM());
            dto.setNotifyConnectionProblem(impl.notifyConnectionProblem());
            dto.setNotifyPostprocessingProblem(impl.notifyPostprocessingProblem());
            dto.setNotifyClientServerProblem(impl.notifyClientServerProblem());
        }

        return Response.ok(dto).build();
    }

    /**
     * Update notification settings
     */
    @PUT
    public Response updateNotificationSettings(
            @Context SecurityContext securityContext,
            NotificationDTO dto) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        // Get existing notification data to preserve password if not provided
        NotificationGetRequest request = new NotificationGetRequest();
        NotificationGetResponse response = processing.processNotificationGetRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        NotificationDataImplAS2 existingData = (NotificationDataImplAS2) response.getData();

        NotificationDataImplAS2 notificationData = new NotificationDataImplAS2();
        notificationData.setMailServer(dto.getMailServer());
        notificationData.setMailServerPort(dto.getMailServerPort());
        notificationData.setConnectionSecurity(dto.getConnectionSecurity());
        notificationData.setNotificationMail(dto.getNotificationMail());
        notificationData.setReplyTo(dto.getReplyTo());
        notificationData.setUsesSMTPAuthCredentials(dto.isUseSMTPAuthCredentials());
        notificationData.setSMTPUser(dto.getSmtpUser());

        // Only update password if provided, otherwise preserve existing password
        if (dto.getSmtpPass() != null && !dto.getSmtpPass().isEmpty()) {
            notificationData.setSMTPPass(dto.getSmtpPass().toCharArray());
        } else {
            // Preserve existing password from database
            notificationData.setSMTPPass(existingData.getSMTPPass());
        }

        notificationData.setMaxNotificationsPerMin(dto.getMaxNotificationsPerMin());
        notificationData.setNotifyCertExpire(dto.isNotifyCertExpire());
        notificationData.setNotifyTransactionError(dto.isNotifyTransactionError());
        notificationData.setNotifySystemFailure(dto.isNotifySystemFailure());
        notificationData.setNotifyCEM(dto.isNotifyCEM());
        notificationData.setNotifyConnectionProblem(dto.isNotifyConnectionProblem());
        notificationData.setNotifyPostprocessingProblem(dto.isNotifyPostprocessingProblem());
        notificationData.setNotifyClientServerProblem(dto.isNotifyClientServerProblem());

        NotificationSetMessage message = new NotificationSetMessage();
        message.setData(notificationData);

        String userName = securityContext.getUserPrincipal() != null
                ? securityContext.getUserPrincipal().getName()
                : "api-user";

        processing.processNotificationSetMessage(message, userName, "REST-API");

        return Response.ok(new SuccessResponse("Notification settings updated successfully")).build();
    }

    /**
     * DTO for notification settings
     */
    public static class NotificationDTO {
        private String mailServer;
        private int mailServerPort;
        private int connectionSecurity;
        private String notificationMail;
        private String replyTo;
        private boolean useSMTPAuthCredentials;
        private String smtpUser;
        private String smtpPass;
        private int maxNotificationsPerMin;
        private boolean notifyCertExpire;
        private boolean notifyTransactionError;
        private boolean notifySystemFailure;
        private boolean notifyCEM;
        private boolean notifyConnectionProblem;
        private boolean notifyPostprocessingProblem;
        private boolean notifyClientServerProblem;

        // Getters and setters
        public String getMailServer() { return mailServer; }
        public void setMailServer(String mailServer) { this.mailServer = mailServer; }

        public int getMailServerPort() { return mailServerPort; }
        public void setMailServerPort(int mailServerPort) { this.mailServerPort = mailServerPort; }

        public int getConnectionSecurity() { return connectionSecurity; }
        public void setConnectionSecurity(int connectionSecurity) { this.connectionSecurity = connectionSecurity; }

        public String getNotificationMail() { return notificationMail; }
        public void setNotificationMail(String notificationMail) { this.notificationMail = notificationMail; }

        public String getReplyTo() { return replyTo; }
        public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

        public boolean isUseSMTPAuthCredentials() { return useSMTPAuthCredentials; }
        public void setUseSMTPAuthCredentials(boolean useSMTPAuthCredentials) { this.useSMTPAuthCredentials = useSMTPAuthCredentials; }

        public String getSmtpUser() { return smtpUser; }
        public void setSmtpUser(String smtpUser) { this.smtpUser = smtpUser; }

        public String getSmtpPass() { return smtpPass; }
        public void setSmtpPass(String smtpPass) { this.smtpPass = smtpPass; }

        public int getMaxNotificationsPerMin() { return maxNotificationsPerMin; }
        public void setMaxNotificationsPerMin(int maxNotificationsPerMin) { this.maxNotificationsPerMin = maxNotificationsPerMin; }

        public boolean isNotifyCertExpire() { return notifyCertExpire; }
        public void setNotifyCertExpire(boolean notifyCertExpire) { this.notifyCertExpire = notifyCertExpire; }

        public boolean isNotifyTransactionError() { return notifyTransactionError; }
        public void setNotifyTransactionError(boolean notifyTransactionError) { this.notifyTransactionError = notifyTransactionError; }

        public boolean isNotifySystemFailure() { return notifySystemFailure; }
        public void setNotifySystemFailure(boolean notifySystemFailure) { this.notifySystemFailure = notifySystemFailure; }

        public boolean isNotifyCEM() { return notifyCEM; }
        public void setNotifyCEM(boolean notifyCEM) { this.notifyCEM = notifyCEM; }

        public boolean isNotifyConnectionProblem() { return notifyConnectionProblem; }
        public void setNotifyConnectionProblem(boolean notifyConnectionProblem) { this.notifyConnectionProblem = notifyConnectionProblem; }

        public boolean isNotifyPostprocessingProblem() { return notifyPostprocessingProblem; }
        public void setNotifyPostprocessingProblem(boolean notifyPostprocessingProblem) { this.notifyPostprocessingProblem = notifyPostprocessingProblem; }

        public boolean isNotifyClientServerProblem() { return notifyClientServerProblem; }
        public void setNotifyClientServerProblem(boolean notifyClientServerProblem) { this.notifyClientServerProblem = notifyClientServerProblem; }
    }

    /**
     * DTO for error responses
     */
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    /**
     * DTO for success responses
     */
    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
