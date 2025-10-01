package org.springshop.api.mapper.user;

import org.springshop.api.dto.user.UserResponseDTO;
import org.springshop.api.model.user.User;

public class UserMapper {
    public static UserResponseDTO toResponseDTO(User user) {
        if(user == null) return null;
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setSub(user.getSub());
        return dto;
    }
}