package org.springshop.shipment_service.repository.shipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.shipment_service.model.shipment.Shipment;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    
    List<Shipment> findAllByOrderId(Integer orderId);
}