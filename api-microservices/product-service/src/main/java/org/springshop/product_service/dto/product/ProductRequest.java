package org.springshop.product_service.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    
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