package org.springshop.api.service.checkout;
import org.springframework.stereotype.Service;
import org.springshop.api.model.order.Order;
import org.springshop.api.service.order.OrderService;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.transaction.Transactional;

@Service
@Transactional 
public class CheckoutService {
    private final OrderService orderService;
    public CheckoutService(OrderService orderService) {
        this.orderService = orderService;
    }
    public String createCheckoutSession(Integer orderId) throws StripeException {
        
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
        
        // El backend devuelve la URL de Stripe
        return session.getUrl(); 
    }
}
