package de.mendelson.comm.as2.servlet.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.mendelson.util.security.cert.KeystoreCertificate;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Jackson ObjectMapper configuration for REST API
 * Configures JSON serialization settings
 *
 * @author S.Heller
 */
@Provider
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public JacksonObjectMapperProvider() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);

        // Add mixin to control KeystoreCertificate serialization
        mapper.addMixIn(KeystoreCertificate.class, KeystoreCertificateMixin.class);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
