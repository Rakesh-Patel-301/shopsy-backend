package com.example.ecommerce.repository;

import com.example.ecommerce.model.Review;
import com.example.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find all reviews for a product
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    
    // Find reviews by product ID
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    // Calculate average rating for a product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    BigDecimal findAverageRatingByProduct(@Param("product") Product product);
    
    // Count reviews for a product
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = :product")
    Long countReviewsByProduct(@Param("product") Product product);
    
    // Find reviews with rating greater than or equal to
    List<Review> findByProductAndRatingGreaterThanEqualOrderByCreatedAtDesc(Product product, BigDecimal rating);
    
    // Find recent reviews
    List<Review> findTop10ByOrderByCreatedAtDesc();
} 