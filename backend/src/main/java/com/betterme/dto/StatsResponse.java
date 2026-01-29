package com.betterme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Stats DTO ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ This DTO provides dashboard statistics to the admin panel. ║
 * ║ Using a DTO allows us to control exactly what data is sent to frontend. ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {

    private long totalUsers;
    private long totalAdmins;
    private long totalActivityLogs;
    private long totalAnnouncements;
    private long activeAnnouncements;

    // Activity breakdown (login, registration, etc.)
    private Map<String, Long> activityBreakdown;
}
