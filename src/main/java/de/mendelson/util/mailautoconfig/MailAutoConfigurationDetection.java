//$Header: /as4/de/mendelson/util/mailautoconfig/MailAutoConfigurationDetection.java 5     19/02/25 9:39 Heller $
package de.mendelson.util.mailautoconfig;

import de.mendelson.util.XPathHelper;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xml.sax.InputSource;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Configuration detection for a mail server from a give mail address
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class MailAutoConfigurationDetection {

    public MailAutoConfigurationDetection() {
    }

    /**
     * Downloads a XML that contains a client config and extracts the mail
     * server configurations Will return null if this fails.
     */
    private List<MailServiceConfiguration> extractClientConfig(String clientConfigURL,
            List<String> allowedServiceList) {
        String clientConfigXML = this.downloadXML(clientConfigURL);
        if (clientConfigXML == null) {
            return (null);
        }
        List<MailServiceConfiguration> list = new ArrayList<MailServiceConfiguration>();
        try {
            XPathHelper xpathHelper = new XPathHelper(new InputSource(new StringReader(clientConfigXML)));
            //right format?
            if (!xpathHelper.pathExists("/clientConfig/emailProvider")) {
                return (null);
            }
            String mailProviderLongName = xpathHelper.getValue("/clientConfig/emailProvider/displayName");
            //collect inbound configuration
            int inboundConfigCount = xpathHelper.getNodeCount("/clientConfig/emailProvider/incomingServer");
            for (int i = 1; i <= inboundConfigCount; i++) {
                String serviceName = xpathHelper.getValue("/clientConfig/emailProvider/incomingServer[" + i + "]/@type");
                if (serviceName != null && allowedServiceList.contains(serviceName)) {
                    String hostname = xpathHelper.getValue("/clientConfig/emailProvider/incomingServer[" + i + "]/hostname");
                    String portStr = xpathHelper.getValue("/clientConfig/emailProvider/incomingServer[" + i + "]/port");
                    int port = 0;
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (Exception e) {
                    }
                    String securityStr = xpathHelper.getValue("/clientConfig/emailProvider/incomingServer[" + i + "]/socketType");
                    int security = MailServiceConfiguration.SECURITY_PLAIN;
                    if (securityStr.equals("STARTTLS")) {
                        security = MailServiceConfiguration.SECURITY_START_TLS;
                    } else if (securityStr.equals("SSL")) {
                        security = MailServiceConfiguration.SECURITY_TLS;
                    }
                    MailServiceConfiguration configuration = new MailServiceConfiguration(serviceName,
                            hostname, port,
                            security, mailProviderLongName);
                    list.add(configuration);
                }
            }
            //collect outbound configuration
            int outboundConfigCount = xpathHelper.getNodeCount("/clientConfig/emailProvider/outgoingServer");
            for (int i = 1; i <= outboundConfigCount; i++) {
                String serviceName = xpathHelper.getValue("/clientConfig/emailProvider/outgoingServer[" + i + "]/@type");
                String hostname = xpathHelper.getValue("/clientConfig/emailProvider/outgoingServer[" + i + "]/hostname");
                String portStr = xpathHelper.getValue("/clientConfig/emailProvider/outgoingServer[" + i + "]/port");
                int port = 0;
                try {
                    port = Integer.parseInt(portStr);
                } catch (Exception e) {
                }
                String securityStr = xpathHelper.getValue("/clientConfig/emailProvider/outgoingServer[" + i + "]/socketType");
                int security = MailServiceConfiguration.SECURITY_PLAIN;
                if (securityStr.equals("STARTTLS")) {
                    security = MailServiceConfiguration.SECURITY_START_TLS;
                } else if (securityStr.equals("SSL")) {
                    security = MailServiceConfiguration.SECURITY_TLS;
                }
                MailServiceConfiguration configuration = new MailServiceConfiguration(serviceName,
                        hostname, port,
                        security, mailProviderLongName);
                list.add(configuration);
            }
        } catch (Exception e) {
        }
        if (!list.isEmpty()) {
            return (list);
        }
        return (null);
    }

    /**
     * Tries to detect a mail server configuration form a give mail address and
     * returns it as list
     *
     * @param mailAddress The mail address to check the service settings for
     * @param allowedServiceList The list of allowed services
     *
     */
    public List<MailServiceConfiguration> detectConfiguration(String mailAddress,
            List<String> allowedServiceList) {
        try {
            String mailHost = mailAddress.substring(mailAddress.indexOf("@") + 1);
            //auto detection URL is defined in DNS SRV record
            String autoconfigURL = this.lookupSRVRecordForAutoConfigURL(mailHost);
            if (autoconfigURL != null) {
                autoconfigURL = autoconfigURL + "/mail/config-v1.1.xml?emailaddress=" + mailAddress;
                List<MailServiceConfiguration> list = this.extractClientConfig(autoconfigURL, allowedServiceList);
                if (list != null) {
                    return (list);
                }
            }
            //auto detection via http and autoconfig.example.com
            autoconfigURL = "http://autoconfig." + mailHost + "/mail/config-v1.1.xml?emailaddress=" + mailAddress;
            List<MailServiceConfiguration> list = this.extractClientConfig(autoconfigURL, allowedServiceList);
            if (list != null) {
                return (list);
            }
            //auto detection via https and autoconfig.example.com
            autoconfigURL = "https://autoconfig." + mailHost + "/mail/config-v1.1.xml?emailaddress=" + mailAddress;
            list = this.extractClientConfig(autoconfigURL, allowedServiceList);
            if (list != null) {
                return (list);
            }
            //auto detection via http and .well-known URL
            autoconfigURL = "http://" + mailHost + "/.well-known/autoconfig/mail/config-v1.1.xml?emailaddress="
                    + mailAddress;
            list = this.extractClientConfig(autoconfigURL, allowedServiceList);
            if (list != null) {
                return (list);
            }
            //auto detection via https and .well-known URL
            autoconfigURL = "https://" + mailHost + "/.well-known/autoconfig/mail/config-v1.1.xml?emailaddress="
                    + mailAddress;
            list = this.extractClientConfig(autoconfigURL, allowedServiceList);
            if (list != null) {
                return (list);
            }
            //auto detection via thunderbird database
            autoconfigURL = "https://autoconfig.thunderbird.net/v1.1/" + mailHost;
            list = this.extractClientConfig(autoconfigURL, allowedServiceList);
            if (list != null) {
                return (list);
            }
            //auto detection via thunderbird database and MX record
            String mxRecordTarget = this.lookupTargetFromMXRecord(mailHost);
            if (mxRecordTarget != null) {
                autoconfigURL = "https://autoconfig.thunderbird.net/v1.1/" + mxRecordTarget;
                list = this.extractClientConfig(autoconfigURL, allowedServiceList);
                if (list != null) {
                    return (list);
                }
            }
        } catch (Exception e) {
        }
        return (null);
    }

    /**
     * Checks the DNS MX (mail exchange) record of a given domain and returns
     * the target domain
     */
    private String lookupTargetFromMXRecord(String domain) {
        try {
            Record[] records = new Lookup(domain, Type.MX).run();
            if (records != null && records.length > 0) {
                    MXRecord mx = (MXRecord) records[0];
                    String target = mx.getTarget().toString(true);
                    return (target.substring(target.indexOf(".") + 1));
            }
        } catch (TextParseException e) {
        }
        return (null);
    }

    /**
     * Checks the SRV (service) record of a given domain with a request to the
     * service _autodiscover. This will return the autodiscover URL if this is
     * defined - else this will return null
     *
     * @param domain Domain to check the autodiscover service at
     * @return
     */
    private String lookupSRVRecordForAutoConfigURL(String domain) {
        try {
            Record[] records = new Lookup("_autodiscover._tcp." + domain, Type.SRV).run();
            if (records != null && records.length > 0) {
                    SRVRecord srv = (SRVRecord) records[0];
                    StringBuilder autoconfigURL = new StringBuilder();
                    if (srv.getPort() == 443) {
                        autoconfigURL.append("https://");
                    } else {
                        autoconfigURL.append("http://");
                    }
                    autoconfigURL.append(srv.getTarget().toString(true))
                            .append(":")
                            .append(srv.getPort());
                    return (autoconfigURL.toString());
            }
        } catch (TextParseException e) {
        }
        return (null);
    }

    /**
     * Downloads an XML from a given URL using the java http client. If the
     * return value is not HTTP 200 this will return null. If there is a
     * redirection on the target this will also return null.
     */
    private String downloadXML(String url) {
        HttpClient httpClient = HttpClient.newBuilder()
                //By default, the connection timeout period is 15000 milliseconds
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    //By default, the read timeout period is 60000 milliseconds 
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response
                    = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return (response.body());
            }
        } catch (Exception e) {
        }
        return (null);
    }

}
