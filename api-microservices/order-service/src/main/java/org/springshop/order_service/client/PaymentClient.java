package org.springshop.order_service.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springshop.order_service.dto.payment.PaymentResponseDto;

@Component
public abstract class PaymentClient {
    public abstract List<PaymentResponseDto> getPaymentsByOrderId(Integer orderId);
}
