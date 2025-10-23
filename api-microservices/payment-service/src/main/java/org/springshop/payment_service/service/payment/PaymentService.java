package org.springshop.payment_service.service.payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.payment_service.client.OrderClient;
import org.springshop.payment_service.dto.payment.PaymentRequestDto;
import org.springshop.payment_service.dto.payment.PaymentResponseDto;
import org.springshop.payment_service.mapper.payment.PaymentMapper;
import org.springshop.payment_service.model.order.Order;
import org.springshop.payment_service.model.payment.Payment;
import org.springshop.payment_service.model.payment.PaymentStatus;
import org.springshop.payment_service.repository.payment.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional 
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    
    // Constructor actualizado
    public PaymentService(PaymentRepository paymentRepository, OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.orderClient = orderClient;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream()
            .map(PaymentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PaymentResponseDto> getPaymentById(Integer id) {
        return paymentRepository.findById(id)
            .map(PaymentMapper::toResponseDto); 
    }
    
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByOrderId(Integer orderId) {
        return paymentRepository.findAllByOrderId(orderId).stream()
            .map(PaymentMapper::toResponseDto)
            .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {
        Order order = orderClient.findById(requestDto.getOrderId()).orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + requestDto.getOrderId()));

        Payment newPayment = PaymentMapper.toEntity(requestDto, order.getId(), PaymentStatus.PENDING);
        Payment savedPayment = paymentRepository.save(newPayment);

        return PaymentMapper.toResponseDto(savedPayment);
    }

    public PaymentResponseDto updatePayment(Integer id, PaymentRequestDto requestDto) {
        Payment existingPayment = findPaymentOrThrow(id);
        Order order = findOrderOrThrow(requestDto.getOrderId());
        
        PaymentMapper.updatePayment(existingPayment, requestDto, order.getId());
        
        Payment savedPayment = paymentRepository.save(existingPayment);
        return PaymentMapper.toResponseDto(savedPayment);
    }

    public void deletePayment(Integer id) {
        Payment payment = findPaymentOrThrow(id);
        paymentRepository.delete(payment);
    }

    public Payment findPaymentOrThrow(Integer paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));
    }
    
    private Order findOrderOrThrow(Integer orderId) {
        return orderClient.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}