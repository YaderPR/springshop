package org.springshop.payment_service.mapper.payment;

import org.springshop.payment_service.dto.payment.PaymentRequestDto;
import org.springshop.payment_service.dto.payment.PaymentResponseDto;
import org.springshop.payment_service.model.payment.Payment;
import org.springshop.payment_service.model.payment.PaymentStatus;

public class PaymentMapper {
    public static Payment toEntity(PaymentRequestDto dto, Integer orderId, PaymentStatus status) {
        if (dto == null) return null;
        
        Payment payment = new Payment();
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setCurrency(dto.getCurrency());
        
        payment.setStatus(status); 
        payment.setOrderId(orderId);
        
        return payment;
    }

    public static PaymentResponseDto toResponseDto(Payment entity) {
        if (entity == null) return null;

        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setMethod(entity.getMethod());
        dto.setStatus(entity.getStatus());
        dto.setTransactionId(entity.getTransactionId());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getOrderId() != null) {
            dto.setOrderId(entity.getOrderId()); 
        }

        return dto;
    }

    public static void updatePayment(Payment existing, PaymentRequestDto dto, Integer orderId) {
        if (existing == null || dto == null) return;
        
        existing.setAmount(dto.getAmount());
        existing.setMethod(dto.getMethod());
        existing.setCurrency(dto.getCurrency()); 
        
        existing.setOrderId(orderId);
    }
}