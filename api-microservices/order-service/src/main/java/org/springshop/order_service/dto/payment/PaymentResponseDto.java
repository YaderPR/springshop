package org.springshop.order_service.dto.payment;

import java.time.LocalDateTime;

import org.springshop.order_service.model.payment.PaymentStatus;

import lombok.Data;

@Data
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