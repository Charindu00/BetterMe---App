package com.betterme.repository;

import com.betterme.model.Role;
import com.betterme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Repository Pattern ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ A Repository handles all DATABASE OPERATIONS for an entity. ║
 * ║ ║
 * ║ JpaRepository<User, Long> means: ║
 * ║ - User = The entity type we're working with ║
 * ║ - Long = The type of the primary key (id) ║
 * ║ ║
 * ║ MAGIC: Spring creates the implementation automatically! ║
 * ║ You just define the interface, Spring does the rest. ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 * 
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FREE METHODS from JpaRepository: ║
 * ║ - save(entity) → INSERT or UPDATE ║
 * ║ - findById(id) → SELECT by ID ║
 * ║ - findAll() → SELECT all rows ║
 * ║ - deleteById(id) → DELETE by ID ║
 * ║ - count() → COUNT rows ║
 * ║ - existsById(id) → Check if exists ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ─────────────────────────────────────────────────────────────────────
     * CUSTOM QUERY METHODS
     * ─────────────────────────────────────────────────────────────────────
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
