package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.entity.UserMessage;
import com.spkd.tinycrm.tinyos.service.UserMessageService;
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
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:8080"})
public class UserMessageController {

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMessages(HttpServletRequest request) {
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
            List<UserMessage> messages = userMessageService.getMessagesForUser(user);
            long unreadCount = userMessageService.getUnreadCountForUser(user);
            
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

    @GetMapping("/sent")
    public ResponseEntity<Map<String, Object>> getSentMessages(HttpServletRequest request) {
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
            List<UserMessage> sentMessages = userMessageService.getSentMessagesByUser(user);
            
            response.put("success", true);
            response.put("messages", sentMessages);
            response.put("totalMessages", sentMessages.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving sent messages: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Map<String, Object>> getConversation(@PathVariable Long userId, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long currentUserId = (Long) session.getAttribute("userId");
            Optional<User> currentUserOpt = userService.findById(currentUserId);
            Optional<User> otherUserOpt = userService.findById(userId);
            
            if (currentUserOpt.isEmpty() || otherUserOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User currentUser = currentUserOpt.get();
            User otherUser = otherUserOpt.get();
            
            List<UserMessage> conversation = userMessageService.getConversationBetweenUsers(currentUser, otherUser);
            
            // Mark messages from the other user as read
            userMessageService.markConversationAsRead(currentUser, otherUser);
            
            response.put("success", true);
            response.put("messages", conversation);
            response.put("participant", otherUser);
            response.put("totalMessages", conversation.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving conversation: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> messageData, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long senderId = (Long) session.getAttribute("userId");
            Long recipientId = Long.valueOf(messageData.get("recipientId").toString());
            String subject = messageData.get("subject").toString();
            String message = messageData.get("message").toString();
            String priorityStr = messageData.getOrDefault("priority", "NORMAL").toString();
            
            Optional<User> senderOpt = userService.findById(senderId);
            Optional<User> recipientOpt = userService.findById(recipientId);
            
            if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Sender or recipient not found");
                return ResponseEntity.status(404).body(response);
            }
            
            UserMessage.MessagePriority priority;
            try {
                priority = UserMessage.MessagePriority.valueOf(priorityStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                priority = UserMessage.MessagePriority.NORMAL;
            }
            
            UserMessage sentMessage = userMessageService.sendMessage(
                senderOpt.get(), 
                recipientOpt.get(), 
                subject, 
                message, 
                priority
            );
            
            response.put("success", true);
            response.put("message", "Message sent successfully");
            response.put("sentMessage", sentMessage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error sending message: " + e.getMessage());
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
            Optional<UserMessage> messageOpt = userMessageService.getMessageById(messageId);
            
            if (messageOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Message not found");
                return ResponseEntity.status(404).body(response);
            }
            
            UserMessage message = messageOpt.get();
            
            // Check if user is the recipient
            if (!message.getRecipient().getId().equals(userId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return ResponseEntity.status(403).body(response);
            }
            
            UserMessage updatedMessage = userMessageService.markAsRead(messageId);
            
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
            Optional<UserMessage> messageOpt = userMessageService.getMessageById(messageId);
            
            if (messageOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Message not found");
                return ResponseEntity.status(404).body(response);
            }
            
            UserMessage message = messageOpt.get();
            
            // Check if user is either sender or recipient
            if (!message.getSender().getId().equals(userId) && !message.getRecipient().getId().equals(userId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return ResponseEntity.status(403).body(response);
            }
            
            userMessageService.deleteMessage(messageId);
            
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

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAvailableUsers(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long currentUserId = (Long) session.getAttribute("userId");
            List<User> allUsers = userService.getAllUsers();
            
            // Remove current user from the list and convert to DTOs
            List<Map<String, Object>> userDtos = allUsers.stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .map(user -> {
                    Map<String, Object> userDto = new HashMap<>();
                    userDto.put("id", user.getId());
                    userDto.put("username", user.getUsername());
                    userDto.put("firstName", user.getFirstName());
                    userDto.put("lastName", user.getLastName());
                    userDto.put("email", user.getEmail());
                    userDto.put("avatarUrl", user.getAvatarUrl());
                    userDto.put("jobTitle", user.getJobTitle());
                    userDto.put("department", user.getDepartment());
                    return userDto;
                })
                .collect(java.util.stream.Collectors.toList());
            
            response.put("success", true);
            response.put("users", userDtos);
            response.put("totalUsers", userDtos.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}