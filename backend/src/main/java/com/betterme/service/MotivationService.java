package com.betterme.service;

import com.betterme.dto.DashboardSummary;
import com.betterme.dto.HabitResponse;
import com.betterme.dto.MotivationResponse;
import com.betterme.dto.MotivationResponse.MotivationContext;
import com.betterme.dto.MotivationResponse.MotivationType;
import com.betterme.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MotivationService {

    private final GeminiService geminiService;
    private final DashboardService dashboardService;
    private final HabitService habitService;

    // Fallback quotes when AI is not available
    private static final List<String> FALLBACK_QUOTES = List.of(
            "Every day is a new opportunity to become a better version of yourself! 🌟",
            "Small steps lead to big changes. Keep going! 💪",
            "You're building habits that will change your life. Be proud! 🔥",
            "Consistency beats perfection. Just show up! ⭐",
            "Your future self will thank you for the work you're doing today! 🚀",
            "Progress, not perfection. You're doing amazing! ✨",
            "Each check-in is proof of your dedication. Keep it up! 💎",
            "The best time to start was yesterday. The second best time is now! ⏰");

    // DAILY MOTIVATION

    /**
     * Get personalized daily motivation based on user's stats
     */
    public MotivationResponse getDailyMotivation(User user) {
        DashboardSummary stats = dashboardService.getSummary(user);

        MotivationContext context = MotivationContext.builder()
                .currentStreak(stats.getCurrentStreakTotal())
                .totalHabits((int) stats.getActiveHabits())
                .completedToday((int) stats.getCompletedToday())
                .build();

        if (!geminiService.isConfigured()) {
            return buildFallbackResponse(MotivationType.DAILY, context);
        }

        String prompt = buildDailyPrompt(stats);
        String aiMessage = geminiService.generateContent(prompt);

        return MotivationResponse.builder()
                .message(aiMessage)
                .type(MotivationType.DAILY)
                .aiGenerated(true)
                .generatedAt(LocalDateTime.now())
                .context(context)
                .build();
    }

    private String buildDailyPrompt(DashboardSummary stats) {
        return String.format("""
                You are a friendly, encouraging habit coach named "Coach AI".

                The user's current stats:
                - Active habits: %d
                - Completed today: %d out of %d (%.1f%%)
                - Current total streak: %d days
                - Longest single streak: %d days (%s)
                - Total check-ins all time: %d
                - Days active: %d

                Give a personalized, encouraging message (2-3 sentences max).
                Be specific about their stats. Use emojis sparingly.
                If they haven't completed all habits today, gently encourage them.
                If they have a good streak, celebrate it!
                """,
                stats.getActiveHabits(),
                stats.getCompletedToday(),
                stats.getActiveHabits(),
                stats.getCompletionPercentage(),
                stats.getCurrentStreakTotal(),
                stats.getLongestStreak(),
                stats.getLongestStreakHabit() != null ? stats.getLongestStreakHabit() : "none yet",
                stats.getTotalCheckIns(),
                stats.getDaysActive());
    }

    // HABIT-SPECIFIC TIPS

    /**
     * Get AI tips for a specific habit
     */
    public MotivationResponse getHabitTips(User user, Long habitId) {
        HabitResponse habit = habitService.getHabit(habitId, user);

        MotivationContext context = MotivationContext.builder()
                .habitName(habit.getName())
                .currentStreak(habit.getCurrentStreak())
                .build();

        if (!geminiService.isConfigured()) {
            return buildFallbackResponse(MotivationType.HABIT_TIP, context);
        }

        String prompt = buildHabitPrompt(habit);
        String aiMessage = geminiService.generateContent(prompt);

        return MotivationResponse.builder()
                .message(aiMessage)
                .type(MotivationType.HABIT_TIP)
                .aiGenerated(true)
                .generatedAt(LocalDateTime.now())
                .context(context)
                .build();
    }

    private String buildHabitPrompt(HabitResponse habit) {
        return String.format("""
                You are a friendly habit coach. Give specific, actionable tips.

                The user is tracking this habit:
                - Name: "%s"
                - Description: %s
                - Frequency: %s
                - Current streak: %d days
                - Total check-ins: %d
                - Goal: %s

                Give 2-3 practical tips to help them succeed with this specific habit.
                Be specific to the habit type. Keep it concise and encouraging.
                Use bullet points.
                """,
                habit.getName(),
                habit.getDescription() != null ? habit.getDescription() : "Not specified",
                habit.getFrequency(),
                habit.getCurrentStreak(),
                habit.getTotalCheckIns(),
                habit.getTarget() != null ? habit.getTarget() : "Complete daily");
    }

    // CELEBRATION

    /**
     * Get celebration message for achievements
     */
    public MotivationResponse getCelebration(User user) {
        DashboardSummary stats = dashboardService.getSummary(user);

        MotivationContext context = MotivationContext.builder()
                .currentStreak(stats.getCurrentStreakTotal())
                .totalHabits((int) stats.getActiveHabits())
                .completedToday((int) stats.getCompletedToday())
                .build();

        if (!geminiService.isConfigured()) {
            return buildFallbackResponse(MotivationType.CELEBRATION, context);
        }

        String prompt = buildCelebrationPrompt(stats);
        String aiMessage = geminiService.generateContent(prompt);

        return MotivationResponse.builder()
                .message(aiMessage)
                .type(MotivationType.CELEBRATION)
                .aiGenerated(true)
                .generatedAt(LocalDateTime.now())
                .context(context)
                .build();
    }

    private String buildCelebrationPrompt(DashboardSummary stats) {
        return String.format("""
                You are an enthusiastic celebration coach! 🎉

                The user has achieved something great:
                - Total streak across all habits: %d days
                - Longest single streak: %d days on "%s"
                - Total check-ins ever: %d
                - Days active: %d

                Write a SHORT, enthusiastic celebration message (2 sentences max).
                Be very positive and use celebratory emojis!
                Mention their specific achievement.
                """,
                stats.getCurrentStreakTotal(),
                stats.getLongestStreak(),
                stats.getLongestStreakHabit() != null ? stats.getLongestStreakHabit() : "their habits",
                stats.getTotalCheckIns(),
                stats.getDaysActive());
    }

    // CHAT

    /**
     * Chat with the AI coach
     */
    public MotivationResponse chat(User user, String userMessage) {
        DashboardSummary stats = dashboardService.getSummary(user);

        MotivationContext context = MotivationContext.builder()
                .currentStreak(stats.getCurrentStreakTotal())
                .totalHabits((int) stats.getActiveHabits())
                .completedToday((int) stats.getCompletedToday())
                .build();

        if (!geminiService.isConfigured()) {
            return buildFallbackResponse(MotivationType.CHAT, context);
        }

        String prompt = buildChatPrompt(userMessage, stats);
        String aiMessage = geminiService.generateContent(prompt);

        return MotivationResponse.builder()
                .message(aiMessage)
                .type(MotivationType.CHAT)
                .aiGenerated(true)
                .generatedAt(LocalDateTime.now())
                .context(context)
                .build();
    }

    private String buildChatPrompt(String userMessage, DashboardSummary stats) {
        return String.format("""
                You are Coach AI, a friendly and supportive habit coach in the BetterMe app.

                User's context:
                - Active habits: %d
                - Completed today: %d/%d
                - Current streak total: %d days

                User's message: "%s"

                Respond helpfully and encouragingly. Be conversational.
                Keep response to 2-3 sentences max.
                If they ask about habits, give practical advice.
                If they seem discouraged, be supportive.
                """,
                stats.getActiveHabits(),
                stats.getCompletedToday(),
                stats.getActiveHabits(),
                stats.getCurrentStreakTotal(),
                userMessage);
    }

    // FALLBACK

    private MotivationResponse buildFallbackResponse(MotivationType type, MotivationContext context) {
        String message = FALLBACK_QUOTES.get(new Random().nextInt(FALLBACK_QUOTES.size()));

        return MotivationResponse.builder()
                .message(message)
                .type(type)
                .aiGenerated(false)
                .generatedAt(LocalDateTime.now())
                .context(context)
                .build();
    }
}
