package org.springshop.product_service.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import java.util.Objects;
import java.util.Set;

import org.springshop.product_service.dto.product.CategoryRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "image_url", nullable = true, length = 200)
    private String imageUrl;
    @OneToMany(mappedBy = "category")
    private Set<Product> products;

    public Category(String name) {
        this.name = name;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return obj != null && Objects.equals(category.id, id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    public static Category mapFromDTO(CategoryRequest categoryDTO) {
        return new Category(categoryDTO.getName());
    }
}
