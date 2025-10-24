package org.springshop.webhook_service.model.payment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Payment {

    private Integer id;
    private Integer orderId;
    private String method;
    private Double amount;
    private String currency;
    private PaymentStatus status; 
    private String transactionId; 
    private LocalDateTime createdAt;

}