package org.springshop.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // Importante
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("dev") // ✅ Se activa SOLO cuando el perfil 'dev' está activo
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        // Deshabilita CSRF (importante para evitar errores POST/PUT/DELETE)
        http.csrf(csrf -> csrf.disable());
        
        // HABILITA TODAS LAS RUTAS SIN AUTENTICACIÓN
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()); 
        
        // Deshabilita la autenticación de recursos OAUTH2/JWT
        http.oauth2ResourceServer(oauth2 -> oauth2.disable());

        return http.build();
    }
}