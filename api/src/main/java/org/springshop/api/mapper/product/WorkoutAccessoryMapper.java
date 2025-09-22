package org.springshop.api.mapper.product;

import org.springshop.api.model.product.WorkoutAccessory;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.model.product.Category;
import org.springshop.api.model.product.WorkoutAccessoryCategory;

public class WorkoutAccessoryMapper {

    public static WorkoutAccessoryResponseDTO toDTO(WorkoutAccessory accessory) {
        if (accessory == null) return null;
        WorkoutAccessoryResponseDTO dto = new WorkoutAccessoryResponseDTO();
        dto.setId(accessory.getId());
        dto.setName(accessory.getName());
        dto.setDescription(accessory.getDescription());
        dto.setPrice(accessory.getPrice());
        dto.setStock(accessory.getStock());
        dto.setImageUrl(accessory.getImageUrl());
        dto.setCategoryName(accessory.getCategory() != null ? accessory.getCategory().getName() : null);

        dto.setMaterial(accessory.getMaterial());
        dto.setDimensions(accessory.getDimensions());
        dto.setWeight(accessory.getWeight());
        dto.setColor(accessory.getColor());
        dto.setWorkoutAccessoryCategoryName(
            accessory.getWorkoutAccessoryCategory() != null ? accessory.getWorkoutAccessoryCategory().getName() : null
        );
        return dto;
    }

    public static WorkoutAccessory toEntity(WorkoutAccessoryRequestDTO dto, Category category, WorkoutAccessoryCategory categoryWorkoutAccessory) {
        if (dto == null) return null;
        WorkoutAccessory accessory = new WorkoutAccessory();
        accessory.setName(dto.getName());
        accessory.setDescription(dto.getDescription());
        accessory.setPrice(dto.getPrice());
        accessory.setStock(dto.getStock());
        accessory.setImageUrl(dto.getImageUrl());
        accessory.setCategory(category);

        accessory.setMaterial(dto.getMaterial());
        accessory.setDimensions(dto.getDimensions());
        accessory.setWeight(dto.getWeight());
        accessory.setColor(dto.getColor());
        accessory.setWorkoutAccessoryCategory(categoryWorkoutAccessory);
        return accessory;
    }
    public static void updateEntity(WorkoutAccessory existing, WorkoutAccessoryRequestDTO dto, Category category, WorkoutAccessoryCategory categoryWorkoutAccessory) {
        ProductMapper.updateEntity(existing, dto, category);
        existing.setMaterial(dto.getMaterial());
        existing.setDimensions(dto.getDimensions());
        existing.setWeight(dto.getWeight());
        existing.setColor(dto.getColor());
        existing.setWorkoutAccessoryCategory(categoryWorkoutAccessory);
    }
}

