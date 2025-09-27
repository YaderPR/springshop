package org.springshop.api.gateway.dto;

import lombok.Data;

@Data
public class PaymentChargeRequest {
    private String token; // El token de pago seguro emitido por el frontend
    private Double amount;
    private String currency; // Ej: "usd", "eur"
    private String descripcion;
}