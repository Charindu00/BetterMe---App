package com.betterme.config;

import com.betterme.model.Role;
import com.betterme.model.User;
import com.betterme.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: CommandLineRunner ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ CommandLineRunner runs code AFTER Spring Boot starts but BEFORE the app ║
 * ║ is ready to accept requests. Perfect for: ║
 * ║ ║
 * ║ 1. Database seeding (creating default data) ║
 * ║ 2. Cache warming ║
 * ║ 3. Validation checks ║
 * ║ ║
 * ║ Flow: Spring Starts → DataSeeder.run() → App Ready ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Component
@RequiredArgsConstructor
@Slf4j // Lombok: creates a logger named "log"
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Read from application.yml or environment variables
    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;

    /**
     * This method runs on application startup
     */
    @Override
    public void run(String... args) {
        createDefaultAdmin();
    }

    /**
     * Creates default admin user if one doesn't exist
     */
    private void createDefaultAdmin() {
        // Check if admin already exists
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("✅ Admin user already exists: {}", adminEmail);
            return;
        }

        // Create new admin user
        User admin = User.builder()
                .name(adminName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);

        log.info("═══════════════════════════════════════════════════════════");
        log.info("🔐 DEFAULT ADMIN CREATED");
        log.info("   Email:    {}", adminEmail);
        log.info("   Password: {}", adminPassword);
        log.info("   ⚠️  Change these credentials in production!");
        log.info("═══════════════════════════════════════════════════════════");
    }
}
