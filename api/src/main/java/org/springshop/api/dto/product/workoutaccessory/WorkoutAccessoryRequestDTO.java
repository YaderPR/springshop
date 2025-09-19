// WorkoutAccessoryRequestDTO.java
package org.springshop.api.dto.product.workoutaccessory;

import org.springshop.api.dto.product.ProductRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkoutAccessoryRequestDTO extends ProductRequestDTO {
    private String material;
    private String dimensions;
    private Double weight;
    private String color;
    private Integer workoutAccessoryCategoryId; // se recibe id de la categoría de accesorio
}

