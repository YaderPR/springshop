// ProductResponseDTO.java
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
    private String categoryName; // devolvemos el nombre de la categoría
}

