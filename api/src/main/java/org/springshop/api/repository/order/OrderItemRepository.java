package org.springshop.api.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    public List<OrderItem> findAllByOrderId(Integer id);
}
