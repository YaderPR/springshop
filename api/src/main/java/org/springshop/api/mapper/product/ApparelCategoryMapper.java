package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.apparel.ApparelCategoryRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelCategoryResponseDTO;
import org.springshop.api.model.product.ApparelCategory; 

public class ApparelCategoryMapper {

    /**
     * Convierte el DTO de solicitud a una nueva entidad ApparelCategory.
     */
    public static ApparelCategory toEntity(ApparelCategoryRequestDTO dto) {
        if (dto == null) return null;
        ApparelCategory apparelCategory = new ApparelCategory();
        apparelCategory.setName(dto.getName());
        return apparelCategory;
    }

    /**
     * Convierte la entidad ApparelCategory a su DTO de respuesta.
     */
    // El nombre toResponseDTO es consistente y correcto.
    public static ApparelCategoryResponseDTO toResponseDTO(ApparelCategory entity) {
        if (entity == null) return null;
        ApparelCategoryResponseDTO dto = new ApparelCategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    /**
     * Actualiza una entidad existente con los datos del DTO.
     */
    // CONVENCION: Renombramos updateEntity a updateApparelCategory
    public static void updateApparelCategory(ApparelCategory existing, ApparelCategoryRequestDTO dto) {
        if (existing == null || dto == null) return;
        // Asumiendo que el único campo actualizable es 'name'
        existing.setName(dto.getName());
    }
}