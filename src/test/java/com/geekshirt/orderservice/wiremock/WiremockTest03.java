package com.geekshirt.orderservice.wiremock;

import com.geekshirt.orderservice.dto.OrderRequest;
import com.geekshirt.orderservice.dto.OrderResponse;
import com.geekshirt.orderservice.util.OrderServiceDataTestUtils;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WiremockTest03 {

    @Autowired
    private TestRestTemplate testRestTemplate;

    //permite ejecutar cualquier número de instancias de WireMock
    // y proporciona un control total sobre la configuración.
    @RegisterExtension
    static WireMockExtension wm1 = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8089))
            .build();

    @RegisterExtension
    static WireMockExtension wm2 = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8087))
            .build();

    @Test
    public void wiremockControllerTest()  {
        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("1");
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> entity = new HttpEntity<>(orderRequest, header);

        testRestTemplate.postForEntity("/order/create", entity, OrderResponse.class);
    }
}
