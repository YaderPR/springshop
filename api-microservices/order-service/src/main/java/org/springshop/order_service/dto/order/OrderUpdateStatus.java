package org.springshop.order_service.dto.order;

import org.springshop.order_service.model.order.OrderStatus;
import lombok.Data;

@Data
public class OrderUpdateStatus {
    private OrderStatus status;
}
