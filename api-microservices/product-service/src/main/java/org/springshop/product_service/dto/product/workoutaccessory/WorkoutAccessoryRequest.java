package org.springshop.product_service.dto.product.workoutaccessory;
import org.springshop.product_service.dto.product.ProductRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; 

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkoutAccessoryRequest extends ProductRequest {
    
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