package org.springshop.product_service.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.product_service.model.product.Supplement;

import java.util.List;

public interface SupplementRepository extends JpaRepository<Supplement, Integer> {
    List<Supplement> findAllByCategoryId(Integer categoryId);
}