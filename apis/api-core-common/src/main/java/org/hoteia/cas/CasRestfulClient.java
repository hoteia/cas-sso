package org.hoteia.cas;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasRestfulClient {

    private static final String TICKETS_URL = "/cas/v1/tickets";
    private static final Pattern TICKET_REGEX = Pattern.compile(".*/cas/v1/tickets/(.+)");

    private final String serverUrl;
    private final String service;

    public CasRestfulClient(String serverUrl, String service) {
        this.serverUrl = serverUrl;
        this.service = service;
    }

    public String getServiceTicket(String serviceTicketGrantingUrl) throws IOException {
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

    public String getTicketGrantingTicket(String username, String password) throws IOException {
        String url = getServiceTicketGrantingUrl(username, password);
        return extractTicketGrantingTicketFromUrl(url);
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

    public String extractTicketGrantingTicketFromUrl(String serviceTicketGrantingUrl) {
        Matcher matcher = TICKET_REGEX.matcher(serviceTicketGrantingUrl);
        return matcher.matches() ? matcher.group(1) : "";
    }

    public String createTicketGrantingTicketFromUrl(String ticketGrantingTicket) {
        return serverUrl + TICKETS_URL + "/" + ticketGrantingTicket;
    }

    public boolean logout(String ticketGrantingTicket) throws IOException {
        String logoutUrl = createTicketGrantingTicketFromUrl(ticketGrantingTicket);
        HttpDelete delete = new HttpDelete(logoutUrl);
        HttpResponse response = HttpClientParserUtil.execute(delete);
        EntityUtils.consume(response.getEntity());
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    public static void main(String... args) throws IOException {
        CasRestfulClient client = new CasRestfulClient("https://hal9000:8443", "https://HAL9000:9443/webapp-sso-redirect-test/j_spring_cas_security_check");
        String serviceTicketGrantingUrl = client.getServiceTicketGrantingUrl("casuser", "Mellon");
        client.getServiceTicket(serviceTicketGrantingUrl);
        client.logout(client.extractTicketGrantingTicketFromUrl(serviceTicketGrantingUrl));
    }
}
