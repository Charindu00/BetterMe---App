package com.betterme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCalendar {

    private int year;
    private int month;
    private String monthName; // "February", "March", etc.

    // CALENDAR DATA
    /**
     * All dates where at least one habit was checked in
     */
    private Set<LocalDate> checkedDates;

    /**
     * Per-habit breakdown for the month
     */
    private List<HabitMonthData> habits;

    // SUMMARY
    private int totalDaysInMonth;
    private int daysWithCheckIns;
    private double monthlyCompletionRate;

    /**
     * Check-in data for a single habit in this month
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabitMonthData {
        private Long habitId;
        private String habitName;
        private String icon;
        private Set<LocalDate> checkedDates;
        private int checkInCount;
        private int currentStreak;
    }
}
