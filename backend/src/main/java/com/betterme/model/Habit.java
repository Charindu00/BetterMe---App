package com.betterme.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 */
@Entity
@Table(name = "habits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * HABIT DETAILS
     */
    @NotBlank(message = "Habit name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Description too long")
    private String description;

    /**
     * How often should this habit be done?
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private HabitFrequency frequency = HabitFrequency.DAILY;

    /**
     * Optional: Target for the habit (e.g., "30 minutes", "10 pages")
     */
    private String target;

    /**
     * Emoji/icon for visual display
     */
    @Builder.Default
    private String icon = "✅";

    /**
     * STREAK TRACKING
     * Streaks are calculated based on consecutive check-ins
     */
    @Column(name = "current_streak")
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(name = "longest_streak")
    @Builder.Default
    private Integer longestStreak = 0;

    @Column(name = "total_checkins")
    @Builder.Default
    private Integer totalCheckIns = 0;

    @Column(name = "last_checkin_date")
    private LocalDateTime lastCheckInDate;

    /**
     * RELATIONSHIPS
     * 
     * @ManyToOne = Many habits can belong to one user
     * @JoinColumn = Creates a user_id column in habits table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * TIMESTAMPS
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Is this habit still active or archived?
     */
    @Builder.Default
    private Boolean active = true;

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
