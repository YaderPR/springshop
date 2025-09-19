// ApparelResponseDTO.java
package org.springshop.api.dto.product.apparel;

import org.springshop.api.dto.product.ProductResponseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApparelResponseDTO extends ProductResponseDTO {
    private String size;
    private String color;
    private String brand;
    private String apparelCategoryName; // devolvemos el nombre de la categoría de ropa
}
