package org.springshop.order_service.dto.checkout;

import lombok.Data;

@Data
public class CheckoutResponseDto {
    private Integer orderId;
    private Integer paymentId;
    private String status;
    private String redirectUrl;
}

