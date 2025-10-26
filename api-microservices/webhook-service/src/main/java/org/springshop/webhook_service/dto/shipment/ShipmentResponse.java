package org.springshop.webhook_service.dto.shipment;

import java.time.LocalDateTime;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentResponse {

    private Integer id;
    private String trackingNumber;
    private String carrier; 
    private ShipmentStatus status; 
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private Integer orderId; 

}