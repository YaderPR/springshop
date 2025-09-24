package org.springshop.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springshop.api.dto.product.ProductResponseDTO;
import org.springshop.api.dto.product.ProductRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelRequestDTO;
import org.springshop.api.dto.product.apparel.ApparelResponseDTO;
import org.springshop.api.dto.product.supplement.SupplementRequestDTO;
import org.springshop.api.dto.product.supplement.SupplementResponseDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryRequestDTO;
import org.springshop.api.dto.product.workoutaccessory.WorkoutAccessoryResponseDTO;
import org.springshop.api.service.product.ProductService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private static final String BASE_URL = "/api/products";

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // -------------------- PRODUCT --------------------
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        return ResponseEntity.created(URI.create(BASE_URL + "/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable("id") Integer id) {
        return wrapOrNotFound(productService.getProductById(id));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable("id") Integer id,
            @RequestBody ProductRequestDTO dto) {
        return wrapOrNotFound(Optional.of(productService.updateProduct(id, dto)));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- APPAREL --------------------
    @PostMapping("/apparels")
    public ResponseEntity<ApparelResponseDTO> createApparel(@RequestBody ApparelRequestDTO dto) {
        ApparelResponseDTO response = productService.createApparel(dto);
        return ResponseEntity.created(URI.create(BASE_URL + "/apparels/" + response.getId()))
                .body(response);
    }

    @GetMapping("/apparels")
    public ResponseEntity<List<ApparelResponseDTO>> getAllApparels() {
        return ResponseEntity.ok(productService.getAllApparels());
    }

    @GetMapping("/apparels/{id}")
    public ResponseEntity<ApparelResponseDTO> getApparelById(@PathVariable("id") Integer id) {
        return wrapOrNotFound(productService.getApparelById(id));
    }

    @PutMapping("/apparels/{id}")
    public ResponseEntity<ApparelResponseDTO> updateApparel(@PathVariable("id") Integer id,
            @RequestBody ApparelRequestDTO dto) {
        return wrapOrNotFound(Optional.of(productService.updateApparel(id, dto)));
    }

    @DeleteMapping("/apparels/{id}")
    public ResponseEntity<Void> deleteApparel(@PathVariable("id") Integer id) {
        productService.deleteApparel(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- WORKOUT ACCESSORIES --------------------
    @PostMapping("/workout-accessories")
    public ResponseEntity<WorkoutAccessoryResponseDTO> createWorkoutAccessory(
            @RequestBody WorkoutAccessoryRequestDTO dto) {
        WorkoutAccessoryResponseDTO response = productService.createWorkoutAccessory(dto);
        return ResponseEntity.created(URI.create(BASE_URL + "/workout-accessories/" + response.getId()))
                .body(response);
    }

    @GetMapping("/workout-accessories")
    public ResponseEntity<List<WorkoutAccessoryResponseDTO>> getAllWorkoutAccessories() {
        return ResponseEntity.ok(productService.getAllWorkoutAccessories());
    }

    @GetMapping("/workout-accessories/{id}")
    public ResponseEntity<WorkoutAccessoryResponseDTO> getWorkoutAccessoryById(@PathVariable("id") Integer id) {
        return wrapOrNotFound(productService.getWorkoutAccessoryById(id));
    }

    @PutMapping("/workout-accessories/{id}")
    public ResponseEntity<WorkoutAccessoryResponseDTO> updateWorkoutAccessory(@PathVariable("id") Integer id,
            @RequestBody WorkoutAccessoryRequestDTO dto) {
        return wrapOrNotFound(Optional.of(productService.updateWorkoutAccessory(id, dto)));
    }

    @DeleteMapping("/workout-accessories/{id}")
    public ResponseEntity<Void> deleteWorkoutAccessory(@PathVariable("id") Integer id) {
        productService.deleteWorkoutAccessory(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- SUPPLEMENTS --------------------
    @PostMapping("/supplements")
    public ResponseEntity<SupplementResponseDTO> createSupplement(@RequestBody SupplementRequestDTO dto) {
        SupplementResponseDTO response = productService.createSuplement(dto);
        return ResponseEntity.created(URI.create(BASE_URL + "/supplements/" + response.getId()))
                .body(response);
    }

    @GetMapping("/supplements")
    public ResponseEntity<List<SupplementResponseDTO>> getAllSupplements() {
        return ResponseEntity.ok(productService.getAllSuplements());
    }

    @GetMapping("/supplements/{id}")
    public ResponseEntity<SupplementResponseDTO> getSupplementById(@PathVariable("id") Integer id) {
        return wrapOrNotFound(productService.getSuplementById(id));
    }

    @PutMapping("/supplements/{id}")
    public ResponseEntity<SupplementResponseDTO> updateSupplement(@PathVariable("id") Integer id,
            @RequestBody SupplementRequestDTO dto) {
        return wrapOrNotFound(Optional.of(productService.updateSuplementById(id, dto)));
    }

    @DeleteMapping("/supplements/{id}")
    public ResponseEntity<Void> deleteSupplement(@PathVariable("id") Integer id) {
        productService.deleteSuplementById(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- HELPER --------------------
    private <T> ResponseEntity<T> wrapOrNotFound(Optional<T> maybeResponse) {
        return maybeResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
