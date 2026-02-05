package com.betterme.repository;

import com.betterme.model.Notification;
import com.betterme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a user, newest first
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Get only unread notifications
    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);

    // Count unread notifications
    long countByUserAndReadFalse(User user);

    // Mark all as read for user
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = ?1 AND n.read = false")
    int markAllAsReadForUser(User user);
}
