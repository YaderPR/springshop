package org.transportracker.api.repository.user;

import org.transportracker.api.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByKeycloakUserId(String keycloakUserId);

    Optional<User> findByEmail(String email);

    boolean existsByKeycloakUserId(String keycloakUserId);

    boolean existsByEmail(String email);
}

