package com.betterme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * RESPONSE DTO - What we SEND to the client
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * After successful login/register, we return:
 * - JWT token (for future authenticated requests)
 * - User info (for immediate use in UI)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token; // JWT token
    @Builder.Default
    private String type = "Bearer"; // Token type (always "Bearer" for JWT)
    private Long id; // User ID
    private String name; // User name
    private String email; // User email
    private String role; // User role (USER or ADMIN)
    private String message; // Success/error message
}
