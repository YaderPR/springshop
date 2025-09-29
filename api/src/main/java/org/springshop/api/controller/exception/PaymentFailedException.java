package org.springshop.api.controller.exception;

// Usamos RuntimeException para que Spring lo maneje automáticamente
public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}