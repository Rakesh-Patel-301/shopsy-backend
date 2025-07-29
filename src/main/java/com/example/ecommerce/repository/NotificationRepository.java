package com.example.ecommerce.repository;

import com.example.ecommerce.model.Notification;
import com.example.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);
    
    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.priority = :priority ORDER BY n.createdAt DESC")
    List<Notification> findByUserAndPriorityOrderByCreatedAtDesc(@Param("user") User user, @Param("priority") Notification.NotificationPriority priority);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.read = false")
    long countUnreadNotificationsByUser(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.user = :user AND n.read = false")
    void markAllAsReadByUser(@Param("user") User user, @Param("readAt") LocalDateTime readAt);
    
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.id = :id")
    void markAsReadById(@Param("id") Long id, @Param("readAt") LocalDateTime readAt);
    
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("since") LocalDateTime since);
    
    void deleteByUserAndReadTrue(User user);
} 