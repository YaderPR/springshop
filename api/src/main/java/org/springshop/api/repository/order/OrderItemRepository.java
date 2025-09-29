package org.springshop.api.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    public List<OrderItem> findAllByOrderId(Integer id);
    Optional<OrderItem> findByOrderIdAndProductId(Integer orderId, Integer productId);
}
