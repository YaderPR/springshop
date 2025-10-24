package org.springshop.webhook_service.client;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
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
        
        // 1. Construir la URL completa con el Query Parameter
        String url = UriComponentsBuilder.fromUriString(paymentServiceBaseUrl)
                                         .path("/api/payments") // El endpoint base en el servicio de Pagos
                                         .toUriString();
        //RequestEntity req = RequestEntity.BodyBuilder<>

        try {
            // 2. Ejecutar la llamada GET con RestTemplate
            // Usamos exchange() en lugar de getForObject() para manejar listas de objetos
            ResponseEntity<Payment> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                null, // No hay cuerpo de petición
                new ParameterizedTypeReference<Payment>() {} // Tipo de retorno List<...>
            );
            
            // 3. Devolver la lista
            return response.getBody() != null ? Optional.of(response.getBody()) : Optional.empty();
            
        } catch (Exception e) {
            // 4. Manejar errores (conexión, 4xx, 5xx)
            System.err.println("Error creating payment with id " + payment.getId() + ": " + e.getMessage());
            // En caso de fallo, devolvemos una lista vacía para no romper el flujo de la orden.
            return Optional.of(payment); 
        }
    }
}