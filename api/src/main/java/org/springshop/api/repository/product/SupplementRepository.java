package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.product.Supplement;

import java.util.List;

public interface SupplementRepository extends JpaRepository<Supplement, Integer> {
    List<Supplement> findAllByCategoryId(Integer categoryId);
}