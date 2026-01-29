package com.betterme.repository;

import com.betterme.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * Find all active announcements, ordered by priority (highest first)
     */
    List<Announcement> findByIsActiveTrueOrderByPriorityDescCreatedAtDesc();

    /**
     * Find all announcements ordered by creation date
     */
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
