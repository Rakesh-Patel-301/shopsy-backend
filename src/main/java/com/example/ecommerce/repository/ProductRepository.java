package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Basic search by name or description
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    // Search suggestions for autocomplete
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.name ASC")
    List<Product> findSearchSuggestions(@Param("keyword") String keyword, Pageable pageable);
    
    // Advanced search with category filter
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.category = :category")
    Page<Product> searchProductsWithCategory(@Param("keyword") String keyword, @Param("category") Category category, Pageable pageable);
    
    // Advanced search with brand filter
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.brand = :brand")
    Page<Product> searchProductsWithBrand(@Param("keyword") String keyword, @Param("brand") String brand, Pageable pageable);
    
    // Advanced search with price filter
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> searchProductsWithPrice(@Param("keyword") String keyword, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    
    // Advanced search with category and brand filters
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.category = :category AND p.brand = :brand")
    Page<Product> searchProductsWithCategoryAndBrand(@Param("keyword") String keyword, @Param("category") Category category, @Param("brand") String brand, Pageable pageable);
    
    // Advanced search with category and price filters
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.category = :category AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> searchProductsWithCategoryAndPrice(@Param("keyword") String keyword, @Param("category") Category category, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    
    // Advanced search with brand and price filters
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.brand = :brand AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> searchProductsWithBrandAndPrice(@Param("keyword") String keyword, @Param("brand") String brand, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    
    // Advanced search with all filters
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.category = :category AND p.brand = :brand AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> searchProductsWithAllFilters(@Param("keyword") String keyword, @Param("category") Category category, @Param("brand") String brand, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    
    // Get distinct categories for search results
    @Query("SELECT DISTINCT c.name FROM Product p JOIN p.category c WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<String> findDistinctCategoriesByKeyword(@Param("keyword") String keyword);
    
    // Get distinct brands for search results
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.brand IS NOT NULL")
    List<String> findDistinctBrandsByKeyword(@Param("keyword") String keyword);
    
    // Get price range for search results
    @Query("SELECT MIN(p.price), MAX(p.price) FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Object[] findPriceRangeByKeyword(@Param("keyword") String keyword);
    
    // Find related products by category (excluding current product)
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.id != :productId ORDER BY p.rating DESC, p.soldCount DESC")
    List<Product> findRelatedProductsByCategory(@Param("category") Category category, @Param("productId") Long productId, Pageable pageable);
    
    // Find related products by brand (excluding current product)
    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.id != :productId ORDER BY p.rating DESC, p.soldCount DESC")
    List<Product> findRelatedProductsByBrand(@Param("brand") String brand, @Param("productId") Long productId, Pageable pageable);
    
    // Find related products by price range (excluding current product)
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.id != :productId ORDER BY p.rating DESC, p.soldCount DESC")
    List<Product> findRelatedProductsByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("productId") Long productId, Pageable pageable);
    
    // Find by category
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    // Find by brand
    Page<Product> findByBrand(String brand, Pageable pageable);
    
    // Find by price range
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Find products with discount
    Page<Product> findByDiscountPercentageGreaterThan(Integer discountPercentage, Pageable pageable);
    
    // Find by rating
    Page<Product> findByRatingGreaterThanEqual(BigDecimal rating, Pageable pageable);
    
    // Find in stock products
    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);
    
    // Find by category and price range
    Page<Product> findByCategoryAndPriceBetween(Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Find top rated products
    @Query("SELECT p FROM Product p ORDER BY p.rating DESC, p.reviewCount DESC")
    Page<Product> findTopRatedProducts(Pageable pageable);
    
    // Find discounted products
    @Query("SELECT p FROM Product p WHERE p.discountPercentage > 0 ORDER BY p.discountPercentage DESC")
    Page<Product> findDiscountedProducts(Pageable pageable);
    
    // Admin dashboard methods
    List<Product> findByStockLessThanEqual(Integer stock);
    
    @Query("SELECT p FROM Product p ORDER BY p.soldCount DESC")
    List<Product> findTopSellingProducts();
    
    @Query("SELECT c.name as categoryName, COUNT(p) as productCount FROM Product p JOIN p.category c GROUP BY c.id, c.name")
    List<Object[]> getCategoryStatistics();
    
    Long countByStockGreaterThan(Integer stock);
    
    Long countByStockLessThanEqual(Integer stock);
    
    Long countByDiscountPercentageGreaterThan(Double discountPercentage);
    
    // Category filtering methods
    Page<Product> findByCategoryAndBrand(Category category, String brand, Pageable pageable);
    
    Page<Product> findByCategoryAndBrandAndPriceBetween(Category category, String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.category = :category AND p.brand IS NOT NULL")
    List<String> findDistinctBrandsByCategory(@Param("category") Category category);
    
    @Query("SELECT MIN(p.price) as minPrice, MAX(p.price) as maxPrice FROM Product p WHERE p.category = :category")
    Object[] findPriceRangeByCategory(@Param("category") Category category);
} 