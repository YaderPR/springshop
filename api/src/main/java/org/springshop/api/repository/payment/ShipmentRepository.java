package org.springshop.api.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.payment.Shipment;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    
    /**
     * Encuentra todos los envíos asociados a un ID de orden específico.
     * Esto soporta el endpoint de relación GET /api/orders/{orderId}/shipments.
     */
    List<Shipment> findAllByOrderId(Integer orderId);
}