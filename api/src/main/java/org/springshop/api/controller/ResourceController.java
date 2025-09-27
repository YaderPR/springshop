package org.springshop.api.controller;

/*
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResourceController {

    @GetMapping("/protegida")
    public String protectedResource(@AuthenticationPrincipal Jwt jwt) {
        // Extraer roles de realm_access.roles
        Object roles = jwt.getClaimAsMap("realm_access") != null 
            ? jwt.getClaimAsMap("realm_access").get("roles") 
            : null;
        // Extraer autoridades de Spring Security
        String authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        return "Acceso concedido para el usuario: " + jwt.getSubject() +
               ", Roles: " + roles +
               ", Authorities: " + authorities;
    }

    @GetMapping("/publica")
    public String publicResource() {
        return "Este es un recurso público, no se requiere autenticación.";
    }
}
*/