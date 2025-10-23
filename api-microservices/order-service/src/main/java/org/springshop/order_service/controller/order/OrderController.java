package org.springshop.order_service.controller.order;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.order_service.controller.exception.StockException;
import org.springshop.order_service.dto.checkout.CheckoutRequestDto;
import org.springshop.order_service.dto.order.OrderRequestDto;
import org.springshop.order_service.dto.order.OrderResponseDto;
import org.springshop.order_service.dto.payment.PaymentResponseDto;
import org.springshop.order_service.model.order.Order;
import org.springshop.order_service.service.checkout.CheckoutService;
import org.springshop.order_service.service.order.OrderService;
import org.springshop.order_service.client.PaymentClient;

import com.stripe.exception.StripeException;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentClient paymentClient;
    private final CheckoutService checkoutService;

    public OrderController(OrderService orderService, PaymentClient paymentClient, CheckoutService checkoutService) {
        this.orderService = orderService;
        this.paymentClient = paymentClient;
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createOrderAndStartCheckout(
            @RequestBody CheckoutRequestDto requestDto) {

        Integer cartId = requestDto.getCartId();
        Integer userId = requestDto.getUserId();
        Integer addressId = requestDto.getAddressId();

        try {
            if (cartId == null || userId == null || addressId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Faltan IDs de carrito, usuario o dirección."));
            }
            Order newOrder = orderService.createOrderFromCart(cartId, userId, addressId);
            String checkoutUrl = checkoutService.createCheckoutSession(newOrder.getId());

            // URL de redirección
            return ResponseEntity.ok(Map.of(
                    "checkoutUrl", checkoutUrl,
                    "orderId", newOrder.getId().toString()));

        } catch (StockException e) {
            // Stock agotado (409 Conflict)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "code", "INSUFFICIENT_STOCK"));
        } catch (EntityNotFoundException e) {
            // Entidad no encontrada (404 Not Found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (StripeException e) {
            // Error de pasarela de pago (502 Bad Gateway)
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Error al iniciar la sesión de Stripe: " + e.getMessage()));
        } catch (Exception e) {
            // Manejo de errores genéricos (500 Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al procesar la orden: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Integer id) {

        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Integer id,
            @RequestBody OrderRequestDto requestDto) {

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

    @GetMapping("/{orderId:\\d+}/payments")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByOrder(@PathVariable Integer orderId) {

        List<PaymentResponseDto> payments = paymentClient.getPaymentsByOrderId(orderId);
        
        return ResponseEntity.ok(payments);
    }
}