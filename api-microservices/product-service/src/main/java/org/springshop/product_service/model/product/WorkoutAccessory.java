package org.springshop.product_service.model.product;

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
@Table
public class WorkoutAccessory extends Product {
    @Column(name = "material", length = 100)
    private String material; //Material
    @Column(name = "dimensions", length = 100)
    private String dimensions; //Dimensiones
    @Column(name = "weight")
    private Double weight; //Peso en kg
    @Column(name = "color", length = 50)
    private String color; //Color
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private WorkoutAccessoryCategory workoutAccessoryCategory; //Categoría de accesorio de entrenamiento

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WorkoutAccessory that = (WorkoutAccessory) obj;
        return super.id != null && that.id != null && that.id.equals(id);
    }
    @Override
    public int hashCode() {
        return super.id != null ? super.id.hashCode() : 0;
    }
}
