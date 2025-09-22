package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.product.Category;

public class ProductMapper {
    public static ProductResponseDTO toDTO(Product product) {
        if (product == null) return null;
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        return dto;
    }

    public static Product toEntity(ProductRequestDTO dto, Category category) {
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
    public static void updateEntity(Product existing, ProductRequestDTO dto, Category category) {
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);
    }
}

