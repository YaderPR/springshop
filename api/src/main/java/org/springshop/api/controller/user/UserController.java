package org.springshop.api.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.user.UserResponseDTO;
import org.springshop.api.service.user.UserService;

import java.util.Optional; // Importamos Optional

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Sincroniza el perfil del usuario autenticado (crea si no existe, actualiza si es necesario).
     * Mapeado a POST /api/users/me/sync (o simplemente /api/users/sync)
     */
    @PostMapping("/me/sync")
    public ResponseEntity<UserResponseDTO> syncProfile(JwtAuthenticationToken authentication) {
        String sub = authentication.getToken().getSubject();
        
        // El servicio ya se encarga de la lógica de creación/actualización y devuelve el DTO.
        UserResponseDTO dto = userService.syncUser(sub); 
        
        // Devolvemos 200 OK. Si el usuario se creó, se podría usar 201, pero 200 es común para sync.
        return ResponseEntity.ok(dto); 
    }
    
    /**
     * Obtiene el perfil del usuario autenticado.
     * Mapeado a GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(JwtAuthenticationToken authentication) {
        String sub = authentication.getToken().getSubject();
        
        // CORRECCIÓN: Usamos el método de servicio que devuelve Optional
        Optional<UserResponseDTO> maybeDto = userService.getUserBySub(sub);
        
        // CORRECCIÓN: Usamos wrapOrNotFound para manejar el 404
        return wrapOrNotFound(maybeDto);
    }
    
    // -------------------- HELPER --------------------
    
    /**
     * Convierte un Optional<T> en ResponseEntity<T> (200 OK) o 404 Not Found.
     * Este helper es vital para la consistencia del manejo de 404.
     */
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
}