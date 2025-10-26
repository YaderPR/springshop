package org.springshop.webhook_service.dto.shipment;

import java.time.LocalDateTime;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShipmentRequest {

    @NotBlank
    private String trackingNumber;

    @NotBlank
    private String carrier;

    @NotNull
    private Integer orderId; 

    private ShipmentStatus status; 

    private LocalDateTime shippedAt; 
    private LocalDateTime deliveredAt;
    
}