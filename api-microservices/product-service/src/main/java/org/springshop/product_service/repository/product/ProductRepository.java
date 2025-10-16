package org.springshop.product_service.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springshop.product_service.model.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    public List<Product> findAllByCategoryId(Integer categoryId);
    
}
