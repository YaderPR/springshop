package org.springshop.api.dto.checkout;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutResponseDto {
    private Integer orderId;
    private Integer paymentId;
    private String status;
    private String redirectUrl;
}

