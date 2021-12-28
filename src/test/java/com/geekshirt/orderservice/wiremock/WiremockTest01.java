package com.geekshirt.orderservice.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

//La anotacion ejecutará un solo servidor WireMock,
// por defecto en un puerto aleatorio, solo HTTP (no HTTPS).
@WireMockTest
public class WiremockTest01 {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String PATH_URL = "/test";

    //Para obtener el número de puerto en ejecución,
    // la URL base o una instancia de DSL, puede declarar un parámetro de tipo WireMockRuntimeInfo
    @Test
    public void test(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        stubFor(get(urlEqualTo(PATH_URL))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", APPLICATION_JSON.toString())
                .withBody("Test")));

        HttpGet request = new HttpGet(String.format("http://localhost:%s%s", wmRuntimeInfo.getHttpPort(), PATH_URL));
        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = convertResponseToString(httpResponse);

        verify(getRequestedFor(urlEqualTo(PATH_URL)));
        assertEquals("Test", stringResponse);
        assertEquals(OK.value(), httpResponse.getStatusLine().getStatusCode());
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
