// WorkoutAccessoryCategoryRequestDTO.java (AJUSTADO)
package org.springshop.api.dto.product.workoutaccessory;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank; // ¡Añadimos esta importación!

@Data
@NoArgsConstructor
public class WorkoutAccessoryCategoryRequestDTO {
    
    // Aseguramos que el nombre de la categoría no esté vacío
    @NotBlank 
    private String name;
}