package org.springshop.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.repository.user.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/me")
    public ResponseEntity<String> me(JwtAuthenticationToken authentication) {
        //User user = userRepository.findBySub(jwt.getClaimAsString(""));
        String userId = authentication.getName();
        String username = authentication.getToken().getClaimAsString("preferred_username");
        String email = authentication.getToken().getClaimAsString("email");
    
    
        return ResponseEntity.ok("Hola, " + username + "! Tu ID es: " + userId + " y tu correo: " + email + " AuthToString: " + authentication.toString());
    }
}
