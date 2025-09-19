// SuplementResponseDTO.java
package org.springshop.api.dto.product.suplement;

import org.springshop.api.dto.product.ProductResponseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SuplementResponseDTO extends ProductResponseDTO {
    private String brand;
    private String flavor;
    private String size;
    private String ingredients;
    private String usageInstructions;
    private String warnings;
}
