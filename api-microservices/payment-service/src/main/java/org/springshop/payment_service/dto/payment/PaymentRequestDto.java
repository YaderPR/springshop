package org.springshop.payment_service.dto.payment;

import org.springshop.payment_service.model.payment.PaymentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    @NotNull
    private Integer orderId;
    
    @NotBlank
    private String currency; 

    @NotBlank
    private String method;

    @NotNull
    private Double amount;

    @NotNull
    private PaymentStatus status; // PENDING, SUCCESS, FAILED, REFUNDED

    @NotNull
    private String transactionId; // referencia de pasarela (Stripe, PayPal, etc.)
}