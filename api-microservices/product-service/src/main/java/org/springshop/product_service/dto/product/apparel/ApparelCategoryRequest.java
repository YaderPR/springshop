package org.springshop.product_service.dto.product.apparel;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank; 

@Data
@NoArgsConstructor
public class ApparelCategoryRequest {
    
    @NotBlank 
    private String name;
}