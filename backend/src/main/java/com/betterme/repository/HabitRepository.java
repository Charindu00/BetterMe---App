package com.betterme.repository;

import com.betterme.model.Habit;
import com.betterme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Habit Repository ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Provides data access methods for Habit entity. ║
 * ║ ║
 * ║ Notice the naming convention for query methods: ║
 * ║ findByUser → SELECT * FROM habits WHERE user_id = ? ║
 * ║ findByUserAndActive → WHERE user_id = ? AND active = ? ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    /**
     * Find all habits for a specific user
     */
    List<Habit> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find only active habits for a user
     */
    List<Habit> findByUserAndActiveOrderByCreatedAtDesc(User user, Boolean active);

    /**
     * Find a specific habit ensuring it belongs to the user
     * (Important for security - prevents accessing other users' habits!)
     */
    Optional<Habit> findByIdAndUser(Long id, User user);

    /**
     * Count user's habits (for stats)
     */
    long countByUser(User user);

    /**
     * Count user's active habits
     */
    long countByUserAndActive(User user, Boolean active);

    /**
     * Get user's habits with highest streaks
     * 
     * LEARNING POINT: Custom JPQL Query
     * When Spring can't generate query from method name, use @Query
     */
    @Query("SELECT h FROM Habit h WHERE h.user = ?1 ORDER BY h.currentStreak DESC")
    List<Habit> findTopStreaksByUser(User user);
}
