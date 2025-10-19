package org.springshop.user_service.dto.user;

import lombok.Data;

@Data
public class UserResponse {
    
    private Integer id; 
    private String sub; 
    private String username;
    
}