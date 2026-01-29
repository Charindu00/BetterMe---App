package com.betterme.service;

import com.betterme.model.ActivityLog;
import com.betterme.model.User;
import com.betterme.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Service Layer for Logging ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ This service provides a clean API for logging activities. ║
 * ║ Other services just call: activityLogService.log(...) ║
 * ║ ║
 * ║ Benefits of this approach: ║
 * ║ 1. Single place to modify logging logic ║
 * ║ 2. Easy to add features (e.g., async logging, external services) ║
 * ║ 3. Clean separation of concerns ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * Log an activity for a known user
     */
    public void log(User user, String action, String details, HttpServletRequest request) {
        ActivityLog activityLog = ActivityLog.builder()
                .userId(user.getId())
                .userEmail(user.getEmail())
                .action(action)
                .details(details)
                .ipAddress(getClientIp(request))
                .build();

        activityLogRepository.save(activityLog);
        log.debug("Activity logged: {} - {} - {}", user.getEmail(), action, details);
    }

    /**
     * Log an activity without a user (e.g., failed login attempt)
     */
    public void logAnonymous(String action, String details, HttpServletRequest request) {
        ActivityLog activityLog = ActivityLog.builder()
                .action(action)
                .details(details)
                .ipAddress(getClientIp(request))
                .build();

        activityLogRepository.save(activityLog);
        log.debug("Anonymous activity logged: {} - {}", action, details);
    }

    /**
     * Get recent logs for admin dashboard
     */
    public List<ActivityLog> getRecentLogs() {
        return activityLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    /**
     * Get total count of all logs
     */
    public long getTotalCount() {
        return activityLogRepository.count();
    }

    /**
     * Extract client IP address from request
     * Handles proxy/load balancer scenarios
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // Check for forwarded headers (common with proxies/load balancers)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, first one is the client
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
