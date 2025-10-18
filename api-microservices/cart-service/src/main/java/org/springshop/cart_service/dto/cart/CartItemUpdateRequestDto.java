package org.springshop.cart_service.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartItemUpdateRequestDto {
    private Integer productId;
    private Integer quantity;
}

