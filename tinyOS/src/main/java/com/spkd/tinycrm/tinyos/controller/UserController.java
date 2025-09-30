package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.entity.User;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:8080"})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long currentUserId = (Long) session.getAttribute("userId");
            
            // Check if current user is admin
            Optional<User> currentUserOpt = userService.findById(currentUserId);
            if (currentUserOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Current user not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User currentUser = currentUserOpt.get();
            if (currentUser.getRole() != User.Role.ADMIN) {
                response.put("success", false);
                response.put("message", "Access denied. Admin privileges required");
                return ResponseEntity.status(403).body(response);
            }
            
            // Check if target user exists
            Optional<User> targetUserOpt = userService.findById(id);
            if (targetUserOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            // Prevent admin from deleting themselves
            if (currentUserId.equals(id)) {
                response.put("success", false);
                response.put("message", "Cannot delete your own account");
                return ResponseEntity.badRequest().body(response);
            }
            
            User targetUser = targetUserOpt.get();
            
            // Clean up user's avatar file if exists
            if (targetUser.getAvatarUrl() != null && !targetUser.getAvatarUrl().isEmpty()) {
                try {
                    String filename = targetUser.getAvatarUrl().substring(targetUser.getAvatarUrl().lastIndexOf("/") + 1);
                    java.nio.file.Path filePath = java.nio.file.Paths.get("uploads/avatars/" + filename);
                    java.nio.file.Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    // Log but don't fail the deletion
                    System.err.println("Could not delete avatar file: " + e.getMessage());
                }
            }
            
            userService.deleteUser(id);
            
            response.put("success", true);
            response.put("message", "User deleted successfully");
            response.put("deletedUser", targetUser.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<Map<String, Object>> getAllUsersForAdmin(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long currentUserId = (Long) session.getAttribute("userId");
            
            // Check if current user is admin
            Optional<User> currentUserOpt = userService.findById(currentUserId);
            if (currentUserOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Current user not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User currentUser = currentUserOpt.get();
            if (currentUser.getRole() != User.Role.ADMIN) {
                response.put("success", false);
                response.put("message", "Access denied. Admin privileges required");
                return ResponseEntity.status(403).body(response);
            }
            
            List<User> users = userService.getAllUsers();
            response.put("success", true);
            response.put("users", users);
            response.put("totalUsers", users.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
