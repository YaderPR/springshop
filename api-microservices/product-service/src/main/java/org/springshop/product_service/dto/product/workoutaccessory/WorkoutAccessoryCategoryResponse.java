package org.springshop.product_service.dto.product.workoutaccessory;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkoutAccessoryCategoryResponse {

    private Integer id;
    private String name;
    private String imageUrl;
    private List<Integer> accessoryIds;
}