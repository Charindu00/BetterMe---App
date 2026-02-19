package com.betterme.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 
 */
@Entity // Marks this as a JPA entity (database table)
@Table(name = "users") // Table name in database
@Data // Lombok: generates getters/setters
@Builder // Lombok: builder pattern
@NoArgsConstructor // Lombok: empty constructor
@AllArgsConstructor // Lombok: all-args constructor
public class User implements UserDetails {

    /**
     * PRIMARY KEY
     * 
     * @Id marks this as the primary key
     * @GeneratedValue tells database to auto-generate IDs (1, 2, 3...)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * VALIDATION ANNOTATIONS
     * These validate data BEFORE it reaches the database
     * 
     * @NotBlank = Cannot be null or empty
     * @Size = Length constraints
     * @Email = Must be valid email format
     * @Column = Database column configuration
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true) // unique = no duplicate emails
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    /**
     * USER ROLE
     * Determines access level: USER or ADMIN
     * 
     * @Enumerated(STRING) stores the enum as text ("USER", "ADMIN")
     * @Builder.Default ensures default value works with Lombok builder
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * Profile picture stored as Base64 string or URL
     */
    @Column(name = "profile_picture", columnDefinition = "TEXT")
    private String profilePicture;

    /**
     * Email verification status
     */
    @Column(name = "email_verified", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * TIMESTAMPS
     * Good practice: Always track when records are created/updated
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA LIFECYCLE CALLBACKS
     * 
     * @PrePersist runs BEFORE inserting to database
     * @PreUpdate runs BEFORE updating in database
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * UserDetails INTERFACE IMPLEMENTATION
     * Spring Security requires UserDetails interface for authentication.
     * These methods tell Spring Security about the user.
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returns user's roles/permissions based on their actual role
        // Spring Security expects "ROLE_" prefix
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        // Spring Security uses this for authentication
        // We use email as username
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account never expires
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account is never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Password never expires
    }

    @Override
    public boolean isEnabled() {
        return true; // Account is always enabled
    }
}
