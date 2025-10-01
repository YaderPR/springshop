package org.springshop.api.dto.product.supplement;

import org.springshop.api.dto.product.ProductRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplementRequestDTO extends ProductRequestDTO { 
    
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