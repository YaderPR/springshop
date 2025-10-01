package org.springshop.api.dto.product.apparel;

import org.springshop.api.dto.product.ProductRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApparelRequestDTO extends ProductRequestDTO {

    @NotBlank 
    private String size; 
    
    @NotBlank 
    private String color;
    
    @NotBlank 
    private String brand;
    
    private Integer apparelCategoryId;
}