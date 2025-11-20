package org.springshop.order_service.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.order_service.model.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    public List<Order> findByUserId(Integer userId);
    public Optional<Order> findTopOrderByUserIdOrderByIdDesc(Integer userId);
}
