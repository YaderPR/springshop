package org.springshop.api.model.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;


@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "suplements")
public class Suplement extends Product {
    @Column(name = "brand", nullable = false, length = 100)
    private String brand; //Marca
    @Column(name = "flavor", length = 50)
    private String flavor; //Sabor
    @Column(name = "size", length = 50)
    private String size; //Tamaño
    @Column(name = "ingredients", length = 500)
    private String ingredients; //Ingredientes
    @Column(name = "usage_instructions", length = 500)
    private String usageInstructions; //Instrucciones de uso
    @Column(name = "warnings", length = 500)
    private String warnings; //Advertencias
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Suplement suplement = (Suplement) obj;
        return super.id != null && Objects.equals(super.id, suplement.id);
    }
    @Override
    public int hashCode() {
        return super.id != null ? super.id.hashCode() : 0;
    }
}
