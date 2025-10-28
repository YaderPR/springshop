package org.springshop.user_service.controller.user;

import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.user_service.dto.user.UserProfilePictureURLResponse;
import org.springshop.user_service.dto.user.UserResponse;
import org.springshop.user_service.dto.user.UserSync;
import org.springshop.user_service.service.user.UserProfileService;
import org.springshop.user_service.service.user.UserService;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@RestController
@RequestMapping("/api/v2/users")
public class UserController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    public UserController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }
    /*
    @PostMapping("/me/sync")
    public ResponseEntity<UserResponseDTO> syncProfile(JwtAuthenticationToken authentication) {

        String sub = authentication.getToken().getSubject();
        UserResponseDTO dto = userService.syncUser(sub);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(JwtAuthenticationToken authentication) {

        String sub = authentication.getToken().getSubject();
        Optional<UserResponseDTO> maybeDto = userService.getUserBySub(sub);

        return wrapOrNotFound(maybeDto);
    }
    */
    //Endpoint temporal hasta integrar Keycloak
    @PostMapping("/me/sync")
public ResponseEntity<UserResponse> syncProfile(
    // 🚨 Usar required = false 🚨
    @RequestHeader(value = "X-Auth-Subject", required = false) String sub 
) {
    if (sub == null || sub.isEmpty()) {
        // Loggear o devolver un 401/403 si la identidad es nula
        // Esto confirmará que el encabezado no llegó.
        return ResponseEntity.status(401).build();
    }
    
    UserResponse dto = userService.syncUser(sub);
    return ResponseEntity.ok(dto);
}
    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    @GetMapping("/{userId:\\d+}/profile-url")
    public ResponseEntity<UserProfilePictureURLResponse> findProfilePictureUrlById(@PathVariable Integer userId) {
        UserProfilePictureURLResponse responseDto = userProfileService.getPictureUrlById(userId);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/subject/{userSub}")
    public ResponseEntity<UserResponse> findUserBySub(@PathVariable String userSub) {
        return ResponseEntity.ok(userService.getUserBySub(userSub));
    }
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {

        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}