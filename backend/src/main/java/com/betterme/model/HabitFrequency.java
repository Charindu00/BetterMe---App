package com.betterme.model;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Frequency Enum ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ How often a habit should be performed. ║
 * ║ ║
 * ║ This affects streak calculations: ║
 * ║ - DAILY: Must check in every day to maintain streak ║
 * ║ - WEEKLY: Must check in at least once per week ║
 * ║ - CUSTOM: User-defined frequency ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
public enum HabitFrequency {
    DAILY, // Every day
    WEEKLY, // Once per week
    WEEKDAYS, // Monday-Friday only
    CUSTOM // User-defined
}
