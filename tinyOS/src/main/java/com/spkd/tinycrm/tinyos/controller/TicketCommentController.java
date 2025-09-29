package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import com.spkd.tinycrm.tinyos.entity.TicketComment;
import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.service.InboxService;
import com.spkd.tinycrm.tinyos.service.SupportTicketService;
import com.spkd.tinycrm.tinyos.service.TicketCommentService;
import com.spkd.tinycrm.tinyos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:8080"})
public class TicketCommentController {

    @Autowired
    private TicketCommentService ticketCommentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SupportTicketService supportTicketService;
    
    @Autowired
    private InboxService inboxService;

    @GetMapping
    public List<TicketComment> getAllComments() {
        return ticketCommentService.getAllComments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketComment> getCommentById(@PathVariable Long id) {
        return ticketCommentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Map<String, Object>> getCommentsByTicketId(@PathVariable Long ticketId) {
        try {
            // First check if ticket exists
            Optional<SupportTicket> ticketOpt = supportTicketService.getTicketById(ticketId);
            if (ticketOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Ticket not found");
                return ResponseEntity.status(404).body(response);
            }
            
            List<TicketComment> comments = ticketCommentService.getCommentsByTicketId(ticketId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("comments", comments);
            response.put("ticketId", ticketId);
            response.put("totalComments", comments.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving comments: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(@RequestBody TicketComment ticketComment, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long userId = (Long) session.getAttribute("userId");
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User currentUser = userOpt.get();
            
            // Set comment author and creation date
            ticketComment.setAuthor(currentUser.getUsername());
            ticketComment.setCreatedDate(new Date());
            
            // Save the comment
            TicketComment savedComment = ticketCommentService.createComment(ticketComment);
            
            // Create inbox notifications for ticket owner and other participants
            if (savedComment.getSupportTicket() != null) {
                SupportTicket ticket = savedComment.getSupportTicket();
                String notificationTitle = "New comment on ticket #" + ticket.getId();
                String notificationMessage = currentUser.getFirstName() + " " + currentUser.getLastName() + 
                                           " added a comment: " + ticketComment.getComment();
                
                // Notify ticket creator if it's not the current user
                if (ticket.getCreatedBy() != null && !ticket.getCreatedBy().equals(currentUser.getUsername())) {
                    Optional<User> ticketCreatorOpt = userService.findByUsername(ticket.getCreatedBy());
                    if (ticketCreatorOpt.isPresent()) {
                        inboxService.createCommentNotification(
                            ticketCreatorOpt.get(), 
                            currentUser, 
                            savedComment,
                            notificationTitle,
                            notificationMessage
                        );
                    }
                }
                
                // TODO: Notify other users who have commented on this ticket
                // This would require getting all unique comment authors for this ticket
            }
            
            response.put("success", true);
            response.put("message", "Comment added successfully");
            response.put("comment", savedComment);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating comment: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketComment> updateComment(@PathVariable Long id, @RequestBody TicketComment ticketComment) {
        TicketComment updatedComment = ticketCommentService.updateComment(id, ticketComment);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        ticketCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}