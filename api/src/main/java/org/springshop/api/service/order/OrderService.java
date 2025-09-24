package org.springshop.api.service.order;

import org.springframework.stereotype.Service;
import org.springshop.api.repository.order.OrderItemRepository;
import org.springshop.api.repository.order.OrderRepository;

@Service
public class OrderService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }
       
}
