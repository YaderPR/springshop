package org.springshop.product_service.dto.product.supplement; 

import org.springshop.product_service.dto.product.ProductResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplementResponse extends ProductResponse {
    private String brand;
    private String flavor;
    private String size;
    private String ingredients;
    private String usageInstructions;
    private String warnings;
}