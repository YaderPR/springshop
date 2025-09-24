package org.springshop.api.controller.payment;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.payment.PaymentRequestDto;
import org.springshop.api.dto.payment.PaymentResponseDto;
import org.springshop.api.service.payment.PaymentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final static String BASE_URL = "/api/payments";
    PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto requestDto) {
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);
        return ResponseEntity.created(URI.create(BASE_URL + responseDto.getId())).body(responseDto);
    }
    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    @GetMapping("{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
    @PutMapping("{id}")
    public ResponseEntity<PaymentResponseDto> updatePaymentById(@PathVariable Integer id, @RequestBody PaymentRequestDto requestDto) {
        return ResponseEntity.ok(paymentService.updatePaymentById(id, requestDto));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePaymentById(@PathVariable Integer id) {
        paymentService.deletePaymentById(id);
        return ResponseEntity.noContent().build();
    }
}
