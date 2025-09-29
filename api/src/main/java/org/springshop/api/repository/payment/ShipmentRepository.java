package org.springshop.api.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.payment.Shipment;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    
    List<Shipment> findAllByOrderId(Integer orderId);
}