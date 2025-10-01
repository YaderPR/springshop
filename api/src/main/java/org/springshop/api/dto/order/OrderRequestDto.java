package org.springshop.api.dto.order;


import org.springshop.api.model.order.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// OrderRequestDto (CORREGIDO Y SIMPLIFICADO)
@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDto {
    
    @NotNull
    private Integer userId;
    
    @NotNull
    private Integer addressId; 

    @NotBlank
    @Size(max = 50)
    private OrderStatus status; 
}