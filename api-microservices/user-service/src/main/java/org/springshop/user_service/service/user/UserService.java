package org.springshop.user_service.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante
import org.springshop.user_service.dto.user.UserResponse;
import org.springshop.user_service.mapper.user.UserMapper;
import org.springshop.user_service.model.user.User;
import org.springshop.user_service.repository.user.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException; // Para manejo de 404

@Service
@Transactional // Aplicamos transaccionalidad a nivel de clase
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse syncUser(String sub) {
        // La lógica original usando orElseGet es concisa y correcta para esta operación
        User user = userRepository.findBySub(sub)
                .orElseGet(() -> {
                    // Si no existe, crearlo
                    User newUser = new User();
                    newUser.setSub(sub);
                    // Lógica para inicializar el username, email, etc., si es posible desde el token
                    
                    // Aseguramos que la creación y guardado ocurra dentro de esta transacción
                    return userRepository.save(newUser);
                });

        // 3. (Opcional) Guardar si hubo actualizaciones basadas en el token JWT.
        // Si no hay cambios, Spring Data JPA no hará UPDATE, solo un SELECT.
        // Si hay lógica de actualización aquí, descomentar la siguiente línea:
        // userRepository.save(user); 

        return UserMapper.toResponseDTO(user); // CONSISTENCIA: Usamos toResponseDTO
    }
    
    // -------------------- MÉTODOS DE BÚSQUEDA --------------------
    @Transactional(readOnly = true)
    public UserResponse getUserBySub(String sub) { // CONSISTENCIA: Devuelve Optional
        // CORRECCIÓN: Usamos Optional.map para transformar la entidad si existe
        return userRepository.findBySub(sub)
                .map(UserMapper::toResponseDTO).orElseThrow(() -> new EntityNotFoundException("User not found with subject: " + sub));
    }
    
    /**
     * Obtiene un usuario por su ID primario.
     * Este método es útil para el endpoint GET /api/users/{id}.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponseDTO).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toResponseDTO).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public User findUserEntityByIdOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
    
}