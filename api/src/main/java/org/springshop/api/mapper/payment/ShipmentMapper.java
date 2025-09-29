package org.springshop.api.mapper.payment;

import org.springshop.api.dto.payment.ShipmentRequestDto;
import org.springshop.api.dto.payment.ShipmentResponseDto;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Shipment;

public class ShipmentMapper {

    public static Shipment toEntity(ShipmentRequestDto dto, Order order) {
        if (dto == null)
            return null;
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(dto.getTrackingNumber());
        shipment.setCarrier(dto.getCarrier());
        shipment.setStatus(dto.getStatus());
        shipment.setOrder(order);
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

        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }

        return dto;
    }

    public static void updateShipment(Shipment existing, ShipmentRequestDto requestDto, Order order) {
        if (existing == null || requestDto == null)
            return;

        existing.setCarrier(requestDto.getCarrier());
        existing.setTrackingNumber(requestDto.getTrackingNumber());
        existing.setStatus(requestDto.getStatus());
        existing.setShippedAt(requestDto.getShippedAt());
        existing.setDeliveredAt(requestDto.getDeliveredAt());
        existing.setOrder(order);
    }
}