package org.springshop.api.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.order.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    
}
