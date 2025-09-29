package org.springshop.api.dto.product.workoutaccessory;
import org.springshop.api.dto.product.ProductRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; 

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkoutAccessoryRequestDTO extends ProductRequestDTO {
    
    @NotBlank 
    private String material;
    
    @NotBlank 
    private String dimensions;
    
    @NotNull 
    private Double weight;
    
    @NotBlank 
    private String color;
    
    private Integer workoutAccessoryCategoryId; 
}