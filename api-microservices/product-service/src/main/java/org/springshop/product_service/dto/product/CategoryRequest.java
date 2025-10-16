package org.springshop.product_service.dto.product;

import jakarta.validation.constraints.NotBlank; 
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequest {
    
    @NotBlank 
    private String name;
}