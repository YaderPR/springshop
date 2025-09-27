package org.springshop.api.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartItemUpdateRequestDto {
    private Integer productId; // Si permites cambiar el producto en un ítem existente
    private Integer quantity;
    // ELIMINADO: price (viene del Servidor/Product)
}

