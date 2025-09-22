package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.api.model.product.Supplement;

@Repository
public interface SuplementRepository extends JpaRepository<Supplement, Integer> {
}
