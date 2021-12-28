package com.geekshirt.orderservice.wiremock;

import com.geekshirt.orderservice.client.CustomerServiceClient;
import com.geekshirt.orderservice.dto.AccountDto;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockRestServiceServer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@WireMockTest(httpPort = 8085)
@TestPropertySource(properties = {"{customerservice.url}=http://localhost:${wiremock.server.port}/api/v1/account"})
public class SpringCloudContractRunnerTest03 {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Test
    public void test01(WireMockRuntimeInfo wmRuntimeInfo) {
        String httpBaseUrl = wmRuntimeInfo.getHttpBaseUrl();

        MockRestServiceServer server = WireMockRestServiceServer.with(this.restTemplate)
                .baseUrl(httpBaseUrl+"/api/v1/account/1").stubs("classpath:/mappings/customer-client/customer-client-mock-with-file.json")
                .build();

        Optional<AccountDto> accountDto = this.customerServiceClient.findAccountById("1");
        Assertions.assertNotNull(accountDto);
        server.verify();
    }
}
