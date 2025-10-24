package org.springshop.shipment_service.controller.exception;

public class StockException extends RuntimeException {
    public StockException(String message) {
        super(message);
    }
}
