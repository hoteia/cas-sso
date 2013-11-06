package org.hoteia.cas;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class CasRestfulClient {

    private static final String TICKETS_URL = "/cas/v1/tickets";

    private final String serverUrl;

    public CasRestfulClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServiceTicket(String username, String password, String service) throws IOException {
        String serviceTicketGrantingUrl = getServiceTicketGrantingUrl(username, password);
        Map<String, String> data = ImmutableMap.of("service", service);
        HttpResponse response = HttpClientParserUtil.postDataToUrl(serviceTicketGrantingUrl, data);

        try {
            return isServiceTicketRequestSuccessful(response) ? extractServiceTicket(response) : "";
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

    private String extractServiceTicket(HttpResponse response) throws IOException {
        return CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
    }

    public String getServiceTicketGrantingUrl(String username, String password) throws IOException {
        Map<String, String> data = ImmutableMap.of("username", username, "password", password);
        HttpResponse response = HttpClientParserUtil.postDataToUrl(serverUrl + TICKETS_URL, data);

        try {
            return isTicketGrantingTicketRequestSuccessful(response) ? extractServiceTicketGrantingUrl(response) : "";
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

    private boolean isTicketGrantingTicketRequestSuccessful(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
    }

    private String extractServiceTicketGrantingUrl(HttpResponse response) {
        return response.getFirstHeader("Location").getValue();
    }

    private boolean isServiceTicketRequestSuccessful(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    public static void main(String... args) throws IOException {
        CasRestfulClient client = new CasRestfulClient("https://hal9000:8443");
        client.getServiceTicket("casuser", "Mellon", "https://HAL9000:9443/webapp-sso-redirect-test/j_spring_cas_security_check");
    }
}
