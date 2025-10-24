package org.springshop.shipment_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springshop.shipment_service.model.order.Order;

@Component
public class OrderClient {
    private final RestTemplate restTemplate;
    private final String orderServiceBaseUrl;

    public OrderClient(
        RestTemplateBuilder builder,
        @Value("http://localhost:8086") String orderServiceBaseUrl) {
        
        this.restTemplate = builder.build();
        this.orderServiceBaseUrl = orderServiceBaseUrl;
    }
    public Optional<Order> findById(Integer orderId) {
        
        // 1. Construir la URL completa para el endpoint: /users/{userId}
        String url = orderServiceBaseUrl + "/api/v2/orders/{userId}";
        
        try {
            // 2. Realizar la llamada GET. El segundo argumento es la clase esperada (User.class).
            // El tercer argumento son las variables de la URI (userId).
            Order order = restTemplate.getForObject(url, Order.class, orderId);
            
            // 3. Si la respuesta es 200 OK y devuelve un cuerpo, devuelve Optional.of(user).
            return Optional.ofNullable(order);
            
        } catch (HttpClientErrorException.NotFound ex) {
            // 4. Capturar el error 404 (Not Found), que indica que el usuario no existe.
            // Esto corresponde al comportamiento esperado de un repositorio cuando no encuentra un ID.
            return Optional.empty();
            
        } catch (HttpClientErrorException ex) {
            // 5. Capturar otros errores 4xx (400, 401, 403, etc.) y relanzarlos o manejarlos.
            // Para el propósito de findById, podrías relanzar un error si no es 404.
            throw new RuntimeException("Error al comunicarse con el servicio de usuarios: " + ex.getStatusCode(), ex);

        } catch (Exception ex) {
            // 6. Capturar otros errores (conexión, timeouts, etc.).
            throw new RuntimeException("Error inesperado en el servicio de usuarios: ", ex);
        }
    }
}
