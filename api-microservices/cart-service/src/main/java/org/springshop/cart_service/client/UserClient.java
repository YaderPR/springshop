package org.springshop.cart_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springshop.cart_service.model.user.User; // Importación de tu entidad/modelo User

@Component
public class UserClient {
    
    // Inyecta el RestTemplate
    private final RestTemplate restTemplate;
    
    // URL base del microservicio de usuarios (Se obtiene de application.properties)
    private final String userServiceBaseUrl;

    public UserClient(RestTemplate restTemplate, 
                      @Value("${user.service.url}") String userServiceBaseUrl) {
        this.restTemplate = restTemplate;
        //${clients.user-service.url} cuando ya este configurado como arquitectura de microservicios
        this.userServiceBaseUrl = userServiceBaseUrl;
    }
    
    public Optional<User> findById(Integer userId) {
        
        // 1. Construir la URL completa para el endpoint: /users/{userId}
        String url = userServiceBaseUrl + "/api/v2/users/{userId}";
        
        try {
            // 2. Realizar la llamada GET. El segundo argumento es la clase esperada (User.class).
            // El tercer argumento son las variables de la URI (userId).
            User user = restTemplate.getForObject(url, User.class, userId);
            
            // 3. Si la respuesta es 200 OK y devuelve un cuerpo, devuelve Optional.of(user).
            return Optional.ofNullable(user);
            
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