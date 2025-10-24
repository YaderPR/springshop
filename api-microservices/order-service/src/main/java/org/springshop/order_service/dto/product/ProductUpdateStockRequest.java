package org.springshop.order_service.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductUpdateStockRequest {
    @NotNull
    private Integer quantityChange;
}
