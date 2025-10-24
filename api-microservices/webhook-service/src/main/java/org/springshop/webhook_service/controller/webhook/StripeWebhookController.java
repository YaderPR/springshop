package org.springshop.webhook_service.controller.webhook;

import com.stripe.model.Event;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.webhook_service.service.webhook.WebhookService;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/webhooks")
public class StripeWebhookController {

    private final WebhookService webhookService;

    @Value("${WEBHOOK_SECRET_KEY}")
    private String webhookSecret;

    public StripeWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Endpoint para recibir y procesar todos los eventos de Stripe.
     * 
     * @param payload   El cuerpo RAW de la solicitud HTTP.
     * @param sigHeader El encabezado 'Stripe-Signature' para verificación de
     *                  seguridad.
     */
    @PostMapping("/stripe")
    public ResponseEntity<Map<String, String>> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        // 1. VERIFICACIÓN DE LA FIRMA (Seguridad crítica)
        try {
            // Verifica que la solicitud realmente proviene de Stripe y no de un atacante
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            // Error si la firma no es válida. Devolvemos 400.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Firma de Webhook no válida."));
        } catch (Exception e) {
            // Otro error de parseo, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Payload de Stripe inválido."));
        }

        // 2. PROCESAMIENTO DEL EVENTO (Delegado al servicio)
        try {
            webhookService.processEvent(event);
            // Stripe espera un 200 OK en 30 segundos, de lo contrario, reintenta.
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            // Si hay un error al procesar el evento (ej. error de DB),
            // NO devolvemos 200 OK. Esto le indica a Stripe que reintente más tarde.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
