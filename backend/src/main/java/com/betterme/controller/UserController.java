package com.betterme.controller;

import com.betterme.dto.*;
import com.betterme.model.User;
import com.betterme.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user));
    }

    /**
     * Update user profile (name)
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    /**
     * Change password
     */
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody PasswordChangeRequest request) {
        try {
            userService.changePassword(user, request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Upload/update profile picture
     */
    @PostMapping("/avatar")
    public ResponseEntity<UserProfileResponse> uploadAvatar(
            @AuthenticationPrincipal User user,
            @RequestBody AvatarUploadRequest request) {
        return ResponseEntity.ok(userService.updateAvatar(user, request.getAvatar()));
    }

    /**
     * Remove profile picture
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<UserProfileResponse> removeAvatar(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.removeAvatar(user));
    }
}
