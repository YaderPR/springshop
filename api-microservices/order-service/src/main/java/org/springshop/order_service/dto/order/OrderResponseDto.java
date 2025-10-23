package org.springshop.order_service.dto.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springshop.order_service.model.order.OrderStatus;

import lombok.Data;

@Data
public class OrderResponseDto {

    private Integer id;
    private OrderStatus status;
    private Double totalAmount;
    private Integer userId;
    private List<OrderItemResponseDto> items;
    private Integer addressId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}

