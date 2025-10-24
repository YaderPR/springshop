package org.springshop.payment_service.controller.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.payment_service.dto.payment.PaymentRequestDto;
import org.springshop.payment_service.dto.payment.PaymentResponseDto;
import org.springshop.payment_service.service.payment.PaymentService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v2/payments")
public class PaymentController {

    private final static String BASE_URL = "/api/v2/payments";
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto requestDto) {
        
        PaymentResponseDto responseDto = paymentService.createPayment(requestDto);
        URI location = URI.create(BASE_URL + "/" + responseDto.getId());

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {

        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Integer id) {

        return wrapOrNotFound(paymentService.getPaymentById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<PaymentResponseDto> updatePayment(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentRequestDto requestDto) {

        PaymentResponseDto updatedDto = paymentService.updatePayment(id, requestDto);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {

        paymentService.deletePayment(id);

        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {

        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}