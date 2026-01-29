package com.betterme.controller;

import com.betterme.dto.AuthResponse;
import com.betterme.dto.LoginRequest;
import com.betterme.dto.RegisterRequest;
import com.betterme.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * ─────────────────────────────────────────────────────────────────────
     * REGISTER ENDPOINT
     * ─────────────────────────────────────────────────────────────────────
     * POST /api/auth/register
     * 
     * HttpServletRequest is injected by Spring to get client info (IP, etc.)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.register(request, httpRequest));
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * LOGIN ENDPOINT
     * ─────────────────────────────────────────────────────────────────────
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * HEALTH CHECK ENDPOINT
     * ─────────────────────────────────────────────────────────────────────
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("BetterMe API is running! 🚀");
    }
}
