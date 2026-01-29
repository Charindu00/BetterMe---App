package com.betterme.service;

import com.betterme.dto.AuthResponse;
import com.betterme.dto.LoginRequest;
import com.betterme.dto.RegisterRequest;
import com.betterme.model.User;
import com.betterme.repository.UserRepository;
import com.betterme.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Service Layer ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Services contain BUSINESS LOGIC - the actual "brain" of your app. ║
 * ║ ║
 * ║ Architecture: Controller → Service → Repository → Database ║
 * ║ ║
 * ║ Controller: Handles HTTP requests, validation ║
 * ║ Service: Business logic, rules, calculations ║
 * ║ Repository: Database operations ║
 * ║ ║
 * ║ WHY separate them? ║
 * ║ 1. Single Responsibility - each class does one thing ║
 * ║ 2. Testability - easy to test services in isolation ║
 * ║ 3. Reusability - same service can be used by multiple controllers ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Service
@RequiredArgsConstructor // Lombok: constructor injection
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * ─────────────────────────────────────────────────────────────────────
     * REGISTER NEW USER
     * ─────────────────────────────────────────────────────────────────────
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .message("Email already registered!")
                    .build();
        }

        // Create new user (password is HASHED, never stored as plain text!)
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 🔒 Encrypt!
                .build();

        // Save to database
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String jwtToken = jwtService.generateToken(savedUser);

        // Return response with token
        return AuthResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .message("Registration successful! Welcome to BetterMe!")
                .build();
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * LOGIN EXISTING USER
     * ─────────────────────────────────────────────────────────────────────
     */
    public AuthResponse login(LoginRequest request) {
        // Authenticate user (throws exception if invalid credentials)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // Find user in database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);

        // Return response with token
        return AuthResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .message("Login successful! Welcome back!")
                .build();
    }
}
