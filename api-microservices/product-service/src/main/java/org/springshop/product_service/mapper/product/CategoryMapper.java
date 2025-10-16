package org.springshop.product_service.mapper.product;

import org.springshop.product_service.dto.product.CategoryRequest;
import org.springshop.product_service.dto.product.CategoryResponse;
import org.springshop.product_service.model.product.Category;
import org.springshop.product_service.model.product.Product;

import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category toEntity(CategoryRequest dto) {
        if (dto == null)
            return null;
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public static void updateCategory(Category existing, CategoryRequest dto) {
        if (existing == null || dto == null)
            return;

        existing.setName(dto.getName());
    }

    public static CategoryResponse toResponseDTO(Category category) {
        if (category == null)
            return null;

        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getProducts() != null) {
            dto.setProductIds(
                    category.getProducts()
                            .stream()
                            .map(Product::getId)
                            .collect(Collectors.toList()));
        }

        return dto;
    }
}