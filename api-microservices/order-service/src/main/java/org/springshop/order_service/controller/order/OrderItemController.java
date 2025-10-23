package org.springshop.order_service.controller.order;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.order_service.dto.order.OrderItemRequestDto;
import org.springshop.order_service.dto.order.OrderItemResponseDto;
import org.springshop.order_service.model.order.Order;
import org.springshop.order_service.service.order.OrderItemService;
import org.springshop.order_service.service.order.OrderService;

@RestController
@RequestMapping("/api/orders/{orderId:\\d+}/items")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;

    public OrderItemController(OrderItemService orderItemService, OrderService orderService) {
        this.orderItemService = orderItemService;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItems(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    @GetMapping("/{itemId:\\d+}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable Integer orderId,
            @PathVariable Integer itemId) {

        return ResponseEntity.ok(orderItemService.getOrderItemById(itemId));
    }

    @PostMapping
    public ResponseEntity<OrderItemResponseDto> createOrderItem(@PathVariable Integer orderId,
            @RequestBody OrderItemRequestDto requestDto) {

        Order order = orderService.findOrderOrThrow(orderId);
        OrderItemResponseDto responseDto = orderItemService.createOrderItem(order, requestDto);
        orderService.updateOrderTotal(orderId);
        URI location = URI.create("/api/orders/" + orderId + "/items/" + responseDto.getId());

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/{itemId:\\d+}")
    public ResponseEntity<OrderItemResponseDto> updateOrderItem(
            @PathVariable Integer orderId,
            @PathVariable Integer itemId,
            @RequestBody OrderItemRequestDto requestDto) {

        OrderItemResponseDto responseDto = orderItemService.updateOrderItem(orderId, itemId, requestDto);
        orderService.updateOrderTotal(orderId);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{itemId:\\d+}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Integer orderId, @PathVariable Integer itemId) {

        orderItemService.deleteOrderItem(orderId, itemId);
        orderService.updateOrderTotal(orderId);

        return ResponseEntity.noContent().build();
    }
}