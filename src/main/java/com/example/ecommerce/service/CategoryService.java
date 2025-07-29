package com.example.ecommerce.service;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Map<String, Object> getProductsByCategoryWithFilters(
            Long categoryId,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String brand,
            Double minPrice,
            Double maxPrice) {
        
        // Validate category exists
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Create sort object
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Build query based on filters
        Page<Product> productsPage;
        
        if (brand != null && minPrice != null && maxPrice != null) {
            productsPage = productRepository.findByCategoryAndBrandAndPriceBetween(
                    category, brand, BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice), pageable);
        } else if (brand != null) {
            productsPage = productRepository.findByCategoryAndBrand(category, brand, pageable);
        } else if (minPrice != null && maxPrice != null) {
            productsPage = productRepository.findByCategoryAndPriceBetween(
                    category, BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice), pageable);
        } else {
            productsPage = productRepository.findByCategory(category, pageable);
        }

        // Get available brands and price range for this category
        List<String> availableBrands = productRepository.findDistinctBrandsByCategory(category);
        Object[] priceRangeResult = productRepository.findPriceRangeByCategory(category);
        
        // Convert price range result to map
        Map<String, Object> priceRange = new HashMap<>();
        if (priceRangeResult != null && priceRangeResult.length >= 2) {
            priceRange.put("minPrice", priceRangeResult[0]);
            priceRange.put("maxPrice", priceRangeResult[1]);
        } else {
            priceRange.put("minPrice", 0);
            priceRange.put("maxPrice", 1000);
        }

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("category", category);
        response.put("products", productsPage.getContent());
        response.put("totalElements", productsPage.getTotalElements());
        response.put("totalPages", productsPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        response.put("availableBrands", availableBrands);
        response.put("priceRange", priceRange);
        
        // Create filters map
        Map<String, Object> filters = new HashMap<>();
        filters.put("brand", brand);
        filters.put("minPrice", minPrice);
        filters.put("maxPrice", maxPrice);
        filters.put("sortBy", sortBy);
        filters.put("sortDir", sortDir);
        response.put("filters", filters);

        return response;
    }
} 