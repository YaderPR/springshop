package org.springshop.api.model.payment;

public enum ShipmentStatus {
    CREATED,    // cuando se genera el envío
    SHIPPED,    // cuando sale de bodega
    IN_TRANSIT, // opcional: en camino
    DELIVERED,  // entregado al cliente
    RETURNED    // devuelto por el cliente
}

