package org.springshop.api.dto.payment;

import java.time.LocalDateTime;

import org.springshop.api.model.payment.ShipmentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentRequestDto {

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