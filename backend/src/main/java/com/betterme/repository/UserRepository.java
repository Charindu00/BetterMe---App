package com.betterme.repository;

import com.betterme.model.Role;
import com.betterme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * CUSTOM QUERY METHODS
     * Spring generates SQL from method name! (Query Method Derivation)
     * 
     * findByEmail → SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email exists (for registration validation)
     */
    Boolean existsByEmail(String email);

    /**
     * Count users by role (for admin stats)
     */
    long countByRole(Role role);
}
