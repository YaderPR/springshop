package org.springshop.api.dto.payment;

import java.time.LocalDateTime;

import org.springshop.api.model.payment.ShipmentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentResponseDto {

    private Long id;

    private String trackingNumber;

    private String carrier;

    private ShipmentStatus status; // CREATED, SHIPPED, DELIVERED, RETURNED

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    private Integer orderId; // referencia a la orden
}

