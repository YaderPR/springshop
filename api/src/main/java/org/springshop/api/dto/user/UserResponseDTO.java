// UserResponseDTO.java (AJUSTADO)
package org.springshop.api.dto.user;

import lombok.Data;

@Data
public class UserResponseDTO {
    
    // CORRECCIÓN: Usar Integer en lugar de int para permitir null y consistencia
    // con las clases envoltorio utilizadas en JPA y otros DTOs.
    private Integer id; 
    
    // El 'sub' es correcto, pero lo hacemos privado explícitamente (aunque Lombok lo hace).
    private String sub; 
    
    // Si se añadieran más campos de perfil, irían aquí (ej: email, username).
}