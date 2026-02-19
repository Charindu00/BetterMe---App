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
public class MotivationResponse {

    private String message;
    private MotivationType type;
    private boolean aiGenerated;
    private LocalDateTime generatedAt;

    // Context that was used to generate the message
    private MotivationContext context;

    public enum MotivationType {
        DAILY, // General daily motivation
        HABIT_TIP, // Specific habit advice
        CELEBRATION, // Achievement celebration
        ENCOURAGEMENT, // When user is struggling
        CHAT // Chat conversation response
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MotivationContext {
        private int currentStreak;
        private int totalHabits;
        private int completedToday;
        private String habitName;
        private String achievementName;
    }
}
