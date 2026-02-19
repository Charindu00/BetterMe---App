package com.betterme.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 */
@Entity
@Table(name = "habit_checkins",
        // Unique constraint: One check-in per habit per day
        uniqueConstraints = @UniqueConstraint(columnNames = { "habit_id",
                "checkin_date" }, name = "uk_habit_checkin_date"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Which habit was this check-in for?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    /**
     * Which date was this check-in for?
     * Using LocalDate (date only, no time)
     */
    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkInDate;

    /**
     * Was the habit completed?
     * true = completed, false = skipped/partial
     */
    @Builder.Default
    private Boolean completed = true;

    /**
     * Optional notes about the check-in
     */
    @Column(length = 500)
    private String notes;

    /**
     * When was this record created?
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.checkInDate == null) {
            this.checkInDate = LocalDate.now();
        }
    }
}
