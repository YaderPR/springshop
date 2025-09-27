// Archivo: org.springshop.api.controller.order.CartItemController.java (NUEVO)

package org.springshop.api.controller.order;

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
import org.springshop.api.dto.order.CartItemCreateRequestDto;
import org.springshop.api.dto.order.CartItemResponseDto;
import org.springshop.api.dto.order.CartItemUpdateRequestDto;
import org.springshop.api.service.order.CartItemService;
import org.springshop.api.service.order.CartService; // Necesario para calcular totales

@RestController
@RequestMapping("/api/carts/{cartId:\\d+}/items")
public class CartItemController {
    
    private final CartItemService cartItemService;
    private final CartService cartService; // Para la operación de calcular el total

    public CartItemController(CartItemService cartItemService, CartService cartService) {
        this.cartItemService = cartItemService;
        this.cartService = cartService;
    }

    // -------------------- Items (Sub-recurso) --------------------
    
    // ✅ 1. Obtener todos los ítems de un carrito (GET /api/carts/{cartId}/items)
    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> getCartItems(@PathVariable Integer cartId) {
        return ResponseEntity.ok(cartItemService.getCartItemsByCartId(cartId));
    }
    
    // ✅ 2. Añadir/Actualizar cantidad de un ítem (POST /api/carts/{cartId}/items)
    // Usamos POST aquí para la acción de "añadir" (que el servicio maneja como upsert)
    @PostMapping
    public ResponseEntity<CartItemResponseDto> addItemToCart(@PathVariable Integer cartId, @RequestBody CartItemCreateRequestDto requestDto) {
        
        // Delega al servicio la lógica inteligente de añadir o actualizar la cantidad
        CartItemResponseDto responseDto = cartItemService.addItemOrUpdateQuantity(cartId, requestDto);

        // Principio REST: Devolver 201 Created con el URI del nuevo/actualizado sub-recurso
        URI location = URI.create("/api/carts/" + cartId + "/items/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }
    
    // ✅ 3. Actualizar la cantidad de un ítem existente (PUT /api/carts/{cartId}/items/{itemId})
    @PutMapping("/{itemId:\\d+}")
    public ResponseEntity<CartItemResponseDto> updateCartItem(
            @PathVariable Integer cartId, 
            @PathVariable Integer itemId, 
            @RequestBody CartItemUpdateRequestDto requestDto) {
        
        // Utiliza el método de actualización que reemplaza el valor de cantidad
        CartItemResponseDto responseDto = cartItemService.updateCartItem(cartId, itemId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    // ✅ 4. Eliminar un ítem (DELETE /api/carts/{cartId}/items/{itemId})
    @DeleteMapping("/{itemId:\\d+}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Integer cartId, @PathVariable Integer itemId) {
        cartItemService.deleteCartItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }
    
    // -------------------- Endpoint Adicional Sugerido --------------------
    
    // ✅ 5. Obtener el total del carrito (GET /api/carts/{cartId}/total)
    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotal(@PathVariable Integer cartId) {
        double total = cartService.calculateCartTotals(cartId);
        return ResponseEntity.ok(total);
    }
}