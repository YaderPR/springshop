package org.springshop.api.controller.exception;

public class StockException extends RuntimeException {
    public StockException(String message) {
        super(message);
    }
}
