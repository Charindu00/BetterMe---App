package com.betterme.model;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Enum in Java ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ An enum is a special type that has a FIXED set of constants. ║
 * ║ ║
 * ║ Why use enum for roles: ║
 * ║ 1. Type safety - can't accidentally use "ADMIM" (typo) ║
 * ║ 2. IDE autocomplete ║
 * ║ 3. Easy to extend - just add new values ║
 * ║ ║
 * ║ In database, this is stored as a STRING (e.g., "USER" or "ADMIN") ║
 * ║ JPA handles the conversion automatically! ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
public enum Role {
    USER, // Regular app user
    ADMIN // Has access to admin dashboard
}
