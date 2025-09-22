package org.springshop.api.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
}
