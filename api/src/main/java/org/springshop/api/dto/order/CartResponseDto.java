package org.springshop.api.dto.order;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartResponseDto {
    private Integer id;
    private Integer userId;
    private Set<CartItemResponseDto> items;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}

