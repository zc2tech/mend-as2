package de.mendelson.comm.as2.servlet.rest.auth;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * JAX-RS filter that validates JWT tokens from HttpOnly cookies
 * Allows unauthenticated access to /auth endpoints
 *
 * @author S.Heller
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE = "as2_access_token";
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Allow authentication endpoints without token
        if (path.startsWith("auth/")) {
            return;
        }

        // Extract token from cookie
        Cookie cookie = requestContext.getCookies().get(ACCESS_TOKEN_COOKIE);
        if (cookie == null) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Missing authentication token\"}")
                            .build()
            );
            return;
        }

        String token = cookie.getValue();

        // Validate token
        if (!jwtTokenProvider.validateToken(token)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Invalid or expired token\"}")
                            .build()
            );
            return;
        }

        // Set security context
        String username = jwtTokenProvider.getUsernameFromToken(token);
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> username;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true; // All authenticated users have access
            }

            @Override
            public boolean isSecure() {
                return requestContext.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "JWT";
            }
        });
    }
}
