package com.geekshirt.orderservice.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import wiremock.org.apache.http.HttpResponse;
import wiremock.org.apache.http.client.methods.HttpGet;
import wiremock.org.apache.http.impl.client.CloseableHttpClient;
import wiremock.org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class WiremockErrorTest {

    private static WireMockServer wireMockServer;
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String PATH_URL = "/invalidPath";

    @BeforeAll
    static void beforeAll() {
        wireMockServer = buildWireMockServer();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    private static WireMockServer buildWireMockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        return wireMockServer;
    }

    @Test
    public void endpointNotFoundTest() throws IOException {
        configureFor("localhost", wireMockServer.port());

        HttpGet request = new HttpGet(String.format("http://localhost:%s%s", wireMockServer.port(), PATH_URL));
        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = convertResponseToString(httpResponse);

        verify(getRequestedFor(urlEqualTo(PATH_URL)));
        assertEquals("{\"status\":\"Error\",\"message\":\"Endpoint not found\"}", stringResponse);
        assertEquals(NOT_FOUND.value(), httpResponse.getStatusLine().getStatusCode());
        assertEquals(APPLICATION_JSON.toString(), httpResponse.getFirstHeader("Content-Type").getValue());
    }

    private static String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, UTF_8);
        String stringResponse = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return stringResponse;
    }
}
