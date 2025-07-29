package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<Product> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchProducts(keyword, page, size);
    }

    @GetMapping("/search/advanced")
    public Page<Product> searchProductsAdvanced(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        String searchKeyword = keyword != null ? keyword : "";
        return productService.searchProductsAdvanced(searchKeyword, page, size, sortBy, sortDir, 
                                                   category, brand, minPrice, maxPrice);
    }

    @GetMapping("/search/suggestions")
    public List<Product> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int limit) {
        return productService.getSearchSuggestions(keyword, limit);
    }

    @GetMapping("/search/filters")
    public Map<String, Object> getSearchFilters(@RequestParam String keyword) {
        return productService.getSearchFilters(keyword);
    }

    @GetMapping("/related/{productId}")
    public List<Product> getRelatedProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "4") int limit) {
        return productService.getRelatedProducts(productId, limit);
    }

    @GetMapping("/category/{categoryId}")
    public Page<Product> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByCategory(categoryId, page, size);
    }

    @GetMapping("/brand/{brand}")
    public Page<Product> getProductsByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByBrand(brand, page, size);
    }

    @GetMapping("/price-range")
    public Page<Product> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByPriceRange(minPrice, maxPrice, page, size);
    }

    @GetMapping("/discounted")
    public Page<Product> getDiscountedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getDiscountedProducts(page, size);
    }

    @GetMapping("/top-rated")
    public Page<Product> getTopRatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getTopRatedProducts(page, size);
    }

    @GetMapping("/rating/{rating}")
    public Page<Product> getProductsByRating(
            @PathVariable BigDecimal rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByRating(rating, page, size);
    }

    @GetMapping("/in-stock")
    public Page<Product> getInStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getInStockProducts(page, size);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return productService.getAllCategories();
    }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
        return productService.createCategory(category);
    }

    // Admin dashboard endpoints
    @GetMapping("/admin/low-stock")
    public List<Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

    @GetMapping("/admin/top-selling")
    public List<Product> getTopSellingProducts() {
        return productService.getTopSellingProducts();
    }

    @GetMapping("/admin/category-stats")
    public List<Map<String, Object>> getCategoryStatistics() {
        return productService.getCategoryStatistics();
    }

    @GetMapping("/admin/stats")
    public Map<String, Long> getProductStatistics() {
        return productService.getProductStatistics();
    }
} 