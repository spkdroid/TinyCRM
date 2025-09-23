package com.spkd.tinycrm.tinyos.controller;

import com.spkd.tinycrm.tinyos.dto.LoginRequest;
import com.spkd.tinycrm.tinyos.dto.LoginResponse;
import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            LoginResponse response = authenticationService.login(loginRequest, ipAddress, userAgent);
            
            if (response.isSuccess()) {
                // Store session token in HTTP session for easy access
                HttpSession session = request.getSession(true);
                session.setAttribute("sessionToken", response.getSessionToken());
                session.setAttribute("userId", response.getUser().getId());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse(false, "Login failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session != null) {
                String sessionToken = (String) session.getAttribute("sessionToken");
                if (sessionToken != null) {
                    authenticationService.logout(sessionToken);
                }
                session.invalidate();
            }
            
            response.put("success", true);
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        try {
            User registeredUser = authenticationService.registerUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", registeredUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session == null) {
                response.put("success", false);
                response.put("message", "No active session");
                return ResponseEntity.status(401).body(response);
            }
            
            String sessionToken = (String) session.getAttribute("sessionToken");
            if (sessionToken == null) {
                response.put("success", false);
                response.put("message", "No session token found");
                return ResponseEntity.status(401).body(response);
            }
            
            Optional<User> userOpt = authenticationService.validateSession(sessionToken);
            if (userOpt.isPresent()) {
                response.put("success", true);
                response.put("user", userOpt.get());
                return ResponseEntity.ok(response);
            } else {
                session.invalidate();
                response.put("success", false);
                response.put("message", "Invalid or expired session");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error validating session: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, Object>> logoutAllSessions(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Map<String, Object> response = new HashMap<>();
            
            if (session != null) {
                String sessionToken = (String) session.getAttribute("sessionToken");
                if (sessionToken != null) {
                    Optional<User> userOpt = authenticationService.validateSession(sessionToken);
                    if (userOpt.isPresent()) {
                        authenticationService.logoutAllUserSessions(userOpt.get());
                    }
                }
                session.invalidate();
            }
            
            response.put("success", true);
            response.put("message", "All sessions logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error logging out all sessions: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0].trim();
        }
    }
}
