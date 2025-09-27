package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.CategoryRequestDTO;
import org.springshop.api.dto.product.CategoryResponseDTO;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.Product;

import java.util.stream.Collectors;

public class CategoryMapper {

    /**
     * Convierte CategoryRequestDTO a una nueva entidad Category.
     */
    public static Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    /**
     * Actualiza una entidad Category existente con los datos del DTO.
     * (Método añadido para ser usado por CategoryService).
     */
    public static void updateCategory(Category existing, CategoryRequestDTO dto) {
        if (existing == null || dto == null) return;
        
        // Asumiendo que solo se actualiza el nombre
        existing.setName(dto.getName());
        // Se podría añadir lógica para otros campos si existieran
    }

    /**
     * Convierte una entidad Category a su DTO de respuesta.
     */
    // Renombramos toResponseDTO a toResponseDTO (ya estaba bien, solo reconfirmamos la convención)
    public static CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) return null;

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        // NOTA: Para categorías de catálogo, obtener todos los IDs de producto 
        // puede ser costoso (N+1 query o traer mucha data). 
        // Solo haz esto si la relación está cargada (EAGER) o si se usa una query específica.
        if (category.getProducts() != null) {
            dto.setProductIds(
                category.getProducts()
                    .stream()
                    .map(Product::getId)
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }
}