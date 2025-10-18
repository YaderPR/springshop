package org.springshop.cart_service.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartItemResponseDto {
    private Integer id;
    private Integer productId;
    private Integer cartId;
    private Integer quantity;
    private Double price; 
}
