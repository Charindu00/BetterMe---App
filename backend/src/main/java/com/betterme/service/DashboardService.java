package com.betterme.service;

import com.betterme.dto.*;
import com.betterme.model.Habit;
import com.betterme.model.HabitCheckIn;
import com.betterme.model.User;
import com.betterme.repository.HabitCheckInRepository;
import com.betterme.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Dashboard Service ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Aggregates data from multiple sources into dashboard-friendly formats. ║
 * ║ ║
 * ║ Key concepts: ║
 * ║ 1. Data aggregation - combining multiple queries into one response ║
 * ║ 2. Date calculations - working with Java's date/time API ║
 * ║ 3. Stream processing - functional style data transformation ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final HabitRepository habitRepository;
    private final HabitCheckInRepository checkInRepository;

    // Motivational quotes for the dashboard
    private static final List<String> QUOTES = List.of(
            "Small steps every day lead to big changes! 🚀",
            "You're building something amazing, one day at a time! 💪",
            "Consistency is the key to success! 🔑",
            "Every check-in is a vote for your future self! ✨",
            "Keep going! Your streak is proof of your dedication! 🔥",
            "Progress, not perfection! 🌟",
            "The best time to start was yesterday. The next best time is now! ⏰",
            "You're stronger than you think! 💎");

    // ═══════════════════════════════════════════════════════════════════════
    // DASHBOARD SUMMARY
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get overview statistics for the dashboard
     */
    public DashboardSummary getSummary(User user) {
        LocalDate today = LocalDate.now();
        List<Habit> activeHabits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

        // Count today's completions
        long completedToday = activeHabits.stream()
                .filter(h -> checkInRepository.existsByHabitAndCheckInDate(h, today))
                .count();

        long totalActive = activeHabits.size();

        // Find best streak
        Habit bestStreakHabit = activeHabits.stream()
                .max(Comparator.comparing(Habit::getLongestStreak))
                .orElse(null);

        // Calculate totals
        int currentStreakTotal = activeHabits.stream()
                .mapToInt(Habit::getCurrentStreak)
                .sum();

        long totalCheckIns = activeHabits.stream()
                .mapToLong(Habit::getTotalCheckIns)
                .sum();

        // Days since first habit
        int daysActive = activeHabits.stream()
                .map(Habit::getCreatedAt)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .map(createdAt -> (int) ChronoUnit.DAYS.between(createdAt.toLocalDate(), today))
                .orElse(0);

        double completionPercentage = totalActive > 0
                ? (completedToday * 100.0) / totalActive
                : 0;

        return DashboardSummary.builder()
                .totalHabits(habitRepository.countByUser(user))
                .activeHabits(totalActive)
                .completedToday(completedToday)
                .remainingToday(totalActive - completedToday)
                .completionPercentage(Math.round(completionPercentage * 10) / 10.0)
                .currentStreakTotal(currentStreakTotal)
                .longestStreak(bestStreakHabit != null ? bestStreakHabit.getLongestStreak() : 0)
                .longestStreakHabit(bestStreakHabit != null ? bestStreakHabit.getName() : null)
                .totalCheckIns(totalCheckIns)
                .daysActive(daysActive)
                .motivationalQuote(QUOTES.get(new Random().nextInt(QUOTES.size())))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // WEEKLY PROGRESS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get progress for the last 7 days
     */
    public WeeklyProgress getWeeklyProgress(User user) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        List<Habit> activeHabits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);
        List<WeeklyProgress.DayProgress> days = new ArrayList<>();

        int totalCompletions = 0;
        int totalPossible = 0;

        // Build data for each day
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            // Count completions for this day
            int completed = 0;
            for (Habit habit : activeHabits) {
                if (checkInRepository.existsByHabitAndCheckInDate(habit, date)) {
                    completed++;
                }
            }

            int total = activeHabits.size();
            double percentage = total > 0 ? (completed * 100.0) / total : 0;

            days.add(WeeklyProgress.DayProgress.builder()
                    .date(date)
                    .dayName(dayName)
                    .completed(completed)
                    .total(total)
                    .percentage(Math.round(percentage * 10) / 10.0)
                    .isToday(date.equals(today))
                    .build());

            totalCompletions += completed;
            totalPossible += total;
        }

        double weeklyRate = totalPossible > 0
                ? (totalCompletions * 100.0) / totalPossible
                : 0;

        return WeeklyProgress.builder()
                .weekStart(weekStart)
                .weekEnd(today)
                .days(days)
                .totalCompletions(totalCompletions)
                .totalPossible(totalPossible)
                .weeklyCompletionRate(Math.round(weeklyRate * 10) / 10.0)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MONTHLY CALENDAR
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get calendar data for a specific month
     */
    public MonthlyCalendar getMonthlyCalendar(User user, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        List<Habit> activeHabits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

        Set<LocalDate> allCheckedDates = new HashSet<>();
        List<MonthlyCalendar.HabitMonthData> habitDataList = new ArrayList<>();

        for (Habit habit : activeHabits) {
            List<HabitCheckIn> checkIns = checkInRepository
                    .findByHabitAndCheckInDateBetweenOrderByCheckInDateDesc(habit, monthStart, monthEnd);

            Set<LocalDate> habitCheckedDates = checkIns.stream()
                    .map(HabitCheckIn::getCheckInDate)
                    .collect(Collectors.toSet());

            allCheckedDates.addAll(habitCheckedDates);

            habitDataList.add(MonthlyCalendar.HabitMonthData.builder()
                    .habitId(habit.getId())
                    .habitName(habit.getName())
                    .icon(habit.getIcon())
                    .checkedDates(habitCheckedDates)
                    .checkInCount(habitCheckedDates.size())
                    .currentStreak(habit.getCurrentStreak())
                    .build());
        }

        int daysInMonth = yearMonth.lengthOfMonth();
        double completionRate = daysInMonth > 0
                ? (allCheckedDates.size() * 100.0) / daysInMonth
                : 0;

        return MonthlyCalendar.builder()
                .year(year)
                .month(month)
                .monthName(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .checkedDates(allCheckedDates)
                .habits(habitDataList)
                .totalDaysInMonth(daysInMonth)
                .daysWithCheckIns(allCheckedDates.size())
                .monthlyCompletionRate(Math.round(completionRate * 10) / 10.0)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STREAKS LEADERBOARD
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get user's habits sorted by streak (for leaderboard view)
     */
    public List<HabitResponse> getStreakLeaderboard(User user) {
        LocalDate today = LocalDate.now();

        return habitRepository.findTopStreaksByUser(user).stream()
                .filter(Habit::getActive)
                .limit(10)
                .map(habit -> {
                    boolean checkedToday = checkInRepository.existsByHabitAndCheckInDate(habit, today);
                    return HabitResponse.fromEntity(habit, checkedToday);
                })
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ACHIEVEMENTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get user's achievements with progress
     */
    public List<Achievement> getAchievements(User user) {
        List<Habit> activeHabits = habitRepository.findByUserAndActiveOrderByCreatedAtDesc(user, true);

        // Calculate stats for achievement checking
        int longestStreak = activeHabits.stream()
                .mapToInt(Habit::getLongestStreak)
                .max()
                .orElse(0);

        long totalCheckIns = activeHabits.stream()
                .mapToLong(Habit::getTotalCheckIns)
                .sum();

        long totalHabits = habitRepository.countByUser(user);

        // Check for perfect day
        LocalDate today = LocalDate.now();
        long completedToday = activeHabits.stream()
                .filter(h -> checkInRepository.existsByHabitAndCheckInDate(h, today))
                .count();
        boolean hasPerfectDay = !activeHabits.isEmpty() && completedToday == activeHabits.size();

        List<Achievement> achievements = new ArrayList<>();

        // Streak achievements
        achievements.add(createStreakAchievement(Achievement.AchievementType.FIRST_STREAK, longestStreak));
        achievements.add(createStreakAchievement(Achievement.AchievementType.WEEK_WARRIOR, longestStreak));
        achievements.add(createStreakAchievement(Achievement.AchievementType.FORTNIGHT_FIGHTER, longestStreak));
        achievements.add(createStreakAchievement(Achievement.AchievementType.HABIT_MASTER, longestStreak));
        achievements.add(createStreakAchievement(Achievement.AchievementType.LEGENDARY, longestStreak));

        // Consistency achievements
        achievements
                .add(createConsistencyAchievement(Achievement.AchievementType.GETTING_STARTED, (int) totalCheckIns));
        achievements.add(createConsistencyAchievement(Achievement.AchievementType.CONSISTENT, (int) totalCheckIns));
        achievements.add(createConsistencyAchievement(Achievement.AchievementType.DEDICATED, (int) totalCheckIns));
        achievements.add(createConsistencyAchievement(Achievement.AchievementType.UNSTOPPABLE, (int) totalCheckIns));

        // Milestone achievements
        achievements.add(createMilestoneAchievement(Achievement.AchievementType.FIRST_HABIT, (int) totalHabits));
        achievements.add(createMilestoneAchievement(Achievement.AchievementType.HABIT_COLLECTOR, (int) totalHabits));
        achievements.add(Achievement.fromType(
                Achievement.AchievementType.PERFECT_DAY,
                hasPerfectDay ? 1 : 0,
                hasPerfectDay,
                null));

        // Sort: unlocked first, then by progress
        achievements.sort((a, b) -> {
            if (a.isUnlocked() != b.isUnlocked()) {
                return a.isUnlocked() ? -1 : 1;
            }
            return Double.compare(b.getProgressPercentage(), a.getProgressPercentage());
        });

        return achievements;
    }

    private Achievement createStreakAchievement(Achievement.AchievementType type, int currentStreak) {
        boolean unlocked = currentStreak >= type.requiredProgress;
        return Achievement.fromType(type, Math.min(currentStreak, type.requiredProgress), unlocked, null);
    }

    private Achievement createConsistencyAchievement(Achievement.AchievementType type, int totalCheckIns) {
        boolean unlocked = totalCheckIns >= type.requiredProgress;
        return Achievement.fromType(type, Math.min(totalCheckIns, type.requiredProgress), unlocked, null);
    }

    private Achievement createMilestoneAchievement(Achievement.AchievementType type, int count) {
        boolean unlocked = count >= type.requiredProgress;
        return Achievement.fromType(type, Math.min(count, type.requiredProgress), unlocked, null);
    }
}
