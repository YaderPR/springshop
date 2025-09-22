package org.springshop.api.mapper.product;

import org.springshop.api.model.product.Supplement;
import org.springshop.api.dto.product.supplement.SupplementRequestDTO;
import org.springshop.api.dto.product.supplement.SupplementResponseDTO;
import org.springshop.api.model.product.Category;

public class SuplementMapper {

    public static SupplementResponseDTO toDTO(Supplement suplement) {
        if (suplement == null) return null;
        SupplementResponseDTO dto = new SupplementResponseDTO();
        dto.setId(suplement.getId());
        dto.setName(suplement.getName());
        dto.setDescription(suplement.getDescription());
        dto.setPrice(suplement.getPrice());
        dto.setStock(suplement.getStock());
        dto.setImageUrl(suplement.getImageUrl());
        dto.setCategoryName(suplement.getCategory() != null ? suplement.getCategory().getName() : null);

        dto.setBrand(suplement.getBrand());
        dto.setFlavor(suplement.getFlavor());
        dto.setSize(suplement.getSize());
        dto.setIngredients(suplement.getIngredients());
        dto.setUsageInstructions(suplement.getUsageInstructions());
        dto.setWarnings(suplement.getWarnings());
        return dto;
    }

    public static Supplement toEntity(SupplementRequestDTO dto, Category category) {
        if (dto == null) return null;
        Supplement suplement = new Supplement();
        suplement.setName(dto.getName());
        suplement.setDescription(dto.getDescription());
        suplement.setPrice(dto.getPrice());
        suplement.setStock(dto.getStock());
        suplement.setImageUrl(dto.getImageUrl());
        suplement.setCategory(category);

        suplement.setBrand(dto.getBrand());
        suplement.setFlavor(dto.getFlavor());
        suplement.setSize(dto.getSize());
        suplement.setIngredients(dto.getIngredients());
        suplement.setUsageInstructions(dto.getUsageInstructions());
        suplement.setWarnings(dto.getWarnings());
        return suplement;
    }
    public static void updateEntity(Supplement existing, SupplementRequestDTO dto, Category category) {
        ProductMapper.updateEntity(existing, dto, category);
        existing.setBrand(dto.getBrand());
        existing.setFlavor(dto.getFlavor());
        existing.setSize(dto.getSize());
        existing.setIngredients(dto.getIngredients());
        existing.setUsageInstructions(dto.getUsageInstructions());
        existing.setWarnings(dto.getWarnings());
    }
}
