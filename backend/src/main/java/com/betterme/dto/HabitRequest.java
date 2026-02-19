package com.betterme.dto;

import com.betterme.model.HabitFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitRequest {

    @NotBlank(message = "Habit name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;

    @Size(max = 500, message = "Description too long")
    private String description;

    private HabitFrequency frequency;

    private String target;

    private String icon;
}
