// PaymentRequestDto.java (AJUSTADO)
package org.springshop.api.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    @NotNull
    private Integer orderId;
    
    @NotBlank // ✅ CAMBIO: El frontend envía el token de la tarjeta
    private String paymentToken; 
    
    @NotBlank // ✅ CAMBIO: La divisa es requerida (ej. "usd")
    private String currency; 

    @NotBlank
    private String method; // CARD, PAYPAL, etc.

    @NotNull
    private Double amount;

    private String description;
}