package com.example.ecommerce.controller;

import com.example.ecommerce.model.Wishlist;
import com.example.ecommerce.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "http://localhost:3000")
public class WishlistController {
    
    @Autowired
    private WishlistService wishlistService;
    
    @GetMapping
    public ResponseEntity<List<Wishlist>> getUserWishlist(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<Wishlist> wishlist = wishlistService.getUserWishlist(username);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/add/{productId}")
    public ResponseEntity<Wishlist> addToWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        try {
            String username = authentication.getName();
            Wishlist wishlist = wishlistService.addToWishlist(username, productId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        try {
            String username = authentication.getName();
            wishlistService.removeFromWishlist(username, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlistStatus(
            Authentication authentication,
            @PathVariable Long productId) {
        try {
            String username = authentication.getName();
            boolean isInWishlist = wishlistService.isInWishlist(username, productId);
            return ResponseEntity.ok(Map.of("inWishlist", isInWishlist));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 