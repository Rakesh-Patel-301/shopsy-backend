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
@Table(name = "product_inventory")
public class ProductInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false, unique = true)
    private String sku;
    
    @Column(nullable = false)
    private Integer currentStock;
    
    @Column(nullable = false)
    private Integer reservedStock = 0;
    
    @Column(nullable = false)
    private Integer availableStock;
    
    @Column(nullable = false)
    private Integer minimumStockLevel = 5;
    
    @Column(nullable = false)
    private Integer maximumStockLevel = 1000;
    
    @Column(nullable = false)
    private boolean trackInventory = true;
    
    @Column(nullable = false)
    private boolean allowBackorder = false;
    
    @Column(nullable = false)
    private boolean lowStockAlert = false;
    
    @Column(nullable = false)
    private LocalDateTime lastStockUpdate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastStockUpdate = LocalDateTime.now();
        updateAvailableStock();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateAvailableStock();
    }
    
    public void updateAvailableStock() {
        this.availableStock = this.currentStock - this.reservedStock;
        this.lowStockAlert = this.availableStock <= this.minimumStockLevel;
    }
    
    public boolean canReserveStock(int quantity) {
        if (!trackInventory) return true;
        if (allowBackorder) return true;
        return availableStock >= quantity;
    }
    
    public void reserveStock(int quantity) {
        if (canReserveStock(quantity)) {
            this.reservedStock += quantity;
            updateAvailableStock();
        } else {
            throw new RuntimeException("Insufficient stock available");
        }
    }
    
    public void releaseReservedStock(int quantity) {
        this.reservedStock = Math.max(0, this.reservedStock - quantity);
        updateAvailableStock();
    }
    
    public void updateStock(int newStock) {
        this.currentStock = newStock;
        this.lastStockUpdate = LocalDateTime.now();
        updateAvailableStock();
    }
    
    public void addStock(int quantity) {
        this.currentStock += quantity;
        this.lastStockUpdate = LocalDateTime.now();
        updateAvailableStock();
    }
    
    public void removeStock(int quantity) {
        if (this.currentStock >= quantity) {
            this.currentStock -= quantity;
            this.lastStockUpdate = LocalDateTime.now();
            updateAvailableStock();
        } else {
            throw new RuntimeException("Insufficient stock to remove");
        }
    }
} 