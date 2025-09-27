// ApparelRequestDTO.java (CORRECTO)
package org.springshop.api.dto.product.apparel;

import org.springshop.api.dto.product.ProductRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank; // Asumimos que esta importación es necesaria si añadimos validaciones específicas

@Data
@EqualsAndHashCode(callSuper = true)
public class ApparelRequestDTO extends ProductRequestDTO {
    // Es buena práctica añadir validaciones específicas aquí:
    @NotBlank 
    private String size; 
    
    @NotBlank 
    private String color;
    
    @NotBlank 
    private String brand;
    
    private Integer apparelCategoryId;
}