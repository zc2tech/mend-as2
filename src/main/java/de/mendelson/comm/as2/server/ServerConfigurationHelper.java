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
package de.mendelson.comm.as2.server;

import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import java.net.InetAddress;

/**
 * Helper class to get server configuration information like hostname and ports.
 * Centralizes logic for getting actual listening ports and hostname.
 *
 * @author Julian Xu
 */
public class ServerConfigurationHelper {

    /**
     * Get the actual hostname of the server.
     * Tries to get canonical hostname first, falls back to regular hostname.
     * If the hostname doesn't contain a dot (not a FQDN), falls back to localhost.
     *
     * @return Server hostname, or "localhost" as last resort
     */
    public static String getHostname() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostname = inetAddress.getCanonicalHostName();

            // If canonical hostname is still an IP or localhost, try to get the hostname
            if (hostname.equals("localhost") || hostname.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                hostname = inetAddress.getHostName();
            }

            // If hostname doesn't contain a dot, it's not a proper internet name (FQDN)
            // Fall back to localhost in this case
            if (hostname != null && !hostname.contains(".")) {
                return "localhost";
            }

            return hostname;
        } catch (Exception e) {
            // Use localhost as fallback
            return "localhost";
        }
    }

    /**
     * Get HTTPS port from HTTP server configuration.
     * Returns the port of the first SSL/TLS listener found.
     * Handles test mode automatically (reads actual listening ports).
     *
     * @param configInfo HTTP server configuration info
     * @return HTTPS port, or null if no HTTPS listener found
     */
    public static Integer getHttpsPort(HTTPServerConfigInfo configInfo) {
        if (configInfo == null) {
            return null;
        }

        for (HTTPServerConfigInfo.Listener listener : configInfo.getListener()) {
            String protocol = listener.getProtocol();
            if (protocol != null && protocol.toLowerCase().contains("ssl")) {
                return listener.getPort();
            }
        }

        return null;
    }

    /**
     * Get HTTP port from HTTP server configuration.
     * Returns the port of the first non-SSL listener found.
     * Handles test mode automatically (reads actual listening ports).
     *
     * @param configInfo HTTP server configuration info
     * @return HTTP port, or null if no HTTP listener found
     */
    public static Integer getHttpPort(HTTPServerConfigInfo configInfo) {
        if (configInfo == null) {
            return null;
        }

        for (HTTPServerConfigInfo.Listener listener : configInfo.getListener()) {
            String protocol = listener.getProtocol();
            if (protocol == null || !protocol.toLowerCase().contains("ssl")) {
                return listener.getPort();
            }
        }

        return null;
    }

    /**
     * Get the preferred listener (HTTPS first, then HTTP).
     * Used for generating URLs when only one protocol is needed.
     *
     * @param configInfo HTTP server configuration info
     * @return Preferred listener, or null if none found
     */
    public static HTTPServerConfigInfo.Listener getPreferredListener(HTTPServerConfigInfo configInfo) {
        if (configInfo == null || configInfo.getListener().isEmpty()) {
            return null;
        }

        // Try to find HTTPS/SSL listener first
        for (HTTPServerConfigInfo.Listener listener : configInfo.getListener()) {
            String protocol = listener.getProtocol();
            if (protocol != null && protocol.toLowerCase().contains("ssl")) {
                return listener;
            }
        }

        // Fallback to first listener (HTTP)
        return configInfo.getListener().get(0);
    }

    /**
     * Get the preferred listener (HTTPS first, then HTTP).
     * Used for generating URLs when only one protocol is needed.
     *
     * @param configInfo  HTTP server configuration info
     * @param reqProtocol Required protocol (e.g. "http" or "https") to prefer
     * @return Preferred listener, or null if none found
     */
    public static HTTPServerConfigInfo.Listener getListener(String reqProtocol, HTTPServerConfigInfo configInfo) {
        if (configInfo == null || configInfo.getListener().isEmpty()) {
            return null;
        }

        // Try to find HTTPS/SSL listener first
        for (HTTPServerConfigInfo.Listener listener : configInfo.getListener()) {
            String protocol = listener.getProtocol();
            if (protocol == null) {
                continue;
            }
            if (protocol.toLowerCase().contains("ssl") && reqProtocol.equals("https")) {
                return listener;
            } else {
                // if it's  not ssl/https, then it's http
                if (reqProtocol.equals("http")) {
                   return listener;
                }
            }
        }
        return null;
    }

    /**
     * Check if a listener is HTTPS/SSL.
     *
     * @param listener Listener to check
     * @return true if HTTPS/SSL, false otherwise
     */
    public static boolean isHttpsListener(HTTPServerConfigInfo.Listener listener) {
        if (listener == null) {
            return false;
        }
        String protocol = listener.getProtocol();
        return protocol != null && protocol.toLowerCase().contains("ssl");
    }

    /**
     * Get protocol string (http or https) from listener.
     *
     * @param listener Listener
     * @return "https" or "http"
     */
    public static String getProtocol(HTTPServerConfigInfo.Listener listener) {
        return isHttpsListener(listener) ? "https" : "http";
    }
}
