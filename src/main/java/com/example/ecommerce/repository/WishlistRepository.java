package com.example.ecommerce.repository;

import com.example.ecommerce.model.Wishlist;
import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    List<Wishlist> findByUserOrderByAddedDateDesc(User user);
    
    @Query("SELECT w FROM Wishlist w WHERE w.user = :user AND w.product.id = :productId")
    Optional<Wishlist> findByUserAndProduct(@Param("user") User user, @Param("productId") Long productId);
    
    boolean existsByUserAndProduct(User user, com.example.ecommerce.model.Product product);
    
    void deleteByUserAndProduct(User user, com.example.ecommerce.model.Product product);
} 