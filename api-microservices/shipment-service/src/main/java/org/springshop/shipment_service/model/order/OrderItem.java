package org.springshop.shipment_service.model.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderItem {

    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Double price;

}
