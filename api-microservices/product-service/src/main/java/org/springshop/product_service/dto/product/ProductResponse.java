package org.springshop.product_service.dto.product;

import lombok.Data;

@Data
public class ProductResponse {
    
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Integer categoryId; 
    private String categoryName; 

}