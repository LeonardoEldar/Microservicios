package com.geekshirt.orderservice.wiremock;

import com.geekshirt.orderservice.client.CustomerServiceClient;
import com.geekshirt.orderservice.client.PaymentServiceClient;
import com.geekshirt.orderservice.dto.AccountDto;
import com.geekshirt.orderservice.dto.Confirmation;
import com.geekshirt.orderservice.dto.PaymentRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockAccount;
import static com.geekshirt.orderservice.util.OrderServiceDataTestUtils.getMockPaymentRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {"{customerservice.url}=http://localhost:${wiremock.server.port}/api/v1/account"})
@TestPropertySource(properties = {"{paymentservice.url}=http://localhost:${wiremock.server.port}/api/v1/payment/authorize"})
public class UpdateEndpointServiceTest {

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Test
    public void test01() {
        Optional<AccountDto> accountDto = this.customerServiceClient.findAccountById("1");
        Assertions.assertNotNull(accountDto);
    }

    @Test
    public void test02() {
        PaymentRequest mockPaymentRequest = getMockPaymentRequest(getMockAccount());
        Confirmation confirmation = this.paymentServiceClient.authorize(mockPaymentRequest);
        Assertions.assertNotNull(confirmation);
    }
}
