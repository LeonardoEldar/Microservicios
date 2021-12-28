package com.geekshirt.orderservice.wiremock;

import com.geekshirt.orderservice.dto.AccountDto;
import com.geekshirt.orderservice.dto.Confirmation;
import com.geekshirt.orderservice.dto.PaymentRequest;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockAccount;
import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockPaymentRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest()
public class SpringCloudContractRunnerTest01 {

    private static final String PATH_URL_01 = "/api/v1/account/1";
    private static final String PATH_URL_02 = "/api/v1/payment/authorize";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void test01(WireMockRuntimeInfo wmRuntimeInfo) {
        String formatUrl = String.format("http://localhost:%s%s", wmRuntimeInfo.getHttpPort(), PATH_URL_01);
        AccountDto accountDto = testRestTemplate.getForObject(formatUrl, AccountDto.class);
        WireMock.verify(1, getRequestedFor(urlEqualTo(PATH_URL_01)));
        Assertions.assertNotNull(accountDto);
    }

    @Test
    public void test02(WireMockRuntimeInfo wmRuntimeInfo) {
        String formatUrl = String.format("http://localhost:%s%s", wmRuntimeInfo.getHttpPort(), PATH_URL_02);
        PaymentRequest mockPaymentRequest = getMockPaymentRequest(getMockAccount());
        Confirmation confirmation = testRestTemplate.postForObject(formatUrl, mockPaymentRequest, Confirmation.class);
        verify(exactly(1), postRequestedFor(urlEqualTo(PATH_URL_02)));
    }
}
