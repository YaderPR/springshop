package org.springshop.api.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderItemResponseDto {

    private Integer id; // id del item

    private Integer productId; // referencia al producto

    private Integer quantity;

    private Double price; // precio unitario
}

