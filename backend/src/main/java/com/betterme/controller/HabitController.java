package com.betterme.controller;

import com.betterme.dto.HabitRequest;
import com.betterme.dto.HabitResponse;
import com.betterme.model.HabitCheckIn;
import com.betterme.model.User;
import com.betterme.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 */
@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    // LIST & GET

    /**
     * Get all habits for the logged-in user
     */
    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAllHabits(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(habitService.getUserHabits(user));
    }

    /**
     * Get a specific habit by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HabitResponse> getHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(habitService.getHabit(id, user));
    }

    /**
     * Get user's habit statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<HabitService.HabitStats> getStats(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(habitService.getUserStats(user));
    }

    // CREATE & UPDATE

    /**
     * Create a new habit
     * 
     * @Valid triggers validation on HabitRequest fields
     */
    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(
            @Valid @RequestBody HabitRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(habitService.createHabit(request, user));
    }

    /**
     * Update an existing habit
     */
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody HabitRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(habitService.updateHabit(id, request, user));
    }

    /**
     * Delete (archive) a habit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        habitService.deleteHabit(id, user);
        return ResponseEntity.ok(Map.of("message", "Habit deleted successfully"));
    }

    // CHECK-IN

    /**
     * Check in to a habit for today
     * 
     * This is the MAIN action users will do daily!
     */
    @PostMapping("/{id}/checkin")
    public ResponseEntity<HabitResponse> checkIn(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal User user) {

        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(habitService.checkIn(id, user, notes));
    }

    /**
     * Get check-in history for a habit
     * 
     * @param days Number of days to look back (default: 30)
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<HabitCheckIn>> getHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(habitService.getCheckInHistory(id, user, days));
    }
}
