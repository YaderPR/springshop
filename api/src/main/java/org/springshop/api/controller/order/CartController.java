// Archivo: org.springshop.api.controller.order.CartController.java

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
import org.springshop.api.dto.order.CartRequestDto;
import org.springshop.api.dto.order.CartResponseDto;
import org.springshop.api.service.order.CartService;

import jakarta.persistence.EntityNotFoundException; // Usaremos esta excepción para el 404

@RestController
@RequestMapping("/api/carts")
public class CartController {
    
    private final CartService cartService;
    
    // NOTA: BASE_URL ya no es necesario si usamos el método created
    
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // -------------------- Carts (Recurso Principal) --------------------
    
    // ✅ 1. Obtener todos los carritos (GET /api/carts)
    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    // ✅ 2. Obtener un carrito por su ID (GET /api/carts/{id})
    @GetMapping("/{id:\\d+}") // Aseguramos que el ID sea numérico
    public ResponseEntity<CartResponseDto> getCartById(@PathVariable Integer id) {
        // Mejor práctica: Devolver 404 si no se encuentra. 
        // Ya que el servicio devuelve Optional<CartResponseDto>, lo manejamos aquí.
        // Opcional: Si usamos @ControllerAdvice, el servicio lanzaría EntityNotFoundException y Spring lo manejaría.
        return cartService.getCartById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + id));
    }

    // ✅ 3. Crear un carrito (POST /api/carts)
    @PostMapping
    public ResponseEntity<CartResponseDto> createCart(@RequestBody CartRequestDto dto) {
        CartResponseDto responseDto = cartService.createCart(dto);
        
        // Principio REST: Devolver 201 Created con el URI del nuevo recurso
        URI location = URI.create("/api/carts/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }

    // ✅ 4. Actualizar un carrito (PUT /api/carts/{id})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<CartResponseDto> updateCart(@PathVariable Integer id, @RequestBody CartRequestDto requestDto) {
        // 200 OK con el recurso actualizado
        return ResponseEntity.ok(cartService.updateCart(id, requestDto));
    }

    // ✅ 5. Eliminar un carrito (DELETE /api/carts/{id})
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCart(@PathVariable Integer id) {
        cartService.deleteCart(id);
        // Principio REST: Devolver 204 No Content para una eliminación exitosa
        return ResponseEntity.noContent().build();
    }
}