// ProductRequestDTO.java (CORRECTO)
package org.springshop.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Integer stock;

    @NotBlank
    private String imageUrl;
    
    private Integer categoryId; 
}