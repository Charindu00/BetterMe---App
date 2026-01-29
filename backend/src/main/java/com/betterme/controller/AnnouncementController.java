package com.betterme.controller;

import com.betterme.model.Announcement;
import com.betterme.model.User;
import com.betterme.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Mixed Access Controller ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ This controller has endpoints with DIFFERENT access levels: ║
 * ║ ║
 * ║ /api/announcements/active - PUBLIC (anyone can see active announcements) ║
 * ║ /api/admin/announcements - ADMIN ONLY (create/delete announcements) ║
 * ║ ║
 * ║ The security is configured in SecurityConfig, not here. ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@RestController
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;

    // ═══════════════════════════════════════════════════════════════════════
    // PUBLIC ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get all active announcements (PUBLIC - no auth required)
     */
    @GetMapping("/api/announcements/active")
    public ResponseEntity<List<Announcement>> getActiveAnnouncements() {
        return ResponseEntity.ok(
                announcementRepository.findByIsActiveTrueOrderByPriorityDescCreatedAtDesc());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ADMIN ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get ALL announcements (including inactive) - ADMIN ONLY
     */
    @GetMapping("/api/admin/announcements")
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        return ResponseEntity.ok(
                announcementRepository.findAllByOrderByCreatedAtDesc());
    }

    /**
     * Create a new announcement - ADMIN ONLY
     * 
     * @AuthenticationPrincipal gives us the logged-in user
     */
    @PostMapping("/api/admin/announcements")
    public ResponseEntity<Announcement> createAnnouncement(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User user) {

        Announcement announcement = Announcement.builder()
                .title((String) request.get("title"))
                .message((String) request.get("message"))
                .priority(request.get("priority") != null
                        ? ((Number) request.get("priority")).intValue()
                        : 0)
                .isActive(request.get("isActive") != null
                        ? (Boolean) request.get("isActive")
                        : true)
                .createdBy(user.getEmail())
                .build();

        return ResponseEntity.ok(announcementRepository.save(announcement));
    }

    /**
     * Toggle announcement active status - ADMIN ONLY
     */
    @PutMapping("/api/admin/announcements/{id}/toggle")
    public ResponseEntity<Announcement> toggleAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        announcement.setIsActive(!announcement.getIsActive());
        return ResponseEntity.ok(announcementRepository.save(announcement));
    }

    /**
     * Delete an announcement - ADMIN ONLY
     */
    @DeleteMapping("/api/admin/announcements/{id}")
    public ResponseEntity<Map<String, String>> deleteAnnouncement(@PathVariable Long id) {
        if (!announcementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        announcementRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Announcement deleted successfully"));
    }
}
