package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.model.product.Apparel;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.ApparelCategory; 

public class ApparelMapper {

    /**
     * Convierte una entidad Apparel a su DTO de respuesta.
     */
    // CONVENCION: Renombramos toDTO a toResponseDTO
    public static ApparelResponseDTO toResponseDTO(Apparel apparel) {
        if (apparel == null) return null;
        
        ApparelResponseDTO dto = new ApparelResponseDTO();
        
        // Mapeo de campos de Product (herencia)
        dto.setId(apparel.getId());
        dto.setName(apparel.getName());
        dto.setDescription(apparel.getDescription());
        dto.setPrice(apparel.getPrice());
        dto.setStock(apparel.getStock());
        dto.setImageUrl(apparel.getImageUrl());
        
        // Mapeo de Categoría genérica
        if (apparel.getCategory() != null) {
            dto.setCategoryId(apparel.getCategory().getId()); // AÑADIDO: ID genérico
            dto.setCategoryName(apparel.getCategory().getName());
        }

        // Mapeo de campos de Apparel (especializados)
        dto.setSize(apparel.getSize());
        dto.setColor(apparel.getColor());
        dto.setBrand(apparel.getBrand());
        
        // Mapeo de ApparelCategory específica
        if (apparel.getApparelCategory() != null) {
            dto.setApparelCategoryId(apparel.getApparelCategory().getId()); // AÑADIDO: ID específico
            dto.setApparelCategoryName(apparel.getApparelCategory().getName());
        }
        
        return dto;
    }

    /**
     * Convierte un DTO de solicitud a una nueva entidad Apparel.
     */
    public static Apparel toEntity(ApparelRequestDTO dto, Category category, ApparelCategory apparelCategory) {
        if (dto == null) return null;
        
        Apparel apparel = new Apparel();
        
        // Mapeo de campos comunes de Product
        apparel.setName(dto.getName());
        apparel.setDescription(dto.getDescription());
        apparel.setPrice(dto.getPrice());
        apparel.setStock(dto.getStock());
        apparel.setImageUrl(dto.getImageUrl());
        apparel.setCategory(category);

        // Mapeo de campos específicos de Apparel
        apparel.setSize(dto.getSize());
        apparel.setColor(dto.getColor());
        apparel.setBrand(dto.getBrand());
        apparel.setApparelCategory(apparelCategory);
        
        return apparel;
    }
    
    /**
     * Actualiza una entidad Apparel existente con los datos de un DTO de solicitud.
     */
    // CONVENCION: Renombramos updateEntity a updateApparel
    public static void updateApparel(Apparel existing, ApparelRequestDTO dto, Category category, ApparelCategory apparelCategory) {
        if (existing == null || dto == null) return;
        
        // DELEGACIÓN: Reutilizamos el método del ProductMapper para los campos comunes
        // NOTA: Para que esto funcione, ApparelRequestDTO debe extender ProductRequestDTO, 
        // o necesitamos un DTO intermedio para los campos comunes.
        // Asumiendo que AppareltRequestDTO contiene todos los campos de ProductRequestDTO:
        
        // ProductMapper.updateProduct(existing, dto, category); 
        // -> Comentado si el DTO de Apparel NO extiende el DTO de Product

        // Mapeo manual de campos comunes (si no hay herencia de DTOs)
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);
        
        // Mapeo de campos específicos de Apparel
        existing.setSize(dto.getSize());
        existing.setColor(dto.getColor());
        existing.setBrand(dto.getBrand());
        existing.setApparelCategory(apparelCategory);
    }
}