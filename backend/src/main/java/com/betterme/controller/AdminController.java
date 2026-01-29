package com.betterme.controller;

import com.betterme.dto.StatsResponse;
import com.betterme.dto.UserResponse;
import com.betterme.model.ActivityLog;
import com.betterme.model.Role;
import com.betterme.model.User;
import com.betterme.repository.ActivityLogRepository;
import com.betterme.repository.AnnouncementRepository;
import com.betterme.repository.UserRepository;
import com.betterme.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Admin Controller ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ All endpoints here are protected by .hasRole("ADMIN") in SecurityConfig ║
 * ║ ║
 * ║ This provides a central dashboard for administrators to: ║
 * ║ 1. View system statistics ║
 * ║ 2. Manage users ║
 * ║ 3. View activity logs ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AnnouncementRepository announcementRepository;
    private final ActivityLogService activityLogService;

    // ═══════════════════════════════════════════════════════════════════════
    // DASHBOARD STATS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        // Count users by role
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        // Activity stats
        long totalLogs = activityLogRepository.count();
        Map<String, Long> activityBreakdown = new HashMap<>();
        activityBreakdown.put("logins", activityLogRepository.countByAction("USER_LOGIN"));
        activityBreakdown.put("registrations", activityLogRepository.countByAction("USER_REGISTERED"));
        activityBreakdown.put("failedLogins", activityLogRepository.countByAction("LOGIN_FAILED"));

        // Announcement stats
        long totalAnnouncements = announcementRepository.count();
        long activeAnnouncements = announcementRepository
                .findByIsActiveTrueOrderByPriorityDescCreatedAtDesc().size();

        StatsResponse stats = StatsResponse.builder()
                .totalUsers(totalUsers)
                .totalAdmins(totalAdmins)
                .totalActivityLogs(totalLogs)
                .totalAnnouncements(totalAnnouncements)
                .activeAnnouncements(activeAnnouncements)
                .activityBreakdown(activityBreakdown)
                .build();

        return ResponseEntity.ok(stats);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // USER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get all users (without passwords!)
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    /**
     * Delete a user (cannot delete yourself!)
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            HttpServletRequest request) {

        // Prevent self-deletion
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "You cannot delete your own account!"));
        }

        // Check if user exists
        User userToDelete = userRepository.findById(id)
                .orElse(null);

        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }

        // Log the action
        activityLogService.log(
                currentUser,
                "USER_DELETED",
                "Admin deleted user: " + userToDelete.getEmail(),
                request);

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    /**
     * Change a user's role
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User currentUser,
            HttpServletRequest httpRequest) {

        // Prevent changing own role
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "You cannot change your own role!"));
        }

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String newRole = request.get("role");
        if (newRole == null || (!newRole.equals("USER") && !newRole.equals("ADMIN"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role. Must be USER or ADMIN"));
        }

        Role oldRole = user.getRole();
        user.setRole(Role.valueOf(newRole));
        userRepository.save(user);

        // Log the action
        activityLogService.log(
                currentUser,
                "ROLE_CHANGED",
                "Changed role for " + user.getEmail() + " from " + oldRole + " to " + newRole,
                httpRequest);

        return ResponseEntity.ok(Map.of(
                "message", "Role updated successfully",
                "user", user.getEmail(),
                "newRole", newRole));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ACTIVITY LOGS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get recent activity logs
     */
    @GetMapping("/activity-logs")
    public ResponseEntity<List<ActivityLog>> getActivityLogs() {
        return ResponseEntity.ok(activityLogService.getRecentLogs());
    }
}
