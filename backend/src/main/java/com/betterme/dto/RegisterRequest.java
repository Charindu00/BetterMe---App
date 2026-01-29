package com.betterme.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: DTO (Data Transfer Object)                               ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ DTOs are objects that carry data between layers of your application.     ║
 * ║                                                                          ║
 * ║ WHY use DTOs instead of Entity directly?                                 ║
 * ║ 1. Security: Don't expose internal entity structure                      ║
 * ║ 2. Flexibility: API request/response can differ from database schema     ║
 * ║ 3. Validation: Separate validation for different operations              ║
 * ║ 4. Performance: Return only needed fields                                ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

// ═══════════════════════════════════════════════════════════════════════════
// REQUEST DTOs - What the client SENDS to us
// ═══════════════════════════════════════════════════════════════════════════

/**
 * Used for user registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
