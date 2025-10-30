package org.springshop.order_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springshop.order_service.dto.product.ProductResponse;
import org.springshop.order_service.dto.product.ProductUpdateStockRequest;
import org.springshop.order_service.model.product.Product; // Importación asumida para la clase Product

@Component
public class ProductClient {
    
    private final RestTemplate restTemplate;
    
    // URL base del microservicio de productos (Obtenida de application.properties)
    private final String productServiceBaseUrl;

    public ProductClient(RestTemplateBuilder builder, 
                         @Value("${product.service.url}") String productServiceBaseUrl) {
        this.restTemplate = builder.build();
        this.productServiceBaseUrl = productServiceBaseUrl;
    }
    
    /**
     * Busca un producto por su ID en el servicio externo de productos.
     * Mapea la respuesta 200 a Product y la 404 a Optional.empty().
     * @param productId El ID del producto a buscar.
     * @return Un Optional que contiene el Product si existe, o un Optional vacío.
     */
    public Optional<Product> findById(Integer productId) {
        
        // 1. Construir la URL completa para el endpoint: /api/products/{id}
        String url = productServiceBaseUrl + "/api/v2/products/{id}";
        
        try {
            // 2. Realizar la llamada GET. El segundo argumento es la clase esperada (Product.class).
            Product product = restTemplate.getForObject(url, Product.class, productId);
            
            // 3. Si la respuesta es 200 OK y devuelve un cuerpo, devuelve Optional.of(product).
            return Optional.ofNullable(product);
            
        } catch (HttpClientErrorException.NotFound ex) {
            // 4. Capturar el error 404 (Not Found) que indica que el producto no existe.
            return Optional.empty();
            
        } catch (HttpClientErrorException ex) {
            // 5. Capturar otros errores HTTP (ej. 400, 500)
            throw new RuntimeException("Error al comunicarse con el servicio de productos: " + ex.getStatusCode(), ex);

        } catch (Exception ex) {
            // 6. Capturar errores de conexión, timeouts, etc.
            throw new RuntimeException("Error inesperado al buscar producto: ", ex);
        }
    }
    public ProductResponse updateStock(Integer productId, Integer quantityChange) {
        
        // 1. Construir la URL completa: /api/v2/products/{productId}/stock
        String url = productServiceBaseUrl + "/api/v2/products/{productId}/stock";

        // 2. Crear el DTO de la petición con el cambio de cantidad
        ProductUpdateStockRequest requestBody = new ProductUpdateStockRequest(quantityChange);
        
        // 3. Definir Headers (Necesario para PATCH y JSON)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 4. Crear la Entidad HTTP para la petición
        HttpEntity<ProductUpdateStockRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 5. Ejecutar la llamada PATCH con RestTemplate.exchange()
            ResponseEntity<ProductResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.PATCH, 
                entity, // El DTO con el cambio de cantidad
                ProductResponse.class, // El tipo de retorno que se espera (el producto actualizado)
                productId // Variable de la URI
            );
            
            // 6. Devolver el cuerpo de la respuesta (el producto actualizado)
            return response.getBody();

        } catch (HttpClientErrorException ex) {
            // Manejo de errores 4xx (404 Not Found, 400 Bad Request/StockException)
            System.err.println("Error al ajustar stock (HTTP " + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
            throw new RuntimeException("Fallo al actualizar stock: " + ex.getMessage());

        } catch (HttpServerErrorException ex) {
            // Manejo de errores 5xx
            System.err.println("Error de servidor al ajustar stock (HTTP " + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
            throw new RuntimeException("Servicio de productos falló.", ex);

        } catch (ResourceAccessException ex) {
            // Manejo de errores de conexión/red
            System.err.println("Error de conexión al servicio de productos: " + ex.getMessage());
            throw new RuntimeException("Servicio de productos no disponible.", ex);
        }
    }
}