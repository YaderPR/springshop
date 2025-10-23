package org.springshop.order_service.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springshop.order_service.dto.payment.PaymentResponseDto;

@Component
public class PaymentClient {

    private final RestTemplate restTemplate;
    private final String paymentServiceBaseUrl;

    public PaymentClient(
        RestTemplateBuilder builder,
        @Value("${client.payment-service.base-url}") String paymentServiceBaseUrl) {
        
        this.restTemplate = builder.build();
        this.paymentServiceBaseUrl = paymentServiceBaseUrl;
    }

    public List<PaymentResponseDto> getPaymentsByOrderId(Integer orderId) {
        
        // 1. Construir la URL completa con el Query Parameter
        String url = UriComponentsBuilder.fromUriString(paymentServiceBaseUrl)
                                         .path("/api/payments") // El endpoint base en el servicio de Pagos
                                         .queryParam("orderId", orderId) // Añadir el parámetro de consulta
                                         .toUriString();

        try {
            // 2. Ejecutar la llamada GET con RestTemplate
            // Usamos exchange() en lugar de getForObject() para manejar listas de objetos
            ResponseEntity<List<PaymentResponseDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null, // No hay cuerpo de petición
                new ParameterizedTypeReference<List<PaymentResponseDto>>() {} // Tipo de retorno List<...>
            );
            
            // 3. Devolver la lista
            return response.getBody() != null ? response.getBody() : List.of();
            
        } catch (Exception e) {
            // 4. Manejar errores (conexión, 4xx, 5xx)
            System.err.println("Error fetching payments for order " + orderId + ": " + e.getMessage());
            // En caso de fallo, devolvemos una lista vacía para no romper el flujo de la orden.
            return List.of(); 
        }
    }
}