package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;
    
    @Column(nullable = false)
    private String customerEmail;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column
    private String orderNumber;
    
    @Column
    private String assignedTo;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime resolvedAt;
    
    public enum TicketStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }
    
    public enum TicketPriority {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    public enum TicketCategory {
        GENERAL_INQUIRY, ORDER_ISSUE, TECHNICAL_PROBLEM, 
        BILLING_ISSUE, RETURN_REFUND, PRODUCT_QUESTION
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TicketStatus.OPEN;
        }
        if (priority == null) {
            priority = TicketPriority.MEDIUM;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 