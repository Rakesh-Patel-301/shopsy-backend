package com.example.ecommerce.repository;

import com.example.ecommerce.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    List<SupportTicket> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    
    List<SupportTicket> findByStatusOrderByCreatedAtDesc(SupportTicket.TicketStatus status);
    
    List<SupportTicket> findByPriorityOrderByCreatedAtDesc(SupportTicket.TicketPriority priority);
    
    List<SupportTicket> findByCategoryOrderByCreatedAtDesc(SupportTicket.TicketCategory category);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.customerEmail = :email AND t.status IN ('OPEN', 'IN_PROGRESS') ORDER BY t.createdAt DESC")
    List<SupportTicket> findActiveTicketsByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.status = :status")
    long countByStatus(@Param("status") SupportTicket.TicketStatus status);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.assignedTo = :agent ORDER BY t.priority DESC, t.createdAt ASC")
    List<SupportTicket> findByAssignedAgent(@Param("agent") String agent);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.status IN ('OPEN', 'IN_PROGRESS') ORDER BY t.priority DESC, t.createdAt ASC")
    List<SupportTicket> findActiveTickets();
} 