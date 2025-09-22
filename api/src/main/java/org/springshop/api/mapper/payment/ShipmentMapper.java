package org.springshop.api.mapper.payment;


import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.model.payment.Shipment;

public class ShipmentMapper {

    // Convierte de RequestDto -> Entidad
    public static Shipment toEntity(ShipmentRequestDto dto) {
        if (dto == null) return null;

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(dto.getTrackingNumber());
        shipment.setCarrier(dto.getCarrier());
        shipment.setStatus(dto.getStatus());
        return shipment;
    }

    // Convierte de Entidad -> ResponseDto
    public static ShipmentResponseDto toDto(Shipment entity) {
        if (entity == null) return null;

        ShipmentResponseDto dto = new ShipmentResponseDto();
        dto.setId(entity.getId());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setCarrier(entity.getCarrier());
        dto.setStatus(entity.getStatus());
        dto.setShippedAt(entity.getShippedAt());
        dto.setDeliveredAt(entity.getDeliveredAt());

        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }

        return dto;
    }
}

