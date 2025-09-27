// ApparelResponseDTO.java (AJUSTADO)
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
    
    // Campo añadido: El ID es esencial para la integridad de la API
    private Integer apparelCategoryId; 
    
    private String apparelCategoryName; 
}