package org.springshop.api.dto.product.workoutaccessory;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank; 

@Data
@NoArgsConstructor
public class WorkoutAccessoryCategoryRequestDTO {

    @NotBlank 
    private String name;

}