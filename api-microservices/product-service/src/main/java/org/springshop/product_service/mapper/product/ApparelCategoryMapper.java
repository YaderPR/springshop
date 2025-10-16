package org.springshop.product_service.mapper.product;

import org.springshop.product_service.dto.product.apparel.ApparelCategoryRequest;
import org.springshop.product_service.dto.product.apparel.ApparelCategoryResponse;
import org.springshop.product_service.model.product.ApparelCategory; 

public class ApparelCategoryMapper {
    public static ApparelCategory toEntity(ApparelCategoryRequest dto) {
        if (dto == null) return null;
        ApparelCategory apparelCategory = new ApparelCategory();
        apparelCategory.setName(dto.getName());
        return apparelCategory;
    }
    public static ApparelCategoryResponse toResponseDTO(ApparelCategory entity) {
        if (entity == null) return null;
        ApparelCategoryResponse dto = new ApparelCategoryResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
    public static void updateApparelCategory(ApparelCategory existing, ApparelCategoryRequest dto) {
        if (existing == null || dto == null) return;
        existing.setName(dto.getName());
    }
}