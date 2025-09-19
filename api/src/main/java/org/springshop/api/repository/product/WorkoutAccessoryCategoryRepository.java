package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.api.model.product.WorkoutAccessoryCategory;

@Repository
public interface WorkoutAccessoryCategoryRepository extends JpaRepository<WorkoutAccessoryCategory, Integer> {
}
