package de.mendelson.comm.as2.servlet.rest.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Global exception mapper for REST API
 * Catches all exceptions and returns JSON error responses
 *
 * @author S.Heller
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger logger = Logger.getLogger("de.mendelson.as2.server");

    @Override
    public Response toResponse(Throwable exception) {
        logger.warning("REST API error: [" + exception.getClass().getSimpleName() + "] " + exception.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ))
                .build();
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
