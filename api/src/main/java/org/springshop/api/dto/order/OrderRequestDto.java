package org.springshop.api.dto.order;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// OrderRequestDto (CORREGIDO Y SIMPLIFICADO)
@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDto {
    @NotNull
    private Integer userId;
    
    @NotNull
    private Integer addressId; 
    
    // Si la orden se crea a partir de un carrito, se podría añadir:
    // @NotNull
    // private Integer cartId; 
    
    @NotBlank
    @Size(max = 50)
    // NOTA: El status inicial (e.g., "PENDIENTE") debería ser gestionado en el Servicio, no por el DTO
    private String status; 
    
    // ELIMINADO: totalAmount (calculado)
    // ELIMINADO: items (se añaden en el Service al procesar el Cart)
}