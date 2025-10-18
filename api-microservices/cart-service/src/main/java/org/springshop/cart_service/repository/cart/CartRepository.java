package org.springshop.cart_service.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.cart_service.model.cart.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    
}
