package com.geekshirt.orderservice.utils;

public enum ExceptionMessagesEnum {
    EMPTY_ORDER_ITEM("Los items de la orden están vacíos"),
    ACCOUNT_NOT_FOUND("Account not found"),
    ORDER_NOT_FOUND("Order not found");

    private final String value;

    ExceptionMessagesEnum(String msg) {
        value = msg;
    }

    public String getValue() {
        return value;
    }
}
