package org.springshop.product_service.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.product_service.model.product.WorkoutAccessoryCategory;

@Repository
public interface WorkoutAccessoryCategoryRepository extends JpaRepository<WorkoutAccessoryCategory, Integer> {
}
