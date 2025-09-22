package org.springshop.api.mapper.payment;

import org.springshop.api.dto.payment.PaymentRequestDto;
import org.springshop.api.dto.payment.PaymentResponseDto;
import org.springshop.api.model.payment.Payment;

public class PaymentMapper {

    // Convierte de RequestDto -> Entidad
    public static Payment toEntity(PaymentRequestDto dto) {
        if (dto == null) return null;

        Payment payment = new Payment();
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setStatus(dto.getStatus());
        return payment;
    }

    // Convierte de Entidad -> ResponseDto
    public static PaymentResponseDto toDto(Payment entity) {
        if (entity == null) return null;

        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setMethod(entity.getMethod());
        dto.setStatus(entity.getStatus());
        dto.setTransactionId(entity.getTransactionId());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }

        return dto;
    }
}

