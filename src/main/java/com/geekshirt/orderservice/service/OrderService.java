package com.geekshirt.orderservice.service;

import com.geekshirt.orderservice.client.CustomerServiceClient;
import com.geekshirt.orderservice.client.InventoryServiceClient;
import com.geekshirt.orderservice.dto.AccountDto;
import com.geekshirt.orderservice.dto.Confirmation;
import com.geekshirt.orderservice.dto.OrderRequest;
import com.geekshirt.orderservice.dto.ShipmentOrderResponse;
import com.geekshirt.orderservice.entities.Order;
import com.geekshirt.orderservice.entities.OrderDetail;
import com.geekshirt.orderservice.exception.AccountNotFoundException;
import com.geekshirt.orderservice.exception.OrderNotFoundException;
import com.geekshirt.orderservice.exception.PaymentNotAcceptedException;
import com.geekshirt.orderservice.producer.ShippingOrderProducer;
import com.geekshirt.orderservice.repositories.OrderRepository;
import com.geekshirt.orderservice.utils.ExceptionMessagesEnum;
import com.geekshirt.orderservice.utils.OrderPaymentStatus;
import com.geekshirt.orderservice.utils.OrderStatus;
import com.geekshirt.orderservice.utils.OrderValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.geekshirt.orderservice.utils.Constants.TAX_IMPORT;
import static com.geekshirt.orderservice.utils.ExceptionMessagesEnum.ORDER_NOT_FOUND;

@Slf4j
@Service
public class OrderService {

    @Autowired
    CustomerServiceClient customerServiceClient;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private PaymentProcessorService paymentService;

    @Autowired
    private InventoryServiceClient inventoryClient;

    @Autowired
    private ShippingOrderProducer shipmentMessageProducer;

    @Autowired
    private OrderMailService mailService;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Order createOrder(OrderRequest orderRequest) throws PaymentNotAcceptedException {
        OrderValidator.validateOrder(orderRequest);

        AccountDto account = customerServiceClient.findAccountById(orderRequest.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(ExceptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue()));

        Order newOrder = initOrder(orderRequest);

        Confirmation confirmation = paymentService.processPayment(newOrder, account);

        log.info("Received Payment Confirmation: {}", confirmation);

        String paymentStatus = confirmation.getTransactionStatus();
        newOrder.setPaymentStatus(OrderPaymentStatus.valueOf(paymentStatus));

        if (paymentStatus.equals(OrderPaymentStatus.DENIED.name())) {
            newOrder.setStatus(OrderStatus.NA);
            orderRepository.save(newOrder);
            throw new PaymentNotAcceptedException("The Credit Card added to your account was not accepted, please verify.");
        }

        log.info("Updating Inventory: {}", orderRequest.getItems());
        inventoryClient.updateInventory(orderRequest.getItems());

        log.info("Sending Request to Shipping Service.");
        shipmentMessageProducer.send(newOrder.getOrderId(), account);

        return orderRepository.save(newOrder);
    }

    private Order initOrder(OrderRequest orderRequest) {
        Order orderObj = new Order();
        orderObj.setOrderId(UUID.randomUUID().toString());
        orderObj.setAccountId(orderRequest.getAccountId());
        orderObj.setStatus(OrderStatus.PENDING);

        List<OrderDetail> orderDetails = orderRequest.getItems().stream()
                .map(item -> OrderDetail.builder()
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .upc(item.getUpc())
                        .tax((item.getPrice() * item.getQuantity()) * TAX_IMPORT)
                        .totalAmount((item.getPrice() * item.getQuantity()))
                        .order(orderObj).build())
                .collect(Collectors.toList());

        orderObj.setDetails(orderDetails);
        orderObj.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getTotalAmount).sum());
        orderObj.setTotalTax(orderObj.getTotalAmount() * TAX_IMPORT);
        orderObj.setTotalAmountTax(orderObj.getTotalAmount() + orderObj.getTotalTax());
        orderObj.setTransactionDate(new Date());

        return orderObj;
    }

    @Cacheable(value = "orders")
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Order findOrderById(String orderId) {
        Optional<Order> order = Optional.ofNullable(orderRepository.findOrderByOrderId(orderId));
        return order.orElseThrow((() -> new OrderNotFoundException(ORDER_NOT_FOUND.getValue())));
    }

    public Order findyId(Long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException(ORDER_NOT_FOUND.getValue()));
    }

    @Cacheable(value = "ordersAccount", key = "#accountId")
    public List<Order> findOrdersByAccountId(String accountId) {
        Optional<List<Order>> orders = Optional.ofNullable(orderRepository.findOrdersByAccountId(accountId));
        return orders.orElseThrow(() -> new OrderNotFoundException("Orders were not found"));
    }

    public void updateShipmentOrder(ShipmentOrderResponse response) {
        try {
            Order order = findOrderById(response.getOrderId());
            order.setStatus(OrderStatus.valueOf(response.getShippingStatus()));
            orderRepository.save(order);
            // Para que funcione se tiene que hablitar apps menos seguras.
            mailService.sendEmail(order, response);
        }
        catch(OrderNotFoundException orderNotFound) {
            log.info("The Following Order was not found: {} with tracking Id: {}", response.getOrderId(), response.getTrackingId());
        }
        catch(Exception e) {
            log.info("An error occurred sending email: " + e.getMessage());
        }
    }
}
