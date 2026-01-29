package com.betterme.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Announcements ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ A simple way for admins to communicate with all users. ║
 * ║ ║
 * ║ Use cases: ║
 * ║ 1. New feature announcements ║
 * ║ 2. System maintenance notices ║
 * ║ 3. Motivational messages ║
 * ║ 4. Important updates ║
 * ║ ║
 * ║ The isActive flag lets admins hide announcements without deleting them. ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Entity
@Table(name = "announcements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    /**
     * Is this announcement currently visible to users?
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Priority level (higher = more important)
     * Can be used for styling (e.g., high priority = red banner)
     */
    @Builder.Default
    private Integer priority = 0;

    /**
     * Who created this announcement?
     */
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
