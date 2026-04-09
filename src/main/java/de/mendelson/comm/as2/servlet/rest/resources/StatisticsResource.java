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

import de.mendelson.comm.as2.statistic.StatisticExportRequest;
import de.mendelson.comm.as2.statistic.StatisticExportResponse;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.RestApplication;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * REST API for statistics operations
 * Handles statistics export and reporting
 *
 */
@Path("/statistics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StatisticsResource {

    /**
     * Export statistics report
     * Query parameters:
     * - startDate: Start date in milliseconds since epoch
     * - endDate: End date in milliseconds since epoch
     * - timestep: Time step (hour, day, month)
     * - localStationId: Filter by local station ID
     * - partnerId: Filter by partner ID
     */
    @GET
    @Path("/export")
    @Produces("text/csv")
    public Response exportStatistics(
            @QueryParam("startDate") Long startDateMs,
            @QueryParam("endDate") Long endDateMs,
            @QueryParam("timestep") String timestep,
            @QueryParam("localStationId") Integer localStationId,
            @QueryParam("partnerId") Integer partnerId) {

        AS2ServerProcessing processing = RestApplication.ServerProcessingHolder.getInstance();
        if (processing == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorResponse("Server processing not available"))
                    .build();
        }

        // Default dates if not provided
        Date startDate = startDateMs != null ? new Date(startDateMs) : new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        Date endDate = endDateMs != null ? new Date(endDateMs) : new Date();

        // Timestep values: 1 (hour), 2 (day), 3 (month) - default to day
        long timestepValue = 2; // day
        if ("hour".equalsIgnoreCase(timestep)) {
            timestepValue = 1;
        } else if ("month".equalsIgnoreCase(timestep)) {
            timestepValue = 3;
        }

        StatisticExportRequest request = new StatisticExportRequest();
        request.setStartDate(startDate.getTime());
        request.setEndDate(endDate.getTime());
        request.setTimestep(timestepValue);
        request.setLocalStation(null); // Would need partner lookup by ID
        request.setPartner(null);       // Would need partner lookup by ID

        StatisticExportResponse response = processing.processStatisticExportRequest(request);

        if (response.getException() != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(response.getException().getMessage()))
                    .build();
        }

        byte[] data = response.getDataBytes();
        if (data == null || data.length == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("No statistics data available"))
                    .build();
        }

        return Response.ok(new ByteArrayInputStream(data))
                .header("Content-Disposition", "attachment; filename=\"statistics.csv\"")
                .type("text/csv")
                .build();
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
}
