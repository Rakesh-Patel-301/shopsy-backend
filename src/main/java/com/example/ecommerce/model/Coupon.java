package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupons")
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;
    
    @Column(nullable = false)
    private BigDecimal discountValue;
    
    @Column(nullable = false)
    private BigDecimal minimumOrderAmount;
    
    @Column(nullable = false)
    private Integer maxUsageLimit;
    
    @Column(nullable = false)
    private Integer currentUsageCount = 0;
    
    @Column(nullable = false)
    private Integer maxUsagePerUser = 1;
    
    @Column(nullable = false)
    private LocalDateTime validFrom;
    
    @Column(nullable = false)
    private LocalDateTime validUntil;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT,
        FREE_SHIPPING
    }
    
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active && 
               now.isAfter(validFrom) && 
               now.isBefore(validUntil) && 
               currentUsageCount < maxUsageLimit;
    }
    
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount.compareTo(minimumOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        switch (discountType) {
            case PERCENTAGE:
                return orderAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
            case FIXED_AMOUNT:
                return discountValue.compareTo(orderAmount) > 0 ? orderAmount : discountValue;
            case FREE_SHIPPING:
                return BigDecimal.ZERO; // Will be handled separately
            default:
                return BigDecimal.ZERO;
        }
    }
} 