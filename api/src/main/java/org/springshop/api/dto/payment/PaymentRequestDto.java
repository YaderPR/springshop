package org.springshop.api.dto.payment;


import org.springshop.api.model.payment.PaymentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    @NotBlank
    private String method; // "CARD", "PAYPAL", "CASH_ON_DELIVERY"

    @NotNull
    private Double amount;

    private PaymentStatus status;

    @NotNull
    private Integer orderId; // referencia a la orden
}

