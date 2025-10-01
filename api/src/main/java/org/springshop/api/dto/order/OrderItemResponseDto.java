package org.springshop.api.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderItemResponseDto {

    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Double price;

}
