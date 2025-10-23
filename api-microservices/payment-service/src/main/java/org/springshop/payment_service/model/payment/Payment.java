// Payment.java (AJUSTADO)
package org.springshop.payment_service.model.payment;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String method; // CARD, PAYPAL, CASH_ON_DELIVERY, etc.

    private Double amount;
    private String currency; 

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, SUCCESS, FAILED, REFUNDED
    private String transactionId; // referencia de pasarela (Stripe, PayPal, etc.)
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    
    @Column(name = "order_id")
    private Integer orderId;
}