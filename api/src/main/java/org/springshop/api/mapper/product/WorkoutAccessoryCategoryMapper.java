package org.springshop.api.mapper.product;

import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryCategoryResponseDTO;
import org.springshop.api.model.product.WorkoutAccessoryCategory;

public class WorkoutAccessoryCategoryMapper {

    /**
     * Convierte el DTO de solicitud a una nueva entidad WorkoutAccessoryCategory.
     */
    public static WorkoutAccessoryCategory toEntity(WorkoutAccessoryCategoryRequestDTO dto) {
        if (dto == null) return null;
        WorkoutAccessoryCategory category = new WorkoutAccessoryCategory();
        category.setName(dto.getName());
        return category;
    }

    /**
     * Convierte la entidad WorkoutAccessoryCategory a su DTO de respuesta.
     */
    public static WorkoutAccessoryCategoryResponseDTO toResponseDTO(WorkoutAccessoryCategory entity) {
        if (entity == null) return null;
        WorkoutAccessoryCategoryResponseDTO dto = new WorkoutAccessoryCategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        // NOTA: Para mantener la consistencia con CategoryResponseDTO, aquí podríamos 
        // añadir una lista de IDs de accesorios si fuera necesario, pero por simplicidad, lo omitimos.
        return dto;
    }

    /**
     * Actualiza una entidad existente con los datos del DTO.
     */
    // CONVENCION: Renombramos updateEntity a updateWorkoutAccessoryCategory 
    public static void updateWorkoutAccessoryCategory(WorkoutAccessoryCategory existing, WorkoutAccessoryCategoryRequestDTO dto) {
        if (existing == null || dto == null) return;
        // Asumiendo que 'name' es el único campo actualizable.
        existing.setName(dto.getName());
    }
}