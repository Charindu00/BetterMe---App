package com.betterme.model;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Goal Type Enum ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ Different ways to measure goal progress: ║
 * ║ ║
 * ║ COUNT - Track a number (e.g., "Read 12 books") ║
 * ║ STREAK - Maintain consecutive days (e.g., "30-day meditation streak") ║
 * ║ DURATION - Track time spent (e.g., "Exercise 100 hours") ║
 * ║ HABIT_LINK - Link to a habit's progress ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
public enum GoalType {
    COUNT, // Track a count (read 12 books, run 50 miles)
    STREAK, // Achieve a streak (30-day streak)
    DURATION, // Track time in minutes (100 hours of exercise)
    HABIT_LINK // Linked to a habit's total check-ins
}
