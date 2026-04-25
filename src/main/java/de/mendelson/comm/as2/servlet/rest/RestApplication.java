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

package de.mendelson.comm.as2.servlet.rest;

import de.mendelson.comm.as2.server.AS2ServerProcessing;
import de.mendelson.comm.as2.servlet.rest.auth.AdminOnlyFilter;
import de.mendelson.comm.as2.servlet.rest.auth.AuthenticationResource;
import de.mendelson.comm.as2.servlet.rest.auth.JwtAuthenticationFilter;
import de.mendelson.comm.as2.servlet.rest.exceptions.ApiExceptionMapper;
import de.mendelson.comm.as2.servlet.rest.resources.CertificateResource;
import de.mendelson.comm.as2.servlet.rest.resources.IPWhitelistResource;
import de.mendelson.comm.as2.servlet.rest.resources.MessageResource;
import de.mendelson.comm.as2.servlet.rest.resources.NotificationResource;
import de.mendelson.comm.as2.servlet.rest.resources.PartnerResource;
import de.mendelson.comm.as2.servlet.rest.resources.PreferencesResource;
import de.mendelson.comm.as2.servlet.rest.resources.StatisticsResource;
import de.mendelson.comm.as2.servlet.rest.resources.SystemResource;
import de.mendelson.comm.as2.servlet.rest.resources.TrackerMessageResource;
import de.mendelson.comm.as2.servlet.rest.resources.UserHttpAuthPreferenceResource;
import de.mendelson.comm.as2.servlet.rest.resources.UserManagementResource;
import de.mendelson.comm.as2.servlet.rest.resources.UserTrackerAuthResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * JAX-RS application configuration for REST API
 * Configures Jackson, registers resources, sets up dependency injection
 *
 */
public class RestApplication extends ResourceConfig {

    public RestApplication() {
        // Register Jackson for JSON
        register(JacksonFeature.class);
        register(JacksonObjectMapperProvider.class);
        register(MultiPartFeature.class);

        // Register authentication filter
        register(JwtAuthenticationFilter.class);

        // Register authorization filters
        register(AdminOnlyFilter.class);

        // Register exception mappers
        register(ApiExceptionMapper.class);

        // Register resource classes
        register(AuthenticationResource.class);
        register(SystemResource.class);
        register(PartnerResource.class);
        register(CertificateResource.class);
        register(MessageResource.class);
        register(StatisticsResource.class);
        register(PreferencesResource.class);
        register(NotificationResource.class);
        register(UserManagementResource.class);
        register(UserHttpAuthPreferenceResource.class);
        register(TrackerMessageResource.class);
        register(IPWhitelistResource.class);
        register(UserTrackerAuthResource.class);

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
