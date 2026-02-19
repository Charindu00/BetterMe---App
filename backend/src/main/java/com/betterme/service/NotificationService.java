package com.betterme.service;

import com.betterme.model.Notification;
import com.betterme.model.Notification.NotificationType;
import com.betterme.model.User;
import com.betterme.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Get all notifications for a user
     */
    public List<Notification> getNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    /**
     * Get unread count
     */
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    /**
     * Mark all notifications as read
     */
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

    /**
     * Mark single notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    /**
     * Create a new notification
     */
    @Transactional
    public Notification createNotification(User user, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .build();
        return notificationRepository.save(notification);
    }

    // Helper methods to create specific notification types

    public void notifyStreak(User user, int days) {
        String message = String.format("🔥 Congratulations! You've hit a %d-day streak!", days);
        createNotification(user, message, NotificationType.STREAK);
    }

    public void notifyGoalProgress(User user, String goalTitle, int percentage) {
        String message = String.format("🎯 Goal \"%s\" is now %d%% complete!", goalTitle, percentage);
        createNotification(user, message, NotificationType.GOAL);
    }

    public void notifyGoalCompleted(User user, String goalTitle) {
        String message = String.format("🏆 Amazing! You completed your goal: \"%s\"!", goalTitle);
        createNotification(user, message, NotificationType.GOAL);
    }

    public void notifyAchievement(User user, String achievementName) {
        String message = String.format("✨ Achievement Unlocked: %s!", achievementName);
        createNotification(user, message, NotificationType.ACHIEVEMENT);
    }

    public void notifyReminder(User user, String habitName) {
        String message = String.format("⏰ Don't forget to complete \"%s\" today!", habitName);
        createNotification(user, message, NotificationType.REMINDER);
    }
}
