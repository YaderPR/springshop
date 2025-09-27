package org.springshop.api.mapper.user;

import org.springshop.api.dto.user.UserResponseDTO;
import org.springshop.api.model.user.User;

public class UserMapper {
    
    /**
     * Convierte una entidad User a su DTO de respuesta.
     */
    // CONVENCIÓN: Renombramos toDTO a toResponseDTO
    public static UserResponseDTO toResponseDTO(User user) {
        if(user == null) return null;
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setSub(user.getSub());
        
        // Asumimos que User también podría tener más campos
        // dto.setUsername(user.getUsername());
        // dto.setEmail(user.getEmail());
        
        return dto;
    }
    
    // NOTA: Para el dominio de User, rara vez necesitamos toEntity o update,
    // ya que la creación y actualización suele ser manejada por la sincronización (syncUser)
    // o por un perfil update separado.
}