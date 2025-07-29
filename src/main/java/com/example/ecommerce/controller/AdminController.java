package com.example.ecommerce.controller;

import com.example.ecommerce.model.*;
import com.example.ecommerce.service.*;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "https://shopsy-frontend.netlify.app/"})
public class AdminController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get basic statistics
        long totalProducts = productService.getAllProducts(0, 1).getTotalElements();
        
        // Get recent orders (simplified)
        List<Order> recentOrders = orderService.getAllOrders();
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }
        
        // Get low stock products
        List<Product> lowStockProducts = productService.getLowStockProducts();
        
        stats.put("totalProducts", totalProducts);
        stats.put("totalOrders", recentOrders.size());
        stats.put("totalUsers", 0); // Will be implemented later
        stats.put("recentOrders", recentOrders);
        stats.put("lowStockProducts", lowStockProducts);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/products/analytics")
    public ResponseEntity<Map<String, Object>> getProductAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Get top selling products
        List<Product> topSellingProducts = productService.getTopSellingProducts();
        
        // Get category distribution
        List<Map<String, Object>> categoryStats = productService.getCategoryStatistics();
        
        analytics.put("topSellingProducts", topSellingProducts);
        analytics.put("categoryStats", categoryStats);
        
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/orders/analytics")
    public ResponseEntity<Map<String, Object>> getOrderAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Simplified order statistics
        List<Order> allOrders = orderService.getAllOrders();
        Map<String, Long> orderStatusStats = new HashMap<>();
        orderStatusStats.put("TOTAL", (long) allOrders.size());
        
        // Simplified revenue statistics
        Map<String, Double> revenueStats = new HashMap<>();
        double totalRevenue = allOrders.stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        revenueStats.put("totalRevenue", totalRevenue);
        
        analytics.put("orderStatusStats", orderStatusStats);
        analytics.put("revenueStats", revenueStats);
        
        return ResponseEntity.ok(analytics);
    }
    
    @PostMapping("/products/bulk-update")
    public ResponseEntity<String> bulkUpdateProducts(@RequestBody List<Product> products) {
        try {
            for (Product product : products) {
                productService.updateProduct(product.getId(), product);
            }
            return ResponseEntity.ok("Products updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating products: " + e.getMessage());
        }
    }
    
    @PostMapping("/products/bulk-delete")
    public ResponseEntity<String> bulkDeleteProducts(@RequestBody List<Long> productIds) {
        try {
            for (Long id : productIds) {
                productService.deleteProduct(id);
            }
            return ResponseEntity.ok("Products deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting products: " + e.getMessage());
        }
    }
    
    @GetMapping("/users/analytics")
    public ResponseEntity<Map<String, Object>> getUserAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Simplified user statistics
        Map<String, Long> userStats = new HashMap<>();
        userStats.put("totalUsers", 0L);
        userStats.put("activeUsers", 0L);
        userStats.put("adminUsers", 0L);
        userStats.put("customerUsers", 0L);
        
        analytics.put("userStats", userStats);
        analytics.put("activeUsers", List.of());
        
        return ResponseEntity.ok(analytics);
    }
}