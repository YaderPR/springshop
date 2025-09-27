package org.springshop.api.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante
import org.springshop.api.dto.user.UserResponseDTO;
import org.springshop.api.mapper.user.UserMapper;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.user.UserRepository;

import java.util.Optional;
import jakarta.persistence.EntityNotFoundException; // Para manejo de 404

@Service
@Transactional // Aplicamos transaccionalidad a nivel de clase
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // -------------------- LÓGICA DE SINCRONIZACIÓN Y AUTENTICACIÓN --------------------

    /**
     * Busca un usuario por 'sub' (ID de autenticación). Si no existe, lo crea.
     * Esta es la lógica central para el 'login' de un nuevo usuario en el sistema.
     * @param sub El ID del sujeto (subject) proporcionado por el proveedor de identidad.
     */
    public UserResponseDTO syncUser(String sub) {
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

    /**
     * Obtiene un usuario por su 'sub' (Subject ID).
     * @param sub El ID del sujeto (subject) proporcionado por el proveedor de identidad.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserBySub(String sub) { // CONSISTENCIA: Devuelve Optional
        // CORRECCIÓN: Usamos Optional.map para transformar la entidad si existe
        return userRepository.findBySub(sub)
                .map(UserMapper::toResponseDTO);
    }
    
    /**
     * Obtiene un usuario por su ID primario.
     * Este método es útil para el endpoint GET /api/users/{id}.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getUserById(Integer id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponseDTO);
    }
    
    // -------------------- MÉTODOS DE SOPORTE PARA OTROS SERVICIOS --------------------

    /**
     * Busca la entidad User por su ID o lanza EntityNotFoundException.
     * Este es el método central de soporte que usarán otros servicios (e.g., AddressService, OrderService).
     */
    @Transactional(readOnly = true)
    public User findUserEntityByIdOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}