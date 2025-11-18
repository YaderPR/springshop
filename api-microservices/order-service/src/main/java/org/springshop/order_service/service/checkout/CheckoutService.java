package org.springshop.order_service.service.checkout;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    // URL web de fallback (si la app no proporciona una URL de redirección)
    private final String WEB_BASE_URL = "http://localhost:5173";
    public CheckoutService(OrderService orderService, CartClient cartClient) {
        this.orderService = orderService;
        this.cartClient = cartClient;
    }
    // 🔑 MÉTODOS ACTUALIZADO: Acepta un redirectUrl (que puede ser un Deep Link)
  public String createCheckoutSession(Integer orderId, Integer cartId, String clientRedirectUrl) throws StripeException {
    
    // 1. OBTENER DATOS DE LA ORDEN
    Order order = orderService.findOrderOrThrow(orderId);
    long amountInCents = (long) (order.getTotalAmount() * 100);
    
    // 2. DEFINIR LAS URLs DE REDIRECCIÓN DINÁMICAMENTE
        // Usamos la URL del cliente (Deep Link) si está presente, de lo contrario, la URL web por defecto.
        String successRedirectUrl;
        String cancelRedirectUrl;

        if (StringUtils.hasText(clientRedirectUrl)) {
            // Si es un Deep Link, no necesita el baseUrl, y ya debe incluir el esquema (ej: springshop://)
            // Añadimos el orderId y el session_id para que la app sepa qué pasó.
            successRedirectUrl = clientRedirectUrl + "/success?orderId=" + orderId + "&session_id={CHECKOUT_SESSION_ID}";
            cancelRedirectUrl = clientRedirectUrl + "/cancel?orderId=" + orderId;
        } else {
            // Usamos la URL web por defecto
            successRedirectUrl = WEB_BASE_URL + "/pago-exitoso?session_id={CHECKOUT_SESSION_ID}";
            cancelRedirectUrl = WEB_BASE_URL + "/carrito";
        }
    
    SessionCreateParams params = SessionCreateParams.builder()
            // 🔑 Usamos las URLs dinámicas
        .setSuccessUrl(successRedirectUrl) 
        .setCancelUrl(cancelRedirectUrl)
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
        // 3. AGREGAR LOS ITEMS 
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
        
        // 4. METADATA
        .putMetadata("order_id", orderId.toString())
        .build();

    Session session = Session.create(params);
    cartClient.clearCart(cartId);
    // El backend devuelve la URL de Stripe
    return session.getUrl(); 
  }
}
