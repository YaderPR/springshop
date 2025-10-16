package org.springshop.product_service.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.product_service.model.product.Apparel;

@Repository
public interface ApparelRepository extends JpaRepository<Apparel, Integer> {    
    public List<Apparel> findAllByCategoryId(Integer categoryId);
}
