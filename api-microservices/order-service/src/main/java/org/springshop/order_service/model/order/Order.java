package org.springshop.order_service.model.order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    @OneToMany(mappedBy = "order")
    private Set<OrderItem> items = new HashSet<>();
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Column(name = "address_id", nullable = false)
    private Integer addressId;
    @Column(name = "shipment_id", nullable = false)
    private Integer shipmentId;
    @CreationTimestamp
    private LocalDateTime createAt;
    @UpdateTimestamp
    private LocalDateTime updateAt;

}
