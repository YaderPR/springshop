// SuplementRequestDTO.java
package org.springshop.api.dto.product.suplement;

import org.springshop.api.dto.product.ProductRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SuplementRequestDTO extends ProductRequestDTO {
    private String brand;
    private String flavor;
    private String size;
    private String ingredients;
    private String usageInstructions;
    private String warnings;
}

