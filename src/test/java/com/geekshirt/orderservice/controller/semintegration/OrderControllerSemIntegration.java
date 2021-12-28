package com.geekshirt.orderservice.controller.semintegration;

import com.geekshirt.orderservice.dto.OrderRequest;
import com.geekshirt.orderservice.dto.OrderResponse;
import com.geekshirt.orderservice.service.PaymentProcessorService;
import com.geekshirt.orderservice.utils.OrderPaymentStatus;
import com.geekshirt.orderservice.util.OrderServiceDataTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
//Spring Boot Test levanta el contenedor y se conecta a la base real y a los microservicios asociados
//En estas pruebas en algunos casos se podría realizar mocks de microservicios pero igualmente los
//otros servicios se conectan de forma real a la base y hacen las llamadas a las api.
//Levantar los demas proyectos para que funcione la prueba.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerSemIntegration {

    //Hace un Mock porque el payment puede dar aprobado o rechazado de forma aleatoria.
    @MockBean
    private PaymentProcessorService paymentService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void shouldCreateNewOrderWhenCreateOrderEndpointIsCalled() {
        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("1");

        Mockito.when(paymentService.processPayment(any(), any()))
                .thenReturn(OrderServiceDataTestUtils.getMockPayment(orderRequest.getAccountId(), OrderPaymentStatus.APPROVED));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> entity = new HttpEntity<>(orderRequest, header);

        ResponseEntity<OrderResponse> response = testRestTemplate.postForEntity("/order/create", entity, OrderResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        OrderResponse bodyResponse = response.getBody();
        Assertions.assertNotNull(bodyResponse);
        Assertions.assertEquals(orderRequest.getAccountId(), bodyResponse.getAccountId());
        Assertions.assertEquals(orderRequest.getItems().size(), bodyResponse.getDetails().size());
        Assertions.assertNotNull(bodyResponse.getOrderId());
        Assertions.assertFalse(bodyResponse.getOrderId().isEmpty());
        Assertions.assertEquals(Double.valueOf("1005d"), bodyResponse.getTotalAmount());
        Assertions.assertEquals(Double.valueOf("160.8"), bodyResponse.getTotalTax());
        Assertions.assertEquals(Double.valueOf("1165.8"), bodyResponse.getTotalAmountTax());
        Assertions.assertNotNull(bodyResponse.getTransactionDate());
    }
}
