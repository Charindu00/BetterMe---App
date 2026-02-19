package com.betterme.service;

import com.betterme.model.Habit;
import com.betterme.model.HabitCheckIn;
import com.betterme.model.User;
import com.betterme.repository.HabitCheckInRepository;
import com.betterme.repository.HabitRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final HabitRepository habitRepository;
    private final HabitCheckInRepository checkInRepository;

    // TREND DATA (for line/bar charts)

    /**
     * Get daily trend data for the last N days
     */
    public TrendData getDailyTrends(User user, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<Habit> habits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);
        if (habits.isEmpty()) {
            return TrendData.empty(days);
        }

        List<Long> habitIds = habits.stream().map(Habit::getId).collect(Collectors.toList());
        List<HabitCheckIn> checkIns = checkInRepository.findByHabitIdInAndCheckInDateBetween(
                habitIds, startDate, endDate);

        // Group check-ins by date
        Map<LocalDate, Long> checkInsByDate = checkIns.stream()
                .collect(Collectors.groupingBy(HabitCheckIn::getCheckInDate, Collectors.counting()));

        // Build data points
        List<TrendDataPoint> dataPoints = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            long count = checkInsByDate.getOrDefault(current, 0L);
            double rate = habits.size() > 0 ? (count * 100.0) / habits.size() : 0;

            dataPoints.add(TrendDataPoint.builder()
                    .date(current)
                    .label(current.toString())
                    .checkIns((int) count)
                    .totalHabits(habits.size())
                    .completionRate(Math.round(rate * 10) / 10.0)
                    .build());

            current = current.plusDays(1);
        }

        // Calculate overall average
        double avgRate = dataPoints.stream()
                .mapToDouble(TrendDataPoint::getCompletionRate)
                .average()
                .orElse(0);

        return TrendData.builder()
                .period("daily")
                .dataPoints(dataPoints)
                .averageCompletionRate(Math.round(avgRate * 10) / 10.0)
                .totalCheckIns(checkIns.size())
                .build();
    }

    /**
     * Get weekly trend data for the last N weeks
     */
    public TrendData getWeeklyTrends(User user, int weeks) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(weeks);

        List<Habit> habits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);
        if (habits.isEmpty()) {
            return TrendData.empty(weeks);
        }

        List<Long> habitIds = habits.stream().map(Habit::getId).collect(Collectors.toList());
        List<HabitCheckIn> checkIns = checkInRepository.findByHabitIdInAndCheckInDateBetween(
                habitIds, startDate, endDate);

        // Group by week
        Map<Integer, List<HabitCheckIn>> byWeek = checkIns.stream()
                .collect(Collectors
                        .groupingBy(c -> c.getCheckInDate().get(java.time.temporal.WeekFields.ISO.weekOfYear())));

        List<TrendDataPoint> dataPoints = new ArrayList<>();
        LocalDate weekStart = startDate.with(DayOfWeek.MONDAY);

        for (int i = 0; i < weeks; i++) {
            int weekNum = weekStart.get(java.time.temporal.WeekFields.ISO.weekOfYear());
            List<HabitCheckIn> weekCheckIns = byWeek.getOrDefault(weekNum, Collections.emptyList());

            int expectedPerWeek = habits.size() * 7;
            double rate = expectedPerWeek > 0 ? (weekCheckIns.size() * 100.0) / expectedPerWeek : 0;

            dataPoints.add(TrendDataPoint.builder()
                    .date(weekStart)
                    .label("Week " + weekNum)
                    .checkIns(weekCheckIns.size())
                    .totalHabits(habits.size())
                    .completionRate(Math.round(rate * 10) / 10.0)
                    .build());

            weekStart = weekStart.plusWeeks(1);
        }

        double avgRate = dataPoints.stream()
                .mapToDouble(TrendDataPoint::getCompletionRate)
                .average()
                .orElse(0);

        return TrendData.builder()
                .period("weekly")
                .dataPoints(dataPoints)
                .averageCompletionRate(Math.round(avgRate * 10) / 10.0)
                .totalCheckIns(checkIns.size())
                .build();
    }

    // HEATMAP DATA (GitHub style)

    /**
     * Get year heatmap data (GitHub contribution style)
     */
    public HeatmapData getYearHeatmap(User user, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Habit> habits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);
        if (habits.isEmpty()) {
            return HeatmapData.empty(year);
        }

        List<Long> habitIds = habits.stream().map(Habit::getId).collect(Collectors.toList());
        List<HabitCheckIn> checkIns = checkInRepository.findByHabitIdInAndCheckInDateBetween(
                habitIds, startDate, endDate);

        // Group check-ins by date
        Map<LocalDate, Long> checkInsByDate = checkIns.stream()
                .collect(Collectors.groupingBy(HabitCheckIn::getCheckInDate, Collectors.counting()));

        // Build heatmap cells
        List<HeatmapCell> cells = new ArrayList<>();
        LocalDate current = startDate;
        int maxCheckIns = habits.size();

        while (!current.isAfter(endDate)) {
            int count = checkInsByDate.getOrDefault(current, 0L).intValue();
            int level = calculateIntensityLevel(count, maxCheckIns);

            cells.add(HeatmapCell.builder()
                    .date(current)
                    .count(count)
                    .level(level) // 0-4 intensity levels
                    .build());

            current = current.plusDays(1);
        }

        return HeatmapData.builder()
                .year(year)
                .cells(cells)
                .totalCheckIns(checkIns.size())
                .daysWithActivity((int) checkInsByDate.size())
                .longestStreak(calculateLongestStreak(checkInsByDate.keySet()))
                .build();
    }

    private int calculateIntensityLevel(int count, int max) {
        if (count == 0)
            return 0;
        if (max == 0)
            return 0;
        double ratio = (double) count / max;
        if (ratio >= 1.0)
            return 4;
        if (ratio >= 0.75)
            return 3;
        if (ratio >= 0.5)
            return 2;
        return 1;
    }

    private int calculateLongestStreak(Set<LocalDate> activeDays) {
        if (activeDays.isEmpty())
            return 0;

        List<LocalDate> sorted = activeDays.stream()
                .sorted()
                .collect(Collectors.toList());

        int longest = 1;
        int current = 1;

        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).equals(sorted.get(i - 1).plusDays(1))) {
                current++;
                longest = Math.max(longest, current);
            } else {
                current = 1;
            }
        }

        return longest;
    }

    // PER-HABIT ANALYTICS

    /**
     * Get completion rates for each habit
     */
    public List<HabitAnalytics> getHabitAnalytics(User user, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        List<Habit> habits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

        return habits.stream().map(habit -> {
            long checkIns = checkInRepository.countByHabitAndCheckInDateAfter(habit, startDate);
            double rate = days > 0 ? (checkIns * 100.0) / days : 0;

            return HabitAnalytics.builder()
                    .habitId(habit.getId())
                    .habitName(habit.getName())
                    .icon(habit.getIcon())
                    .totalCheckIns((int) checkIns)
                    .periodDays(days)
                    .completionRate(Math.round(rate * 10) / 10.0)
                    .currentStreak(habit.getCurrentStreak())
                    .longestStreak(habit.getLongestStreak())
                    .build();
        }).sorted((a, b) -> Double.compare(b.getCompletionRate(), a.getCompletionRate()))
                .collect(Collectors.toList());
    }

    // DTOs

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private String period;
        private List<TrendDataPoint> dataPoints;
        private double averageCompletionRate;
        private int totalCheckIns;

        public static TrendData empty(int days) {
            return TrendData.builder()
                    .period("daily")
                    .dataPoints(Collections.emptyList())
                    .averageCompletionRate(0)
                    .totalCheckIns(0)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataPoint {
        private LocalDate date;
        private String label;
        private int checkIns;
        private int totalHabits;
        private double completionRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeatmapData {
        private int year;
        private List<HeatmapCell> cells;
        private int totalCheckIns;
        private int daysWithActivity;
        private int longestStreak;

        public static HeatmapData empty(int year) {
            return HeatmapData.builder()
                    .year(year)
                    .cells(Collections.emptyList())
                    .totalCheckIns(0)
                    .daysWithActivity(0)
                    .longestStreak(0)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeatmapCell {
        private LocalDate date;
        private int count;
        private int level; // 0-4 intensity
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabitAnalytics {
        private Long habitId;
        private String habitName;
        private String icon;
        private int totalCheckIns;
        private int periodDays;
        private double completionRate;
        private int currentStreak;
        private int longestStreak;
    }
}
