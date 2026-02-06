package com.betterme.service;

import com.betterme.dto.PasswordChangeRequest;
import com.betterme.dto.ProfileUpdateRequest;
import com.betterme.dto.UserProfileResponse;
import com.betterme.model.User;
import com.betterme.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get user profile information
     */
    public UserProfileResponse getProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Update user name
     */
    @Transactional
    public UserProfileResponse updateProfile(User user, ProfileUpdateRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        User saved = userRepository.save(user);
        return getProfile(saved);
    }

    /**
     * Change password with verification
     */
    @Transactional
    public void changePassword(User user, PasswordChangeRequest request) {
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Upload/update profile picture (Base64)
     */
    @Transactional
    public UserProfileResponse updateAvatar(User user, String avatarBase64) {
        user.setProfilePicture(avatarBase64);
        User saved = userRepository.save(user);
        return getProfile(saved);
    }

    /**
     * Remove profile picture
     */
    @Transactional
    public UserProfileResponse removeAvatar(User user) {
        user.setProfilePicture(null);
        User saved = userRepository.save(user);
        return getProfile(saved);
    }
}
