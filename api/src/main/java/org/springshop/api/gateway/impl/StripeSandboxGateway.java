package org.springshop.api.gateway.impl;

import com.stripe.Stripe; // Importar la clase principal de Stripe
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import jakarta.annotation.PostConstruct; // Importante para inicializar
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springshop.api.gateway.PaymentGateway;
import org.springshop.api.gateway.dto.PaymentChargeRequest;
import org.springshop.api.gateway.dto.PaymentGatewayResponse;

@Service
@Profile("dev") // ✅ Solo se usa en el perfil de desarrollo para el sandbox
public class StripeSandboxGateway implements PaymentGateway {

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    @PostConstruct
    public void init() {
        // Inicializa Stripe con la clave secreta.
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentGatewayResponse charge(PaymentChargeRequest request) {
        // Stripe requiere el monto en la unidad más pequeña (centavos, no dólares)
        Long amountInCents = Math.round(request.getAmount() * 100);

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(request.getCurrency())
                .setSource(request.getToken()) // El token de pago
                .setDescription(request.getDescripcion() != null ? request.getDescripcion():"Cargo por orden de SpringShop")
                .build();

        try {
            // La llamada real al sandbox de Stripe
            Charge charge = Charge.create(params);

            if (charge.getPaid() != null && charge.getPaid()) {
                return new PaymentGatewayResponse(
                        true,
                        charge.getId(), // ID de la transacción de Stripe
                        "Pago procesado exitosamente en modo Sandbox."
                );
            } else {
                return new PaymentGatewayResponse(
                        false,
                        null,
                        "Pago rechazado por el banco."
                );
            }

        } catch (StripeException e) {
            // Capturar errores de la API de Stripe (ej: clave secreta mala, token inválido, etc.)
            return new PaymentGatewayResponse(
                    false,
                    null,
                    "Error de Stripe API: " + e.getMessage()
            );
        }
    }
}