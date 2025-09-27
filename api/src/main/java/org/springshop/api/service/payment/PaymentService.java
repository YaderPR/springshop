package org.springshop.api.service.payment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springshop.api.controller.exception.PaymentFailedException;
import org.springshop.api.dto.payment.PaymentRequestDto;
import org.springshop.api.dto.payment.PaymentResponseDto;
import org.springshop.api.mapper.payment.PaymentMapper;
import org.springshop.api.model.order.Order;
import org.springshop.api.model.payment.Payment;
import org.springshop.api.model.payment.PaymentStatus; // Importar el estado de pago
import org.springshop.api.repository.order.OrderRepository;
import org.springshop.api.repository.payment.PaymentRepository;

// Importaciones del Gateway
import org.springshop.api.gateway.PaymentGateway; // Nueva importación
import org.springshop.api.gateway.dto.PaymentChargeRequest;
import org.springshop.api.gateway.dto.PaymentGatewayResponse;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional 
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway; // ✅ Inyección del Gateway
    
    // Constructor actualizado
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway; // Inicialización
    }

    // -------------------- LECTURA (READ) Y BÚSQUEDA (sin cambios) --------------------
    
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

    // -------------------- LÓGICA DE PAGO (CORE) --------------------

    /**
     * Procesa un pago real a través del Payment Gateway (Stripe Sandbox en perfil 'dev').
     * @param requestDto Debe contener el orderId, el monto, y el token de pago seguro.
     */
    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {
        // 1. Validar y obtener la Orden
        Order order = findOrderOrThrow(requestDto.getOrderId());
        
        // 2. Preparar la solicitud para el Gateway
        PaymentChargeRequest chargeRequest = new PaymentChargeRequest();
        // Asumiendo que PaymentRequestDto ahora tiene getPaymentToken() y getCurrency()
        chargeRequest.setToken(requestDto.getPaymentToken()); 
        chargeRequest.setAmount(requestDto.getAmount());
        chargeRequest.setCurrency(requestDto.getCurrency() != null ? requestDto.getCurrency() : "USD"); // Valor por defecto

        // 3. Procesar el cargo a través de la pasarela (StripeSandboxGateway inyectado)
        PaymentGatewayResponse gatewayResponse = paymentGateway.charge(chargeRequest);

        // 4. Analizar el resultado del Gateway y guardar el pago
        Payment payment = PaymentMapper.toEntity(requestDto, order);
        payment.setTransactionId(gatewayResponse.getTransactionId());
        
        if (gatewayResponse.isSuccess()) {
            // El pago fue exitoso
            payment.setStatus(PaymentStatus.SUCCESS);
            // ✅ Lógica de Negocio Crucial: Aquí se actualizaría la ORDEN a un estado de 'PROCESAMIENTO'
            // order.setStatus(OrderStatus.PROCESSING); 
            // orderRepository.save(order);
        } else {
            // El pago falló (tarjeta rechazada, etc.)
            payment.setStatus(PaymentStatus.FAILED);
            // Lanzamos una excepción para que el frontend pueda manejar el 400 Bad Request
            // La transacción de Spring hará rollback si es necesario.
            paymentRepository.save(payment); // Guardamos el registro de pago fallido
            throw new PaymentFailedException("Payment failed. Reason: " + gatewayResponse.getMessage()); 
        }

        // 5. Guardar el pago y devolver la respuesta
        Payment savedPayment = paymentRepository.save(payment);
        
        return PaymentMapper.toResponseDto(savedPayment);
    }

    // -------------------- OTROS CRUD (sin cambios) --------------------
    
    public PaymentResponseDto updatePayment(Integer id, PaymentRequestDto requestDto) {
        Payment existingPayment = findPaymentOrThrow(id);
        Order order = findOrderOrThrow(requestDto.getOrderId());
        
        PaymentMapper.updatePayment(existingPayment, requestDto, order);
        
        Payment savedPayment = paymentRepository.save(existingPayment);
        return PaymentMapper.toResponseDto(savedPayment);
    }

    public void deletePayment(Integer id) {
        Payment payment = findPaymentOrThrow(id);
        paymentRepository.delete(payment);
    }

    // -------------------- MÉTODOS AUXILIARES DE BÚSQUEDA (sin cambios) --------------------

    public Payment findPaymentOrThrow(Integer paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));
    }
    
    private Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }
}