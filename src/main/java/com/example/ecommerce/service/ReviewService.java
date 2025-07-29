package com.example.ecommerce.service;

import com.example.ecommerce.model.Review;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Review createReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        updateProductRating(review.getProduct());
        return savedReview;
    }

    public Optional<Review> getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public Review updateReview(Long reviewId, Review reviewDetails) {
        Optional<Review> existingReview = reviewRepository.findById(reviewId);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(reviewDetails.getRating());
            review.setComment(reviewDetails.getComment());
            review.setReviewerName(reviewDetails.getReviewerName());
            review.setReviewerEmail(reviewDetails.getReviewerEmail());
            
            Review updatedReview = reviewRepository.save(review);
            updateProductRating(review.getProduct());
            return updatedReview;
        }
        throw new RuntimeException("Review not found");
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        if (review.isPresent()) {
            Product product = review.get().getProduct();
            reviewRepository.deleteById(reviewId);
            updateProductRating(product);
        }
    }

    public BigDecimal getAverageRating(Product product) {
        BigDecimal avgRating = reviewRepository.findAverageRatingByProduct(product);
        return avgRating != null ? avgRating : BigDecimal.ZERO;
    }

    public Long getReviewCount(Product product) {
        return reviewRepository.countReviewsByProduct(product);
    }

    public List<Review> getRecentReviews() {
        return reviewRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<Review> getReviewsByRating(Product product, BigDecimal minRating) {
        return reviewRepository.findByProductAndRatingGreaterThanEqualOrderByCreatedAtDesc(product, minRating);
    }

    @Transactional
    private void updateProductRating(Product product) {
        BigDecimal avgRating = getAverageRating(product);
        Long reviewCount = getReviewCount(product);
        
        product.setRating(avgRating);
        product.setReviewCount(reviewCount.intValue());
        productRepository.save(product);
    }
} 