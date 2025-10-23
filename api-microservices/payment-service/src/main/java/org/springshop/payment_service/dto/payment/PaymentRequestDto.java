package org.springshop.payment_service.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    @NotNull
    private Integer orderId;
    
    @NotBlank
    private String paymentToken; 
    
    @NotBlank
    private String currency; 

    @NotBlank
    private String method;

    @NotNull
    private Double amount;

    private String description;
}