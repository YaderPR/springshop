package org.springshop.product_service.dto.product.workoutaccessory;

import org.springshop.product_service.dto.product.ProductResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkoutAccessoryResponse extends ProductResponse {

    private String material;
    private String dimensions;
    private Double weight;
    private String color;
    private Integer workoutAccessoryCategoryId; 
    private String workoutAccessoryCategoryName; 
    
}