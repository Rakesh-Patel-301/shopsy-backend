package com.example.ecommerce.repository;

import com.example.ecommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByActiveTrue();
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validFrom <= :now AND c.validUntil >= :now AND c.currentUsageCount < c.maxUsageLimit")
    List<Coupon> findValidCoupons(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validFrom <= :now AND c.validUntil >= :now AND c.currentUsageCount < c.maxUsageLimit AND c.minimumOrderAmount <= :orderAmount")
    List<Coupon> findValidCouponsForOrderAmount(@Param("now") LocalDateTime now, @Param("orderAmount") java.math.BigDecimal orderAmount);
    
    List<Coupon> findByDiscountType(Coupon.DiscountType discountType);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validUntil >= :now ORDER BY c.validUntil ASC")
    List<Coupon> findExpiringSoon(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Coupon c WHERE c.currentUsageCount >= c.maxUsageLimit * 0.8")
    List<Coupon> findNearlyExpiredUsage();
} 