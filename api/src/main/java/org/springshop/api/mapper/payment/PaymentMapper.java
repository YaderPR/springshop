package org.springshop.api.mapper.payment;

import org.springshop.api.dto.payment.PaymentRequestDto;
import org.springshop.api.dto.payment.PaymentResponseDto;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Payment;
import org.springshop.api.model.payment.PaymentStatus;

public class PaymentMapper {
    public static Payment toEntity(PaymentRequestDto dto, Order order, PaymentStatus status) {
        if (dto == null) return null;
        
        Payment payment = new Payment();
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setCurrency(dto.getCurrency());
        
        payment.setStatus(status); 
        payment.setOrder(order);
        
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

        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId()); 
        }

        return dto;
    }

    public static void updatePayment(Payment existing, PaymentRequestDto dto, Order order) {
        if (existing == null || dto == null) return;
        
        existing.setAmount(dto.getAmount());
        existing.setMethod(dto.getMethod());
        existing.setCurrency(dto.getCurrency()); 
        
        existing.setOrder(order);
    }
}