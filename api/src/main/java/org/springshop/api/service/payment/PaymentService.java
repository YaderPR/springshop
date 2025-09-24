package org.springshop.api.service.payment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springshop.api.dto.payment.PaymentRequestDto;
import org.springshop.api.dto.payment.PaymentResponseDto;
import org.springshop.api.mapper.payment.PaymentMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Payment;
import org.springshop.api.repository.order.OrderRepository;
import org.springshop.api.repository.payment.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentService {
    PaymentRepository paymentRepository;
    OrderRepository orderRepository;
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {
        Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + requestDto.getOrderId()));
        Payment savedPayment = paymentRepository.save(PaymentMapper.toEntity(requestDto, order));
        return PaymentMapper.toDto(savedPayment);
    }
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentMapper::toDto).collect(Collectors.toList());
    }
    public PaymentResponseDto getPaymentById(Integer id) {
        return PaymentMapper.toDto(paymentRepository.findById(id).orElseGet(null));
    }
    public PaymentResponseDto updatePaymentById(Integer id, PaymentRequestDto requestDto) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        Order order = orderRepository.findById(payment.getOrder().getId()).orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + payment.getOrder().getId()));
        PaymentMapper.updateEntity(payment, requestDto, order);
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentMapper.toDto(savedPayment);
    }
    public void deletePaymentById(Integer id) {
        if(!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }
}
