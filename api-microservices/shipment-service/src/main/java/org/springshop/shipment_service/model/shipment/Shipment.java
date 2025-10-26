package org.springshop.shipment_service.model.shipment;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String trackingNumber; // número de seguimiento de la empresa de envíos

    private String carrier; // DHL, FedEx, UPS, etc. (puede ser un enum)

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status; // CREATED, SHIPPED, DELIVERED, RETURNED
    @CreationTimestamp
    private LocalDateTime shippedAt;
    
    private LocalDateTime deliveredAt;

    @Column(name = "order_id")
    private Integer orderId;

}

