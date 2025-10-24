package org.springshop.webhook_service.client;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springshop.webhook_service.dto.order.OrderResponse;
import org.springshop.webhook_service.dto.order.OrderUpdateStatus;
import org.springshop.webhook_service.model.order.Order;

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

        // 1. Construir la URL completa para el endpoint: /orders/{orderId}
        String url = orderServiceBaseUrl + "/api/v2/orders/{orderId}";

        try {
            // 2. Realizar la llamada GET. El segundo argumento es la clase esperada
            // (Order.class).
            // El tercer argumento son las variables de la URI (userId).
            Order order = restTemplate.getForObject(url, Order.class, orderId);

            // 3. Si la respuesta es 200 OK y devuelve un cuerpo, devuelve
            // Optional.of(order).
            return Optional.ofNullable(order);

        } catch (HttpClientErrorException.NotFound ex) {
            // 4. Capturar el error 404 (Not Found), que indica que el usuario no existe.
            // Esto corresponde al comportamiento esperado de un repositorio cuando no
            // encuentra un ID.
            return Optional.empty();

        } catch (HttpClientErrorException ex) {
            // 5. Capturar otros errores 4xx (400, 401, 403, etc.) y relanzarlos o
            // manejarlos.
            // Para el propósito de findById, podrías relanzar un error si no es 404.
            throw new RuntimeException("Error al comunicarse con el servicio de usuarios: " + ex.getStatusCode(), ex);

        } catch (Exception ex) {
            // 6. Capturar otros errores (conexión, timeouts, etc.).
            throw new RuntimeException("Error inesperado en el servicio de usuarios: ", ex);
        }
    }

    public OrderResponse updateOrderStatus(Integer orderId, OrderUpdateStatus orderUpdateStatus) {
        // 1. Definir la URL del endpoint a consumir.
        // Suponiendo que el servicio de órdenes está en "http://order-service/orders"
        // y que orderUpdateStatus tiene el ID de la orden.
        String url = orderServiceBaseUrl + "/orders/{orderId}";

        // 2. Definir los Headers: Es CRÍTICO especificar el tipo de contenido como JSON
        // y aceptar JSON en la respuesta.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 3. Crear el HttpEntity con el cuerpo (payload) y los headers.
        // El cuerpo es el objeto OrderUpdateStatus, que será serializado a JSON
        // por RestTemplate.
        HttpEntity<OrderUpdateStatus> entity = new HttpEntity<>(orderUpdateStatus, headers);

        try {
            // 4. Ejecutar la petición usando el método exchange() con HttpMethod.PATCH.
            // El tipo de respuesta se establece como void, ya que la respuesta PATCH
            // a menudo es un 204 No Content o simplemente no se necesita el cuerpo.
            ResponseEntity<OrderResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    entity,
                    OrderResponse.class // O OrderResponseDto.class si el servicio devuelve el objeto actualizado
            );
            System.out.println("Estado de la orden " + orderId + " actualizado correctamente.");
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Manejo de errores del cliente (4xx): 404 Not Found, 400 Bad Request, 403
            // Forbidden, etc.
            System.err.println("Error del cliente al actualizar la orden (HTTP " + e.getStatusCode() + "): "
                    + e.getResponseBodyAsString());
            throw new RuntimeException("Error al actualizar la orden (Cliente): " + e.getMessage());

        } catch (HttpServerErrorException e) {
            // Manejo de errores del servidor (5xx): 500 Internal Server Error, etc.
            System.err.println("Error del servidor al actualizar la orden (HTTP " + e.getStatusCode() + "): "
                    + e.getResponseBodyAsString());
            throw new RuntimeException("Error al actualizar la orden (Servidor): " + e.getMessage());

        } catch (ResourceAccessException e) {
            // Manejo de errores de red o conexión (Ej: servicio de órdenes caído).
            System.err.println("Error de conexión con el servicio de órdenes: " + e.getMessage());
            throw new RuntimeException("Servicio de órdenes no disponible: " + e.getMessage());
        }
    }
}
