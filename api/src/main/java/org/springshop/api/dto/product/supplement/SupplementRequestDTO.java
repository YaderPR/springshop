// SuplementRequestDTO.java
package org.springshop.api.dto.product.supplement;

import org.springshop.api.dto.product.ProductRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplementRequestDTO extends ProductRequestDTO {
    private String brand;
    private String flavor;
    private String size;
    private String ingredients;
    private String usageInstructions;
    private String warnings;
}

