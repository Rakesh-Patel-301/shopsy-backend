package com.example.ecommerce.controller;

import com.example.ecommerce.model.SupportTicket;
import com.example.ecommerce.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupportTicketController {
    
    private final SupportTicketService supportTicketService;
    
    @PostMapping("/tickets")
    public ResponseEntity<SupportTicket> createTicket(@RequestBody SupportTicket ticket) {
        SupportTicket createdTicket = supportTicketService.createTicket(ticket);
        return ResponseEntity.ok(createdTicket);
    }
    
    @GetMapping("/tickets")
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        List<SupportTicket> tickets = supportTicketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/tickets/{id}")
    public ResponseEntity<SupportTicket> getTicketById(@PathVariable Long id) {
        return supportTicketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tickets/customer/{email}")
    public ResponseEntity<List<SupportTicket>> getTicketsByEmail(@PathVariable String email) {
        List<SupportTicket> tickets = supportTicketService.getTicketsByEmail(email);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/tickets/active")
    public ResponseEntity<List<SupportTicket>> getActiveTickets() {
        List<SupportTicket> tickets = supportTicketService.getActiveTickets();
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/tickets/status/{status}")
    public ResponseEntity<List<SupportTicket>> getTicketsByStatus(@PathVariable String status) {
        try {
            SupportTicket.TicketStatus ticketStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
            List<SupportTicket> tickets = supportTicketService.getTicketsByStatus(ticketStatus);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/tickets/priority/{priority}")
    public ResponseEntity<List<SupportTicket>> getTicketsByPriority(@PathVariable String priority) {
        try {
            SupportTicket.TicketPriority ticketPriority = SupportTicket.TicketPriority.valueOf(priority.toUpperCase());
            List<SupportTicket> tickets = supportTicketService.getTicketsByPriority(ticketPriority);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/tickets/category/{category}")
    public ResponseEntity<List<SupportTicket>> getTicketsByCategory(@PathVariable String category) {
        try {
            SupportTicket.TicketCategory ticketCategory = SupportTicket.TicketCategory.valueOf(category.toUpperCase());
            List<SupportTicket> tickets = supportTicketService.getTicketsByCategory(ticketCategory);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<SupportTicket> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            SupportTicket.TicketStatus status = SupportTicket.TicketStatus.valueOf(request.get("status").toUpperCase());
            SupportTicket updatedTicket = supportTicketService.updateTicketStatus(id, status);
            return ResponseEntity.ok(updatedTicket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/tickets/{id}/assign")
    public ResponseEntity<SupportTicket> assignTicket(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String agentName = request.get("agentName");
            SupportTicket updatedTicket = supportTicketService.assignTicket(id, agentName);
            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/tickets/{id}/priority")
    public ResponseEntity<SupportTicket> updateTicketPriority(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            SupportTicket.TicketPriority priority = SupportTicket.TicketPriority.valueOf(request.get("priority").toUpperCase());
            SupportTicket updatedTicket = supportTicketService.updateTicketPriority(id, priority);
            return ResponseEntity.ok(updatedTicket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        supportTicketService.deleteTicket(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/tickets/stats")
    public ResponseEntity<Map<String, Long>> getTicketStats() {
        Map<String, Long> stats = Map.of(
            "open", supportTicketService.getTicketCountByStatus(SupportTicket.TicketStatus.OPEN),
            "inProgress", supportTicketService.getTicketCountByStatus(SupportTicket.TicketStatus.IN_PROGRESS),
            "resolved", supportTicketService.getTicketCountByStatus(SupportTicket.TicketStatus.RESOLVED),
            "closed", supportTicketService.getTicketCountByStatus(SupportTicket.TicketStatus.CLOSED)
        );
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/tickets/agent/{agent}")
    public ResponseEntity<List<SupportTicket>> getTicketsByAgent(@PathVariable String agent) {
        List<SupportTicket> tickets = supportTicketService.getTicketsByAssignedAgent(agent);
        return ResponseEntity.ok(tickets);
    }
} 