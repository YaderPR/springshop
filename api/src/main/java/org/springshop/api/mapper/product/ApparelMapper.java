package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.model.product.Apparel;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.ApparelCategory;

public class ApparelMapper {
    public static ApparelResponseDTO toResponseDTO(Apparel apparel) {
        if (apparel == null)
            return null;

        ApparelResponseDTO dto = new ApparelResponseDTO();

        dto.setId(apparel.getId());
        dto.setName(apparel.getName());
        dto.setDescription(apparel.getDescription());
        dto.setPrice(apparel.getPrice());
        dto.setStock(apparel.getStock());
        dto.setImageUrl(apparel.getImageUrl());
        if (apparel.getCategory() != null) {
            dto.setCategoryId(apparel.getCategory().getId());
            dto.setCategoryName(apparel.getCategory().getName());
        }
        dto.setSize(apparel.getSize());
        dto.setColor(apparel.getColor());
        dto.setBrand(apparel.getBrand());

        if (apparel.getApparelCategory() != null) {
            dto.setApparelCategoryId(apparel.getApparelCategory().getId());
            dto.setApparelCategoryName(apparel.getApparelCategory().getName());
        }

        return dto;
    }

    public static Apparel toEntity(ApparelRequestDTO dto, Category category, ApparelCategory apparelCategory) {
        if (dto == null)
            return null;

        Apparel apparel = new Apparel();
        apparel.setName(dto.getName());
        apparel.setDescription(dto.getDescription());
        apparel.setPrice(dto.getPrice());
        apparel.setStock(dto.getStock());
        apparel.setImageUrl(dto.getImageUrl());
        apparel.setCategory(category);
        apparel.setSize(dto.getSize());
        apparel.setColor(dto.getColor());
        apparel.setBrand(dto.getBrand());
        apparel.setApparelCategory(apparelCategory);

        return apparel;
    }

    public static void updateApparel(Apparel existing, ApparelRequestDTO dto, Category category,
            ApparelCategory apparelCategory) {
        if (existing == null || dto == null)
            return;

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);
        existing.setSize(dto.getSize());
        existing.setColor(dto.getColor());
        existing.setBrand(dto.getBrand());
        existing.setApparelCategory(apparelCategory);
    }
}