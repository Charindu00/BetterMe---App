package com.betterme.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 */
@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GOAL DETAILS

    @NotBlank(message = "Goal title is required")
    @Column(nullable = false)
    private String title;

    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GoalType type = GoalType.COUNT;

    /**
     * Emoji for visual display
     */
    @Builder.Default
    private String icon = "🎯";

    /**
     * Category for grouping (Health, Learning, Fitness, etc.)
     */
    private String category;

    // PROGRESS TRACKING

    @NotNull
    @Positive(message = "Target must be positive")
    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @Column(name = "current_value")
    @Builder.Default
    private Integer currentValue = 0;

    /**
     * Unit of measurement (books, miles, hours, days, etc.)
     */
    private String unit;

    // DATES

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // RELATIONSHIPS

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Optional: Link to a habit for automatic progress tracking
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_habit_id")
    private Habit linkedHabit;

    // STATUS

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Boolean completed = false;

    // TIMESTAMPS

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // HELPER METHODS

    /**
     * Calculate progress percentage (0-100)
     */
    public double getProgressPercentage() {
        if (targetValue == 0)
            return 0;
        return Math.min(100.0, (currentValue * 100.0) / targetValue);
    }

    /**
     * Check if goal is completed
     */
    public boolean isGoalCompleted() {
        return currentValue >= targetValue;
    }

    /**
     * Check if goal is overdue
     */
    public boolean isOverdue() {
        return deadline != null && LocalDate.now().isAfter(deadline) && !completed;
    }
}
