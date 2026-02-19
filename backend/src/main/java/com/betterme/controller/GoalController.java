package com.betterme.controller;

import com.betterme.dto.GoalRequest;
import com.betterme.dto.GoalResponse;
import com.betterme.model.User;
import com.betterme.service.GoalService;
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
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    /**
     * Get all goals for the user
     */
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getUserGoals(user));
    }

    /**
     * Get a specific goal
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getGoal(id, user));
    }

    /**
     * Get goal statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<GoalService.GoalStats> getStats(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getStats(user));
    }

    /**
     * Create a new goal
     */
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.createGoal(request, user));
    }

    /**
     * Update a goal
     */
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.updateGoal(id, request, user));
    }

    /**
     * Delete a goal
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        goalService.deleteGoal(id, user);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }

    /**
     * Update progress on a goal
     * 
     * Request body: { "value": 5 } or { "increment": 1 }
     */
    @PostMapping("/{id}/progress")
    public ResponseEntity<GoalResponse> updateProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal User user) {

        if (request.containsKey("value")) {
            return ResponseEntity.ok(goalService.updateProgress(id, request.get("value"), user));
        } else if (request.containsKey("increment")) {
            return ResponseEntity.ok(goalService.incrementProgress(id, request.get("increment"), user));
        } else {
            throw new RuntimeException("Must provide 'value' or 'increment'");
        }
    }
}
