package org.springshop.api.mapper.product;

import org.springshop.api.model.product.Supplement;
import org.springshop.api.dto.product.supplement.SupplementRequestDTO;
import org.springshop.api.dto.product.supplement.SupplementResponseDTO;
import org.springshop.api.model.product.Category;

// CORRECCIÓN 1: Clase renombrada de SuplementMapper a SupplementMapper
public class SupplementMapper {

    /**
     * Convierte una entidad Supplement a su DTO de respuesta.
     */
    // CORRECCIÓN 2: Renombramos toDTO a toResponseDTO.
    public static SupplementResponseDTO toResponseDTO(Supplement supplement) { // CORRECCIÓN 3: Variable renombrada
        if (supplement == null) return null;
        SupplementResponseDTO dto = new SupplementResponseDTO();
        
        // Mapeo de campos de Product (herencia)
        dto.setId(supplement.getId());
        dto.setName(supplement.getName());
        dto.setDescription(supplement.getDescription());
        dto.setPrice(supplement.getPrice());
        dto.setStock(supplement.getStock());
        dto.setImageUrl(supplement.getImageUrl());
        
        // Mapeo de Categoría (AÑADIMOS EL ID para consistencia en la respuesta)
        if (supplement.getCategory() != null) {
            dto.setCategoryId(supplement.getCategory().getId()); // AÑADIDO: El ID es crucial
            dto.setCategoryName(supplement.getCategory().getName());
        }

        // Mapeo de campos específicos de Supplement
        dto.setBrand(supplement.getBrand());
        dto.setFlavor(supplement.getFlavor());
        dto.setSize(supplement.getSize());
        dto.setIngredients(supplement.getIngredients());
        dto.setUsageInstructions(supplement.getUsageInstructions());
        dto.setWarnings(supplement.getWarnings());
        
        return dto;
    }

    /**
     * Convierte un DTO de solicitud a una nueva entidad Supplement.
     */
    public static Supplement toEntity(SupplementRequestDTO dto, Category category) {
        if (dto == null) return null;
        
        // CORRECCIÓN 3: Variable renombrada y clase instanciada
        Supplement supplement = new Supplement(); 
        
        // Mapeo de campos comunes de Product
        supplement.setName(dto.getName());
        supplement.setDescription(dto.getDescription());
        supplement.setPrice(dto.getPrice());
        supplement.setStock(dto.getStock());
        supplement.setImageUrl(dto.getImageUrl());
        supplement.setCategory(category);

        // Mapeo de campos específicos de Supplement
        supplement.setBrand(dto.getBrand());
        supplement.setFlavor(dto.getFlavor());
        supplement.setSize(dto.getSize());
        supplement.setIngredients(dto.getIngredients());
        supplement.setUsageInstructions(dto.getUsageInstructions());
        supplement.setWarnings(dto.getWarnings());
        
        return supplement;
    }
    
    /**
     * Actualiza una entidad Supplement existente con los datos de un DTO de solicitud.
     */
    // CORRECCIÓN 2: Renombramos updateEntity a updateSupplement para consistencia.
    public static void updateSupplement(Supplement existing, SupplementRequestDTO dto, Category category) {
        if (existing == null || dto == null) return;
        
        // DELEGACIÓN: Reutilizamos el método del ProductMapper para campos comunes y la Category
        ProductMapper.updateProduct(existing, dto, category);
        
        // Mapeo de campos específicos de Supplement
        existing.setBrand(dto.getBrand());
        existing.setFlavor(dto.getFlavor());
        existing.setSize(dto.getSize());
        existing.setIngredients(dto.getIngredients());
        existing.setUsageInstructions(dto.getUsageInstructions());
        existing.setWarnings(dto.getWarnings());
    }
}