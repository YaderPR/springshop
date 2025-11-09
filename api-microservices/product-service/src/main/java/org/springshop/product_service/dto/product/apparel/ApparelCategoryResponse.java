package org.springshop.product_service.dto.product.apparel;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApparelCategoryResponse {
    private Integer id;
    private String name;
    private String imageUrl;
    private List<Integer> apparelIds;
}