package org.springshop.api.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartItemCreateRequestDto {
    private Integer productId;
    private Integer quantity;
    // ELIMINADO: cartId (viene de la URL)
    // ELIMINADO: price (viene del Servidor/Product)
}

