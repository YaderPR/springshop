package org.springshop.product_service.mapper.product;

import org.springshop.product_service.dto.product.ProductRequest;
import org.springshop.product_service.dto.product.ProductResponse;
import org.springshop.product_service.model.product.Product;
import org.springshop.product_service.model.product.Category;

public class ProductMapper {
    public static ProductResponse toResponseDto(Product product) {
        if (product == null) return null;
        
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        } else {
            dto.setCategoryId(null);
            dto.setCategoryName(null);
        }
        
        return dto;
    }

    public static Product toEntity(ProductRequest dto, Category category) {
        if (dto == null) return null;
        
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        return product;
    }
    
    public static void updateProduct(Product existing, ProductRequest dto, Category category) {
        
        if (existing == null || dto == null) return;
        
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);
    }
}