package org.springshop.user_service.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.user_service.dto.user.UserProfilePictureURLResponse;
import org.springshop.user_service.dto.user.UserProfileRequest;
import org.springshop.user_service.dto.user.UserProfileResponse;
import org.springshop.user_service.dto.user.UserResponse;
import org.springshop.user_service.model.user.UserProfile;
import org.springshop.user_service.model.user.User;
import org.springshop.user_service.repository.user.UserProfileRepository;
import org.springshop.user_service.mapper.user.UserMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserService userService;
    public UserProfileService(UserProfileRepository userProfileRepository, UserService userService) {
        this.userProfileRepository = userProfileRepository;
        this.userService = userService;
    }
    public UserProfilePictureURLResponse getPictureUrlById(Integer id) {
        UserProfile userProfile = findUserProfileOrThrow(id);
        UserProfilePictureURLResponse dto = new UserProfilePictureURLResponse(userProfile.getProfilePicUrl());
        return dto;
        
    }
    @Transactional(readOnly = true)
    public UserProfile findUserProfileOrThrow(Integer userId) {
        return userProfileRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with profile not found with id: " + userId));
    }
    public UserProfileResponse saveProfile(UserProfileRequest request) {
        UserResponse user = userService.getUserById(request.getUserId());
        UserProfile userProfile = userProfileRepository.save(UserMapper.toEntity(request));

        return UserMapper.toResponseDTO(userProfile);
    }
}
