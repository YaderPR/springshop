package org.springshop.product_service.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductUpdateStockRequest {
    @NotNull
    private Integer quantityChange;
}
