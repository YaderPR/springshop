package org.springshop.api.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springshop.api.model.product.Supplement;

import java.util.List;

public interface SupplementRepository extends JpaRepository<Supplement, Integer> {
    
    /**
     * Encuentra todos los suplementos por el ID de su Categoría genérica (Category).
     * Esto soporta el filtrado implementado en el servicio.
     */
    List<Supplement> findAllByCategoryId(Integer categoryId);
}