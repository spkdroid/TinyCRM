package com.spkd.tinycrm.tinyos.config;

import com.spkd.tinycrm.tinyos.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "app.session.cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class SessionCleanupScheduler {

    @Autowired
    private AuthenticationService authenticationService;

    @Scheduled(fixedRateString = "${app.session.cleanup.interval:3600000}") // Default: 1 hour
    public void cleanupExpiredSessions() {
        try {
            authenticationService.cleanupExpiredSessions();
            System.out.println("Expired sessions cleanup completed");
        } catch (Exception e) {
            System.err.println("Error during session cleanup: " + e.getMessage());
        }
    }
}
