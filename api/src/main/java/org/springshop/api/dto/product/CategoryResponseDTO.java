// Response DTO
package org.springshop.api.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class CategoryResponseDTO {
    private Integer id;
    private String name;
    private List<Integer> productIds; // Solo IDs para simplificar
}

