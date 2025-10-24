package org.springshop.shipment_service.mapper.shipment;

import org.springshop.shipment_service.dto.shipment.ShipmentRequestDto;
import org.springshop.shipment_service.dto.shipment.ShipmentResponseDto;
import org.springshop.shipment_service.model.shipment.Shipment;

public class ShipmentMapper {

    public static Shipment toEntity(ShipmentRequestDto dto, Integer orderId) {
        if (dto == null)
            return null;
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(dto.getTrackingNumber());
        shipment.setCarrier(dto.getCarrier());
        shipment.setStatus(dto.getStatus());
        shipment.setOrderId(orderId);
        return shipment;
    }

    public static ShipmentResponseDto toResponseDto(Shipment entity) {
        if (entity == null)
            return null;

        ShipmentResponseDto dto = new ShipmentResponseDto();
        dto.setId(entity.getId());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setCarrier(entity.getCarrier());
        dto.setStatus(entity.getStatus());
        dto.setShippedAt(entity.getShippedAt());
        dto.setDeliveredAt(entity.getDeliveredAt());

        if (entity.getOrderId() != null) {
            dto.setOrderId(entity.getOrderId());
        }

        return dto;
    }

    public static void updateShipment(Shipment existing, ShipmentRequestDto requestDto, Integer orderId) {
        if (existing == null || requestDto == null)
            return;

        existing.setCarrier(requestDto.getCarrier());
        existing.setTrackingNumber(requestDto.getTrackingNumber());
        existing.setStatus(requestDto.getStatus());
        existing.setShippedAt(requestDto.getShippedAt());
        existing.setDeliveredAt(requestDto.getDeliveredAt());
        existing.setOrderId(orderId);
    }
}