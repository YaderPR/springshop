package org.springshop.api.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.user.UserResponseDTO;
import org.springshop.api.service.user.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/me/sync")
    public ResponseEntity<UserResponseDTO> syncProfile(JwtAuthenticationToken authentication) {
        String sub = authentication.getToken().getSubject();
        UserResponseDTO dto = userService.syncUser(sub);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(JwtAuthenticationToken authentication) {
        String sub = authentication.getToken().getSubject();
        UserResponseDTO dto = userService.getUserBySub(sub);
        if(dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

}
