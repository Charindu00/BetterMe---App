package com.betterme.dto;

import com.betterme.model.Goal;
import com.betterme.model.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for goals
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {

    private Long id;
    private String title;
    private String description;
    private GoalType type;
    private String icon;
    private String category;

    // Progress
    private Integer targetValue;
    private Integer currentValue;
    private String unit;
    private double progressPercentage;

    // Dates
    private LocalDate startDate;
    private LocalDate deadline;
    private LocalDateTime completedAt;

    // Status
    private Boolean completed;
    private Boolean overdue;
    private Integer daysRemaining;

    // Linked habit
    private Long linkedHabitId;
    private String linkedHabitName;

    /**
     * Factory method
     */
    public static GoalResponse fromEntity(Goal goal) {
        Integer daysRemaining = null;
        if (goal.getDeadline() != null && !goal.getCompleted()) {
            daysRemaining = (int) java.time.temporal.ChronoUnit.DAYS
                    .between(LocalDate.now(), goal.getDeadline());
        }

        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .type(goal.getType())
                .icon(goal.getIcon())
                .category(goal.getCategory())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .unit(goal.getUnit())
                .progressPercentage(Math.round(goal.getProgressPercentage() * 10) / 10.0)
                .startDate(goal.getStartDate())
                .deadline(goal.getDeadline())
                .completedAt(goal.getCompletedAt())
                .completed(goal.getCompleted())
                .overdue(goal.isOverdue())
                .daysRemaining(daysRemaining)
                .linkedHabitId(goal.getLinkedHabit() != null ? goal.getLinkedHabit().getId() : null)
                .linkedHabitName(goal.getLinkedHabit() != null ? goal.getLinkedHabit().getName() : null)
                .build();
    }
}
