package com.example.ecommerce.service;

import com.example.ecommerce.model.SupportTicket;
import com.example.ecommerce.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportTicketService {
    
    private final SupportTicketRepository supportTicketRepository;
    
    public SupportTicket createTicket(SupportTicket ticket) {
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        if (ticket.getPriority() == null) {
            ticket.setPriority(SupportTicket.TicketPriority.MEDIUM);
        }
        return supportTicketRepository.save(ticket);
    }
    
    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAll();
    }
    
    public Optional<SupportTicket> getTicketById(Long id) {
        return supportTicketRepository.findById(id);
    }
    
    public List<SupportTicket> getTicketsByEmail(String email) {
        return supportTicketRepository.findByCustomerEmailOrderByCreatedAtDesc(email);
    }
    
    public List<SupportTicket> getActiveTickets() {
        return supportTicketRepository.findActiveTickets();
    }
    
    public List<SupportTicket> getTicketsByStatus(SupportTicket.TicketStatus status) {
        return supportTicketRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    public List<SupportTicket> getTicketsByPriority(SupportTicket.TicketPriority priority) {
        return supportTicketRepository.findByPriorityOrderByCreatedAtDesc(priority);
    }
    
    public List<SupportTicket> getTicketsByCategory(SupportTicket.TicketCategory category) {
        return supportTicketRepository.findByCategoryOrderByCreatedAtDesc(category);
    }
    
    public SupportTicket updateTicketStatus(Long ticketId, SupportTicket.TicketStatus status) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            ticket.setStatus(status);
            ticket.setUpdatedAt(LocalDateTime.now());
            
            if (status == SupportTicket.TicketStatus.RESOLVED || status == SupportTicket.TicketStatus.CLOSED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
            
            return supportTicketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with id: " + ticketId);
    }
    
    public SupportTicket assignTicket(Long ticketId, String agentName) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            ticket.setAssignedTo(agentName);
            ticket.setUpdatedAt(LocalDateTime.now());
            return supportTicketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with id: " + ticketId);
    }
    
    public SupportTicket updateTicketPriority(Long ticketId, SupportTicket.TicketPriority priority) {
        Optional<SupportTicket> optionalTicket = supportTicketRepository.findById(ticketId);
        if (optionalTicket.isPresent()) {
            SupportTicket ticket = optionalTicket.get();
            ticket.setPriority(priority);
            ticket.setUpdatedAt(LocalDateTime.now());
            return supportTicketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with id: " + ticketId);
    }
    
    public void deleteTicket(Long ticketId) {
        supportTicketRepository.deleteById(ticketId);
    }
    
    public long getTicketCountByStatus(SupportTicket.TicketStatus status) {
        return supportTicketRepository.countByStatus(status);
    }
    
    public List<SupportTicket> getTicketsByAssignedAgent(String agent) {
        return supportTicketRepository.findByAssignedAgent(agent);
    }
} 