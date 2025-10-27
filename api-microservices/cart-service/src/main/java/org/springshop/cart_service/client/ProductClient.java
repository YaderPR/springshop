package org.springshop.cart_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springshop.cart_service.model.product.Product; // Importación asumida para la clase Product

@Component
public class ProductClient {
    
    private final RestTemplate restTemplate;
    
    // URL base del microservicio de productos (Obtenida de application.properties)
    private final String productServiceBaseUrl;

    public ProductClient(RestTemplate restTemplate, 
                         @Value("${product.service.url}") String productServiceBaseUrl) {
        this.restTemplate = restTemplate;
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
}