package org.springshop.order_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springshop.order_service.model.cart.Cart;


@Component
public class CartClient {
    private final RestTemplate restTemplate;
    
    // URL base del microservicio de usuarios (Se obtiene de application.properties)
    private final String cartServiceBaseUrl;

    public CartClient(RestTemplateBuilder builder, 
                      @Value("${cart.service.url}") String cartServiceBaseUrl) {
        this.restTemplate = builder.build();
        //${clients.user-service.url} cuando ya este configurado como arquitectura de microservicios
        this.cartServiceBaseUrl = cartServiceBaseUrl;
    }
    public Optional<Cart> findById(Integer cartId) {
        
        // 1. Construir la URL completa para el endpoint: /carts/{cartId}
        String url = cartServiceBaseUrl + "/api/v2/carts/{cartId}";
        
        try {
            // 2. Realizar la llamada GET. El segundo argumento es la clase esperada (User.class).
            // El tercer argumento son las variables de la URI (userId).
            Cart cart = restTemplate.getForObject(url, Cart.class, cartId);
            
            // 3. Si la respuesta es 200 OK y devuelve un cuerpo, devuelve Optional.of(user).
            return Optional.ofNullable(cart);
            
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
    public void clearCart(Integer cartId) {
        
        // 1. Construir la URL completa para el endpoint DELETE
        // Endpoint: /api/v2/carts/{cartId}/items
        // Usamos una sintaxis similar a getForObject para inyectar el ID
        String url = cartServiceBaseUrl + "/api/v2/carts/{cartId}/items";

        try {
            // 2. Ejecutar la llamada DELETE. 
            // Usaremos exchange() que es más versátil que delete()
            // porque maneja excepciones más granularmente si es necesario.
            restTemplate.exchange(
                url, 
                HttpMethod.DELETE, 
                null, // HttpEntity: No enviamos cuerpo en un DELETE
                Void.class, // Clase de respuesta: Esperamos un 204 No Content, por eso usamos Void.class
                cartId // Variables URI: El ID del carrito
            );
            
            // Si llega aquí, la llamada fue exitosa (código 204 No Content)

        } catch (HttpClientErrorException ex) {
            // Manejo de errores 4xx (404 Not Found, 400 Bad Request)
            System.err.println("Error del cliente al limpiar el carrito (HTTP " + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
            // Se puede lanzar una excepción de negocio o envolver la excepción
            throw new RuntimeException("Fallo al limpiar el carrito " + cartId + ": " + ex.getMessage(), ex);

        } catch (HttpServerErrorException ex) {
            // Manejo de errores 5xx (500 Internal Server Error)
            System.err.println("Error del servidor del carrito (HTTP " + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor de carritos al limpiar " + cartId, ex);

        } catch (ResourceAccessException ex) {
            // Manejo de errores de conexión (servicio caído, timeout, etc.)
            System.err.println("Error de conexión al intentar limpiar el carrito " + cartId + ": " + ex.getMessage());
            throw new RuntimeException("Servicio de carritos no disponible.", ex);
        }
    }
}
