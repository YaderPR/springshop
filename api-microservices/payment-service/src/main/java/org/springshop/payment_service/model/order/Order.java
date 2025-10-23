package org.springshop.payment_service.model.order;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
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

