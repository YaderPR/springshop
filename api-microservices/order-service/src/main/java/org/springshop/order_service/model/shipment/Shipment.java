package org.springshop.order_service.model.shipment;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class Shipment {

    private Integer id;
    private String trackingNumber;
    private String carrier; 
    private ShipmentStatus status; 
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private Integer orderId; 
    private LocalDateTime createdAt;

}