package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.apparel.ApparelCategoryRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelCategoryResponseDTO;
import org.springshop.api.model.product.ApparelCategory;   

public class ApparelCategoryMapper {

    public static ApparelCategory toEntity(ApparelCategoryRequestDTO dto) {
        if (dto == null) return null;
        ApparelCategory apparelCategory = new ApparelCategory();
        apparelCategory.setName(dto.getName());
        return apparelCategory;
    }

    public static ApparelCategoryResponseDTO toResponseDTO(ApparelCategory entity) {
        if (entity == null) return null;
        ApparelCategoryResponseDTO dto = new ApparelCategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public static void updateEntity(ApparelCategory existing, ApparelCategoryRequestDTO dto) {
        existing.setName(dto.getName());
    }
}
