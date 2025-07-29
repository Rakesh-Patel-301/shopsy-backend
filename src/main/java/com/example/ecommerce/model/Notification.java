package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority = NotificationPriority.NORMAL;
    
    @Column(nullable = false)
    private boolean read = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime readAt;
    
    @Column
    private String actionUrl;
    
    @Column
    private String imageUrl;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        ORDER_STATUS,
        PROMOTION,
        SYSTEM_UPDATE,
        SECURITY_ALERT,
        PRODUCT_RECOMMENDATION,
        PRICE_DROP,
        BACK_IN_STOCK,
        REVIEW_REQUEST
    }
    
    public enum NotificationPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
    
    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }
} 