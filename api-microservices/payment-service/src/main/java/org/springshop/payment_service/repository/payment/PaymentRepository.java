package org.springshop.payment_service.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.payment_service.model.payment.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    List<Payment> findAllByOrderId(Integer orderId);
}