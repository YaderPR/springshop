package org.springshop.api.dto.order;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderResponseDto {

    private Integer id;

    private String status;

    private Double totalAmount;
    
    private Integer userId;

    private List<OrderItemResponseDto> items;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}

