package de.mendelson.comm.as2.servlet.rest.auth;

import de.mendelson.util.clientserver.user.User;
import de.mendelson.util.clientserver.user.UserAccess;
import de.mendelson.util.security.PBKDF2;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.logging.Logger;

/**
 * REST resource for authentication operations
 * Handles login, logout, and token refresh
 *
 * @author S.Heller
 */
@Path("/auth")
public class AuthenticationResource {

    private static final String ACCESS_TOKEN_COOKIE = "as2_access_token";
    private static final String REFRESH_TOKEN_COOKIE = "as2_refresh_token";
    private static final int ACCESS_TOKEN_MAX_AGE = 15 * 60; // 15 minutes in seconds
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7 days in seconds

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    private final UserAccess userAccess;
    private final Logger logger = Logger.getLogger("de.mendelson.as2.server");

    public AuthenticationResource() {
        this.userAccess = new UserAccess(logger);
    }

    /**
     * Login with username and password
     * Returns JWT tokens in HttpOnly cookies
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        try {
            // Validate credentials
            User user = userAccess.readUser(loginRequest.getUsername());
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid username or password"))
                        .build();
            }

            // Check password
            boolean passwordValid = PBKDF2.validatePassword(
                    loginRequest.getPassword(),
                    user.getPasswdCrypted()
            );

            if (!passwordValid) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid username or password"))
                        .build();
            }

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(loginRequest.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getUsername());

            // Set tokens in HttpOnly cookies
            NewCookie accessCookie = new NewCookie(
                    ACCESS_TOKEN_COOKIE,
                    accessToken,
                    "/",
                    null,
                    null,
                    ACCESS_TOKEN_MAX_AGE,
                    false, // secure (set to true in production with HTTPS)
                    true   // httpOnly
            );

            NewCookie refreshCookie = new NewCookie(
                    REFRESH_TOKEN_COOKIE,
                    refreshToken,
                    "/",
                    null,
                    null,
                    REFRESH_TOKEN_MAX_AGE,
                    false, // secure
                    true   // httpOnly
            );

            return Response.ok(new LoginResponse(loginRequest.getUsername()))
                    .cookie(accessCookie, refreshCookie)
                    .build();

        } catch (Exception e) {
            logger.warning("Login failed: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Authentication error"))
                    .build();
        }
    }

    /**
     * Logout - clear authentication cookies
     */
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        NewCookie accessCookie = new NewCookie(
                ACCESS_TOKEN_COOKIE,
                "",
                "/",
                null,
                null,
                0,
                false,
                true
        );

        NewCookie refreshCookie = new NewCookie(
                REFRESH_TOKEN_COOKIE,
                "",
                "/",
                null,
                null,
                0,
                false,
                true
        );

        return Response.ok(new MessageResponse("Logged out successfully"))
                .cookie(accessCookie, refreshCookie)
                .build();
    }

    /**
     * Refresh access token using refresh token
     */
    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh(@Context HttpHeaders headers) {
        try {
            Cookie refreshCookie = headers.getCookies().get(REFRESH_TOKEN_COOKIE);
            if (refreshCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Missing refresh token"))
                        .build();
            }

            String refreshToken = refreshCookie.getValue();

            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Invalid refresh token"))
                        .build();
            }

            // Generate new access token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.generateAccessToken(username);

            NewCookie accessCookie = new NewCookie(
                    ACCESS_TOKEN_COOKIE,
                    newAccessToken,
                    "/",
                    null,
                    null,
                    ACCESS_TOKEN_MAX_AGE,
                    false,
                    true
            );

            return Response.ok(new MessageResponse("Token refreshed"))
                    .cookie(accessCookie)
                    .build();

        } catch (Exception e) {
            logger.warning("Token refresh failed: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Token refresh failed"))
                    .build();
        }
    }

    // DTOs

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private String username;

        public LoginResponse(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

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

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
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
