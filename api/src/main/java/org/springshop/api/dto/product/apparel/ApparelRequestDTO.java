// ApparelRequestDTO.java
package org.springshop.api.dto.product.apparel;

import org.springshop.api.dto.product.ProductRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApparelRequestDTO extends ProductRequestDTO {
    private String size;
    private String color;
    private String brand;
    private Integer apparelCategoryId; // se recibe el id de la categoría de ropa
}

