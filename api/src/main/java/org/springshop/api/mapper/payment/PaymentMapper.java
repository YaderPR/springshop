package org.springshop.api.mapper.payment;

import org.springshop.api.dto.payment.PaymentRequestDto;
import org.springshop.api.dto.payment.PaymentResponseDto;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Payment;
import org.springshop.api.model.payment.PaymentStatus; // Asegurar la importación del enum

public class PaymentMapper {

    /**
     * Convierte de RequestDto -> Entidad Payment.
     * Los campos de estado y transactionId son establecidos por el servicio/gateway, NO por el request.
     */
    public static Payment toEntity(PaymentRequestDto dto, Order order) {
        if (dto == null) return null;
        
        Payment payment = new Payment();
        
        // Asignación de campos del RequestDto a la Entidad
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setCurrency(dto.getCurrency()); // ✅ CAMBIO: Incluir Currency
        
        // ✅ CORRECCIÓN: El estado inicial de un nuevo pago es PENDING
        payment.setStatus(PaymentStatus.PENDING); 
        
        // La Order es inyectada por el servicio
        payment.setOrder(order);
        
        return payment;
    }

    /**
     * Convierte de Entidad -> ResponseDto.
     */
    public static PaymentResponseDto toResponseDto(Payment entity) {
        if (entity == null) return null;

        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency()); // ✅ CAMBIO: Incluir Currency
        dto.setMethod(entity.getMethod());
        dto.setStatus(entity.getStatus());
        dto.setTransactionId(entity.getTransactionId());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId()); 
        }

        return dto;
    }
    
    /**
     * Actualiza una entidad Payment existente con los datos del RequestDto.
     */
    public static void updatePayment(Payment existing, PaymentRequestDto dto, Order order) {
        if (existing == null || dto == null) return;
        
        // Los campos sensibles como currency y orderId solo se actualizan si son diferentes
        existing.setAmount(dto.getAmount());
        existing.setMethod(dto.getMethod());
        existing.setCurrency(dto.getCurrency()); // ✅ CAMBIO: Incluir Currency
        
        // NOTA: El estado (SUCCESS/FAILED) y transactionId NUNCA deben actualizarse desde un DTO
        // de solicitud. Asumimos que esta actualización es para metadatos.
        
        existing.setOrder(order);
    }
}