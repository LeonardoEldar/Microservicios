package com.geekshirt.orderservice.wiremock;

import com.geekshirt.orderservice.dto.AccountDto;
import com.geekshirt.orderservice.dto.Confirmation;
import com.geekshirt.orderservice.dto.PaymentRequest;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockRestServiceServer;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockAccount;
import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockPaymentRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest()
public class SpringContractRunnerParameterizedTest {

    @Autowired
    private RestTemplate restTemplate;

    private static Stream<Arguments> getProvideParameters() {
        return Stream.of(
                Arguments.of("/api/v1/account/1",
                        "classpath:/mappings/customer-client/customer-client-mock-with-file.json",
                        AccountDto.class.getName()),
                Arguments.of("/api/v1/account/3",
                        "classpath:/mappings/customer-client/customer-client-mock-with-file_02.json",
                        AccountDto.class.getName())
        );
    }

    private static Stream<Arguments> postProvideParameters() {
        PaymentRequest mockPaymentRequest = getMockPaymentRequest(getMockAccount());

        return Stream.of(
                Arguments.of("/api/v1/payment/authorize",
                        "classpath:/mappings/payment-service/payment-service-mock.json",
                        Confirmation.class.getName(),
                        mockPaymentRequest)
        );
    }

    @ParameterizedTest
    @MethodSource("getProvideParameters")
    public void getForObjectTest(String endpoint, String stub, String className, WireMockRuntimeInfo wmRuntimeInfo)
            throws ClassNotFoundException {
        String httpBaseUrl = wmRuntimeInfo.getHttpBaseUrl();
        String urlEndpoint = httpBaseUrl + endpoint;

        MockRestServiceServer server = WireMockRestServiceServer.with(this.restTemplate)
                .baseUrl(httpBaseUrl).stubs(stub)
                .build();

            Class<?> classDto = Class.forName(className);
            Object object = restTemplate.getForObject(urlEndpoint, classDto);
            server.verify();
            Assertions.assertNotNull(object);
    }

    @ParameterizedTest
    @MethodSource("postProvideParameters")
    public void postForObjectTest(String endpoint, String stub, String className, Object request,
                                  WireMockRuntimeInfo wmRuntimeInfo) throws ClassNotFoundException {
        String httpBaseUrl = wmRuntimeInfo.getHttpBaseUrl();
        String urlEndpoint = httpBaseUrl + endpoint;

        MockRestServiceServer server = WireMockRestServiceServer.with(this.restTemplate)
                .baseUrl(httpBaseUrl).stubs(stub)
                .build();

        Class<?> classDto = Class.forName(className);
        Object object = restTemplate.postForObject(urlEndpoint, request, classDto);
        server.verify();
        Assertions.assertNotNull(object);
    }
}
