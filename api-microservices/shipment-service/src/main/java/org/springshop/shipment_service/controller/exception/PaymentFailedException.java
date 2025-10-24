package org.springshop.shipment_service.controller.exception;

// Usamos RuntimeException para que Spring lo maneje automáticamente
public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}