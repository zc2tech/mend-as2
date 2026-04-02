package de.mendelson.comm.as2.servlet.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.auth.AuthenticationResource;
import de.mendelson.comm.as2.servlet.rest.auth.JwtAuthenticationFilter;
import de.mendelson.comm.as2.servlet.rest.exceptions.ApiExceptionMapper;
import de.mendelson.comm.as2.servlet.rest.resources.CemResource;
import de.mendelson.comm.as2.servlet.rest.resources.CertificateResource;
import de.mendelson.comm.as2.servlet.rest.resources.MessageResource;
import de.mendelson.comm.as2.servlet.rest.resources.PartnerResource;
import de.mendelson.comm.as2.servlet.rest.resources.PreferencesResource;
import de.mendelson.comm.as2.servlet.rest.resources.StatisticsResource;
import de.mendelson.comm.as2.servlet.rest.resources.SystemResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * JAX-RS application configuration for REST API
 * Configures Jackson, registers resources, sets up dependency injection
 *
 * @author S.Heller
 */
public class RestApplication extends ResourceConfig {

    public RestApplication() {
        System.out.println("RestApplication: Initializing Jersey REST application...");

        // Register Jackson for JSON
        register(JacksonFeature.class);
        register(JacksonObjectMapperProvider.class);
        register(MultiPartFeature.class);

        // Register authentication filter
        register(JwtAuthenticationFilter.class);

        // Register exception mappers
        register(ApiExceptionMapper.class);

        // Register resource classes
        System.out.println("RestApplication: Registering AuthenticationResource");
        register(AuthenticationResource.class);
        System.out.println("RestApplication: Registering SystemResource");
        register(SystemResource.class);
        register(PartnerResource.class);
        register(CertificateResource.class);
        register(MessageResource.class);
        register(CemResource.class);
        register(StatisticsResource.class);
        register(PreferencesResource.class);

        System.out.println("RestApplication: Jersey REST application initialized");

        // Configure Jackson ObjectMapper
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                // Bind AS2ServerProcessing for injection
                AS2ServerProcessing processing = ServerProcessingHolder.getInstance();
                if (processing != null) {
                    bind(processing).to(AS2ServerProcessing.class);
                }
            }
        });
    }

    /**
     * Static holder for AS2ServerProcessing instance
     * Initialized by AS2Server after startup
     */
    public static class ServerProcessingHolder {
        private static AS2ServerProcessing instance;

        public static void setInstance(AS2ServerProcessing processing) {
            instance = processing;
        }

        public static AS2ServerProcessing getInstance() {
            return instance;
        }
    }
}
