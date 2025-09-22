package org.springshop.api.dto.order;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDto {
    @NotNull
    private Integer userId;

    @NotBlank
    @Size(max = 50)
    private String status;

    @NotNull
    private Double totalAmount;

    @NotNull
    @Size(min = 1) // al menos un item
    private Set<OrderItemRequestDto> items;

}