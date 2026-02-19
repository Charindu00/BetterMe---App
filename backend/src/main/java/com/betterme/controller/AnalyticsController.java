package com.betterme.controller;

import com.betterme.model.User;
import com.betterme.service.AnalyticsService;
import com.betterme.service.AnalyticsService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * DAILY TRENDS
     * GET /api/analytics/trends?period=daily&days=30
     * GET /api/analytics/trends?period=weekly&weeks=12
     * 
     * Returns data for line/bar charts
     */
    @GetMapping("/trends")
    public ResponseEntity<TrendData> getTrends(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) Integer weeks,
            @AuthenticationPrincipal User user) {

        if ("weekly".equals(period)) {
            int w = weeks != null ? weeks : 12;
            return ResponseEntity.ok(analyticsService.getWeeklyTrends(user, w));
        } else {
            int d = days != null ? days : 30;
            return ResponseEntity.ok(analyticsService.getDailyTrends(user, d));
        }
    }

    /**
     * YEAR HEATMAP
     * GET /api/analytics/heatmap
     * GET /api/analytics/heatmap?year=2025
     * 
     * Returns GitHub-style contribution heatmap data
     */
    @GetMapping("/heatmap")
    public ResponseEntity<HeatmapData> getHeatmap(
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal User user) {

        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(analyticsService.getYearHeatmap(user, targetYear));
    }

    /**
     * PER-HABIT ANALYTICS
     * GET /api/analytics/habits
     * GET /api/analytics/habits?days=30
     * 
     * Returns completion rates for each habit
     */
    @GetMapping("/habits")
    public ResponseEntity<List<HabitAnalytics>> getHabitAnalytics(
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(analyticsService.getHabitAnalytics(user, days));
    }
}
