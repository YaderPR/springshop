package org.springshop.product_service.dto.product.apparel;

import org.springshop.product_service.dto.product.ProductRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApparelRequest extends ProductRequest {

    @NotBlank 
    private String size; 
    
    @NotBlank 
    private String color;
    
    @NotBlank 
    private String brand;
    
    private Integer apparelCategoryId;
}