package com.betterme.service;

import com.betterme.dto.HabitRequest;
import com.betterme.dto.HabitResponse;
import com.betterme.model.Habit;
import com.betterme.model.HabitCheckIn;
import com.betterme.model.HabitFrequency;
import com.betterme.model.User;
import com.betterme.repository.HabitCheckInRepository;
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
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitCheckInRepository checkInRepository;

    // CRUD OPERATIONS

    /**
     * Get all habits for the current user
     */
    public List<HabitResponse> getUserHabits(User user) {
        LocalDate today = LocalDate.now();

        return habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true)
                .stream()
                .map(habit -> {
                    boolean checkedToday = checkInRepository
                            .existsByHabitAndCheckInDate(habit, today);
                    return HabitResponse.fromEntity(habit, checkedToday);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get a specific habit by ID
     */
    public HabitResponse getHabit(Long habitId, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        boolean checkedToday = checkInRepository
                .existsByHabitAndCheckInDate(habit, LocalDate.now());

        return HabitResponse.fromEntity(habit, checkedToday);
    }

    /**
     * Create a new habit
     */
    @Transactional
    public HabitResponse createHabit(HabitRequest request, User user) {
        Habit habit = Habit.builder()
                .name(request.getName())
                .description(request.getDescription())
                .frequency(request.getFrequency() != null
                        ? request.getFrequency()
                        : HabitFrequency.DAILY)
                .target(request.getTarget())
                .icon(request.getIcon() != null ? request.getIcon() : "✅")
                .user(user)
                .build();

        Habit saved = habitRepository.save(habit);
        log.info("Created habit '{}' for user {}", saved.getName(), user.getEmail());

        return HabitResponse.fromEntity(saved, false);
    }

    /**
     * Update an existing habit
     */
    @Transactional
    public HabitResponse updateHabit(Long habitId, HabitRequest request, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Update fields
        if (request.getName() != null)
            habit.setName(request.getName());
        if (request.getDescription() != null)
            habit.setDescription(request.getDescription());
        if (request.getFrequency() != null)
            habit.setFrequency(request.getFrequency());
        if (request.getTarget() != null)
            habit.setTarget(request.getTarget());
        if (request.getIcon() != null)
            habit.setIcon(request.getIcon());

        Habit saved = habitRepository.save(habit);

        boolean checkedToday = checkInRepository
                .existsByHabitAndCheckInDate(saved, LocalDate.now());

        return HabitResponse.fromEntity(saved, checkedToday);
    }

    /**
     * Delete (archive) a habit
     */
    @Transactional
    public void deleteHabit(Long habitId, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Soft delete - just mark as inactive
        habit.setActive(false);
        habitRepository.save(habit);

        log.info("Archived habit '{}' for user {}", habit.getName(), user.getEmail());
    }

    // CHECK-IN & STREAK LOGIC

    /**
     * CHECK IN TO A HABIT
     * This is where the STREAK MAGIC happens!
     * 
     * Logic:
     * 1. If already checked in today → return existing
     * 2. Create new check-in record
     * 3. Update streak:
     * - If yesterday was checked: streak++
     * - Else: streak = 1 (starting fresh)
     * 4. Update longest streak if needed
     */
    @Transactional
    public HabitResponse checkIn(Long habitId, User user, String notes) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        LocalDate today = LocalDate.now();

        // Already checked in today?
        if (checkInRepository.existsByHabitAndCheckInDate(habit, today)) {
            log.info("Already checked in today for habit '{}'", habit.getName());
            return HabitResponse.fromEntity(habit, true);
        }

        // Create check-in record
        HabitCheckIn checkIn = HabitCheckIn.builder()
                .habit(habit)
                .checkInDate(today)
                .completed(true)
                .notes(notes)
                .build();
        checkInRepository.save(checkIn);

        // STREAK CALCULATION
        LocalDate yesterday = today.minusDays(1);
        boolean hadYesterdayCheckIn = checkInRepository
                .existsByHabitAndCheckInDate(habit, yesterday);

        if (hadYesterdayCheckIn) {
            // Continuing streak! 🔥
            habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        } else {
            // Starting fresh streak
            habit.setCurrentStreak(1);
        }

        // Update longest streak if current beats it
        if (habit.getCurrentStreak() > habit.getLongestStreak()) {
            habit.setLongestStreak(habit.getCurrentStreak());
        }

        // Update totals
        habit.setTotalCheckIns(habit.getTotalCheckIns() + 1);
        habit.setLastCheckInDate(LocalDateTime.now());

        habitRepository.save(habit);

        log.info("✅ Checked in for '{}' - Streak: {} 🔥",
                habit.getName(), habit.getCurrentStreak());

        return HabitResponse.fromEntity(habit, true);
    }

    /**
     * Get check-in history for a habit
     */
    public List<HabitCheckIn> getCheckInHistory(Long habitId, User user, int days) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        LocalDate startDate = LocalDate.now().minusDays(days);
        return checkInRepository.findRecentCheckIns(habit, startDate);
    }

    // STATS & ANALYTICS

    /**
     * Get habit statistics for a user
     */
    public HabitStats getUserStats(User user) {
        long totalHabits = habitRepository.countByUserAndActive(user, true);
        List<Habit> habits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

        LocalDate today = LocalDate.now();
        long completedToday = habits.stream()
                .filter(h -> checkInRepository.existsByHabitAndCheckInDate(h, today))
                .count();

        int totalStreak = habits.stream()
                .mapToInt(Habit::getCurrentStreak)
                .sum();

        return HabitStats.builder()
                .totalHabits(totalHabits)
                .completedToday(completedToday)
                .remainingToday(totalHabits - completedToday)
                .totalCurrentStreak(totalStreak)
                .build();
    }

    /**
     * Stats DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class HabitStats {
        private long totalHabits;
        private long completedToday;
        private long remainingToday;
        private int totalCurrentStreak;
    }
}
