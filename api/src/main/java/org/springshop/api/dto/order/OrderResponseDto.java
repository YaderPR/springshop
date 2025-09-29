package org.springshop.api.dto.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springshop.api.dto.payment.AddressResponseDto;
import org.springshop.api.model.order.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderResponseDto {

    private Integer id;
    private OrderStatus status;
    private Double totalAmount;
    private Integer userId;
    private List<OrderItemResponseDto> items;
    private AddressResponseDto address;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}

