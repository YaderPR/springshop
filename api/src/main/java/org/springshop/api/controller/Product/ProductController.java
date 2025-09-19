package org.springshop.api.controller.Product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.dto.product.suplement.SuplementRequestDTO;
import org.springshop.api.dto.product.suplement.SuplementResponseDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.service.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // -------------------- PRODUCTO GENÉRICO --------------------
    @PostMapping("/generic")
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/generic")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/generic/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------- APPAREL --------------------
    @PostMapping("/apparels")
    public ResponseEntity<ApparelResponseDTO> createApparel(@RequestBody ApparelRequestDTO dto) {
        return ResponseEntity.ok(productService.createApparel(dto));
    }

    @GetMapping("/apparels")
    public ResponseEntity<List<ApparelResponseDTO>> getAllApparels() {
        return ResponseEntity.ok(productService.getAllApparels());
    }

    @GetMapping("/apparels/{id}")
    public ResponseEntity<ApparelResponseDTO> getApparelById(@PathVariable Integer id) {
        return productService.getApparelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------- WORKOUT ACCESSORIES --------------------
    @PostMapping("/workout-accessories")
    public ResponseEntity<WorkoutAccessoryResponseDTO> createWorkoutAccessory(@RequestBody WorkoutAccessoryRequestDTO dto) {
        return ResponseEntity.ok(productService.createWorkoutAccessory(dto));
    }

    @GetMapping("/workout-accessories")
    public ResponseEntity<List<WorkoutAccessoryResponseDTO>> getAllWorkoutAccessories() {
        return ResponseEntity.ok(productService.getAllWorkoutAccessories());
    }

    @GetMapping("/workout-accessories/{id}")
    public ResponseEntity<WorkoutAccessoryResponseDTO> getWorkoutAccessoryById(@PathVariable Integer id) {
        return productService.getWorkoutAccessoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------- SUPLEMENTS --------------------
    @PostMapping("/suplements")
    public ResponseEntity<SuplementResponseDTO> createSuplement(@RequestBody SuplementRequestDTO dto) {
        return ResponseEntity.ok(productService.createSuplement(dto));
    }

    @GetMapping("/suplements")
    public ResponseEntity<List<SuplementResponseDTO>> getAllSuplements() {
        return ResponseEntity.ok(productService.getAllSuplements());
    }

    @GetMapping("/suplements/{id}")
    public ResponseEntity<SuplementResponseDTO> getSuplementById(@PathVariable Integer id) {
        return productService.getSuplementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
