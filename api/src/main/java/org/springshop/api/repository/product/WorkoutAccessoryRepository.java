package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.product.WorkoutAccessory;

import java.util.List;

public interface WorkoutAccessoryRepository extends JpaRepository<WorkoutAccessory, Integer> {
    
    /**
     * Encuentra todos los accesorios de entrenamiento por el ID de su Categoría genérica (Category).
     * Soporta el filtrado en el endpoint principal: GET /api/products/workoutaccessories?categoryId=N
     */
    List<WorkoutAccessory> findAllByCategoryId(Integer categoryId);
    
    /**
     * Encuentra todos los accesorios de entrenamiento por el ID de su Categoría especializada (WorkoutAccessoryCategory).
     * Soporta la relación en el endpoint de categoría: GET /api/products/workoutaccessories/categories/{id}/workoutaccessories
     */
    List<WorkoutAccessory> findAllByWorkoutAccessoryCategoryId(Integer categoryId);
}