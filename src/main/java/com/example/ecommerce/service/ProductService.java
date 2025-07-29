package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findAll(pageable);
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.searchProducts(keyword, pageable);
    }

    // Advanced search with filtering and sorting
    public Page<Product> searchProductsAdvanced(String keyword, int page, int size, String sortBy, String sortDir, 
                                               String category, String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = createSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // If no filters are applied, use basic search
        if (category == null && brand == null && minPrice == null && maxPrice == null) {
            return productRepository.searchProducts(keyword, pageable);
        }
        
        // Apply filters based on what's provided
        if (category != null && brand != null && minPrice != null && maxPrice != null) {
            Category cat = categoryRepository.findByName(category).orElse(null);
            if (cat != null) {
                return productRepository.searchProductsWithAllFilters(keyword, cat, brand, minPrice, maxPrice, pageable);
            }
        } else if (category != null && brand != null) {
            Category cat = categoryRepository.findByName(category).orElse(null);
            if (cat != null) {
                return productRepository.searchProductsWithCategoryAndBrand(keyword, cat, brand, pageable);
            }
        } else if (category != null && minPrice != null && maxPrice != null) {
            Category cat = categoryRepository.findByName(category).orElse(null);
            if (cat != null) {
                return productRepository.searchProductsWithCategoryAndPrice(keyword, cat, minPrice, maxPrice, pageable);
            }
        } else if (brand != null && minPrice != null && maxPrice != null) {
            return productRepository.searchProductsWithBrandAndPrice(keyword, brand, minPrice, maxPrice, pageable);
        } else if (category != null) {
            Category cat = categoryRepository.findByName(category).orElse(null);
            if (cat != null) {
                return productRepository.searchProductsWithCategory(keyword, cat, pageable);
            }
        } else if (brand != null) {
            return productRepository.searchProductsWithBrand(keyword, brand, pageable);
        } else if (minPrice != null && maxPrice != null) {
            return productRepository.searchProductsWithPrice(keyword, minPrice, maxPrice, pageable);
        }
        
        // Fallback to basic search
        return productRepository.searchProducts(keyword, pageable);
    }

    // Get search suggestions for autocomplete
    public List<Product> getSearchSuggestions(String keyword, int limit) {
        return productRepository.findSearchSuggestions(keyword, PageRequest.of(0, limit));
    }

    // Get available filters for search results
    public Map<String, Object> getSearchFilters(String keyword) {
        Map<String, Object> filters = new HashMap<>();
        
        // Get categories
        List<String> categories = productRepository.findDistinctCategoriesByKeyword(keyword);
        filters.put("categories", categories);
        
        // Get brands
        List<String> brands = productRepository.findDistinctBrandsByKeyword(keyword);
        filters.put("brands", brands);
        
        // Get price range
        Object[] priceRange = productRepository.findPriceRangeByKeyword(keyword);
        if (priceRange != null && priceRange.length == 2) {
            Map<String, BigDecimal> price = new HashMap<>();
            price.put("min", (BigDecimal) priceRange[0]);
            price.put("max", (BigDecimal) priceRange[1]);
            filters.put("priceRange", price);
        }
        
        return filters;
    }

    // Get related products
    public List<Product> getRelatedProducts(Long productId, int limit) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        Product product = productOpt.get();
        Pageable pageable = PageRequest.of(0, limit);
        
        // Try to find related products by category first
        List<Product> relatedByCategory = productRepository.findRelatedProductsByCategory(
            product.getCategory(), productId, pageable);
        
        if (relatedByCategory.size() >= limit) {
            return relatedByCategory.subList(0, limit);
        }
        
        // If not enough by category, add by brand
        if (product.getBrand() != null) {
            List<Product> relatedByBrand = productRepository.findRelatedProductsByBrand(
                product.getBrand(), productId, pageable);
            
            // Combine and remove duplicates
            List<Product> combined = new ArrayList<>(relatedByCategory);
            for (Product p : relatedByBrand) {
                if (!combined.contains(p) && combined.size() < limit) {
                    combined.add(p);
                }
            }
            
            if (combined.size() >= limit) {
                return combined.subList(0, limit);
            }
        }
        
        // If still not enough, add by price range
        BigDecimal minPrice = product.getPrice().multiply(new BigDecimal("0.7"));
        BigDecimal maxPrice = product.getPrice().multiply(new BigDecimal("1.3"));
        
        List<Product> relatedByPrice = productRepository.findRelatedProductsByPriceRange(
            minPrice, maxPrice, productId, pageable);
        
        // Combine all results
        List<Product> allRelated = new ArrayList<>(relatedByCategory);
        for (Product p : relatedByPrice) {
            if (!allRelated.contains(p) && allRelated.size() < limit) {
                allRelated.add(p);
            }
        }
        
        return allRelated.subList(0, Math.min(allRelated.size(), limit));
    }

    private Sort createSort(String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        switch (sortBy.toLowerCase()) {
            case "price":
                return Sort.by(direction, "price");
            case "rating":
                return Sort.by(direction, "rating");
            case "createdat":
                return Sort.by(direction, "createdAt");
            case "name":
            default:
                return Sort.by(direction, "name");
        }
    }
    
    public Page<Product> getProductsByCategory(Long categoryId, int page, int size) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            return productRepository.findByCategory(category.get(), pageable);
        }
        return Page.empty();
    }
    
    public Page<Product> getProductsByBrand(String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByBrand(brand, pageable);
    }
    
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }
    
    public Page<Product> getDiscountedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("discountPercentage").descending());
        return productRepository.findByDiscountPercentageGreaterThan(0, pageable);
    }
    
    public Page<Product> getTopRatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findTopRatedProducts(pageable);
    }
    
    public Page<Product> getProductsByRating(BigDecimal rating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        return productRepository.findByRatingGreaterThanEqual(rating, pageable);
    }
    
    public Page<Product> getInStockProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByStockGreaterThan(0, pageable);
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product existingProduct = product.get();
            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setStock(productDetails.getStock());
            existingProduct.setImageUrl(productDetails.getImageUrl());
            existingProduct.setCategory(productDetails.getCategory());
            existingProduct.setBrand(productDetails.getBrand());
            existingProduct.setModel(productDetails.getModel());
            existingProduct.setOriginalPrice(productDetails.getOriginalPrice());
            existingProduct.setDiscountPercentage(productDetails.getDiscountPercentage());
            existingProduct.setSpecifications(productDetails.getSpecifications());
            return productRepository.save(existingProduct);
        }
        throw new RuntimeException("Product not found");
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    // New methods for admin dashboard
    public List<Product> getLowStockProducts() {
        return productRepository.findByStockLessThanEqual(10);
    }
    
    public List<Product> getTopSellingProducts() {
        return productRepository.findTopSellingProducts();
    }
    
    public List<Map<String, Object>> getCategoryStatistics() {
        List<Object[]> rawStats = productRepository.getCategoryStatistics();
        List<Map<String, Object>> stats = new ArrayList<>();
        
        for (Object[] row : rawStats) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("categoryName", row[0]);
            stat.put("productCount", row[1]);
            stats.add(stat);
        }
        
        return stats;
    }
    
    public Map<String, Long> getProductStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("inStockProducts", productRepository.countByStockGreaterThan(0));
        stats.put("outOfStockProducts", productRepository.countByStockLessThanEqual(0));
        stats.put("discountedProducts", productRepository.countByDiscountPercentageGreaterThan(0.0));
        return stats;
    }
} 