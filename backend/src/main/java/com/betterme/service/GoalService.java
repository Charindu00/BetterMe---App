package com.betterme.service;

import com.betterme.dto.GoalRequest;
import com.betterme.dto.GoalResponse;
import com.betterme.model.Goal;
import com.betterme.model.Habit;
import com.betterme.model.User;
import com.betterme.repository.GoalRepository;
import com.betterme.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Goal Service ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Business logic for goal management. ║
 * ║ ║
 * ║ Key features: ║
 * ║ 1. CRUD operations for goals ║
 * ║ 2. Progress update and completion ║
 * ║ 3. Automatic habit linking ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;
    private final HabitRepository habitRepository;

    // ═══════════════════════════════════════════════════════════════════════
    // CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get all active goals for a user
     */
    public List<GoalResponse> getUserGoals(User user) {
        return goalRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific goal
     */
    public GoalResponse getGoal(Long goalId, User user) {
        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        return GoalResponse.fromEntity(goal);
    }

    /**
     * Create a new goal
     */
    @Transactional
    public GoalResponse createGoal(GoalRequest request, User user) {
        Goal goal = Goal.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .targetValue(request.getTargetValue())
                .unit(request.getUnit())
                .icon(request.getIcon() != null ? request.getIcon() : "🎯")
                .category(request.getCategory())
                .deadline(request.getDeadline())
                .user(user)
                .build();

        // Link to habit if requested
        if (request.getLinkedHabitId() != null) {
            Habit habit = habitRepository.findByIdAndUser(request.getLinkedHabitId(), user)
                    .orElseThrow(() -> new RuntimeException("Linked habit not found"));
            goal.setLinkedHabit(habit);
        }

        Goal saved = goalRepository.save(goal);
        log.info("Created goal '{}' for user {}", saved.getTitle(), user.getEmail());

        return GoalResponse.fromEntity(saved);
    }

    /**
     * Update a goal
     */
    @Transactional
    public GoalResponse updateGoal(Long goalId, GoalRequest request, User user) {
        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (request.getTitle() != null)
            goal.setTitle(request.getTitle());
        if (request.getDescription() != null)
            goal.setDescription(request.getDescription());
        if (request.getType() != null)
            goal.setType(request.getType());
        if (request.getTargetValue() != null)
            goal.setTargetValue(request.getTargetValue());
        if (request.getUnit() != null)
            goal.setUnit(request.getUnit());
        if (request.getIcon() != null)
            goal.setIcon(request.getIcon());
        if (request.getCategory() != null)
            goal.setCategory(request.getCategory());
        if (request.getDeadline() != null)
            goal.setDeadline(request.getDeadline());

        Goal saved = goalRepository.save(goal);
        return GoalResponse.fromEntity(saved);
    }

    /**
     * Delete (archive) a goal
     */
    @Transactional
    public void deleteGoal(Long goalId, User user) {
        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setActive(false);
        goalRepository.save(goal);
        log.info("Archived goal '{}' for user {}", goal.getTitle(), user.getEmail());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PROGRESS TRACKING
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Update progress on a goal
     */
    @Transactional
    public GoalResponse updateProgress(Long goalId, Integer newValue, User user) {
        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setCurrentValue(newValue);

        // Check if goal is now completed
        if (goal.isGoalCompleted() && !goal.getCompleted()) {
            goal.setCompleted(true);
            goal.setCompletedAt(LocalDateTime.now());
            log.info("🎉 Goal '{}' completed!", goal.getTitle());
        }

        Goal saved = goalRepository.save(goal);
        return GoalResponse.fromEntity(saved);
    }

    /**
     * Increment progress by a value
     */
    @Transactional
    public GoalResponse incrementProgress(Long goalId, Integer increment, User user) {
        Goal goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        int newValue = goal.getCurrentValue() + increment;
        goal.setCurrentValue(newValue);

        if (goal.isGoalCompleted() && !goal.getCompleted()) {
            goal.setCompleted(true);
            goal.setCompletedAt(LocalDateTime.now());
            log.info("🎉 Goal '{}' completed!", goal.getTitle());
        }

        Goal saved = goalRepository.save(goal);
        return GoalResponse.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STATS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get goal statistics for a user
     */
    public GoalStats getStats(User user) {
        List<Goal> activeGoals = goalRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);
        long completed = goalRepository.countByUserAndCompleted(user, true);

        List<Goal> upcomingDeadlines = goalRepository.findUpcomingDeadlines(
                user, LocalDate.now().plusDays(7));

        double avgProgress = activeGoals.stream()
                .filter(g -> !g.getCompleted())
                .mapToDouble(Goal::getProgressPercentage)
                .average()
                .orElse(0);

        return GoalStats.builder()
                .totalGoals(activeGoals.size())
                .completedGoals(completed)
                .inProgressGoals(activeGoals.stream().filter(g -> !g.getCompleted()).count())
                .upcomingDeadlines(upcomingDeadlines.size())
                .averageProgress(Math.round(avgProgress * 10) / 10.0)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class GoalStats {
        private long totalGoals;
        private long completedGoals;
        private long inProgressGoals;
        private int upcomingDeadlines;
        private double averageProgress;
    }
}
