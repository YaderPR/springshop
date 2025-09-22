package org.springshop.api.dto.payment;

import java.time.LocalDateTime;

import org.springshop.api.model.payment.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {

    private Long id;

    private String method;

    private Double amount;

    private PaymentStatus status; // PENDING, SUCCESS, FAILED, REFUNDED

    private String transactionId;

    private LocalDateTime createdAt;

    private Integer orderId; // solo referencia a la orden
}

