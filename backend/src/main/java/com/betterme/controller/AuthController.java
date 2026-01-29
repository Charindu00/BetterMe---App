package com.betterme.controller;

import com.betterme.dto.AuthResponse;
import com.betterme.dto.LoginRequest;
import com.betterme.dto.RegisterRequest;
import com.betterme.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: REST Controller ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Controllers handle HTTP requests - they are the "front door" of your API ║
 * ║ ║
 * ║ @RestController combines: ║
 * ║ - @Controller (makes it a web controller) ║
 * ║ - @ResponseBody (automatically converts return to JSON) ║
 * ║ ║
 * ║ BEST PRACTICES: ║
 * ║ 1. Controllers should be THIN - just receive, validate, delegate ║
 * ║ 2. Business logic goes in SERVICE layer ║
 * ║ 3. Use proper HTTP methods (POST for create, GET for read, etc) ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@RestController
@RequestMapping("/api/auth") // Base URL: /api/auth/*
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * ─────────────────────────────────────────────────────────────────────
     * REGISTER ENDPOINT
     * ─────────────────────────────────────────────────────────────────────
     * POST /api/auth/register
     * 
     * Request Body (JSON):
     * {
     * "name": "John Doe",
     * "email": "john@example.com",
     * "password": "password123"
     * }
     * 
     * @Valid triggers validation defined in RegisterRequest DTO
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * LOGIN ENDPOINT
     * ─────────────────────────────────────────────────────────────────────
     * POST /api/auth/login
     * 
     * Request Body (JSON):
     * {
     * "email": "john@example.com",
     * "password": "password123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * HEALTH CHECK ENDPOINT
     * ─────────────────────────────────────────────────────────────────────
     * GET /api/auth/health
     * 
     * Simple endpoint to verify API is running
     * Useful for deployment health checks!
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("BetterMe API is running! 🚀");
    }
}
