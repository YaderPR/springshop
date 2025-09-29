package org.springshop.api.dto.product.apparel;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank; 

@Data
@NoArgsConstructor
public class ApparelCategoryRequestDTO {
    
    @NotBlank 
    private String name;
}