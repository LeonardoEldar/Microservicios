package com.geekshirt.orderservice.wiremock;

import com.geekshirt.orderservice.dto.AccountDto;
import com.geekshirt.orderservice.dto.Confirmation;
import com.geekshirt.orderservice.dto.PaymentRequest;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockRestServiceServer;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockAccount;
import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockPaymentRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest()
public class SpringCloudContractRunnerTest02 {

    private static final String PATH_URL_01 = "/api/v1/account/1";
    private static final String PATH_URL_02 = "/api/v1/payment/authorize";

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test01(WireMockRuntimeInfo wmRuntimeInfo) {
        String httpBaseUrl = wmRuntimeInfo.getHttpBaseUrl();

        MockRestServiceServer server = WireMockRestServiceServer.with(this.restTemplate)
                .baseUrl(httpBaseUrl).stubs("classpath:/mappings/customer-client/customer-client-mock-with-file.json")
                .build();

        String url = httpBaseUrl + PATH_URL_01;
        AccountDto accountDto = restTemplate.getForObject(url, AccountDto.class);
        server.verify();
        Assertions.assertNotNull(accountDto);
    }

    @Test
    public void test02(WireMockRuntimeInfo wmRuntimeInfo) {
        String httpBaseUrl = wmRuntimeInfo.getHttpBaseUrl();

        MockRestServiceServer server = WireMockRestServiceServer.with(this.restTemplate)
                .baseUrl(httpBaseUrl).stubs("classpath:/mappings/payment-service/payment-service-mock.json")
                .build();

        PaymentRequest mockPaymentRequest = getMockPaymentRequest(getMockAccount());
        String url = httpBaseUrl + PATH_URL_02;
        Confirmation confirmation = restTemplate.postForObject(url, mockPaymentRequest, Confirmation.class);
        server.verify();
        Assertions.assertNotNull(confirmation);
    }
}
