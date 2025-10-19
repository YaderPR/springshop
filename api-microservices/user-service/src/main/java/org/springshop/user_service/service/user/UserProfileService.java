package org.springshop.user_service.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.user_service.dto.user.UserProfilePictureURLResponse;
import org.springshop.user_service.model.user.UserProfile;
import org.springshop.user_service.repository.user.UserProfileRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
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
}
