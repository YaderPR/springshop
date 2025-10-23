package org.springshop.order_service.model.cart;

import lombok.Data;

@Data
public class CartItem {
    private Integer id;
    private Integer productId;
    private Integer cartId;
    private Integer quantity;
    private Double price; 
}
