package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponseDTO;
import org.springshop.api.model.product.WorkoutAccessoryCategory;

public class WorkoutAccessoryCategoryMapper {
    public static WorkoutAccessoryCategory toEntity(WorkoutAccessoryCategoryRequestDTO dto) {
        if (dto == null) return null;
        WorkoutAccessoryCategory category = new WorkoutAccessoryCategory();
        category.setName(dto.getName());
        return category;
    }
    public static WorkoutAccessoryCategoryResponseDTO toResponseDTO(WorkoutAccessoryCategory entity) {
        if (entity == null) return null;
        WorkoutAccessoryCategoryResponseDTO dto = new WorkoutAccessoryCategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
    public static void updateWorkoutAccessoryCategory(WorkoutAccessoryCategory existing, WorkoutAccessoryCategoryRequestDTO dto) {
        if (existing == null || dto == null) return;
        existing.setName(dto.getName());
    }
}