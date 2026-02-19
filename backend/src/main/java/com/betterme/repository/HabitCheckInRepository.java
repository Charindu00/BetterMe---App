package com.betterme.repository;

import com.betterme.model.Habit;
import com.betterme.model.HabitCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 */
@Repository
public interface HabitCheckInRepository extends JpaRepository<HabitCheckIn, Long> {

    /**
     * Find check-in for a specific date
     * Used to check "Did I already check in today?"
     */
    Optional<HabitCheckIn> findByHabitAndCheckInDate(Habit habit, LocalDate date);

    /**
     * Does a check-in exist for this habit on this date?
     */
    boolean existsByHabitAndCheckInDate(Habit habit, LocalDate date);

    /**
     * Get check-in history for a habit within a date range
     * Useful for calendar view!
     */
    List<HabitCheckIn> findByHabitAndCheckInDateBetweenOrderByCheckInDateDesc(
            Habit habit, LocalDate startDate, LocalDate endDate);

    /**
     * Get all check-ins for a habit (most recent first)
     */
    List<HabitCheckIn> findByHabitOrderByCheckInDateDesc(Habit habit);

    /**
     * Count total check-ins for a habit
     */
    long countByHabit(Habit habit);

    /**
     * Count completed check-ins (for success rate calculation)
     */
    long countByHabitAndCompleted(Habit habit, Boolean completed);

    /**
     * Get check-ins for the last N days
     * 
     * ?1 = first parameter (habit)
     * ?2 = second parameter (startDate)
     */
    @Query("SELECT c FROM HabitCheckIn c WHERE c.habit = ?1 AND c.checkInDate >= ?2 ORDER BY c.checkInDate DESC")
    List<HabitCheckIn> findRecentCheckIns(Habit habit, LocalDate startDate);

    /**
     * Get check-ins for multiple habits within a date range (for analytics)
     */
    @Query("SELECT c FROM HabitCheckIn c WHERE c.habit.id IN ?1 AND c.checkInDate BETWEEN ?2 AND ?3")
    List<HabitCheckIn> findByHabitIdInAndCheckInDateBetween(List<Long> habitIds, LocalDate startDate,
            LocalDate endDate);

    /**
     * Count check-ins after a date (for analytics)
     */
    long countByHabitAndCheckInDateAfter(Habit habit, LocalDate date);
}
