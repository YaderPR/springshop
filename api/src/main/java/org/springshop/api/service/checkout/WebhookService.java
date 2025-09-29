package org.springshop.api.service.checkout;

import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.order.OrderStatus;
import org.springshop.api.model.payment.Payment;
import org.springshop.api.model.payment.PaymentStatus;
import org.springshop.api.repository.payment.PaymentRepository;
import org.springshop.api.service.order.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookService {

    private final OrderService orderService;
    private final PaymentRepository paymentRepository;

    public WebhookService(OrderService orderService, PaymentRepository paymentRepository) {
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
    }

    // @Transactional para asegurar que la orden y el pago se actualicen atómicamente
    @Transactional
    public void processEvent(Event event) {
        
        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            // Otros eventos importantes:
            // case "payment_intent.succeeded": // Usado para flujos más complejos
            // case "payment_intent.payment_failed":
            // case "charge.refunded": // Para registrar reembolsos
            // ...
            default:
                // Manejar otros tipos de eventos si es necesario, o ignorarlos.
                System.out.println("Evento de Stripe no manejado: " + event.getType());
        }
    }
    
    private void handleCheckoutSessionCompleted(Event event) {
        // Deserializar el objeto Session del evento
        StripeObject object = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(object instanceof Session session)) {
            System.err.println("Objeto de sesión no encontrado o inválido.");
            return;
        }

        // 1. Obtener la orden de tu sistema usando el ID guardado en la metadata
        String orderIdStr = session.getMetadata().get("order_id");
        if (orderIdStr == null) {
            System.err.println("Falta order_id en la metadata de la sesión: " + session.getId());
            return;
        }
        Integer orderId = Integer.parseInt(orderIdStr);
        Order order = orderService.findOrderOrThrow(orderId);
        
        // 2. Prevenir el reprocesamiento
        if (order.getStatus() == OrderStatus.PAID) {
            System.out.println("Orden " + orderId + " ya estaba pagada. Ignorando evento duplicado.");
            return; 
        }

        // 3. Crear y Persistir la entidad Payment
        Payment payment = new Payment();
        // El PaymentIntent.id o el Charge.id es el transactionId más fiable.
        // Session.getPaymentIntent() devuelve el ID del PaymentIntent.
        payment.setTransactionId(session.getPaymentIntent()); 
        payment.setAmount(session.getAmountTotal() / 100.0); // Convertir centavos a decimal
        payment.setCurrency(session.getCurrency());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setOrder(order); 
        // Otros campos como 'method', 'createdAt', etc.

        paymentRepository.save(payment);
        
        // 4. Actualizar el estado de la Orden
        order.setStatus(OrderStatus.PAID);
        // orderRepository.save(order) // OrderService.findOrderOrThrow ya da un objeto persistido que se guardará con @Transactional.

        System.out.println("Pago exitoso registrado para la orden: " + orderId);
    }
}
