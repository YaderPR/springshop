package org.springshop.cart_service.client;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springshop.cart_service.model.user.User; // Asume que la clase User está en este paquete

@Component
public class UserClient {
    
    /**
     * Busca un usuario por su ID.
     * Corresponde a userRepository.findById(userId) en CartService.
     */
    public Optional<User> findById(Integer userId) {
        // Lógica para usar RestTemplate para llamar al servicio de usuarios y obtener un Optional<User>
        return Optional.empty(); 
    }

    // Nota: Aunque CartService solo usa findById, se podrían incluir otros métodos comunes 
    // de un repositorio si se anticipan. En este caso, solo se añade el usado explícitamente.
}
