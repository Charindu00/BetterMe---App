package com.betterme.repository;

import com.betterme.model.Goal;
import com.betterme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Goal Repository ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Data access methods for Goals. ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * Find all goals for a user (most recent first)
     */
    List<Goal> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find active goals only
     */
    List<Goal> findByUserAndActiveOrderByCreatedAtDesc(User user, Boolean active);

    /**
     * Find a goal ensuring it belongs to the user
     */
    Optional<Goal> findByIdAndUser(Long id, User user);

    /**
     * Count goals by completion status
     */
    long countByUserAndCompleted(User user, Boolean completed);

    /**
     * Find goals with deadlines coming up
     */
    @Query("SELECT g FROM Goal g WHERE g.user = ?1 AND g.deadline <= ?2 AND g.completed = false AND g.active = true")
    List<Goal> findUpcomingDeadlines(User user, LocalDate date);

    /**
     * Find completed goals
     */
    List<Goal> findByUserAndCompletedOrderByCompletedAtDesc(User user, Boolean completed);
}
