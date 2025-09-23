package com.spkd.tinycrm.tinyos.service;

import com.spkd.tinycrm.tinyos.dto.LoginRequest;
import com.spkd.tinycrm.tinyos.dto.LoginResponse;
import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.entity.UserSession;
import com.spkd.tinycrm.tinyos.repository.UserRepository;
import com.spkd.tinycrm.tinyos.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserSessionRepository sessionRepository;
    
    private final SecureRandom secureRandom = new SecureRandom();
    private static final int SESSION_DURATION_HOURS = 24;
    private static final int REMEMBER_ME_DURATION_DAYS = 30;

    public LoginResponse login(LoginRequest loginRequest, String ipAddress, String userAgent) {
        try {
            // Find user by username or email
            Optional<User> userOpt = userRepository.findByUsernameOrEmailAndActive(loginRequest.getUsername());
            
            if (!userOpt.isPresent()) {
                return new LoginResponse(false, "Invalid username or password");
            }
            
            User user = userOpt.get();
            
            // Verify password
            if (!verifyPassword(loginRequest.getPassword(), user.getPassword())) {
                return new LoginResponse(false, "Invalid username or password");
            }
            
            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Create session
            String sessionToken = generateSessionToken();
            LocalDateTime expiresAt = calculateExpirationTime(loginRequest.isRememberMe());
            
            UserSession session = new UserSession(sessionToken, user, expiresAt);
            session.setIpAddress(ipAddress);
            session.setUserAgent(userAgent);
            sessionRepository.save(session);
            
            return new LoginResponse(sessionToken, user, expiresAt);
            
        } catch (Exception e) {
            return new LoginResponse(false, "Login failed: " + e.getMessage());
        }
    }

    public boolean logout(String sessionToken) {
        try {
            Optional<UserSession> sessionOpt = sessionRepository.findBySessionTokenAndActiveTrue(sessionToken);
            if (sessionOpt.isPresent()) {
                UserSession session = sessionOpt.get();
                session.setActive(false);
                sessionRepository.save(session);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<User> validateSession(String sessionToken) {
        try {
            Optional<UserSession> sessionOpt = sessionRepository.findBySessionTokenAndActiveTrue(sessionToken);
            
            if (!sessionOpt.isPresent()) {
                return Optional.empty();
            }
            
            UserSession session = sessionOpt.get();
            
            // Check if session is expired
            if (session.isExpired()) {
                session.setActive(false);
                sessionRepository.save(session);
                return Optional.empty();
            }
            
            // Update last accessed time
            session.setLastAccessedAt(LocalDateTime.now());
            sessionRepository.save(session);
            
            return Optional.of(session.getUser());
            
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public User registerUser(User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Hash password
        user.setPassword(hashPassword(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public void logoutAllUserSessions(User user) {
        sessionRepository.deactivateAllUserSessions(user);
    }

    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inactiveThreshold = now.minusDays(7); // Sessions inactive for 7 days
        sessionRepository.deactivateExpiredSessions(now, inactiveThreshold);
    }

    private String generateSessionToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private LocalDateTime calculateExpirationTime(boolean rememberMe) {
        if (rememberMe) {
            return LocalDateTime.now().plusDays(REMEMBER_ME_DURATION_DAYS);
        } else {
            return LocalDateTime.now().plusHours(SESSION_DURATION_HOURS);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }
}
