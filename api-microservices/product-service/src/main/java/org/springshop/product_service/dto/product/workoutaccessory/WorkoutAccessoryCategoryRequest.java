package org.springshop.product_service.dto.product.workoutaccessory;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank; 

@Data
@NoArgsConstructor
public class WorkoutAccessoryCategoryRequest {

    @NotBlank 
    private String name;

}