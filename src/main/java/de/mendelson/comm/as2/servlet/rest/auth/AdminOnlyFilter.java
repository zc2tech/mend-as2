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

package de.mendelson.comm.as2.servlet.rest.auth;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Authorization filter for @AdminOnly annotated endpoints.
 * Checks that the authenticated user is the 'admin' super user.
 * Must run after authentication (hence AUTHORIZATION priority).
 *
 * @author Julian Xu
 */
@Provider
@AdminOnly
@Priority(Priorities.AUTHORIZATION)
public class AdminOnlyFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(AdminOnlyFilter.class.getName());
    private static final String ADMIN_USERNAME = "admin";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get authenticated username from security context
        String username = requestContext.getSecurityContext().getUserPrincipal().getName();

        // Check if user is 'admin' super user
        if (!ADMIN_USERNAME.equals(username)) {
            LOGGER.warning("User '" + username + "' attempted to access admin-only endpoint: " +
                    requestContext.getUriInfo().getPath());

            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"error\":\"Access denied. This endpoint requires admin super user privileges.\"}")
                            .build()
            );
        }
    }
}
