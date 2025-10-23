package org.springshop.order_service.model.product;

import lombok.Data;

@Data
public class Product {
    
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Integer categoryId; 
    private String categoryName; 

}