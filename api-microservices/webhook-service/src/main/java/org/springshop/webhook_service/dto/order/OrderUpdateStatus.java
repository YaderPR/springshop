package org.springshop.webhook_service.dto.order;

import org.springshop.webhook_service.model.order.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderUpdateStatus {
    private OrderStatus status;
}
