package org.springshop.api.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// OrderItemRequestDto (CORREGIDO)
@Setter
@Getter
@NoArgsConstructor
public class OrderItemRequestDto {
    @NotNull
    private Integer productId;
    
    @NotNull
    private Integer quantity;
    
    // ELIMINADO: price (viene del Servidor/Product)
    // ELIMINADO: orderId (viene de la URL)
}

