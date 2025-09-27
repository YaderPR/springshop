// WorkoutAccessoryRequestDTO.java (AJUSTADO)
package org.springshop.api.dto.product.workoutaccessory;
import org.springshop.api.dto.product.ProductRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Necesario para asegurar que el peso se envíe

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkoutAccessoryRequestDTO extends ProductRequestDTO {
    
    @NotBlank // El material es un campo de texto clave
    private String material;
    
    @NotBlank // Las dimensiones son importantes
    private String dimensions;
    
    @NotNull // El peso es un campo numérico clave
    private Double weight;
    
    @NotBlank // El color es un campo de texto clave
    private String color;
    
    private Integer workoutAccessoryCategoryId; // ID de la categoría de accesorio
}