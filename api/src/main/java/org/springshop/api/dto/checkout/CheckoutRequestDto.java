// CheckoutRequestDto.java
package org.springshop.api.dto.checkout;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDto {
    private Integer cartId;
    private Integer userId;
    private Integer addressId;
}
