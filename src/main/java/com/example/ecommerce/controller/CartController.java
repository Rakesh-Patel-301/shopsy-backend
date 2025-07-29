package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam String sessionId) {
        try {
            CartDTO cart = cartService.getOrCreateCart(sessionId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@RequestParam String sessionId, 
                                         @RequestParam Long productId, 
                                         @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            System.out.println("CartController: Adding product " + productId + " with quantity " + quantity + " to cart for session " + sessionId);
            
            CartDTO cart = cartService.addToCart(sessionId, productId, quantity);
            
            System.out.println("CartController: Successfully added product to cart. Cart ID: " + cart.getId() + ", Items count: " + (cart.getItems() != null ? cart.getItems().size() : 0));
            
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.err.println("CartController: Error adding product to cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateQuantity(@RequestParam String sessionId, 
                                              @RequestParam Long productId, 
                                              @RequestParam Integer quantity) {
        try {
            CartDTO cart = cartService.updateCartItemQuantity(sessionId, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartDTO> removeFromCart(@RequestParam String sessionId, 
                                              @RequestParam Long productId) {
        try {
            CartDTO cart = cartService.removeFromCart(sessionId, productId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartDTO> clearCart(@RequestParam String sessionId) {
        try {
            CartDTO cart = cartService.clearCart(sessionId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/session")
    public ResponseEntity<Map<String, String>> generateSessionId() {
        try {
            String sessionId = cartService.generateSessionId();
            return ResponseEntity.ok(Map.of("sessionId", sessionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 