package org.springshop.user_service.mapper.user;

import org.springshop.user_service.dto.user.UserResponse;
import org.springshop.user_service.model.user.User;

public class UserMapper {
    public static UserResponse toResponseDTO(User user) {
        if(user == null) return null;
        
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setSub(user.getSub());
        return dto;
    }
}