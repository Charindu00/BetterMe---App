package com.betterme.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: JWT Authentication Filter ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ This filter runs on EVERY request to check if user is authenticated. ║
 * ║ ║
 * ║ Flow: ║
 * ║ 1. Extract "Authorization: Bearer <token>" header ║
 * ║ 2. Validate the token ║
 * ║ 3. If valid, set user as authenticated ║
 * ║ 4. Pass request to next filter/controller ║
 * ║ ║
 * ║ OncePerRequestFilter ensures this runs exactly once per request ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Component
@RequiredArgsConstructor // Lombok: creates constructor for final fields
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // ─────────────────────────────────────────────────────────────────
        // STEP 1: Get Authorization header
        // ─────────────────────────────────────────────────────────────────
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // If no Authorization header or doesn't start with "Bearer ", skip
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continue to next filter
            return;
        }

        // ─────────────────────────────────────────────────────────────────
        // STEP 2: Extract token (remove "Bearer " prefix)
        // ─────────────────────────────────────────────────────────────────
        jwt = authHeader.substring(7); // "Bearer " is 7 characters

        // ─────────────────────────────────────────────────────────────────
        // STEP 3: Extract email from token
        // ─────────────────────────────────────────────────────────────────
        userEmail = jwtService.extractUsername(jwt);

        // ─────────────────────────────────────────────────────────────────
        // STEP 4: Validate and authenticate
        // ─────────────────────────────────────────────────────────────────
        // Only process if we have an email and user isn't already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user from database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Check if token is valid
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials (null because we already validated)
                        userDetails.getAuthorities() // roles/permissions
                );

                // Add extra details from the request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // SET THE USER AS AUTHENTICATED! 🎉
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue to next filter in chain
        filterChain.doFilter(request, response);
    }
}
