package org.springshop.product_service.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.product_service.model.product.WorkoutAccessory;

import java.util.List;

public interface WorkoutAccessoryRepository extends JpaRepository<WorkoutAccessory, Integer> {
    
    List<WorkoutAccessory> findAllByCategoryId(Integer categoryId);
    List<WorkoutAccessory> findAllByWorkoutAccessoryCategoryId(Integer categoryId);
}