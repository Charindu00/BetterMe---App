package com.betterme.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Activity Logging (Audit Trail) ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Activity logs record WHAT users do in your application. ║
 * ║ ║
 * ║ Why it's important: ║
 * ║ 1. Security - Track suspicious activity ║
 * ║ 2. Debugging - See what led to an error ║
 * ║ 3. Analytics - Understand user behavior ║
 * ║ 4. Compliance - Many regulations require audit trails ║
 * ║ ║
 * ║ This is a simplified version. Production systems often use: ║
 * ║ - ELK Stack (Elasticsearch, Logstash, Kibana) ║
 * ║ - Dedicated audit logging libraries ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Entity
@Table(name = "activity_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Who performed the action?
     * Nullable because some actions (like failed login) might not have a user
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * User's email (for display purposes)
     */
    @Column(name = "user_email")
    private String userEmail;

    /**
     * What action was performed?
     * Examples: "USER_REGISTERED", "USER_LOGIN", "HABIT_CREATED"
     */
    @Column(nullable = false)
    private String action;

    /**
     * Additional details about the action
     * Examples: "Login from Chrome on Windows", "Created habit: Exercise"
     */
    @Column(length = 500)
    private String details;

    /**
     * IP address of the request
     * Useful for security analysis
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * When did this happen?
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
