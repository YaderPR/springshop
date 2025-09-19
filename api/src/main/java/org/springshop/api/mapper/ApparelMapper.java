package org.springshop.api.mapper;

import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.model.product.Apparel;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.ApparelCategory;

public class ApparelMapper {

    public static ApparelResponseDTO toDTO(Apparel apparel) {
        if (apparel == null) return null;
        ApparelResponseDTO dto = new ApparelResponseDTO();
        dto.setId(apparel.getId());
        dto.setName(apparel.getName());
        dto.setDescription(apparel.getDescription());
        dto.setPrice(apparel.getPrice());
        dto.setStock(apparel.getStock());
        dto.setImageUrl(apparel.getImageUrl());
        dto.setCategoryName(apparel.getCategory() != null ? apparel.getCategory().getName() : null);

        dto.setSize(apparel.getSize());
        dto.setColor(apparel.getColor());
        dto.setBrand(apparel.getBrand());
        dto.setApparelCategoryName(apparel.getApparelCategory() != null ? apparel.getApparelCategory().getName() : null);
        return dto;
    }

    public static Apparel toEntity(ApparelRequestDTO dto, Category category, ApparelCategory categoryApparel) {
        if (dto == null) return null;
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
        apparel.setApparelCategory(categoryApparel);
        return apparel;
    }
    public static void updateEntity(Apparel existing, ApparelRequestDTO dto, Category category, ApparelCategory categoryApparel) {
        ProductMapper.updateEntity(existing, dto, category);
        existing.setSize(dto.getSize());
        existing.setColor(dto.getColor());
        existing.setBrand(dto.getBrand());
        existing.setApparelCategory(categoryApparel);
    }
}

