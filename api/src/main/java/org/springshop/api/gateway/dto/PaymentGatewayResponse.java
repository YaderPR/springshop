package org.springshop.api.gateway.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentGatewayResponse {
    private final boolean success;
    private final String transactionId;
    private final String message; // Mensaje de error o de éxito
}