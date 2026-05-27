package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.User;
import java.sql.*;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.data.databaseObject.NotificationDatabaseObject;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class ChecklistDatabaseObject {
    
public boolean addItem(String tourId, String itemText, String createdBy) {
    String sql = "INSERT INTO tour_checklist (tour_id, item_text, created_by) VALUES (?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setString(1, tourId);
        pstmt.setString(2, itemText);
        pstmt.setString(3, createdBy);
        int affected = pstmt.executeUpdate();
        
        if (affected > 0) {
            // Send notification to all tour participants except creator
            sendChecklistNotification(tourId, itemText, createdBy);
            return true;
        }
        return false;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public boolean updateItem(int itemId, String newText) {
        String sql = "UPDATE tour_checklist SET item_text = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newText);
            pstmt.setInt(2, itemId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM tour_checklist WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
private void sendChecklistNotification(String tourId, String itemText, String createdBy) {
    // Get all participants of this tour (user_id list)
    String participantSql = "SELECT user_id FROM tour_participants WHERE tour_id = ? AND user_id != ?";
    List<String> participantIds = new ArrayList<>();
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(participantSql)) {
        pstmt.setString(1, tourId);
        pstmt.setString(2, createdBy);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            participantIds.add(rs.getString("user_id"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return;
    }
    
    if (participantIds.isEmpty()) return;
    
    // Get creator's username for the message
    String creatorName = createdBy;
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("SELECT username FROM users WHERE user_id = ?")) {
        pstmt.setString(1, createdBy);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            creatorName = rs.getString("username");
        }
    } catch (SQLException e) {
        // fallback: use createdBy as name
    }
    
    NotificationDatabaseObject notifDAO = new NotificationDatabaseObject();
    for (String userId : participantIds) {
        // Create a minimal User object (only userId needed for notification)
        User participant = new User(userId, "", "", 0);
        Notification notif = new Notification(
            participant,
            NotificationType.CHECKLIST_ADDED,
            "📝 New To-Do Item",
            creatorName + " added: " + (itemText.length() > 50 ? itemText.substring(0, 47) + "..." : itemText),
            tourId
        );
        notifDAO.save(notif);
    }
}

    public List<Map<String, Object>> getChecklistWithProgress(String tourId, String userId) {
        List<Map<String, Object>> items = new ArrayList<>();
        
        String sql = """
            SELECT c.id, c.item_text, c.created_by, c.created_at,
                   p.completed, p.completed_at,
                   (SELECT COUNT(*) FROM tour_checklist_progress WHERE checklist_id = c.id AND completed = 1) as completion_count
            FROM tour_checklist c
            LEFT JOIN tour_checklist_progress p ON c.id = p.checklist_id AND p.user_id = ?
            WHERE c.tour_id = ?
            ORDER BY c.created_at ASC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            pstmt.setString(2, tourId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("item_text", rs.getString("item_text"));
                item.put("created_by", rs.getString("created_by"));
                item.put("created_at", rs.getString("created_at"));
                item.put("completed", rs.getBoolean("completed"));
                item.put("completed_at", rs.getString("completed_at"));
                item.put("completion_count", rs.getInt("completion_count"));
                items.add(item);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    public boolean toggleCompletion(int checklistId, String userId) {
        String checkSql = "SELECT completed FROM tour_checklist_progress WHERE checklist_id = ? AND user_id = ?";
        String insertSql = "INSERT INTO tour_checklist_progress (checklist_id, user_id, completed, completed_at) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE tour_checklist_progress SET completed = ?, completed_at = ? WHERE checklist_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Check if progress exists
            boolean exists = false;
            boolean currentStatus = false;
            
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, checklistId);
                pstmt.setString(2, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    exists = true;
                    currentStatus = rs.getBoolean("completed");
                }
            }
            
            if (exists) {
                // Update existing
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setBoolean(1, !currentStatus);
                    pstmt.setString(2, !currentStatus ? new Timestamp(System.currentTimeMillis()).toString() : null);
                    pstmt.setInt(3, checklistId);
                    pstmt.setString(4, userId);
                    pstmt.executeUpdate();
                }
            } else {
                // Insert new
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, checklistId);
                    pstmt.setString(2, userId);
                    pstmt.setBoolean(3, true);
                    pstmt.setString(4, new Timestamp(System.currentTimeMillis()).toString());
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getItemCount(String tourId) {
        String sql = "SELECT COUNT(*) FROM tour_checklist WHERE tour_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tourId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}