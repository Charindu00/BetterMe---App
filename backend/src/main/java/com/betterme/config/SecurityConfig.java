package com.betterme.config;

import com.betterme.repository.UserRepository;
import com.betterme.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @Configuration = This class provides Spring beans (objects Spring manages)
 * @EnableWebSecurity = Enable Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserRepository userRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthFilter, UserRepository userRepository) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userRepository = userRepository;
    }

    /**
     * USER DETAILS SERVICE
     * Tells Spring Security HOW to load a user from database
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * PASSWORD ENCODER
     * BCrypt is the industry standard for password hashing
     * NEVER store plain text passwords!
     * 
     * How it works:
     * "password123" → "$2a$10$N9qo8uLOickgx2ZMRZoMy..." (one-way hash)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AUTHENTICATION PROVIDER
     * Connects UserDetailsService + PasswordEncoder
     * Used to authenticate username/password
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AUTHENTICATION MANAGER
     * Used in AuthService to authenticate login requests
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS CONFIGURATION
     * CORS (Cross-Origin Resource Sharing) allows your React frontend
     * (running on localhost:5173) to call your Spring Boot backend
     * (running on localhost:8080)
     * 
     * Without this, browsers block cross-origin requests for security!
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                frontendUrl,
                "http://localhost:5173", // Vite dev server
                "http://localhost:5174", // Vite alternative port
                "http://localhost:3000" // Alternative port
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * SECURITY FILTER CHAIN - THE MAIN SECURITY CONFIGURATION
     * This defines:
     * - Which URLs need authentication
     * - Which URLs are public
     * - How to handle sessions (we use stateless JWT)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT auth)
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // URL Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC endpoints (no authentication needed)
                        .requestMatchers(
                                "/api/auth/**", // Login, Register
                                "/api/public/**", // Any public endpoints
                                "/api/announcements/active", // Public announcements
                                "/error" // Error pages
                        ).permitAll()

                        // ADMIN-ONLY endpoints (requires ADMIN role)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ALL OTHER endpoints require authentication
                        .anyRequest().authenticated())

                // Stateless session (no cookies, only JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Use our custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter BEFORE standard auth filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Allow H2 console frames
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
