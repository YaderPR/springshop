// CategoryRequestDTO.java (AJUSTADO)
package org.springshop.api.dto.product;

import jakarta.validation.constraints.NotBlank; // ¡Añadimos esta importación!
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequestDTO {
    
    // CRÍTICO: Asegurar que el nombre de la categoría no esté vacío
    @NotBlank 
    private String name;
}