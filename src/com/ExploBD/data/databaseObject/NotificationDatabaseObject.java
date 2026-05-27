package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.User;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.NotificationType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDatabaseObject {
    
    private UserDatabaseObject userDAO;

    public NotificationDatabaseObject() {
        this.userDAO = new UserDatabaseObject();
    }

    public void save(Notification notification) {
        String sql = """
            INSERT INTO notifications 
            (notification_id, user_id, type, title, message, related_id, is_read, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, notification.getNotificationId());
            pstmt.setString(2, notification.getUser().getUserId());
            pstmt.setString(3, notification.getType().name());
            pstmt.setString(4, notification.getTitle());
            pstmt.setString(5, notification.getMessage());
            pstmt.setString(6, notification.getRelatedId());
            pstmt.setBoolean(7, notification.isRead());
            pstmt.setTimestamp(8, new Timestamp(notification.getCreatedAt().getTime()));
            
            int rows = pstmt.executeUpdate();
            System.out.println("Notification saved: " + notification.getNotificationId() + " - Rows affected: " + rows);
            
        } catch (SQLException e) {
            System.out.println("Error saving notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Notification> findByUser(String userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(buildNotificationFromResultSet(rs));
            }
            System.out.println("Found " + notifications.size() + " notifications for user: " + userId);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> findUnreadByUser(String userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(buildNotificationFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public void markAsRead(String notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, notificationId);
            int rows = pstmt.executeUpdate();
            System.out.println("Marked notification as read: " + notificationId + " - Rows: " + rows);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Add this method for cleanup
    public void deleteOldNotifications(int daysOld) {
        String sql = "DELETE FROM notifications WHERE created_at < datetime('now', '-' || ? || ' days')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, daysOld);
            int rows = pstmt.executeUpdate();
            System.out.println("Deleted " + rows + " old notifications (older than " + daysOld + " days)");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Notification buildNotificationFromResultSet(ResultSet rs) throws SQLException {
        User user = userDAO.findById(rs.getString("user_id"));
        NotificationType type = NotificationType.valueOf(rs.getString("type"));
        
        Notification notification = new Notification(
            user,
            type,
            rs.getString("title"),
            rs.getString("message"),
            rs.getString("related_id")
        );
        
        notification.setNotificationId(rs.getString("notification_id"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        
        return notification;
    }
}