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
import org.springshop.cart_service.dto.cart.CartRequestDto;
import org.springshop.cart_service.dto.cart.CartResponseDto;
import org.springshop.cart_service.service.cart.CartService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getAllCarts() {

        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CartResponseDto> getCartById(@PathVariable Integer id) {

        return cartService.getCartById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<CartResponseDto> createCart(@RequestBody CartRequestDto dto) {

        CartResponseDto responseDto = cartService.createCart(dto);
        URI location = URI.create("/api/carts/" + responseDto.getId());

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<CartResponseDto> updateCart(@PathVariable Integer id,
            @RequestBody CartRequestDto requestDto) {

        return ResponseEntity.ok(cartService.updateCart(id, requestDto));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCart(@PathVariable Integer id) {

        cartService.deleteCart(id);
        
        return ResponseEntity.noContent().build();
    }
}