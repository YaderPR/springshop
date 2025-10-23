package org.springshop.order_service.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.order_service.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
}
