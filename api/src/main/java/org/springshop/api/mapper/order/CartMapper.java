package org.springshop.api.mapper.order;

import org.springshop.api.dto.order.CartItemCreateRequestDto;
import org.springshop.api.dto.order.CartItemResponseDto;
import org.springshop.api.dto.order.CartItemUpdateRequestDto;
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.model.order.Cart;
import org.springshop.api.model.order.CartItem;
import org.springshop.api.model.product.Product;
import org.springshop.api.model.user.User;

import java.util.HashSet;
import java.util.stream.Collectors;

public class CartMapper {

    public static Cart toEntity(CartRequestDto dto, User user, Double totalAmount) {
        if (dto == null)
            return null;

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalAmount(totalAmount);

        return cart;
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
                : new HashSet<>());
        dto.setCreateAt(cart.getCreateAt());
        dto.setUpdateAt(cart.getUpdateAt());
        return dto;
    }

    public static void updateCart(Cart existing, User user) {
        if (existing == null)
            return;

        existing.setUser(user);
    }

    public static CartItem toEntity(CartItemCreateRequestDto dto, Product product, Cart cart) {
        if (dto == null)
            return null;

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(product.getPrice());

        item.setCart(cart);
        return item;
    }

    public static CartItemResponseDto toResponseDto(CartItem item) {
        if (item == null)
            return null;

        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        dto.setCartId(item.getCart().getId());

        return dto;
    }

    public static void updateCartItem(CartItem existing, CartItemUpdateRequestDto dto, Product product) {
        if (existing == null || dto == null)
            return;
        existing.setQuantity(dto.getQuantity());
        existing.setProduct(product);
    }
}