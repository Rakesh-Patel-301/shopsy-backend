package com.example.ecommerce.controller;

import com.example.ecommerce.model.*;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.dto.CartDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    // Create order from cart
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            // Get cart items for the session using the DTO and then fetch items
            CartDTO cartDTO = cartService.getOrCreateCart(request.getSessionId());
            
            if (cartDTO.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Cart is empty");
            }
            
            // Get the first available user (for demo purposes)
            // In a real app, this would come from the authenticated user
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.badRequest().body("No users found in the system");
            }
            User user = users.get(0); // Use the first user
            
            // Convert CartItemDTOs to CartItems for the order service
            List<CartItem> cartItems = cartDTO.getItems().stream()
                .map(itemDTO -> {
                    CartItem item = new CartItem();
                    item.setId(itemDTO.getId());
                    item.setQuantity(itemDTO.getQuantity());
                    
                    // Fetch the actual product from database to ensure all fields are populated
                    Product product = productRepository.findById(itemDTO.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProduct().getId()));
                    
                    item.setProduct(product);
                    return item;
                })
                .toList();
            
            Order order = orderService.createOrder(
                user.getId(), 
                cartItems, 
                request.getShippingAddress(), 
                request.getPaymentMethod()
            );
            
            // Clear the cart after successful order creation
            cartService.clearCart(request.getSessionId());
            
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }
    
    // Get user's order history
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }
    
    // Get specific order details
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update order status (admin only)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating order status: " + e.getMessage());
        }
    }
    
    // Update tracking number (admin only)
    @PutMapping("/{orderId}/tracking")
    public ResponseEntity<?> updateTrackingNumber(
            @PathVariable Long orderId,
            @RequestBody UpdateTrackingRequest request) {
        try {
            Order updatedOrder = orderService.updateTrackingNumber(orderId, request.getTrackingNumber());
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating tracking number: " + e.getMessage());
        }
    }
    
    // Admin: Get all orders with pagination
    @GetMapping("/admin/all")
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(orders);
    }
    
    // Admin: Get orders by status
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    // Admin: Get order statistics
    @GetMapping("/admin/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        Map<String, Long> statusStats = orderService.getOrderStatusStatistics();
        Map<String, Double> revenueStats = orderService.getRevenueStatistics();
        
        Map<String, Object> statistics = Map.of(
            "statusStatistics", statusStats,
            "revenueStatistics", revenueStats
        );
        
        return ResponseEntity.ok(statistics);
    }
    
    // Request DTOs
    public static class CreateOrderRequest {
        private String sessionId;
        private String shippingAddress;
        private String paymentMethod;
        
        // Getters and setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    
    public static class UpdateOrderStatusRequest {
        private Order.OrderStatus status;
        
        public Order.OrderStatus getStatus() { return status; }
        public void setStatus(Order.OrderStatus status) { this.status = status; }
    }
    
    public static class UpdateTrackingRequest {
        private String trackingNumber;
        
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    }
} 