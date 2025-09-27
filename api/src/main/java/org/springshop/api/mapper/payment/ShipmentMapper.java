package org.springshop.api.mapper.payment;


import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Shipment;

public class ShipmentMapper {

    /**
     * Convierte de RequestDto -> Entidad Shipment.
     */
    public static Shipment toEntity(ShipmentRequestDto dto, Order order) {
        if (dto == null) return null;
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(dto.getTrackingNumber());
        shipment.setCarrier(dto.getCarrier());
        shipment.setStatus(dto.getStatus());
        shipment.setOrder(order);
        return shipment;
    }

    /**
     * Convierte de Entidad -> ResponseDto.
     */
    // CONVENCIÓN: Renombramos toDto a toResponseDto
    public static ShipmentResponseDto toResponseDto(Shipment entity) {
        if (entity == null) return null;

        ShipmentResponseDto dto = new ShipmentResponseDto();
        dto.setId(entity.getId());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setCarrier(entity.getCarrier());
        dto.setStatus(entity.getStatus());
        dto.setShippedAt(entity.getShippedAt());
        dto.setDeliveredAt(entity.getDeliveredAt());

        if (entity.getOrder() != null) {
            // Ya incluye el orderId, lo cual es correcto.
            dto.setOrderId(entity.getOrder().getId());
        }

        return dto;
    }
    
    /**
     * Actualiza una entidad Shipment existente con los datos del RequestDto.
     */
    // CONVENCIÓN: Renombramos updateEntity a updateShipment
    public static void updateShipment(Shipment existing, ShipmentRequestDto requestDto, Order order) {
        if(existing == null || requestDto == null) return;
        
        // Actualizamos los campos mutables del envío
        existing.setCarrier(requestDto.getCarrier());
        existing.setTrackingNumber(requestDto.getTrackingNumber());
        existing.setStatus(requestDto.getStatus());
        existing.setShippedAt(requestDto.getShippedAt());
        existing.setDeliveredAt(requestDto.getDeliveredAt());
        
        // Actualizamos la relación con la Order (basado en la lógica del servicio)
        existing.setOrder(order);
    }
}