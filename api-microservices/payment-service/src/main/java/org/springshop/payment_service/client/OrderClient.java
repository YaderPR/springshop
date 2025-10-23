package org.springshop.payment_service.client;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springshop.payment_service.model.order.Order;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String orderServiceBaseUrl;

    // 1. Constructor para inyectar RestTemplate y la URL base
    public OrderClient(
        RestTemplateBuilder builder,
        @Value("${client.order-service.base-url}") String orderServiceBaseUrl) {
        
        // Crear la instancia de RestTemplate (mejor práctica usar RestTemplateBuilder)
        this.restTemplate = builder.build();
        this.orderServiceBaseUrl = orderServiceBaseUrl;
    }

    // 2. Implementación del método findById
    public Optional<Order> findById(Integer orderId) {
        // Construir la URL completa del endpoint del servicio de Órdenes
        // Asumimos que el endpoint para una orden singular es GET /api/orders/{id}
        String url = orderServiceBaseUrl + "/api/orders/{id}";

        try {
            // Realizar la llamada HTTP GET. Spring automáticamente mapea el JSON a la clase Order.
            Order order = restTemplate.getForObject(url, Order.class, orderId);
            
            // Si getForObject devuelve null (lo que puede pasar si la respuesta no tiene cuerpo),
            // lo manejamos como no encontrado.
            return Optional.ofNullable(order);
            
        } catch (HttpClientErrorException.NotFound e) {
            // Capturar HTTP 404 NOT FOUND, que indica que la Orden no existe.
            System.err.println("Order not found with ID: " + orderId);
            return Optional.empty();
            
        } catch (Exception e) {
            // Capturar otros posibles errores (conexión, 4xx, 5xx)
            System.err.println("Error while fetching order ID " + orderId + ": " + e.getMessage());
            // En un sistema real, podrías lanzar una excepción personalizada o devolver un Optional.empty()
            return Optional.empty(); 
        }
    }
}