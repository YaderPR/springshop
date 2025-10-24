package org.springshop.webhook_service.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderItemResponse {

    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Double price;

}
