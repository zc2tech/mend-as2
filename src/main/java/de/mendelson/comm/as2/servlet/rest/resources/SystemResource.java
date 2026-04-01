package de.mendelson.comm.as2.servlet.rest.resources;

import de.mendelson.comm.as2.AS2ServerVersion;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource for system information
 *
 * @author S.Heller
 */
@Path("/system")
public class SystemResource {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemInfo() {
        SystemInfo info = new SystemInfo();
        info.setProductName(AS2ServerVersion.getFullProductName());
        info.setVersion(AS2ServerVersion.getVersion());
        info.setBuild(AS2ServerVersion.getBuild());

        return Response.ok(info).build();
    }

    public static class SystemInfo {
        private String productName;
        private String version;
        private String build;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBuild() {
            return build;
        }

        public void setBuild(String build) {
            this.build = build;
        }
    }
}
