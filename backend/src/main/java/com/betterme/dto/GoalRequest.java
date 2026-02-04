package com.betterme.dto;

import com.betterme.model.GoalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating/updating goals
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {

    @NotBlank(message = "Goal title is required")
    private String title;

    private String description;

    @NotNull(message = "Goal type is required")
    private GoalType type;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target must be positive")
    private Integer targetValue;

    private String unit;
    private String icon;
    private String category;
    private LocalDate deadline;
    private Long linkedHabitId; // Optional: link to a habit
}
