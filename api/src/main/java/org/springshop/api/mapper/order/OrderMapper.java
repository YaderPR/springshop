package org.springshop.api.mapper.order;

import java.util.stream.Collectors;

import org.springshop.api.dto.order.OrderRequestDto;
import org.springshop.api.dto.order.OrderResponseDto;
import org.springshop.api.mapper.payment.AddressMapper;
import org.springshop.api.dto.order.OrderItemRequestDto;
import org.springshop.api.dto.order.OrderItemResponseDto;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.order.OrderItem;
import org.springshop.api.model.payment.Address;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;

public class OrderMapper {

    public static Order toEntity(OrderRequestDto dto, Address address, User user) {
        if (dto == null)
            return null;

        Order order = new Order();
        order.setUser(user);
        order.setStatus(dto.getStatus());

        order.setShippingAddress(address);
        return order;
    }

    public static OrderResponseDto toResponseDto(Order entity) {
        if (entity == null)
            return null;

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setUserId(entity.getUser().getId());

        dto.setTotalAmount(entity.getTotalAmount());
        dto.setAddress(AddressMapper.toResponseDto(entity.getShippingAddress()));

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

    public static void updateOrder(Order existing, OrderRequestDto dto, Address address, User user) {
        if (existing == null || dto == null)
            return;

        existing.setShippingAddress(address);
        existing.setUser(user);
    }

    public static OrderItem toEntity(OrderItemRequestDto dto, Product product, Order order) {
        if (dto == null)
            return null;

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(product.getPrice());

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

        dto.setProductId(entity.getProduct() != null ? entity.getProduct().getId() : null);

        return dto;
    }

    public static void updateOrderItem(OrderItem existing, OrderItemRequestDto dto, Product product, Order order) {
        if (existing == null || dto == null)
            return;
        existing.setQuantity(dto.getQuantity());
        existing.setOrder(order);
        existing.setProduct(product);
    }
}