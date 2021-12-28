package com.geekshirt.orderservice.wiremock;

import com.geekshirt.orderservice.dto.OrderRequest;
import com.geekshirt.orderservice.dto.OrderResponse;
import com.geekshirt.orderservice.util.OrderServiceDataTestUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WiremockTest02 {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static WireMockServer wireMockServer;
    private static final String   PATH_URL = "/api/v1/account/5";
    private static final String   JSON = "{\"id\":1,\"address\":{\"id\":1,\"street\":\"3142 McDonald Street\",\"city\":\"Brooklyn\",\"state\":\"NY\",\"country\":\"USA\",\"zipCode\":\"11201\"},\"customer\":{\"id\":1,\"firstName\":\"Parker\",\"lastName\":\"Peter\",\"email\":\"peterparker@gmail.com\"},\"creditCard\":{\"id\":1,\"nameOnCard\":\"Peter Parker\",\"number\":\"4565 9018 2124 2422\",\"type\":\"VISA\",\"expirationMonth\":\"07\",\"expirationYear\":\"2024\",\"ccv\":\"212\"},\"status\":\"ACTIVE\"}";

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void init() {
        wireMockServer = buildWireMockServer();
    }

    private static WireMockServer buildWireMockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();
        return wireMockServer;
    }

    @Test
    public void wiremockControllerTest()  {
        configureFor("localhost", wireMockServer.port());

        stubFor(get(urlEqualTo(PATH_URL))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody(JSON)));

        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("5");
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> entity = new HttpEntity<>(orderRequest, header);

        testRestTemplate.postForEntity("/order/create", entity, OrderResponse.class);
    }

    // mappings: contiene ficheros en formato JSON con las combinaciones de solicitud y respuesta
    // files: contiene ficheros con el contenido de las respuestas del body.

    // ver como modificar la ruta base de lectura del wiremock para agregar paquetes
    // revisar como juntar parte del mapping con el file (body)
}
