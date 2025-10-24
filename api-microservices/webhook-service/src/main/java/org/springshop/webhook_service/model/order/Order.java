package org.springshop.webhook_service.model.order;

import java.time.LocalDateTime;
import java.util.List;


import lombok.Data;

@Data
public class Order {

    private Integer id;
    private OrderStatus status;
    private Double totalAmount;
    private Integer userId;
    private List<OrderItem> items;
    private Integer addressId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}

