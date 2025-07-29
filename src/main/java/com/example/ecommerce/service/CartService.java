package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartDTO getOrCreateCart(String sessionId) {
        try {
            System.out.println("CartService: Getting or creating cart for session: " + sessionId);
            
            Cart cart = cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> {
                        System.out.println("CartService: Creating new cart for session: " + sessionId);
                        Cart newCart = new Cart();
                        newCart.setSessionId(sessionId);
                        return cartRepository.save(newCart);
                    });
            
            System.out.println("CartService: Found cart with ID: " + cart.getId());
            
            // Load cart items with product information to avoid lazy loading issues
            List<CartItem> items = cartItemRepository.findByCartWithProduct(cart);
            System.out.println("CartService: Found " + items.size() + " cart items");
            
            cart.setItems(items);
            System.out.println("CartService: Set items on cart, cart items size: " + (cart.getItems() != null ? cart.getItems().size() : 0));
            
            return convertToDTO(cart);
        } catch (Exception e) {
            System.err.println("CartService: Error in getOrCreateCart: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting or creating cart: " + e.getMessage(), e);
        }
    }

    public CartDTO addToCart(String sessionId, Long productId, Integer quantity) {
        try {
            System.out.println("CartService: Adding product " + productId + " with quantity " + quantity + " to cart for session " + sessionId);
            
            Cart cart = getOrCreateCartEntity(sessionId);
            System.out.println("CartService: Found/created cart with ID: " + cart.getId());
            
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
            System.out.println("CartService: Found product: " + product.getName());

            Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
            
            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                System.out.println("CartService: Updated existing cart item, new quantity: " + item.getQuantity());
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                cartItemRepository.save(newItem);
                System.out.println("CartService: Created new cart item with quantity: " + newItem.getQuantity());
            }

            // Return updated cart with items
            Cart updatedCart = getOrCreateCartEntity(sessionId);
            System.out.println("CartService: Returning cart with " + (updatedCart.getItems() != null ? updatedCart.getItems().size() : 0) + " items");
            return convertToDTO(updatedCart);
        } catch (Exception e) {
            System.err.println("CartService: Error in addToCart: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error adding product to cart: " + e.getMessage(), e);
        }
    }

    public CartDTO updateCartItemQuantity(String sessionId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCartEntity(sessionId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (quantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        }

        // Return updated cart with items
        return convertToDTO(getOrCreateCartEntity(sessionId));
    }

    public CartDTO removeFromCart(String sessionId, Long productId) {
        Cart cart = getOrCreateCartEntity(sessionId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        existingItem.ifPresent(cartItemRepository::delete);

        // Return updated cart with items
        return convertToDTO(getOrCreateCartEntity(sessionId));
    }

    public CartDTO clearCart(String sessionId) {
        Cart cart = getOrCreateCartEntity(sessionId);
        List<CartItem> items = cartItemRepository.findByCart(cart);
        cartItemRepository.deleteAll(items);
        
        // Return updated cart with items
        return convertToDTO(getOrCreateCartEntity(sessionId));
    }

    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    // Helper method to get Cart entity (not DTO)
    private Cart getOrCreateCartEntity(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    return cartRepository.save(newCart);
                });
    }

    // Helper method to convert Cart entity to CartDTO
    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setSessionId(cart.getSessionId());
        
        List<CartItem> items = cartItemRepository.findByCartWithProduct(cart);
        List<CartDTO.CartItemDTO> itemDTOs = items.stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
        
        dto.setItems(itemDTOs);
        dto.setTotal(calculateTotal(itemDTOs));
        
        return dto;
    }

    // Helper method to convert CartItem entity to CartItemDTO
    private CartDTO.CartItemDTO convertToItemDTO(CartItem item) {
        CartDTO.CartItemDTO dto = new CartDTO.CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        
        if (item.getProduct() != null) {
            CartDTO.ProductDTO productDTO = new CartDTO.ProductDTO();
            productDTO.setId(item.getProduct().getId());
            productDTO.setName(item.getProduct().getName());
            productDTO.setDescription(item.getProduct().getDescription());
            productDTO.setPrice(item.getProduct().getPrice());
            productDTO.setImageUrl(item.getProduct().getImageUrl());
            productDTO.setBrand(item.getProduct().getBrand());
            productDTO.setModel(item.getProduct().getModel());
            dto.setProduct(productDTO);
        }
        
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    // Helper method to calculate total
    private BigDecimal calculateTotal(List<CartDTO.CartItemDTO> items) {
        return items.stream()
                .map(CartDTO.CartItemDTO::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 