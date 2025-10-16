package org.springshop.product_service.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.product_service.model.product.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {   
}
