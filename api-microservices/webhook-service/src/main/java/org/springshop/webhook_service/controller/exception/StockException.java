package org.springshop.webhook_service.controller.exception;

public class StockException extends RuntimeException {
    public StockException(String message) {
        super(message);
    }
}
