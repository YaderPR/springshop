package org.springshop.webhook_service.dto.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springshop.webhook_service.model.order.OrderStatus;

import lombok.Data;

@Data
public class OrderResponse {

    private Integer id;
    private OrderStatus status;
    private Double totalAmount;
    private Integer userId;
    private List<OrderItemResponse> items;
    private Integer addressId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}

