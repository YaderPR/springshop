package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.product.WorkoutAccessory;

public interface WorkoutAccessoryRepository extends JpaRepository<WorkoutAccessory, Integer> {
}
