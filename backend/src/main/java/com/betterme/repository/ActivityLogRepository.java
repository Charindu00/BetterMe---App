package com.betterme.repository;

import com.betterme.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Page vs List ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ For logs, we use Pageable because: ║
 * ║ - Logs can grow to millions of records ║
 * ║ - Loading all at once = memory problems ║
 * ║ - Pagination loads only what's needed (e.g., 20 at a time) ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * Find logs by user ID (paginated)
     */
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find logs by action type
     */
    List<ActivityLog> findByActionOrderByCreatedAtDesc(String action);

    /**
     * Find recent logs (for dashboard)
     */
    List<ActivityLog> findTop50ByOrderByCreatedAtDesc();

    /**
     * Find logs within date range
     */
    List<ActivityLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start, LocalDateTime end);

    /**
     * Count logs by action (for stats)
     */
    long countByAction(String action);
}
