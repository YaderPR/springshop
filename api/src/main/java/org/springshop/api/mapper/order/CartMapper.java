package org.springshop.api.mapper.order;

import java.util.Set;
import java.util.stream.Collectors;

import org.springshop.api.dto.order.CartItemRequestDto;
import org.springshop.api.dto.order.CartItemResponseDto;
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.order.CartItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;

public class CartMapper {

    public static Cart toEntity(CartRequestDto dto, User user, Set<CartItem> items) {
        if (dto == null)
            return null;

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(items);
        return cart;
    }

    public static CartItem toEntity(CartItemRequestDto dto, Product product) {
        if (dto == null)
            return null;

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }

    public static CartResponseDto toResponseDto(Cart cart) {
        if (cart == null)
            return null;
        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setItems(cart.getItems() != null
                ? cart.getItems().stream()
                        .map(CartMapper::toResponseDto)
                        .collect(Collectors.toSet())
                : null);
        return dto;
    }

    public static CartItemResponseDto toResponseDto(CartItem item) {
        if (item == null)
            return null;
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        return dto;
    }
}
