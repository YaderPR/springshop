package org.springshop.order_service.service.checkout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springshop.order_service.client.CartClient;
import org.springshop.order_service.model.order.Order;
import org.springshop.order_service.service.order.OrderService;
import org.springshop.order_service.controller.exception.ResourceNotFoundException;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CheckoutService {

    // Inicialización del Logger
    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final OrderService orderService;
    private final CartClient cartClient;
    private final String WEB_BASE_URL = "http://localhost:5173";

    public CheckoutService(OrderService orderService, CartClient cartClient) {
        this.orderService = orderService;
        this.cartClient = cartClient;
        log.info("CheckoutService inicializado con OrderService y CartClient.");
    }

    // 🔑 MÉTODOS ACTUALIZADO: Acepta un redirectUrl (que puede ser un Deep Link)
    public String createCheckoutSession(Integer orderId, Integer cartId, String clientRedirectUrl) throws StripeException {

        // --- 1. PRE-CONDICIONES Y OBTENCIÓN DE DATOS ---
        log.info("INICIO de createCheckoutSession: orderId={}, cartId={}, clientRedirectUrl={}",
                orderId, cartId, clientRedirectUrl);

        if (orderId == null) {
            log.error("El orderId es nulo. No se puede proceder con la sesión de checkout.");
            throw new IllegalArgumentException("El ID de la orden no puede ser nulo.");
        }

        Order order;
        try {
            // 1. OBTENER DATOS DE LA ORDEN
            order = orderService.findOrderOrThrow(orderId);
            log.info("Orden {} obtenida exitosamente. TotalAmount: {}", orderId, order.getTotalAmount());
        } catch (ResourceNotFoundException e) {
            log.error("Error: Orden con ID {} no encontrada.", orderId);
            throw new ResourceNotFoundException("Orden no encontrada con ID: " + orderId);
        }

        long amountInCents = (long) (order.getTotalAmount() * 100);
        log.debug("Monto total de la orden convertido a centavos: {} ({})", amountInCents, order.getTotalAmount());

        // --- 2. DEFINIR LAS URLs DE REDIRECCIÓN DINÁMICAMENTE ---
        String successRedirectUrl;
        String cancelRedirectUrl;

        if (StringUtils.hasText(clientRedirectUrl)) {
            // Usar Deep Link
            successRedirectUrl = clientRedirectUrl + "/success?orderId=" + orderId + "&session_id={CHECKOUT_SESSION_ID}";
            cancelRedirectUrl = clientRedirectUrl + "/cancel?orderId=" + orderId;
            log.info("Usando Deep Link. Success URL: {}, Cancel URL: {}", successRedirectUrl, cancelRedirectUrl);
        } else {
            // Usar URL web por defecto
            successRedirectUrl = WEB_BASE_URL + "/pago-exitoso?session_id={CHECKOUT_SESSION_ID}";
            cancelRedirectUrl = WEB_BASE_URL + "/carrito";
            log.info("Usando URL web por defecto. Success URL: {}, Cancel URL: {}", successRedirectUrl, cancelRedirectUrl);
        }

        // --- 3. CONSTRUCCIÓN DE LA SESIÓN DE STRIPE ---
        SessionCreateParams params = SessionCreateParams.builder()
                // 🔑 Usamos las URLs dinámicas
                .setSuccessUrl(successRedirectUrl)
                .setCancelUrl(cancelRedirectUrl)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                // 3. AGREGAR LOS ITEMS (Usando la orden como un único LineItem)
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

        log.debug("Parámetros de la sesión de Stripe construidos. Mode: PAYMENt, Items: 1, Metadata: order_id={}", orderId);

        Session session;
        try {
            // 5. CREAR SESIÓN DE STRIPE
            session = Session.create(params);
            log.info("Sesión de Stripe creada exitosamente. Session ID: {}", session.getId());
        } catch (StripeException e) {
            log.error("Error al crear la sesión de Stripe: {}", e.getMessage(), e);
            // Re-lanzar la excepción de Stripe para manejo de nivel superior
            throw e;
        }

        // --- 6. POST-PROCESAMIENTO: LIMPIAR CARRITO ---
        if (cartId != null) {
            try {
                // Limpiar el carrito solo si el ID es válido
                cartClient.clearCart(cartId);
                log.info("Carrito con ID {} limpiado exitosamente.", cartId);
            } catch (Exception e) {
                // Capturar cualquier excepción de la llamada al Microservicio de Carrito
                log.error("ADVERTENCIA: Fallo al limpiar el carrito con ID {}. La orden continuará. Error: {}", cartId, e.getMessage(), e);
                // NOTA: No re-lanzamos, ya que la sesión de pago ya se creó.
            }
        } else {
            log.warn("cartId es nulo. No se intentará limpiar ningún carrito.");
        }

        // --- 7. RETORNAR URL ---
        log.info("FIN de createCheckoutSession. Retornando URL: {}", session.getUrl());
        return session.getUrl();
    }
}