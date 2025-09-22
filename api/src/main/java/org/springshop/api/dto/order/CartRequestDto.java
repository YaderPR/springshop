package org.springshop.api.dto.order;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartRequestDto {
    private Integer userId;
    private Set<CartItemRequestDto> items;
}

