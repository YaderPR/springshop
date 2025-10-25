package org.springshop.webhook_service.service.webhook;

import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import org.springshop.webhook_service.model.order.Order;
import org.springshop.webhook_service.model.order.OrderStatus;
import org.springshop.webhook_service.client.PaymentClient;
import org.springshop.webhook_service.client.ShipmentClient;
import org.springshop.webhook_service.dto.order.OrderUpdateStatus;
import org.springshop.webhook_service.dto.payment.PaymentRequest;
import org.springshop.webhook_service.dto.payment.PaymentStatus;
import org.springshop.webhook_service.dto.shipment.ShipmentRequest;
import org.springshop.webhook_service.dto.shipment.ShipmentStatus;
import org.springshop.webhook_service.client.OrderClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class WebhookService {

    private final OrderClient orderClient;
    private final PaymentClient paymentClient;
    private final ShipmentClient shipmentClient;

    public WebhookService(OrderClient orderClient, PaymentClient paymentClient, ShipmentClient shipmentClient) {
        this.orderClient = orderClient;
        this.paymentClient = paymentClient;
        this.shipmentClient = shipmentClient;
    }

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
        Order order = findOrderOrThrow(orderId);
        
        // 2. Prevenir el reprocesamiento
        if (order.getStatus() == OrderStatus.PAID) {
            System.out.println("Orden " + orderId + " ya estaba pagada. Ignorando evento duplicado.");
            return; 
        }

        // 3. Crear y Persistir la entidad Payment
        PaymentRequest payment = new PaymentRequest();
        // El PaymentIntent.id o el Charge.id es el transactionId más fiable.
        // Session.getPaymentIntent() devuelve el ID del PaymentIntent.
        payment.setTransactionId(session.getPaymentIntent()); 
        payment.setAmount(session.getAmountTotal() / 100.0); // Convertir centavos a decimal
        payment.setCurrency(session.getCurrency());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setOrderId(order.getId());
        payment.setMethod("stripe checkout"); 
        // Otros campos como 'method', 'createdAt', etc.

        paymentClient.createPayment(payment);
        // 4. Actualizar el estado de la Orden
        //order.setStatus(OrderStatus.PAID);
        orderClient.updateOrderStatus(orderId, new OrderUpdateStatus(OrderStatus.PAID));
        shipmentClient.createShipment(new ShipmentRequest("No assign", "No assign", order.getId(), ShipmentStatus.CREATED, LocalDateTime.now(), LocalDateTime.now()));
        System.out.println("Pago exitoso registrado para la orden: " + orderId);
    }
    public Order findOrderOrThrow(Integer orderId) {
        return orderClient.findById(orderId).orElseThrow(() -> new HttpClientErrorException(HttpStatusCode.valueOf(404)));
    }
}
