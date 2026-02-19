package com.betterme.controller;

import com.betterme.dto.*;
import com.betterme.model.User;
import com.betterme.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * SUMMARY - Quick overview stats
     * GET /api/dashboard/summary
     * 
     * Returns: total habits, completed today, total streaks, etc.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getSummary(user));
    }

    /**
     * WEEKLY PROGRESS - Last 7 days breakdown
     * GET /api/dashboard/weekly
     * 
     * Returns: daily completion data for charting
     */
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyProgress> getWeeklyProgress(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getWeeklyProgress(user));
    }

    /**
     * MONTHLY CALENDAR - Calendar view data
     * GET /api/dashboard/monthly
     * GET /api/dashboard/monthly?year=2026&month=2
     * 
     * Returns: dates with check-ins for calendar highlighting
     */
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyCalendar> getMonthlyCalendar(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        // Default to current month if not specified
        LocalDate now = LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();

        return ResponseEntity.ok(dashboardService.getMonthlyCalendar(user, targetYear, targetMonth));
    }

    /**
     * STREAKS - Habit leaderboard by streak
     * GET /api/dashboard/streaks
     * 
     * Returns: habits sorted by current streak (descending)
     */
    @GetMapping("/streaks")
    public ResponseEntity<List<HabitResponse>> getStreakLeaderboard(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getStreakLeaderboard(user));
    }

    /**
     * ACHIEVEMENTS - Gamification badges
     * GET /api/dashboard/achievements
     * 
     * Returns: all achievements with progress (unlocked first)
     */
    @GetMapping("/achievements")
    public ResponseEntity<List<Achievement>> getAchievements(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getAchievements(user));
    }
}
