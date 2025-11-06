package org.springshop.product_service.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class CategoryResponse {

    private Integer id;
    private String name;
    private String imageUrl;
    private List<Integer> productIds;
    
}