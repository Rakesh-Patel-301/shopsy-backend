package com.example.ecommerce.service;

import com.example.ecommerce.model.Wishlist;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.WishlistRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    
    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Wishlist> getUserWishlist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return wishlistRepository.findByUserOrderByAddedDateDesc(user);
    }
    
    public Wishlist addToWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if already in wishlist
        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product already in wishlist");
        }
        
        Wishlist wishlist = new Wishlist(user, product);
        return wishlistRepository.save(wishlist);
    }
    
    public void removeFromWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        wishlistRepository.deleteByUserAndProduct(user, product);
    }
    
    public boolean isInWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        return wishlistRepository.existsByUserAndProduct(user, product);
    }
} 