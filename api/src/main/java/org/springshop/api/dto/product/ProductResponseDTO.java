// ProductResponseDTO.java (AJUSTADO)
package org.springshop.api.dto.product;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    
    // Campo añadido para completar la referencia de categoría
    private Integer categoryId; 
    
    private String categoryName; 
}