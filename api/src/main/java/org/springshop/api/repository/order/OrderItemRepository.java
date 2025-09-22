package org.springshop.api.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
}
