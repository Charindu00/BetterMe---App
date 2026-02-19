package com.betterme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    private String id; // Unique identifier
    private String name; // Display name
    private String description; // What it's for
    private String icon; // Emoji icon
    private String category; // STREAK, CONSISTENCY, MILESTONE

    private boolean unlocked;
    private LocalDateTime unlockedAt;

    // Progress towards achievement (for locked ones)
    private int currentProgress;
    private int requiredProgress;
    private double progressPercentage;

    /**
     * PREDEFINED ACHIEVEMENTS
     */
    public enum AchievementType {
        // Streak achievements
        FIRST_STREAK("first_streak", "First Flame", "Get your first 3-day streak", "🔥", "STREAK", 3),
        WEEK_WARRIOR("week_warrior", "Week Warrior", "Maintain a 7-day streak", "⭐", "STREAK", 7),
        FORTNIGHT_FIGHTER("fortnight_fighter", "Fortnight Fighter", "Maintain a 14-day streak", "💪", "STREAK", 14),
        HABIT_MASTER("habit_master", "Habit Master", "Achieve a 30-day streak", "🏆", "STREAK", 30),
        LEGENDARY("legendary", "Legendary", "Achieve a 100-day streak", "👑", "STREAK", 100),

        // Consistency achievements
        GETTING_STARTED("getting_started", "Getting Started", "Complete 10 total check-ins", "🌱", "CONSISTENCY", 10),
        CONSISTENT("consistent", "Consistent", "Complete 50 total check-ins", "💎", "CONSISTENCY", 50),
        DEDICATED("dedicated", "Dedicated", "Complete 100 total check-ins", "🎯", "CONSISTENCY", 100),
        UNSTOPPABLE("unstoppable", "Unstoppable", "Complete 500 total check-ins", "🚀", "CONSISTENCY", 500),

        // Milestone achievements
        FIRST_HABIT("first_habit", "First Step", "Create your first habit", "👣", "MILESTONE", 1),
        HABIT_COLLECTOR("habit_collector", "Habit Collector", "Create 5 habits", "📚", "MILESTONE", 5),
        PERFECT_DAY("perfect_day", "Perfect Day", "Complete all habits in a day", "✨", "MILESTONE", 1);

        public final String id;
        public final String name;
        public final String description;
        public final String icon;
        public final String category;
        public final int requiredProgress;

        AchievementType(String id, String name, String description, String icon, String category,
                int requiredProgress) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.category = category;
            this.requiredProgress = requiredProgress;
        }
    }

    /**
     * Factory method to create Achievement from enum type
     */
    public static Achievement fromType(AchievementType type, int currentProgress, boolean unlocked,
            LocalDateTime unlockedAt) {
        return Achievement.builder()
                .id(type.id)
                .name(type.name)
                .description(type.description)
                .icon(type.icon)
                .category(type.category)
                .unlocked(unlocked)
                .unlockedAt(unlockedAt)
                .currentProgress(currentProgress)
                .requiredProgress(type.requiredProgress)
                .progressPercentage(Math.min(100.0, (currentProgress * 100.0) / type.requiredProgress))
                .build();
    }
}
