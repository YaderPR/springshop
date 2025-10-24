package org.springshop.webhook_service.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    
    @NotNull
    private Integer orderId;

    @NotBlank
    private String currency;

    @NotBlank
    private String method;

    @NotNull
    private Double amount;

    @NotNull
    private PaymentStatus status;

    @NotBlank
    private String transactionId; 
}
