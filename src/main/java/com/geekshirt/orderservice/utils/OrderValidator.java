package com.geekshirt.orderservice.utils;

import com.geekshirt.orderservice.dto.OrderRequest;
import com.geekshirt.orderservice.exception.IncorrectOrderRequestException;

public class OrderValidator {
    public static boolean validateOrder (OrderRequest orderRequest) {
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new IncorrectOrderRequestException(ExceptionMessagesEnum.EMPTY_ORDER_ITEM.getValue());
        }
        return true;
    }
}
