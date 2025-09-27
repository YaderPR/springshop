package org.springshop.api.controller.order;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus; // Nuevo import para 201 Created
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt; // Nuevo import para seguridad
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.order.OrderRequestDto;
import org.springshop.api.dto.order.OrderResponseDto;
import org.springshop.api.dto.payment.PaymentRequestDto; // Nuevo import
import org.springshop.api.dto.payment.PaymentResponseDto; // Nuevo import
import org.springshop.api.service.order.OrderService;
import org.springshop.api.service.payment.PaymentService; // ✅ Nuevo import del servicio de pago

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid; // Nuevo import para validar DTOs

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    private final PaymentService paymentService; // ✅ Inyección del servicio de pago
    
    // Constructor actualizado para inyección
    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    // -------------------- ÓRDENES (Recurso Principal) --------------------
    
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        // NOTA: Se recomienda añadir seguridad y paginación aquí.
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Integer id) {
        // En un sistema seguro, se debe verificar que el usuario autenticado sea el dueño de la orden.
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }
    
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto) {
        OrderResponseDto responseDto = orderService.createOrder(requestDto);
        URI location = URI.create("/api/orders/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }
    
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Integer id, @RequestBody OrderRequestDto requestDto) {
        OrderResponseDto responseDto = orderService.updateOrder(id, requestDto); 
        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id); 
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id:\\d+}/total")
    public ResponseEntity<Double> getOrderTotal(@PathVariable Integer id) {
        double total = orderService.calculateOrderTotals(id);
        return ResponseEntity.ok(total);
    }

    // -------------------- PAGOS (Recurso Anidado) --------------------
    
    /**
     * ✅ NUEVO ENDPOINT: Procesa un pago para una Orden específica.
     * Mapea a: POST /api/orders/{orderId}/payments
     * Activa el flujo del Payment Gateway (Stripe Sandbox).
     */
    @PostMapping("/{orderId:\\d+}/payments")
    public ResponseEntity<PaymentResponseDto> processOrderPayment(
        @PathVariable Integer orderId,
        @Valid @RequestBody PaymentRequestDto requestDto,
        // Usamos el token JWT para garantizar que solo el dueño pague la orden
        @AuthenticationPrincipal Jwt jwt) { 
        
        // 1. Asignar el orderId de la ruta al DTO
        requestDto.setOrderId(orderId);
        
        // 2. Aquí debería ir la lógica para asegurar que el ID de la ORDEN
        //    pertenece al 'sub' (ID de usuario) del token JWT.
        
        // 3. Procesar el pago (activa Stripe Sandbox)
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);

        // 4. Retornar 201 Created para indicar que el recurso de Pago se ha creado
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
    // ✅ Endpoint para ver los pagos de una orden
    @GetMapping("/{orderId:\\d+}/payments")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByOrder(@PathVariable Integer orderId) {
        List<PaymentResponseDto> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }
}