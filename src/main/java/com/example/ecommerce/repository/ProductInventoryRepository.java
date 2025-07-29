package com.example.ecommerce.repository;

import com.example.ecommerce.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    
    Optional<ProductInventory> findByProductId(Long productId);
    
    Optional<ProductInventory> findBySku(String sku);
    
    List<ProductInventory> findByCurrentStockLessThanEqual(Integer stockLevel);
    
    List<ProductInventory> findByLowStockAlertTrue();
    
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.availableStock <= pi.minimumStockLevel AND pi.trackInventory = true")
    List<ProductInventory> findLowStockProducts();
    
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.availableStock = 0 AND pi.trackInventory = true AND pi.allowBackorder = false")
    List<ProductInventory> findOutOfStockProducts();
    
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.currentStock > pi.maximumStockLevel")
    List<ProductInventory> findOverstockedProducts();
    
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.trackInventory = true ORDER BY pi.availableStock ASC")
    List<ProductInventory> findProductsByStockLevelAsc();
    
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.trackInventory = true ORDER BY pi.availableStock DESC")
    List<ProductInventory> findProductsByStockLevelDesc();
    
    @Query("SELECT pi FROM ProductInventory pi WHERE pi.lastStockUpdate <= :cutoffTime")
    List<ProductInventory> findProductsNotUpdatedSince(@Param("cutoffTime") java.time.LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(pi) FROM ProductInventory pi WHERE pi.lowStockAlert = true")
    long countLowStockProducts();
    
    @Query("SELECT COUNT(pi) FROM ProductInventory pi WHERE pi.availableStock = 0 AND pi.trackInventory = true")
    long countOutOfStockProducts();
} 