package com.rikkei.bai4.exception;

public class OrderExpiredException extends RuntimeException {

    public OrderExpiredException(String message) {
        super(message);
    }
}