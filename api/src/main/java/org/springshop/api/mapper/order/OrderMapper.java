package org.springshop.api.mapper.order;

import java.util.Set;
import java.util.stream.Collectors;

import org.springshop.api.dto.order.OrderRequestDto;
import org.springshop.api.dto.order.OrderResponseDto;
import org.springshop.api.dto.order.OrderItemRequestDto;
import org.springshop.api.dto.order.OrderItemResponseDto;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.order.OrderItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;

public class OrderMapper {

    public static Order toEntity(OrderRequestDto dto, Set<OrderItem> items, User user) {
        if (dto == null)
            return null;

        Order order = new Order();
        order.setUser(user);
        order.setStatus(dto.getStatus());
        order.setTotalAmount(dto.getTotalAmount());
        order.setItems(items);

        return order;
    }

    public static OrderResponseDto toResponseDto(Order entity) {
        if (entity == null)
            return null;

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setTotalAmount(entity.getTotalAmount());
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

    public static void updateEntity(Order entity, OrderRequestDto dto, Set<OrderItem> items) {
        if (entity == null || dto == null)
            return;

        entity.setStatus(dto.getStatus());
        entity.setTotalAmount(dto.getTotalAmount());

        if (items != null) {
            entity.setItems(items);
        }
    }

    // ---------- Items ----------

    public static OrderItem toEntity(OrderItemRequestDto dto, Product product) {
        if (dto == null)
            return null;

        OrderItem item = new OrderItem();
        item.setProduct(product); // referencia completa
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }

    private static OrderItemResponseDto toResponseDto(OrderItem entity) {
        if (entity == null)
            return null;

        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setPrice(entity.getPrice());
        dto.setProductId(entity.getProduct() != null ? entity.getProduct().getId() : null);
        return dto;
    }
}
