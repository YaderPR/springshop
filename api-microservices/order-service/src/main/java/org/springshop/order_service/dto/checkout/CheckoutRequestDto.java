// CheckoutRequestDto.java
package org.springshop.order_service.dto.checkout;

import lombok.Data;

@Data
public class CheckoutRequestDto {
  private Integer cartId;
  private Integer userId;
  private Integer addressId;
  private String redirectUrl; // 🔑 NUEVO CAMPO
}