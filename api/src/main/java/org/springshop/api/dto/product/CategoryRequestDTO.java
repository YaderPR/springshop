package org.springshop.api.dto.product;

import jakarta.validation.constraints.NotBlank; 
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequestDTO {
    
    @NotBlank 
    private String name;
}