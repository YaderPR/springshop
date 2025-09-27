// ShipmentRequestDto.java (AJUSTADO)
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
    private String carrier; // DHL, FedEx, UPS...

    @NotNull // Clave foránea esencial
    private Integer orderId; 

    // Opcional en el request. El servicio debería inicializarlo o validarlo.
    private ShipmentStatus status; 

    // Las fechas son opcionales al crear. Se manejan por lógica de negocio.
    private LocalDateTime shippedAt; 
    private LocalDateTime deliveredAt;
}