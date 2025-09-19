package org.springshop.api.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "apparels")
public class Apparel extends Product {
    @Column(name = "size", length = 50)
    private String size; //Tamaño
    @Column(name = "color", length = 50)
    private String color; //Color
    @Column(name = "brand", length = 100)
    private String brand; //Marca
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private ApparelCategory apparelCategory; //Categoría de ropa

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Apparel apparel = (Apparel) obj;
        return super.id != null && apparel.id != null && apparel.id.equals(id);
    }
    @Override
    public int hashCode() {
        return super.id != null ? super.id.hashCode() : 0;
    }
}
