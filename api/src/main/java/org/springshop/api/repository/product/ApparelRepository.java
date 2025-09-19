package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.api.model.product.Apparel;

@Repository
public interface ApparelRepository extends JpaRepository<Apparel, Integer> {    
}
