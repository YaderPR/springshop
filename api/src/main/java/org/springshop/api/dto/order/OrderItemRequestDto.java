package org.springshop.api.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderItemRequestDto {

    @NotNull
    private Integer productId;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double price; // precio unitario o subtotal según tu lógica
}

