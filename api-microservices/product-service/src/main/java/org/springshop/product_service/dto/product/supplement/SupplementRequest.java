package org.springshop.product_service.dto.product.supplement;

import org.springshop.product_service.dto.product.ProductRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplementRequest extends ProductRequest { 
    
    @NotBlank
    private String brand;
    
    @NotBlank
    private String flavor;
    
    @NotBlank
    private String size;
    
    @NotBlank
    @Size(max = 2000)
    private String ingredients;
    
    @NotBlank
    private String usageInstructions;
    
    private String warnings;
}