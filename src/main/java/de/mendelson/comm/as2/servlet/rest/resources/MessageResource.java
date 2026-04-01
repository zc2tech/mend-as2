package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.MessageOverviewFilter;
import de.mendelson.comm.as2.message.clientserver.*;
import de.mendelson.comm.as2.log.LogEntry;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * REST API for message operations
 * Handles message listing, details, manual send, payload access
 *
 * @author S.Heller
 */
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

    /**
     * Get list of messages with optional filtering
     * Query parameters:
     * - startTime: Filter by start time (milliseconds since epoch)
     * - endTime: Filter by end time (milliseconds since epoch)
     * - limit: Maximum number of messages to return (default 1000)
     * - messageId: Get specific message by ID
     */
    @GET
    public Response listMessages(
            @QueryParam("startTime") Long startTime,
            @QueryParam("endTime") Long endTime,
            @QueryParam("limit") Integer limit,
            @QueryParam("messageId") String messageId) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageOverviewRequest request;
        if (messageId != null) {
            // Get specific message
            request = new MessageOverviewRequest(messageId);
        } else {
            // Get filtered list
            MessageOverviewFilter filter = new MessageOverviewFilter();

            if (startTime != null) {
                filter.setStartTime(startTime);
            }

            if (endTime != null) {
                filter.setEndTime(endTime);
            }

            if (limit != null) {
                filter.setLimit(limit);
            }

            request = new MessageOverviewRequest(filter);
        }

        MessageOverviewResponse response = processing.processMessageOverviewRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        MessageListResponse dto = new MessageListResponse();
        dto.setMessages(response.getList());
        dto.setTotalCount(response.getMessageSumOnServer());

        return Response.ok(dto).build();
    }

    /**
     * Get message transaction log
     */
    @GET
    @Path("/{messageId}/log")
    public Response getMessageLog(@PathParam("messageId") String messageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageLogRequest request = new MessageLogRequest(messageId);
        MessageLogResponse response = processing.processMessageLogRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        return Response.ok(response.getList()).build();
    }

    /**
     * Get detailed message information
     */
    @GET
    @Path("/{messageId}/details")
    public Response getMessageDetails(@PathParam("messageId") String messageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageDetailRequest request = new MessageDetailRequest(messageId);
        MessageDetailResponse response = processing.processMessageDetailRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        return Response.ok(response.getList()).build();
    }

    /**
     * Get message payload data
     */
    @GET
    @Path("/{messageId}/payload")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMessagePayload(@PathParam("messageId") String messageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessagePayloadRequest request = new MessagePayloadRequest(messageId);
        MessagePayloadResponse response = processing.processMessagePayloadRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        List<AS2Payload> payloads = response.getList();
        if (payloads == null || payloads.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("No payload found for message"))
                    .build();
        }

        // Return first payload
        AS2Payload payload = payloads.get(0);
        byte[] data;
        try {
            data = payload.getData();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error reading payload: " + e.getMessage()))
                    .build();
        }
        String contentType = payload.getContentType();

        if (data == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Payload data not available"))
                    .build();
        }

        return Response.ok(new ByteArrayInputStream(data))
                .header("Content-Disposition", "attachment; filename=\"payload.dat\"")
                .type(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM)
                .build();
    }

    /**
     * Get last message entry for a message
     */
    @GET
    @Path("/{messageId}/last")
    public Response getLastMessage(@PathParam("messageId") String messageId) {
        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        MessageRequestLastMessage request = new MessageRequestLastMessage(messageId);
        MessageResponseLastMessage response = processing.processMessageRequestLastMessage(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        return Response.ok(response.getInfo()).build();
    }

    /**
     * DTO for message list response
     */
    public static class MessageListResponse {
        private List<AS2MessageInfo> messages;
        private int totalCount;

        public List<AS2MessageInfo> getMessages() {
            return messages;
        }

        public void setMessages(List<AS2MessageInfo> messages) {
            this.messages = messages;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
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
