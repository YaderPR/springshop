package org.springshop.user_service.mapper.user;

import org.springshop.user_service.dto.user.UserResponse;
import org.springshop.user_service.dto.user.UserProfileResponse;
import org.springshop.user_service.dto.user.UserProfileRequest;
import org.springshop.user_service.model.user.User;
import org.springshop.user_service.model.user.UserProfile;

public class UserMapper {
    public static UserResponse toResponseDTO(User user) {
        if(user == null) return null;
        
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setSub(user.getSub());
        return dto;
    }
    public static UserProfileResponse toResponseDTO(UserProfile userProfile) {
        if(userProfile == null) return null;
        
        UserProfileResponse dto = new UserProfileResponse();
        dto.setUserId(userProfile.getId());
        dto.setFirstName(userProfile.getFirstName());
        dto.setLastName(userProfile.getLastName());
        dto.setProfilePicUrl(userProfile.getProfilePicUrl());
        return dto;
    }
    public static UserProfile toEntity(UserProfileRequest request) {
        if(request == null) return null;
        UserProfile userProfile = new UserProfile();
        userProfile.setId(request.getUserId());
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setProfilePicUrl(request.getProfilePicUrl());
        return userProfile;
    }
}