package com.ExploBD.domain.entities;

import com.ExploBD.object.User;
import com.ExploBD.domain.enums.NotificationType;
import java.util.Date;
import java.util.UUID;

public class Notification {
    private String notificationId;
    private User user;
    private NotificationType type;
    private String title;
    private String message;
    private String relatedId;
    private boolean isRead;
    private Date createdAt;

    public Notification(User user, NotificationType type, String title, String message, String relatedId) {
        this.notificationId = generateId();
        this.user = user;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedId = relatedId;
        this.isRead = false;
        this.createdAt = new Date();
    }

    private String generateId() {
        return "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void markAsRead() {
        this.isRead = true;
    }

    // Getters
    public String getNotificationId() { return notificationId; }
    public User getUser() { return user; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getRelatedId() { return relatedId; }
    public boolean isRead() { return isRead; }
    public Date getCreatedAt() { return createdAt; }
    
    // Setters (for database loading)
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public void setRead(boolean read) { isRead = read; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}