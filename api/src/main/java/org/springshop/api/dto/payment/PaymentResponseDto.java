package org.springshop.api.dto.payment;

import java.time.LocalDateTime;

import org.springshop.api.model.payment.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {

    private Integer id;
    private Integer orderId;
    private String method;
    private Double amount;
    private String currency;
    private PaymentStatus status; 
    private String transactionId; 
    private LocalDateTime createdAt;

}