package org.springshop.product_service.mapper.product;

import java.util.stream.Collectors;

import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequest;
import org.springshop.product_service.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponse;
import org.springshop.product_service.model.product.Product;
import org.springshop.product_service.model.product.WorkoutAccessoryCategory;

public class WorkoutAccessoryCategoryMapper {
    public static WorkoutAccessoryCategory toEntity(WorkoutAccessoryCategoryRequest dto) {
        if (dto == null) return null;
        WorkoutAccessoryCategory category = new WorkoutAccessoryCategory();
        category.setName(dto.getName());
        category.setImageUrl(dto.getImageUrl());
        return category;
    }
    public static WorkoutAccessoryCategoryResponse toResponseDTO(WorkoutAccessoryCategory entity) {
        if (entity == null) return null;
        WorkoutAccessoryCategoryResponse dto = new WorkoutAccessoryCategoryResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setImageUrl(entity.getImageUrl());
        if (entity.getWorkoutAccessories() != null) {
            dto.setAccessoryIds(
                    entity.getWorkoutAccessories()
                            .stream()
                            .map(Product::getId)
                            .collect(Collectors.toList()));
        }
        return dto;
    }
    public static void updateWorkoutAccessoryCategory(WorkoutAccessoryCategory existing, WorkoutAccessoryCategoryRequest dto) {
        if (existing == null || dto == null) return;
        existing.setName(dto.getName());
        existing.setImageUrl(dto.getImageUrl());
    }
}