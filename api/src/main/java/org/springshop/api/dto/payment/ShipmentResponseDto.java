package org.springshop.api.dto.payment;

import java.time.LocalDateTime;

import org.springshop.api.model.payment.ShipmentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentResponseDto {

    private Integer id;
    private String trackingNumber;
    private String carrier; 
    private ShipmentStatus status; 
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private Integer orderId; 
    private LocalDateTime createdAt;

}