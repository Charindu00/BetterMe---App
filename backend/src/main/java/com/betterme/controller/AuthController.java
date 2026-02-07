package com.betterme.controller;

import com.betterme.dto.*;
import com.betterme.model.User;
import com.betterme.model.VerificationToken;
import com.betterme.model.VerificationToken.TokenType;
import com.betterme.repository.UserRepository;
import com.betterme.repository.VerificationTokenRepository;
import com.betterme.service.AuthService;
import com.betterme.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller with Email Verification Support
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * REGISTER ENDPOINT - Now sends verification email
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = authService.register(request, httpRequest);

        // Send verification email
        try {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(emailService::sendVerificationEmail);
        } catch (Exception e) {
            log.warn("Failed to send verification email: {}", e.getMessage());
            // Don't fail registration if email fails
        }

        return ResponseEntity.ok(response);
    }

    /**
     * LOGIN ENDPOINT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    /**
     * VERIFY EMAIL - Verify user's email with token
     */
    @PostMapping("/verify-email")
    @Transactional
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        Optional<VerificationToken> optToken = tokenRepository.findByTokenAndTokenType(
                token, TokenType.EMAIL_VERIFICATION);

        if (optToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid verification token"));
        }

        VerificationToken verificationToken = optToken.get();

        if (verificationToken.isExpired()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Verification token has expired. Please request a new one."));
        }

        if (verificationToken.isUsed()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "This token has already been used"));
        }

        // Mark user as verified
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Mark token as used
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        log.info("Email verified for user: {}", user.getEmail());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email verified successfully! You can now login."));
    }

    /**
     * RESEND VERIFICATION EMAIL
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No account found with this email"));
        }

        User user = optUser.get();
        if (user.isEmailVerified()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email is already verified"));
        }

        try {
            emailService.sendVerificationEmail(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Verification email sent! Check your inbox."));
        } catch (Exception e) {
            log.error("Failed to resend verification email", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to send email. Please try again later."));
        }
    }

    /**
     * FORGOT PASSWORD - Send password reset email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        Optional<User> optUser = userRepository.findByEmail(request.getEmail());

        // Always return success to prevent email enumeration
        if (optUser.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "If an account exists with this email, you will receive a password reset link."));
        }

        try {
            emailService.sendPasswordResetEmail(optUser.get());
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "If an account exists with this email, you will receive a password reset link."));
    }

    /**
     * RESET PASSWORD - Reset password with token
     */
    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Optional<VerificationToken> optToken = tokenRepository.findByTokenAndTokenType(
                request.getToken(), TokenType.PASSWORD_RESET);

        if (optToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid or expired reset token"));
        }

        VerificationToken resetToken = optToken.get();

        if (resetToken.isExpired()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Reset token has expired. Please request a new one."));
        }

        if (resetToken.isUsed()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "This reset link has already been used"));
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset for user: {}", user.getEmail());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password reset successfully! You can now login with your new password."));
    }

    /**
     * HEALTH CHECK ENDPOINT
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("BetterMe API is running! 🚀");
    }
}
