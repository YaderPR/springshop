package org.springshop.api.service.user;

import org.springframework.stereotype.Service;
import org.springshop.api.dto.user.UserResponseDTO;
import org.springshop.api.mapper.user.UserMapper;
import org.springshop.api.model.user.User;
import org.springshop.api.repository.user.UserRepository;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO syncUser(String sub) {
        // 1. Buscar si ya existe
        User user = userRepository.findBySub(sub)
                .orElseGet(() -> {
                    // 2. Si no existe, crearlo
                    User newUser = new User();
                    newUser.setSub(sub);
                    // aquí podrías inicializar valores por defecto
                    return userRepository.save(newUser);
                });

        // 3. (Opcional) actualizar datos del token si los necesitas
        // user.setEmail(jwt.getClaim("email")); ...
        // userRepository.save(user);

        return UserMapper.toDTO(user);
    }
    public UserResponseDTO getUserBySub(String sub) {
        User user = userRepository.findBySub(sub).orElse(null);
        return UserMapper.toDTO(user);
    }

}
