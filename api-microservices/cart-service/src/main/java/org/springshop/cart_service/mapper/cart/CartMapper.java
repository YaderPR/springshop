package org.springshop.cart_service.mapper.cart;

import org.springshop.cart_service.dto.cart.CartItemCreateRequestDto;
import org.springshop.cart_service.dto.cart.CartItemResponseDto;
import org.springshop.cart_service.dto.cart.CartItemUpdateRequestDto;
import org.springshop.cart_service.dto.cart.CartRequestDto;
import org.springshop.cart_service.dto.cart.CartResponseDto;
import org.springshop.cart_service.model.cart.Cart;
import org.springshop.cart_service.model.cart.CartItem;


public class CartMapper {

    public static Cart toEntity(CartRequestDto dto, Integer userId, Double totalAmount) {
        if (dto == null)
            return null;

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalAmount(totalAmount);

        return cart;
    }

    public static CartResponseDto toResponseDto(Cart cart) {
        if (cart == null)
            return null;

        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setCreateAt(cart.getCreateAt());
        dto.setUpdateAt(cart.getUpdateAt());
        return dto;
    }

    public static void updateCart(Cart existing, Integer userId) {
        if (existing == null)
            return;

        existing.setUserId(userId);
    }

    public static CartItem toEntity(CartItemCreateRequestDto dto, Integer productId, Double productPrice, Cart cart) {
        if (dto == null)
            return null;

        CartItem item = new CartItem();
        item.setProductId(productId);
        item.setQuantity(dto.getQuantity());
        item.setPrice(productPrice);

        item.setCart(cart);
        return item;
    }

    public static CartItemResponseDto toResponseDto(CartItem item) {
        if (item == null)
            return null;

        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        dto.setCartId(item.getCart().getId());

        return dto;
    }

    public static void updateCartItem(CartItem existing, CartItemUpdateRequestDto dto, Integer productId) {
        if (existing == null || dto == null)
            return;
        existing.setQuantity(dto.getQuantity());
        existing.setProductId(productId);
    }
}