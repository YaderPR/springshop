package org.springshop.webhook_service.dto.payment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentResponse {

    private Integer id;
    private Integer orderId;
    private String method;
    private Double amount;
    private String currency;
    private PaymentStatus status; 
    private String transactionId; 
    private LocalDateTime createdAt;

}