package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Order createOrder(Long userId, List<CartItem> cartItems, String shippingAddress, String paymentMethod) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }
        
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            totalAmount = totalAmount.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        
        // Create order
        Order order = new Order();
        order.setUser(user.get());
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items and update product stock
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
            
            // Update product stock and sold count
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            
            // Safely handle soldCount that might be null
            Integer currentSoldCount = product.getSoldCount();
            if (currentSoldCount == null) {
                currentSoldCount = 0;
            }
            product.setSoldCount(currentSoldCount + cartItem.getQuantity());
            
            productRepository.save(product);
        }
        
        return savedOrder;
    }
    
    public List<Order> getUserOrders(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return orderRepository.findByUserOrderByOrderDateDesc(user.get());
        }
        return List.of();
    }
    
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setStatus(status);
            
            if (status == Order.OrderStatus.DELIVERED) {
                existingOrder.setDeliveryDate(LocalDateTime.now());
            }
            
            return orderRepository.save(existingOrder);
        }
        throw new RuntimeException("Order not found");
    }
    
    public Order updateTrackingNumber(Long orderId, String trackingNumber) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setTrackingNumber(trackingNumber);
            return orderRepository.save(existingOrder);
        }
        throw new RuntimeException("Order not found");
    }
    
    // Admin dashboard methods
    public org.springframework.data.domain.Page<Order> getAllOrders(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }
    
    public Map<String, Long> getOrderStatusStatistics() {
        Map<String, Long> stats = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            stats.put(status.name(), count);
        }
        return stats;
    }
    
    public Map<String, Double> getRevenueStatistics() {
        Map<String, Double> stats = new HashMap<>();
        
        // Total revenue
        Double totalRevenue = orderRepository.calculateTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        
        // Monthly revenue (last 6 months)
        for (int i = 0; i < 6; i++) {
            LocalDateTime startDate = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0);
            LocalDateTime endDate = startDate.plusMonths(1);
            Double monthlyRevenue = orderRepository.calculateRevenueBetweenDates(startDate, endDate);
            String monthKey = startDate.getMonth().name() + " " + startDate.getYear();
            stats.put(monthKey, monthlyRevenue != null ? monthlyRevenue : 0.0);
        }
        
        return stats;
    }
} 