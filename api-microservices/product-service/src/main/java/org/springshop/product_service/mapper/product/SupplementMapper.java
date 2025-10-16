package org.springshop.product_service.mapper.product;

import org.springshop.product_service.model.product.Supplement;
import org.springshop.product_service.dto.product.supplement.SupplementRequest;
import org.springshop.product_service.dto.product.supplement.SupplementResponse;
import org.springshop.product_service.model.product.Category;

public class SupplementMapper {

    public static SupplementResponse toResponseDTO(Supplement supplement) {
        if (supplement == null)
            return null;
        SupplementResponse dto = new SupplementResponse();

        dto.setId(supplement.getId());
        dto.setName(supplement.getName());
        dto.setDescription(supplement.getDescription());
        dto.setPrice(supplement.getPrice());
        dto.setStock(supplement.getStock());
        dto.setImageUrl(supplement.getImageUrl());

        if (supplement.getCategory() != null) {
            dto.setCategoryId(supplement.getCategory().getId());
            dto.setCategoryName(supplement.getCategory().getName());
        }

        dto.setBrand(supplement.getBrand());
        dto.setFlavor(supplement.getFlavor());
        dto.setSize(supplement.getSize());
        dto.setIngredients(supplement.getIngredients());
        dto.setUsageInstructions(supplement.getUsageInstructions());
        dto.setWarnings(supplement.getWarnings());

        return dto;
    }

    public static Supplement toEntity(SupplementRequest dto, Category category) {
        if (dto == null)
            return null;

        Supplement supplement = new Supplement();

        supplement.setName(dto.getName());
        supplement.setDescription(dto.getDescription());
        supplement.setPrice(dto.getPrice());
        supplement.setStock(dto.getStock());
        supplement.setImageUrl(dto.getImageUrl());
        supplement.setCategory(category);
        supplement.setBrand(dto.getBrand());
        supplement.setFlavor(dto.getFlavor());
        supplement.setSize(dto.getSize());
        supplement.setIngredients(dto.getIngredients());
        supplement.setUsageInstructions(dto.getUsageInstructions());
        supplement.setWarnings(dto.getWarnings());

        return supplement;
    }

    public static void updateSupplement(Supplement existing, SupplementRequest dto, Category category) {
        if (existing == null || dto == null)
            return;
        ProductMapper.updateProduct(existing, dto, category);
        existing.setBrand(dto.getBrand());
        existing.setFlavor(dto.getFlavor());
        existing.setSize(dto.getSize());
        existing.setIngredients(dto.getIngredients());
        existing.setUsageInstructions(dto.getUsageInstructions());
        existing.setWarnings(dto.getWarnings());
    }
}