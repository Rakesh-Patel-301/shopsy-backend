package com.example.ecommerce.repository;

import com.example.ecommerce.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Payment> findByStatusAndDateRange(@Param("status") Payment.PaymentStatus status, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    BigDecimal getTotalRevenueForPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED' AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    long getFailedPaymentCountForPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodStats();
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt <= :cutoffTime")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
} 