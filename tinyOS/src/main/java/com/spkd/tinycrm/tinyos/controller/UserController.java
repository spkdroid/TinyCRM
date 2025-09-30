package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.dto.UserProfileDto;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:8080"})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<UserProfileDto> userDtos = users.stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", userDtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userService.getUserById(id);
            Map<String, Object> response = new HashMap<>();
            
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User user = userOptional.get();
            response.put("success", true);
            response.put("user", convertToPublicDto(user));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", convertToPublicDto(createdUser));
            response.put("message", "User created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            Map<String, Object> response = new HashMap<>();
            
            if (updatedUser != null) {
                response.put("success", true);
                response.put("user", convertToPublicDto(updatedUser));
                response.put("message", "User updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
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
                    java.nio.file.Path filePath = java.nio.file.Paths.get("/tmp/uploads/avatars/" + filename);
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
            List<UserProfileDto> userDtos = users.stream()
                .map(this::convertToPublicDto)
                .collect(Collectors.toList());
            
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
    
    private UserProfileDto convertToPublicDto(User user) {
        // Public profile - limited information
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setJobTitle(user.getJobTitle());
        dto.setDepartment(user.getDepartment());
        dto.setCompany(user.getCompany());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
