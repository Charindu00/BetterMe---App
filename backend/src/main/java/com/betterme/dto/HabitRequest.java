package com.betterme.dto;

import com.betterme.model.HabitFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Request DTO ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Defines what data the client sends to CREATE or UPDATE a habit. ║
 * ║ ║
 * ║ Why not use the Entity directly? ║
 * ║ 1. Security: Client shouldn't set id, streaks, user fields ║
 * ║ 2. Flexibility: API contract can differ from database schema ║
 * ║ 3. Validation: Different rules for create vs update ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
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
