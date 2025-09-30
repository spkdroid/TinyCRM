package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.dto.TicketUpdateRequest;
import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.service.AuthenticationService;
import com.spkd.tinycrm.tinyos.service.SupportTicketService;
import com.spkd.tinycrm.tinyos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:8080"})
public class SupportTicketController {

    @Autowired
    private SupportTicketService supportTicketService;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllTickets(HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            List<SupportTicket> tickets = supportTicketService.getAllTickets();
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error fetching tickets: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            return supportTicketService.getTicketById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error fetching ticket: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody SupportTicket supportTicket, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            // Set the user who created the ticket
            supportTicket.setCreatedBy(userOpt.get().getUsername());
            SupportTicket createdTicket = supportTicketService.createTicket(supportTicket);
            return ResponseEntity.ok(createdTicket);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error creating ticket: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @RequestBody SupportTicket supportTicket, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            SupportTicket updatedTicket = supportTicketService.updateTicket(id, supportTicket);
            if (updatedTicket != null) {
                return ResponseEntity.ok(updatedTicket);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error updating ticket: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> statusUpdate, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            String newStatus = statusUpdate.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Status is required"));
            }
            
            SupportTicket updatedTicket = supportTicketService.updateTicketStatus(id, newStatus);
            if (updatedTicket != null) {
                return ResponseEntity.ok(updatedTicket);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error updating ticket status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            supportTicketService.deleteTicket(id);
            return ResponseEntity.ok(createSuccessResponse("Ticket deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error deleting ticket: " + e.getMessage()));
        }
    }

    // Enhanced ticket update with partial updates
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchTicket(@PathVariable Long id, @RequestBody TicketUpdateRequest updateRequest, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            SupportTicket updatedTicket = supportTicketService.patchTicket(id, updateRequest);
            if (updatedTicket != null) {
                return ResponseEntity.ok(updatedTicket);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error updating ticket: " + e.getMessage()));
        }
    }

    @GetMapping("/assignable-users")
    public ResponseEntity<?> getAssignableUsers(HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            List<User> users = userService.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error fetching assignable users: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignTicket(@PathVariable Long id, @RequestBody Map<String, Object> assignmentData, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            Long assigneeId = null;
            if (assignmentData.get("assigneeId") != null) {
                assigneeId = Long.valueOf(assignmentData.get("assigneeId").toString());
            }
            
            Optional<SupportTicket> ticketOpt = supportTicketService.getTicketById(id);
            if (!ticketOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            SupportTicket ticket = ticketOpt.get();
            
            if (assigneeId != null) {
                Optional<User> assigneeOpt = userService.findById(assigneeId);
                if (!assigneeOpt.isPresent()) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Assignee not found"));
                }
                ticket.setAssignee(assigneeOpt.get());
            } else {
                ticket.setAssignee(null);
            }
            
            SupportTicket updatedTicket = supportTicketService.updateTicket(id, ticket);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", assigneeId != null ? "Ticket assigned successfully" : "Ticket unassigned successfully");
            response.put("ticket", updatedTicket);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error assigning ticket: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getTicketComments(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> userOpt = validateSession(request);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body(createErrorResponse("Authentication required"));
        }
        
        try {
            // This will be handled by the TicketCommentController, but keeping for compatibility
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Please use /api/comments/ticket/" + id + " endpoint");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("Error: " + e.getMessage()));
        }
    }

    // Helper methods
    private Optional<User> validateSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return Optional.empty();
            }
            
            String sessionToken = (String) session.getAttribute("sessionToken");
            if (sessionToken == null) {
                return Optional.empty();
            }
            
            return authenticationService.validateSession(sessionToken);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }
}
