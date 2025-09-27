// Archivo: org.springshop.api.controller.order.OrderItemController.java (NUEVO)

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
import org.springshop.api.dto.order.OrderItemRequestDto;
import org.springshop.api.dto.order.OrderItemResponseDto;
import org.springshop.api.service.order.OrderItemService;

@RestController
@RequestMapping("/api/orders/{orderId:\\d+}/items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // -------------------- ITEMS DE ÓRDENES (Sub-recurso) --------------------

    // ✅ Obtener todos los ítems de una orden específica
    // GET /api/orders/{orderId}/items
    @GetMapping
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItems(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }
    
    // ✅ Obtener un ítem específico de una orden (Ruta anidada para consistencia)
    // GET /api/orders/{orderId}/items/{itemId}
    @GetMapping("/{itemId:\\d+}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable Integer orderId, @PathVariable Integer itemId) {
        // El servicio verifica la existencia y la pertenencia (aunque lo busquemos por itemId)
        return ResponseEntity.ok(orderItemService.getOrderItemById(itemId));
    }

    // ✅ Crear un nuevo ítem de orden (incluso si es inusual)
    // POST /api/orders/{orderId}/items
    @PostMapping
    public ResponseEntity<OrderItemResponseDto> createOrderItem(@PathVariable Integer orderId, @RequestBody OrderItemRequestDto requestDto) {
        OrderItemResponseDto responseDto = orderItemService.createOrderItem(orderId, requestDto);
        
        // Principio REST: 201 Created con la URI
        URI location = URI.create("/api/orders/" + orderId + "/items/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }
    
    // ✅ Actualizar un ítem de orden
    // PUT /api/orders/{orderId}/items/{itemId}
    @PutMapping("/{itemId:\\d+}")
    public ResponseEntity<OrderItemResponseDto> updateOrderItem(
            @PathVariable Integer orderId, 
            @PathVariable Integer itemId, 
            @RequestBody OrderItemRequestDto requestDto) {
        
        OrderItemResponseDto responseDto = orderItemService.updateOrderItem(orderId, itemId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // ✅ Eliminar un ítem de orden
    // DELETE /api/orders/{orderId}/items/{itemId}
    @DeleteMapping("/{itemId:\\d+}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Integer orderId, @PathVariable Integer itemId) {
        orderItemService.deleteOrderItem(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}