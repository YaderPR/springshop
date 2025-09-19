package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.api.model.product.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {   
}
