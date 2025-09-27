// WorkoutAccessoryResponseDTO.java (AJUSTADO)
package org.springshop.api.dto.product.workoutaccessory;

import org.springshop.api.dto.product.ProductResponseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkoutAccessoryResponseDTO extends ProductResponseDTO {
    private String material;
    private String dimensions;
    private Double weight;
    private String color;
    
    // Campo añadido: El ID es esencial para la integridad de la API
    private Integer workoutAccessoryCategoryId; 
    
    private String workoutAccessoryCategoryName; 
}