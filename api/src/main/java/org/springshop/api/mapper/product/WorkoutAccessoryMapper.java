package org.springshop.api.mapper.product;

import org.springshop.api.model.product.WorkoutAccessory;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.WorkoutAccessoryCategory;

public class WorkoutAccessoryMapper {

    /**
     * Convierte una entidad WorkoutAccessory a su DTO de respuesta.
     */
    // CONVENCION: Renombramos toDTO a toResponseDTO
    public static WorkoutAccessoryResponseDTO toResponseDTO(WorkoutAccessory accessory) {
        if (accessory == null) return null;
        
        WorkoutAccessoryResponseDTO dto = new WorkoutAccessoryResponseDTO();
        
        // Mapeo de campos de Product (herencia)
        dto.setId(accessory.getId());
        dto.setName(accessory.getName());
        dto.setDescription(accessory.getDescription());
        dto.setPrice(accessory.getPrice());
        dto.setStock(accessory.getStock());
        dto.setImageUrl(accessory.getImageUrl());
        
        // Mapeo de Categoría genérica (AÑADIMOS EL ID)
        if (accessory.getCategory() != null) {
            dto.setCategoryId(accessory.getCategory().getId()); 
            dto.setCategoryName(accessory.getCategory().getName());
        }

        // Mapeo de campos específicos de WorkoutAccessory
        dto.setMaterial(accessory.getMaterial());
        dto.setDimensions(accessory.getDimensions());
        dto.setWeight(accessory.getWeight());
        dto.setColor(accessory.getColor());
        
        // Mapeo de WorkoutAccessoryCategory específica (AÑADIMOS EL ID)
        if (accessory.getWorkoutAccessoryCategory() != null) {
            dto.setWorkoutAccessoryCategoryId(accessory.getWorkoutAccessoryCategory().getId()); 
            dto.setWorkoutAccessoryCategoryName(accessory.getWorkoutAccessoryCategory().getName());
        }
        
        return dto;
    }

    /**
     * Convierte un DTO de solicitud a una nueva entidad WorkoutAccessory.
     */
    public static WorkoutAccessory toEntity(WorkoutAccessoryRequestDTO dto, Category category, WorkoutAccessoryCategory categoryWorkoutAccessory) {
        if (dto == null) return null;
        
        WorkoutAccessory accessory = new WorkoutAccessory();
        
        // Mapeo de campos comunes de Product
        accessory.setName(dto.getName());
        accessory.setDescription(dto.getDescription());
        accessory.setPrice(dto.getPrice());
        accessory.setStock(dto.getStock());
        accessory.setImageUrl(dto.getImageUrl());
        accessory.setCategory(category);

        // Mapeo de campos específicos de WorkoutAccessory
        accessory.setMaterial(dto.getMaterial());
        accessory.setDimensions(dto.getDimensions());
        accessory.setWeight(dto.getWeight());
        accessory.setColor(dto.getColor());
        accessory.setWorkoutAccessoryCategory(categoryWorkoutAccessory);
        
        return accessory;
    }
    
    /**
     * Actualiza una entidad WorkoutAccessory existente con los datos de un DTO de solicitud.
     */
    // CONVENCION: Renombramos updateEntity a updateWorkoutAccessory
    public static void updateWorkoutAccessory(WorkoutAccessory existing, WorkoutAccessoryRequestDTO dto, Category category, WorkoutAccessoryCategory categoryWorkoutAccessory) {
        if (existing == null || dto == null) return;
        
        // DELEGACIÓN: Reutilizamos el método del ProductMapper para campos comunes
        // NOTA: Asumimos que la herencia de DTOs permite que 'dto' sea pasado aquí.
        // ProductMapper.updateProduct handles: name, description, price, stock, imageUrl, and category.
        ProductMapper.updateProduct(existing, dto, category); 
        
        // Mapeo de campos específicos de WorkoutAccessory
        existing.setMaterial(dto.getMaterial());
        existing.setDimensions(dto.getDimensions());
        existing.setWeight(dto.getWeight());
        existing.setColor(dto.getColor());
        existing.setWorkoutAccessoryCategory(categoryWorkoutAccessory);
    }
}