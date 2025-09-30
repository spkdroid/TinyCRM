package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.entity.InboxMessage;
import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.service.InboxService;
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
@RequestMapping("/api/inbox")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:8080"})
public class InboxController {

    @Autowired
    private InboxService inboxService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getInboxMessages(HttpServletRequest request) {
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
            
            User user = userOpt.get();
            List<InboxMessage> messages = inboxService.getMessagesForUser(user);
            long unreadCount = inboxService.getUnreadCountForUser(user);
            
            response.put("success", true);
            response.put("messages", messages);
            response.put("unreadCount", unreadCount);
            response.put("totalMessages", messages.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving messages: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadMessages(HttpServletRequest request) {
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
            
            User user = userOpt.get();
            List<InboxMessage> unreadMessages = inboxService.getUnreadMessagesForUser(user);
            
            response.put("success", true);
            response.put("messages", unreadMessages);
            response.put("count", unreadMessages.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving unread messages: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(HttpServletRequest request) {
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
            
            User user = userOpt.get();
            long unreadCount = inboxService.getUnreadCountForUser(user);
            
            response.put("success", true);
            response.put("unreadCount", unreadCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving unread count: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long messageId, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long userId = (Long) session.getAttribute("userId");
            Optional<InboxMessage> messageOpt = inboxService.getMessageById(messageId);
            
            if (messageOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Message not found");
                return ResponseEntity.status(404).body(response);
            }
            
            InboxMessage message = messageOpt.get();
            
            // Check if user owns this message
            if (!message.getRecipient().getId().equals(userId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return ResponseEntity.status(403).body(response);
            }
            
            InboxMessage updatedMessage = inboxService.markAsRead(messageId);
            
            response.put("success", true);
            response.put("message", "Message marked as read");
            response.put("updatedMessage", updatedMessage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error marking message as read: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(HttpServletRequest request) {
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
            
            User user = userOpt.get();
            inboxService.markAllAsReadForUser(user);
            
            response.put("success", true);
            response.put("message", "All messages marked as read");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error marking all messages as read: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long messageId, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long userId = (Long) session.getAttribute("userId");
            Optional<InboxMessage> messageOpt = inboxService.getMessageById(messageId);
            
            if (messageOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Message not found");
                return ResponseEntity.status(404).body(response);
            }
            
            InboxMessage message = messageOpt.get();
            
            // Check if user owns this message
            if (!message.getRecipient().getId().equals(userId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return ResponseEntity.status(403).body(response);
            }
            
            inboxService.deleteMessage(messageId);
            
            response.put("success", true);
            response.put("message", "Message deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting message: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}