package org.springshop.webhook_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springshop.webhook_service.model.payment.Payment;

@Component
public class PaymentClient {

    private final RestTemplate restTemplate;
    private final String paymentServiceBaseUrl;

    public PaymentClient(
            RestTemplateBuilder builder,
            @Value("http://localhost:8087") String paymentServiceBaseUrl) {

        this.restTemplate = builder.build();
        this.paymentServiceBaseUrl = paymentServiceBaseUrl;
    }

    public Optional<Payment> createPayment(Payment payment) {
        // Definimos la URL sin Query Params, solo el path base para la creación (POST)
        String url = UriComponentsBuilder.fromUriString(paymentServiceBaseUrl)
                .path("/api/payments") // El endpoint base en el servicio de Pagos
                .toUriString();

        // 1. Crear el HttpEntity con el cuerpo de la petición (el objeto 'payment')
        // Los headers por defecto de RestTemplate ya suelen ser suficientes
        // (Content-Type: application/json)
        // para un POST simple, pero podemos especificarlos si es necesario.
        HttpEntity<Payment> requestEntity = new HttpEntity<>(payment);

        try {
            // 2. Ejecutar la llamada POST con RestTemplate.exchange()
            // - El método es POST.
            // - La entidad de la petición es 'requestEntity' (contiene el objeto payment).
            // - El tipo de retorno es Payment.class o ParameterizedTypeReference si fuera
            // una lista,
            // pero para un objeto único, la clase es suficiente.
            ResponseEntity<Payment> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity, // <--- Aquí se envía el cuerpo 'payment'
                    Payment.class);

            // 3. Devolver el cuerpo de la respuesta envuelto en Optional
            // El estado 201 Created o 200 OK generalmente devolverá el objeto Payment
            // creado.
            return Optional.ofNullable(response.getBody());

        } catch (Exception e) {
            // 4. Manejar errores (conexión, 4xx, 5xx).
            // Es mejor capturar errores específicos como HttpClientErrorException o
            // HttpServerErrorException.
            System.err.println("Error creando pago para la orden/ID " + payment.getId() + ": " + e.getMessage());

            // Dependiendo de tu lógica de negocio, aquí puedes:
            // A) Relanzar una excepción de servicio.
            // B) Devolver Optional.empty() para indicar que el pago falló.
            return Optional.empty(); // <-- Devolver Optional.empty() es mejor para indicar fallo.
        }
    }
}