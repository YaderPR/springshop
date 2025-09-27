package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.product.Category;

public class ProductMapper {
    
    /**
     * Convierte una entidad Product a su DTO de respuesta.
     * @param product La entidad Product.
     * @return El ProductResponseDTO.
     */
    // Renombramos toDTO a toResponseDto para consistencia con Cart/Order mappers
    public static ProductResponseDTO toResponseDto(Product product) {
        if (product == null) return null;
        
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        
        // Incluir el ID de la categoría es a menudo más útil que solo el nombre,
        // pero mantenemos la lógica de negocio actual, añadiendo el ID para completitud.
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        } else {
            dto.setCategoryId(null);
            dto.setCategoryName(null);
        }
        
        return dto;
    }

    /**
     * Convierte un DTO de solicitud a una nueva entidad Product.
     */
    public static Product toEntity(ProductRequestDTO dto, Category category) {
        if (dto == null) return null;
        
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(category);
        return product;
    }
    
    /**
     * Actualiza una entidad Product existente con los datos de un DTO de solicitud.
     */
    // Renombramos updateEntity a updateProduct para consistencia con Cart/Order mappers
    public static void updateProduct(Product existing, ProductRequestDTO dto, Category category) {
        // Validación de nulidad ya manejada en el Service, pero se mantiene si se desea.
        if (existing == null || dto == null) return;
        
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);
    }
}