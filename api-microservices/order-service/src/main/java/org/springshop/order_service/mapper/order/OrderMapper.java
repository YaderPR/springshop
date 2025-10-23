package org.springshop.order_service.mapper.order;

import java.util.stream.Collectors;

import org.springshop.order_service.dto.order.OrderRequestDto;
import org.springshop.order_service.dto.order.OrderResponseDto;
import org.springshop.order_service.dto.order.OrderItemRequestDto;
import org.springshop.order_service.dto.order.OrderItemResponseDto;
import org.springshop.order_service.model.order.Order;
import org.springshop.order_service.model.order.OrderItem;
public class OrderMapper {

    public static Order toEntity(OrderRequestDto dto, Integer addressId, Integer userId) {
        if (dto == null)
            return null;

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(dto.getStatus());

        order.setAddressId(addressId);
        return order;
    }

    public static OrderResponseDto toResponseDto(Order entity) {
        if (entity == null)
            return null;

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setUserId(entity.getUserId());

        dto.setTotalAmount(entity.getTotalAmount());
        dto.setAddressId(entity.getAddressId());

        dto.setCreateAt(entity.getCreateAt());
        dto.setUpdateAt(entity.getUpdateAt());

        if (entity.getItems() != null) {
            dto.setItems(
                    entity.getItems().stream()
                            .map(OrderMapper::toResponseDto)
                            .collect(Collectors.toList()));
        }
        return dto;
    }

    public static void updateOrder(Order existing, OrderRequestDto dto, Integer addressId, Integer userId) {
        if (existing == null || dto == null)
            return;

        existing.setAddressId(addressId);
        existing.setUserId(userId);
    }

    public static OrderItem toEntity(OrderItemRequestDto dto, Integer productId, Double price, Order order) {
        if (dto == null)
            return null;

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(dto.getQuantity());
        item.setPrice(price);

        item.setOrder(order);
        return item;
    }

    public static OrderItemResponseDto toResponseDto(OrderItem entity) {
        if (entity == null)
            return null;

        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setPrice(entity.getPrice());

        dto.setProductId(entity.getProductId() != null ? entity.getProductId() : null);

        return dto;
    }

    public static void updateOrderItem(OrderItem existing, OrderItemRequestDto dto, Integer productId, Order order) {
        if (existing == null || dto == null)
            return;
        existing.setQuantity(dto.getQuantity());
        existing.setOrder(order);
        existing.setProductId(productId);
    }
}