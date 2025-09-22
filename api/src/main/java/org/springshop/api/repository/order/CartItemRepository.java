package org.springshop.api.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.order.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    
}
