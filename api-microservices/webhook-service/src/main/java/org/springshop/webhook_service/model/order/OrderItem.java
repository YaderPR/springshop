package org.springshop.webhook_service.model.order;

import lombok.Data;

@Data
public class OrderItem {

    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Double price;

}
