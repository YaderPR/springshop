package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.CategoryRequestDTO;
import org.springshop.api.dto.product.CategoryResponseDTO;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.Product;

import java.util.stream.Collectors;

public class CategoryMapper {

    // De RequestDTO → Entity
    public static Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    // De Entity → ResponseDTO
    public static CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) return null;

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        if (category.getProducts() != null) {
            dto.setProductIds(
                category.getProducts()
                        .stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
            );
        }

        return dto;
    }

}

