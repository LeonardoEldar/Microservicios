package com.geekshirt.orderservice.controllers;

import com.geekshirt.orderservice.dto.OrderRequest;
import com.geekshirt.orderservice.dto.OrderResponse;
import com.geekshirt.orderservice.entities.Order;
import com.geekshirt.orderservice.exception.PaymentNotAcceptedException;
import com.geekshirt.orderservice.service.OrderService;
import com.geekshirt.orderservice.utils.EntityDtoConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//La anotacion api se necesita para dercribir los metodos en Swagger
@Api
@RestController
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private EntityDtoConverter converter;

    //Info adicional a los metodos en Swagger
    @ApiOperation(value = "Return all orders", notes = "This operation returns all stored orders")
    @GetMapping(value = "order")
    public ResponseEntity<List<OrderResponse>> findAll(){
        List<Order> allOrders = orderService.findAllOrders();
        List<OrderResponse> orderResponses = converter.convertEntityToDto(allOrders);
        return new ResponseEntity<>(orderResponses, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve an order based on Id", notes = "This operation return an order by Order Id")
    @GetMapping(value = "order/{orderId}")
    public ResponseEntity<OrderResponse> findById(@PathVariable String orderId) {
        Order order = orderService.findOrderById(orderId);
        OrderResponse response = converter.convertEntityToDto(order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve order based on Id", notes = "This operation return an order by primary key")
    @GetMapping(value = "order/generated/{orderId}")
    public ResponseEntity<OrderResponse> findByGenerateId(@PathVariable Long orderId) {
        Order order = orderService.findyId(orderId);
        OrderResponse response = converter.convertEntityToDto(order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Creates an order", notes = "This Operation creates a new order.")
    @PostMapping(value = "order/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest payload) throws PaymentNotAcceptedException {
        Order order = orderService.createOrder(payload);
        return new ResponseEntity<>(converter.convertEntityToDto(order), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Retrieve an orders based on AccountId ", notes = "This Operation returns orders by accountId")
    @GetMapping(value = "order/account/{accountId}")
    public ResponseEntity<List<OrderResponse>> findOrdersByAccountId(@PathVariable String accountId) {
        List<Order> orders = orderService.findOrdersByAccountId(accountId);
        return new ResponseEntity<>(converter.convertEntityToDto(orders), HttpStatus.OK);
    }
}
