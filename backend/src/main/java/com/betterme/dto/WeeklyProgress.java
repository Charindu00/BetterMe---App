package com.betterme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Weekly Progress DTO ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Shows the last 7 days of habit completion. ║
 * ║ ║
 * ║ Frontend can use this for: ║
 * ║ - Bar chart showing daily completions ║
 * ║ - Week-at-a-glance view ║
 * ║ - Progress trends ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyProgress {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<DayProgress> days;
    private int totalCompletions;
    private int totalPossible;
    private double weeklyCompletionRate; // 0-100

    /**
     * Progress for a single day
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayProgress {
        private LocalDate date;
        private String dayName; // "Mon", "Tue", etc.
        private int completed; // Habits completed
        private int total; // Total habits active that day
        private double percentage; // Completion percentage
        private boolean isToday;
    }
}
