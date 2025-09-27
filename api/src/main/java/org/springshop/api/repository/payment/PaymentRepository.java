package org.springshop.api.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.payment.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    /**
     * Encuentra todos los pagos asociados a un ID de orden específico.
     * Esto soporta el endpoint de relación GET /api/orders/{orderId}/payments.
     * * Asumimos que la entidad Payment tiene una relación con Order.
     */
    List<Payment> findAllByOrderId(Integer orderId);
}