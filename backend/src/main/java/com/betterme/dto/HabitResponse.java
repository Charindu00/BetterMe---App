package com.betterme.dto;

import com.betterme.model.Habit;
import com.betterme.model.HabitFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Response DTO ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Defines what data we send BACK to the client. ║
 * ║ ║
 * ║ Notice: No password, no user object (just userId) ║
 * ║ This prevents sensitive data leakage! ║
 * ║ ║
 * ║ The fromEntity() static method is a common pattern to convert ║
 * ║ Entity → DTO cleanly. ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitResponse {

    private Long id;
    private String name;
    private String description;
    private HabitFrequency frequency;
    private String target;
    private String icon;

    // Streak info
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalCheckIns;
    private LocalDateTime lastCheckInDate;

    // Is today checked in?
    private Boolean checkedInToday;

    // Timestamps
    private LocalDateTime createdAt;

    /**
     * ─────────────────────────────────────────────────────────────────────
     * FACTORY METHOD: Entity → DTO conversion
     * ─────────────────────────────────────────────────────────────────────
     */
    public static HabitResponse fromEntity(Habit habit, boolean checkedInToday) {
        return HabitResponse.builder()
                .id(habit.getId())
                .name(habit.getName())
                .description(habit.getDescription())
                .frequency(habit.getFrequency())
                .target(habit.getTarget())
                .icon(habit.getIcon())
                .currentStreak(habit.getCurrentStreak())
                .longestStreak(habit.getLongestStreak())
                .totalCheckIns(habit.getTotalCheckIns())
                .lastCheckInDate(habit.getLastCheckInDate())
                .checkedInToday(checkedInToday)
                .createdAt(habit.getCreatedAt())
                .build();
    }
}
