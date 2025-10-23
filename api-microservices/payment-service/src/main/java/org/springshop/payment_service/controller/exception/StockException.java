package org.springshop.payment_service.controller.exception;

public class StockException extends RuntimeException {
    public StockException(String message) {
        super(message);
    }
}
