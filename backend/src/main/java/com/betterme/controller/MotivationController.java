package com.betterme.controller;

import com.betterme.dto.MotivationResponse;
import com.betterme.model.User;
import com.betterme.service.MotivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 */
@RestController
@RequestMapping("/api/motivation")
@RequiredArgsConstructor
public class MotivationController {

    private final MotivationService motivationService;

    /**
     * DAILY MOTIVATION
     * GET /api/motivation/daily
     * 
     * Returns personalized daily motivation based on user's stats.
     * Great for homepage display!
     */
    @GetMapping("/daily")
    public ResponseEntity<MotivationResponse> getDailyMotivation(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(motivationService.getDailyMotivation(user));
    }

    /**
     * HABIT TIPS
     * POST /api/motivation/habit/{habitId}
     * 
     * Returns AI-generated tips specific to a habit.
     * Useful on habit detail pages.
     */
    @PostMapping("/habit/{habitId}")
    public ResponseEntity<MotivationResponse> getHabitTips(
            @PathVariable Long habitId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(motivationService.getHabitTips(user, habitId));
    }

    /**
     * CELEBRATION
     * GET /api/motivation/celebration
     * 
     * Returns enthusiastic celebration message for achievements.
     * Call after unlocking achievements or hitting streaks!
     */
    @GetMapping("/celebration")
    public ResponseEntity<MotivationResponse> getCelebration(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(motivationService.getCelebration(user));
    }

    /**
     * CHAT WITH COACH
     * POST /api/motivation/chat
     * 
     * Have a conversation with Coach AI!
     * Send user's message, get personalized response.
     * 
     * Request body: { "message": "I'm struggling with my morning routine" }
     */
    @PostMapping("/chat")
    public ResponseEntity<MotivationResponse> chat(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User user) {

        String userMessage = request.getOrDefault("message", "Hello!");
        return ResponseEntity.ok(motivationService.chat(user, userMessage));
    }
}
