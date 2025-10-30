package org.springshop.cart_service.controller.cart;

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
import org.springshop.cart_service.dto.cart.CartItemCreateRequestDto;
import org.springshop.cart_service.dto.cart.CartItemResponseDto;
import org.springshop.cart_service.dto.cart.CartItemUpdateRequestDto;
import org.springshop.cart_service.service.cart.CartItemService;
import org.springshop.cart_service.service.cart.CartService;

@RestController
@RequestMapping("/api/v2/carts/{cartId:\\d+}/items")
public class CartItemController {

    private final CartItemService cartItemService;
    private final CartService cartService;

    public CartItemController(CartItemService cartItemService, CartService cartService) {
        this.cartItemService = cartItemService;
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> getCartItems(@PathVariable Integer cartId) {

        return ResponseEntity.ok(cartItemService.getCartItemsByCartId(cartId));
    }

    @PostMapping
    public ResponseEntity<CartItemResponseDto> addItemToCart(@PathVariable Integer cartId,
            @RequestBody CartItemCreateRequestDto requestDto) {

        CartItemResponseDto responseDto = cartItemService.addItemOrUpdateQuantity(cartId, requestDto);
        URI location = URI.create("/api/v2/carts/" + cartId + "/items/" + responseDto.getId());

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/{itemId:\\d+}")
    public ResponseEntity<CartItemResponseDto> updateCartItem(
            @PathVariable Integer cartId,
            @PathVariable Integer itemId,
            @RequestBody CartItemUpdateRequestDto requestDto) {

        CartItemResponseDto responseDto = cartItemService.updateCartItem(cartId, itemId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{itemId:\\d+}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Integer cartId, @PathVariable Integer itemId) {

        cartItemService.deleteCartItem(cartId, itemId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotal(@PathVariable Integer cartId) {

        double total = cartService.calculateCartTotals(cartId);

        return ResponseEntity.ok(total);
    }
}