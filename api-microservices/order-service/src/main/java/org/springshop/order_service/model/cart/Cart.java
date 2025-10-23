package org.springshop.order_service.model.cart;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class Cart {
    private Integer id;
    private Integer userId;
    private Set<CartItem> items;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}

