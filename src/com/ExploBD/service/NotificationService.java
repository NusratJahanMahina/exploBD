package com.ExploBD.service;

import com.ExploBD.data.databaseObject.NotificationDatabaseObject;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.object.User;
import java.util.List;

public class NotificationService {

    private NotificationDatabaseObject notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDatabaseObject();
    }

    public void createNotification(User user, NotificationType type,
            String title, String message, String relatedId) {
        
        System.out.println("=== CREATING NOTIFICATION ===");
        System.out.println("User: " + (user != null ? user.getDisplayName() : "NULL"));
        System.out.println("Type: " + type);
        System.out.println("Title: " + title);
        System.out.println("Message: " + message);
        
        if (user == null) {
            System.out.println("ERROR: User is null! Cannot create notification.");
            return;
        }
        
        Notification notification = new Notification(user, type, title, message, relatedId);
        notificationDAO.save(notification);
        System.out.println("Notification created with ID: " + notification.getNotificationId());
    }

    public List<Notification> getUserNotifications(String userId) {
        // Don't delete old notifications here - do it separately
        return notificationDAO.findByUser(userId);
    }

    public int getUnreadCount(String userId) {
        return notificationDAO.findUnreadByUser(userId).size();
    }

    public void markAsRead(String notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationDAO.findUnreadByUser(userId);
        for (Notification notification : notifications) {
            notificationDAO.markAsRead(notification.getNotificationId());
        }
        System.out.println("Marked all " + notifications.size() + " notifications as read");
    }
    
    public void cleanupOldNotifications() {
        notificationDAO.deleteOldNotifications(3);
    }
    
}