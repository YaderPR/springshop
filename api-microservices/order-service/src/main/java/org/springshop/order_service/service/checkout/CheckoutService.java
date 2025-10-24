package org.springshop.order_service.service.checkout;
import org.springframework.stereotype.Service;
import org.springshop.order_service.client.CartClient;
import org.springshop.order_service.model.order.Order;
import org.springshop.order_service.service.order.OrderService;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.transaction.Transactional;

@Service
@Transactional 
public class CheckoutService {
    private final OrderService orderService;
    private final CartClient cartClient;
    public CheckoutService(OrderService orderService, CartClient cartClient) {
        this.orderService = orderService;
        this.cartClient = cartClient;
    }
    public String createCheckoutSession(Integer orderId, Integer cartId) throws StripeException {
        
        // 1. OBTENER DATOS DE LA ORDEN
        Order order = orderService.findOrderOrThrow(orderId);
        long amountInCents = (long) (order.getTotalAmount() * 100);
        
        // 2. DEFINIR LAS URLs DE REDIRECCIÓN
        // {CHECKOUT_SESSION_ID} es una variable de Stripe que se reemplazará automáticamente.
        String baseUrl = "http://localhost:8080"; 
        
        SessionCreateParams params = SessionCreateParams.builder()
                .setSuccessUrl(baseUrl + "/pago-exitoso?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/carrito")
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                // 3. AGREGAR LOS ITEMS (Usaremos el total de la orden para simplificar)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amountInCents)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Order #" + orderId)
                                        .build())
                                .build())
                        .build())
                
                // 4. METADATA: Guardar el orderId para recuperarlo más tarde
                .putMetadata("order_id", orderId.toString())
                .build();

        Session session = Session.create(params);
        cartClient.clearCart(cartId);
        // El backend devuelve la URL de Stripe
        return session.getUrl(); 
    }
}
