package com.betterme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Dashboard Summary DTO ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Aggregated statistics shown at the top of the dashboard. ║
 * ║ ║
 * ║ This is a "read-only" DTO - only used for responses, never requests. ║
 * ║ It aggregates data from multiple sources into one convenient object. ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {

    // ─────────────────────────────────────────────────────────────────────
    // HABIT COUNTS
    // ─────────────────────────────────────────────────────────────────────
    private long totalHabits;
    private long activeHabits;

    // ─────────────────────────────────────────────────────────────────────
    // GOAL COUNTS
    // ─────────────────────────────────────────────────────────────────────
    private long activeGoals;

    // ─────────────────────────────────────────────────────────────────────
    // TODAY'S PROGRESS
    // ─────────────────────────────────────────────────────────────────────
    private long completedToday;
    private long remainingToday;
    private double completionPercentage; // 0-100
    private double todayProgress; // same as completionPercentage, for frontend compatibility

    // ─────────────────────────────────────────────────────────────────────
    // STREAK STATS
    // ─────────────────────────────────────────────────────────────────────
    private int currentStreakTotal; // Sum of all current streaks
    private int longestStreak; // Best single habit streak
    private String longestStreakHabit; // Name of habit with longest streak

    // ─────────────────────────────────────────────────────────────────────
    // OVERALL STATS
    // ─────────────────────────────────────────────────────────────────────
    private long totalCheckIns; // All-time check-ins
    private int daysActive; // Days since first habit created

    // ─────────────────────────────────────────────────────────────────────
    // MOTIVATION
    // ─────────────────────────────────────────────────────────────────────
    private String motivationalQuote;
}
