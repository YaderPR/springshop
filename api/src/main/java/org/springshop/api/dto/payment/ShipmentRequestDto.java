package org.springshop.api.dto.payment;

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
    private String carrier; // DHL, FedEx, UPS...

    @NotNull
    private Long orderId; // referencia a la orden

    private ShipmentStatus status;
}

