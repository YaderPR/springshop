package org.springshop.shipment_service.dto.shipment;

import java.time.LocalDateTime;

import org.springshop.shipment_service.model.shipment.ShipmentStatus;

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

}