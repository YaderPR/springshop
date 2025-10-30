package org.springshop.order_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springshop.order_service.model.address.Address;

@Component
public class AddressClient {
        // Inyecta el RestTemplate
    private final RestTemplate restTemplate;
    
    // URL base del microservicio de usuarios (Se obtiene de application.properties)
    private final String addressServiceBaseUrl;

    public AddressClient(RestTemplateBuilder builder, 
                      @Value("${shipment.service.url}") String addressServiceBaseUrl) {
        this.restTemplate = builder.build();
        //${clients.user-service.url} cuando ya este configurado como arquitectura de microservicios
        this.addressServiceBaseUrl = addressServiceBaseUrl;
    }
    public Optional<Address> findById(Integer addressId) {
        
        // 1. Construir la URL completa para el endpoint: /addresses/{addressId}
        String url = addressServiceBaseUrl + "/api/v2/addresses/{addressId}";
        
        try {
            // 2. Realizar la llamada GET. El segundo argumento es la clase esperada (User.class).
            // El tercer argumento son las variables de la URI (userId).
            Address address = restTemplate.getForObject(url, Address.class, addressId);
            
            // 3. Si la respuesta es 200 OK y devuelve un cuerpo, devuelve Optional.of(user).
            return Optional.ofNullable(address);
            
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
