package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.dto.UserProfileDto;
import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:8080"})
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long userId = (Long) session.getAttribute("userId");
            Optional<User> userOptional = userService.findById(userId);
            
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User user = userOptional.get();
            UserProfileDto profileDto = convertToDto(user);
            
            response.put("success", true);
            response.put("profile", profileDto);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving profile: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @RequestBody UserProfileDto profileDto, 
            HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long userId = (Long) session.getAttribute("userId");
            Optional<User> userOptional = userService.findById(userId);
            
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User user = userOptional.get();
            
            // Update user profile fields
            user.setFirstName(profileDto.getFirstName());
            user.setLastName(profileDto.getLastName());
            user.setEmail(profileDto.getEmail());
            user.setPhoneNumber(profileDto.getPhoneNumber());
            user.setAddress(profileDto.getAddress());
            user.setCity(profileDto.getCity());
            user.setState(profileDto.getState());
            user.setZipCode(profileDto.getZipCode());
            user.setCountry(profileDto.getCountry());
            user.setBio(profileDto.getBio());
            user.setJobTitle(profileDto.getJobTitle());
            user.setDepartment(profileDto.getDepartment());
            user.setCompany(profileDto.getCompany());
            user.setDateOfBirth(profileDto.getDateOfBirth());
            user.setTimezone(profileDto.getTimezone());
            user.setLanguage(profileDto.getLanguage());
            user.setEmailNotifications(profileDto.isEmailNotifications());
            user.setSmsNotifications(profileDto.isSmsNotifications());
            
            // Save updated user
            User updatedUser = userService.save(user);
            UserProfileDto updatedProfileDto = convertToDto(updatedUser);
            
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("profile", updatedProfileDto);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null || session.getAttribute("userId") == null) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            
            Long userId = (Long) session.getAttribute("userId");
            Optional<User> userOptional = userService.findById(userId);
            
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "File size must be less than 5MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Please upload a valid image file");
                return ResponseEntity.badRequest().body(response);
            }
            
            // For now, we'll just store a placeholder URL
            // In production, you would upload to AWS S3, Cloudinary, etc.
            String avatarUrl = "/avatars/user_" + userId + "_" + System.currentTimeMillis() + ".jpg";
            
            User user = userOptional.get();
            user.setAvatarUrl(avatarUrl);
            userService.save(user);
            
            response.put("success", true);
            response.put("message", "Avatar uploaded successfully");
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error uploading avatar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        try {
            Optional<User> userOptional = userService.findById(userId);
            Map<String, Object> response = new HashMap<>();
            
            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(404).body(response);
            }
            
            User user = userOptional.get();
            UserProfileDto profileDto = convertToPublicDto(user); // Only public info
            
            response.put("success", true);
            response.put("profile", profileDto);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving profile: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    private UserProfileDto convertToDto(User user) {
        return new UserProfileDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getCity(),
            user.getState(),
            user.getZipCode(),
            user.getCountry(),
            user.getBio(),
            user.getAvatarUrl(),
            user.getJobTitle(),
            user.getDepartment(),
            user.getCompany(),
            user.getDateOfBirth(),
            user.getTimezone(),
            user.getLanguage(),
            user.isEmailNotifications(),
            user.isSmsNotifications(),
            user.getCreatedAt(),
            user.getLastLoginAt()
        );
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