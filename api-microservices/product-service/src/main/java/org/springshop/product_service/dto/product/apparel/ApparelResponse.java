// ApparelResponseDTO.java (AJUSTADO)
package org.springshop.product_service.dto.product.apparel;

import org.springshop.product_service.dto.product.ProductResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApparelResponse extends ProductResponse {

    private String size;
    private String color;
    private String brand;
    private Integer apparelCategoryId; 
    private String apparelCategoryName; 
    
}