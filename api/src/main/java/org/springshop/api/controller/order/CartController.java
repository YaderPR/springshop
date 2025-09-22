package org.springshop.api.controller.order;

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
import org.springshop.api.dto.order.CartItemRequestDto;
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.service.order.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @GetMapping
    public List<CartResponseDto> getAllCarts() {
        return cartService.getAllCarts();
    }
    @GetMapping("/{id}")
    public CartResponseDto getCartById(@RequestBody Integer id) {
        return cartService.getCartById(id);
    }
    @PostMapping
    public CartResponseDto createCart(@RequestBody CartRequestDto dto) {
        return cartService.createCart(dto);
    }
    @PutMapping("/{id:\\d+}")
    public CartResponseDto updateCart(@PathVariable Integer id, @RequestBody CartRequestDto items) {
        return cartService.updateCart(id, items );
    }
    @PutMapping("/{id:\\d+}/items/{itemId:\\d+}")
    public CartResponseDto updateCartItem(@PathVariable Integer id, @PathVariable Integer itemId, @RequestBody CartItemRequestDto item) {
        return cartService.updateCartItem(id, itemId, item);
    }
    @DeleteMapping("/{id}")
    public void deleteCart(@PathVariable Integer id) {
        cartService.deleteCart(id);
    }
    @DeleteMapping("/{id:\\d+}/items/{itemId:\\d+}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Integer id, @PathVariable Integer itemId) {
        cartService.deleteCartItem(id, itemId);
        return ResponseEntity.noContent().build();
    }


}
