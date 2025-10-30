package org.springshop.user_service.dto.user;

import lombok.Data;
@Data
public class UserProfileResponse {
    
    private Integer userId;
    private String firstName;
    private String lastName;
    private String profilePicUrl;
}

