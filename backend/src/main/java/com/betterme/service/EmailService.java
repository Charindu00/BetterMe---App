package com.betterme.service;

import com.betterme.model.User;
import com.betterme.model.VerificationToken;
import com.betterme.model.VerificationToken.TokenType;
import com.betterme.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationTokenRepository tokenRepository;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@betterme.com}")
    private String fromEmail;

    private static final int EMAIL_VERIFICATION_EXPIRY_HOURS = 24;
    private static final int PASSWORD_RESET_EXPIRY_HOURS = 1;

    /**
     * Generate a unique verification token
     */
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Create and save a verification token
     */
    public VerificationToken createVerificationToken(User user, TokenType tokenType) {
        // Delete any existing tokens of this type for the user
        tokenRepository.deleteByUserAndTokenType(user, tokenType);

        int expiryHours = tokenType == TokenType.EMAIL_VERIFICATION
                ? EMAIL_VERIFICATION_EXPIRY_HOURS
                : PASSWORD_RESET_EXPIRY_HOURS;

        VerificationToken token = VerificationToken.builder()
                .token(generateToken())
                .user(user)
                .tokenType(tokenType)
                .expiryDate(LocalDateTime.now().plusHours(expiryHours))
                .build();

        return tokenRepository.save(token);
    }

    /**
     * Send email verification link
     */
    public void sendVerificationEmail(User user) {
        VerificationToken token = createVerificationToken(user, TokenType.EMAIL_VERIFICATION);
        String verificationLink = frontendUrl + "/verify-email?token=" + token.getToken();

        String subject = "Verify your BetterMe account";
        String htmlContent = buildVerificationEmailHtml(user.getName(), verificationLink);

        sendEmail(user.getEmail(), subject, htmlContent);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    /**
     * Send password reset link
     */
    public void sendPasswordResetEmail(User user) {
        VerificationToken token = createVerificationToken(user, TokenType.PASSWORD_RESET);
        String resetLink = frontendUrl + "/reset-password?token=" + token.getToken();

        String subject = "Reset your BetterMe password";
        String htmlContent = buildPasswordResetEmailHtml(user.getName(), resetLink);

        sendEmail(user.getEmail(), subject, htmlContent);
        log.info("Password reset email sent to: {}", user.getEmail());
    }

    /**
     * Send HTML email
     */
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Build verification email HTML
     */
    private String buildVerificationEmailHtml(String name, String link) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f5f5; padding: 40px 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                        .logo { font-size: 28px; font-weight: 800; color: #6366F1; margin-bottom: 30px; }
                        h1 { color: #1e293b; margin-bottom: 20px; }
                        p { color: #64748b; line-height: 1.6; }
                        .btn { display: inline-block; background: linear-gradient(135deg, #6366F1, #8B5CF6); color: white; padding: 14px 32px; text-decoration: none; border-radius: 8px; font-weight: 600; margin: 20px 0; }
                        .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #e2e8f0; font-size: 13px; color: #94a3b8; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="logo">✨ BetterMe</div>
                        <h1>Verify Your Email</h1>
                        <p>Hi %s,</p>
                        <p>Thanks for signing up! Please verify your email address by clicking the button below:</p>
                        <a href="%s" class="btn">Verify Email Address</a>
                        <p>This link will expire in 24 hours.</p>
                        <p>If you didn't create an account, you can safely ignore this email.</p>
                        <div class="footer">
                            <p>© 2026 BetterMe. Build better habits, become your best self.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(name, link);
    }

    /**
     * Build password reset email HTML
     */
    private String buildPasswordResetEmailHtml(String name, String link) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f5f5; padding: 40px 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                        .logo { font-size: 28px; font-weight: 800; color: #6366F1; margin-bottom: 30px; }
                        h1 { color: #1e293b; margin-bottom: 20px; }
                        p { color: #64748b; line-height: 1.6; }
                        .btn { display: inline-block; background: linear-gradient(135deg, #6366F1, #8B5CF6); color: white; padding: 14px 32px; text-decoration: none; border-radius: 8px; font-weight: 600; margin: 20px 0; }
                        .warning { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 12px; margin: 20px 0; border-radius: 4px; }
                        .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #e2e8f0; font-size: 13px; color: #94a3b8; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="logo">✨ BetterMe</div>
                        <h1>Reset Your Password</h1>
                        <p>Hi %s,</p>
                        <p>We received a request to reset your password. Click the button below to create a new password:</p>
                        <a href="%s" class="btn">Reset Password</a>
                        <div class="warning">
                            <strong>⏰ This link expires in 1 hour.</strong>
                        </div>
                        <p>If you didn't request a password reset, please ignore this email or contact support if you're concerned about your account security.</p>
                        <div class="footer">
                            <p>© 2026 BetterMe. Build better habits, become your best self.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(name, link);
    }
}
