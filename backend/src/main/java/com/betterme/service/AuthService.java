package com.betterme.service;

import com.betterme.dto.AuthResponse;
import com.betterme.dto.LoginRequest;
import com.betterme.dto.RegisterRequest;
import com.betterme.model.User;
import com.betterme.repository.UserRepository;
import com.betterme.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final ActivityLogService activityLogService;

        /**
         * ─────────────────────────────────────────────────────────────────────
         * REGISTER NEW USER
         * ─────────────────────────────────────────────────────────────────────
         */
        public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmail())) {
                        // Log failed registration attempt
                        activityLogService.logAnonymous(
                                        "REGISTRATION_FAILED",
                                        "Email already exists: " + request.getEmail(),
                                        httpRequest);

                        return AuthResponse.builder()
                                        .message("Email already registered!")
                                        .build();
                }

                // Create new user (password is HASHED, never stored as plain text!)
                User user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .build();

                // Save to database
                User savedUser = userRepository.save(user);

                // Log successful registration
                activityLogService.log(
                                savedUser,
                                "USER_REGISTERED",
                                "New user registered",
                                httpRequest);

                // Generate JWT token
                String jwtToken = jwtService.generateToken(savedUser);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .type("Bearer")
                                .id(savedUser.getId())
                                .name(savedUser.getName())
                                .email(savedUser.getEmail())
                                .role(savedUser.getRole().name())
                                .message("Registration successful! Welcome to BetterMe!")
                                .build();
        }

        /**
         * ─────────────────────────────────────────────────────────────────────
         * LOGIN EXISTING USER
         * ─────────────────────────────────────────────────────────────────────
         */
        public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
                try {
                        // Authenticate user (throws exception if invalid credentials)
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));

                        // Find user in database
                        User user = userRepository.findByEmail(request.getEmail())
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                        // Log successful login
                        activityLogService.log(
                                        user,
                                        "USER_LOGIN",
                                        "User logged in successfully",
                                        httpRequest);

                        // Generate JWT token
                        String jwtToken = jwtService.generateToken(user);

                        return AuthResponse.builder()
                                        .token(jwtToken)
                                        .type("Bearer")
                                        .id(user.getId())
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .role(user.getRole().name())
                                        .message("Login successful! Welcome back!")
                                        .build();

                } catch (Exception e) {
                        // Log failed login attempt
                        activityLogService.logAnonymous(
                                        "LOGIN_FAILED",
                                        "Failed login attempt for: " + request.getEmail(),
                                        httpRequest);
                        throw e;
                }
        }
}
