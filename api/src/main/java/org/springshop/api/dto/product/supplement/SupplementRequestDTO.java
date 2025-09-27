// Archivo: SupplementRequestDTO.java (CORREGIDO)
package org.springshop.api.dto.product.supplement; // Ortografía corregida

import org.springshop.api.dto.product.ProductRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
// Ortografía corregida
public class SupplementRequestDTO extends ProductRequestDTO { 
    
    @NotBlank // Marca es esencial
    private String brand;
    
    @NotBlank // Sabor o tipo es esencial
    private String flavor;
    
    @NotBlank // Tamaño o formato de venta
    private String size;
    
    @NotBlank
    @Size(max = 2000) // Un límite para la lista de ingredientes
    private String ingredients;
    
    @NotBlank
    private String usageInstructions;
    
    private String warnings; // Puede ser nulo, pero es útil incluirlo
}