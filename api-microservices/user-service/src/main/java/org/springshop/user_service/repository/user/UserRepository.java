package org.springshop.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.user_service.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * Busca un usuario por su Subject ID (sub), que es la clave de autenticación externa
     * usada por OAuth2/OIDC (ej. Auth0, Google).
     */
    Optional<User> findBySub(String sub);
}