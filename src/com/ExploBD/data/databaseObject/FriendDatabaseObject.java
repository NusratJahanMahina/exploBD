package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.User;
import com.ExploBD.domain.entities.FriendRequest;
import com.ExploBD.domain.entities.Friendship;
import com.ExploBD.domain.enums.FriendRequestStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendDatabaseObject {
    
    private UserDatabaseObject userDAO;
    
    public FriendDatabaseObject() {
        this.userDAO = new UserDatabaseObject();
    }
    
    //========== FRIEND REQUESTS ==========
    
    //Sristy did this
    public List<FriendRequest> getPendingRequestsForUser(String userId) {
    List<FriendRequest> requests = new ArrayList<>();
    String sql = """
        SELECT fr.*, 
               u1.user_id as from_id, u1.username as from_username, u1.email as from_email, u1.full_name as from_name,
               u2.user_id as to_id, u2.username as to_username, u2.email as to_email, u2.full_name as to_name
        FROM friend_requests fr
        JOIN users u1 ON fr.from_user_id = u1.user_id
        JOIN users u2 ON fr.to_user_id = u2.user_id
        WHERE fr.to_user_id = ? AND fr.status = 'PENDING'
        ORDER BY fr.sent_at DESC
        """;
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, userId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            User fromUser = new User(
                rs.getString("from_id"),
                rs.getString("from_username"),
                rs.getString("from_email"),
                0
            );
            fromUser.setFullName(rs.getString("from_name"));
            
            User toUser = new User(
                rs.getString("to_id"),
                rs.getString("to_username"),
                rs.getString("to_email"),
                0
            );
            toUser.setFullName(rs.getString("to_name"));
            
            FriendRequest request = new FriendRequest(
                rs.getString("request_id"),
                fromUser,
                toUser,
                FriendRequestStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("sent_at")
            );
            requests.add(request);
        }
        System.out.println("Found " + requests.size() + " pending requests for user " + userId);
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return requests;
}
    public void debugPrintAllFriendships() {
    String sql = "SELECT * FROM friendships";
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        System.out.println("\n=== ALL FRIENDSHIPS IN DATABASE ===");
        int count = 0;
        while (rs.next()) {
            count++;
            System.out.println("Friendship ID: " + rs.getString("friendship_id"));
            System.out.println("  User1: " + rs.getString("user1_id"));
            System.out.println("  User2: " + rs.getString("user2_id"));
            System.out.println("  Since: " + rs.getTimestamp("since"));
            System.out.println("---");
        }
        System.out.println("Total friendships found: " + count);
        System.out.println("================================\n");
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
   /* public List<Friendship> getUserFriendships(String userId) {
    System.out.println("\n=== GET USER FRIENDSHIPS ===");
    System.out.println("Looking for friendships with user: " + userId);
    
    List<Friendship> friendships = new ArrayList<>();
    String sql = """
        SELECT f.*, 
               u1.user_id as u1_id, u1.username as u1_username, u1.email as u1_email, u1.full_name as u1_name,
               u2.user_id as u2_id, u2.username as u2_username, u2.email as u2_email, u2.full_name as u2_name
        FROM friendships f
        JOIN users u1 ON f.user1_id = u1.user_id
        JOIN users u2 ON f.user2_id = u2.user_id
        WHERE f.user1_id = ? OR f.user2_id = ?
        ORDER BY f.since DESC
        """;
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, userId);
        pstmt.setString(2, userId);
        ResultSet rs = pstmt.executeQuery();
        
        int count = 0;
        while (rs.next()) {
            count++;
            System.out.println("Found friendship " + count + ":");
            System.out.println("  Friendship ID: " + rs.getString("friendship_id"));
            System.out.println("  User1 ID: " + rs.getString("u1_id"));
            System.out.println("  User2 ID: " + rs.getString("u2_id"));
            
            User user1 = new User(
                rs.getString("u1_id"),
                rs.getString("u1_username"),
                rs.getString("u1_email"),
                0
            );
            user1.setFullName(rs.getString("u1_name"));
            
            User user2 = new User(
                rs.getString("u2_id"),
                rs.getString("u2_username"),
                rs.getString("u2_email"),
                0
            );
            user2.setFullName(rs.getString("u2_name"));
            
            Friendship friendship = new Friendship(user1, user2);
            friendships.add(friendship);
        }
        System.out.println("Total friendships found in database: " + count);
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return friendships;
}*/
  
   /* public void debugPrintAllRequests() {
    String sql = "SELECT * FROM friend_requests";
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        System.out.println("=== ALL FRIEND REQUESTS IN DATABASE ===");
        while (rs.next()) {
            System.out.println("Request ID: " + rs.getString("request_id"));
            System.out.println("  From: " + rs.getString("from_user_id"));
            System.out.println("  To: " + rs.getString("to_user_id"));
            System.out.println("  Status: " + rs.getString("status"));
            System.out.println("  Sent: " + rs.getTimestamp("sent_at"));
            System.out.println("---");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
}*/
    
  public void debugPrintAllRequests() {
    String sql = "SELECT * FROM friend_requests";
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        System.out.println("\n=== ALL FRIEND REQUESTS IN DATABASE ===");
        int count = 0;
        while (rs.next()) {
            count++;
            System.out.println("Request ID: " + rs.getString("request_id"));
            System.out.println("  From: " + rs.getString("from_user_id"));
            System.out.println("  To: " + rs.getString("to_user_id"));
            System.out.println("  Status: " + rs.getString("status"));
            System.out.println("  Sent: " + rs.getTimestamp("sent_at"));
            System.out.println("---");
        }
        System.out.println("Total requests found: " + count);
        System.out.println("===================================\n");
        
    } catch (SQLException e) {
        System.out.println("Error reading friend_requests table: " + e.getMessage());
        e.printStackTrace();
    }
}  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public boolean saveFriendRequest(FriendRequest request) {
        String sql = """
            INSERT INTO friend_requests (request_id, from_user_id, to_user_id, status, sent_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, request.getRequestId());
            pstmt.setString(2, request.getFrom().getUserId());
            pstmt.setString(3, request.getTo().getUserId());
            pstmt.setString(4, request.getStatus().name());
            pstmt.setTimestamp(5, new Timestamp(request.getSentAt().getTime()));
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean updateFriendRequestStatus(String requestId, FriendRequestStatus status) {
        String sql = "UPDATE friend_requests SET status = ? WHERE request_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, status.name());
            pstmt.setString(2, requestId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
   /* public List<FriendRequest> getPendingRequestsForUser(String userId) {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = """
            SELECT fr.*, 
                   u1.user_id as from_id, u1.username as from_username, u1.email as from_email,
                   u2.user_id as to_id, u2.username as to_username, u2.email as to_email
            FROM friend_requests fr
            JOIN users u1 ON fr.from_user_id = u1.user_id
            JOIN users u2 ON fr.to_user_id = u2.user_id
            WHERE fr.to_user_id = ? AND fr.status = 'PENDING'
            ORDER BY fr.sent_at DESC
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(buildFriendRequestFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return requests;
    }*/
    
    public List<FriendRequest> getSentRequestsFromUser(String userId) {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = """
            SELECT fr.*, 
                   u1.user_id as from_id, u1.username as from_username, u1.email as from_email,
                   u2.user_id as to_id, u2.username as to_username, u2.email as to_email
            FROM friend_requests fr
            JOIN users u1 ON fr.from_user_id = u1.user_id
            JOIN users u2 ON fr.to_user_id = u2.user_id
            WHERE fr.from_user_id = ? AND fr.status = 'PENDING'
            ORDER BY fr.sent_at DESC
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(buildFriendRequestFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return requests;
    }
    
    public FriendRequest findFriendRequest(String fromUserId, String toUserId) {
        String sql = """
            SELECT fr.*, 
                   u1.user_id as from_id, u1.username as from_username, u1.email as from_email,
                   u2.user_id as to_id, u2.username as to_username, u2.email as to_email
            FROM friend_requests fr
            JOIN users u1 ON fr.from_user_id = u1.user_id
            JOIN users u2 ON fr.to_user_id = u2.user_id
            WHERE (fr.from_user_id = ? AND fr.to_user_id = ?)
               OR (fr.from_user_id = ? AND fr.to_user_id = ?)
            ORDER BY fr.sent_at DESC LIMIT 1
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, fromUserId);
            pstmt.setString(2, toUserId);
            pstmt.setString(3, toUserId);
            pstmt.setString(4, fromUserId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildFriendRequestFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    public boolean deleteFriendRequest(String requestId) {
        String sql = "DELETE FROM friend_requests WHERE request_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, requestId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    //========== FRIENDSHIPS ==========
    
    public boolean createFriendship(String user1Id, String user2Id) {
        String id1, id2;
        if (user1Id.compareTo(user2Id) < 0) {
            id1 = user1Id;
            id2 = user2Id;
        } else {
            id1 = user2Id;
            id2 = user1Id;
        }
        
        String sql = """
            INSERT INTO friendships (friendship_id, user1_id, user2_id, since)
            VALUES (?, ?, ?, ?)
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            String friendshipId = "FS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            pstmt.setString(1, friendshipId);
            pstmt.setString(2, id1);
            pstmt.setString(3, id2);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Friendship> getUserFriendships(String userId) {
        List<Friendship> friendships = new ArrayList<>();
        String sql = """
            SELECT f.*, 
                   u1.user_id as u1_id, u1.username as u1_username, u1.email as u1_email, u1.full_name as u1_name,
                   u2.user_id as u2_id, u2.username as u2_username, u2.email as u2_email, u2.full_name as u2_name
            FROM friendships f
            JOIN users u1 ON f.user1_id = u1.user_id
            JOIN users u2 ON f.user2_id = u2.user_id
            WHERE f.user1_id = ? OR f.user2_id = ?
            ORDER BY f.since DESC
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                friendships.add(buildFriendshipFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return friendships;
    }
    
    public boolean removeFriendship(String user1Id, String user2Id) {
        String sql = """
            DELETE FROM friendships 
            WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user1Id);
            pstmt.setString(2, user2Id);
            pstmt.setString(3, user2Id);
            pstmt.setString(4, user1Id);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean areFriends(String user1Id, String user2Id) {
        String sql = """
            SELECT COUNT(*) FROM friendships 
            WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user1Id);
            pstmt.setString(2, user2Id);
            pstmt.setString(3, user2Id);
            pstmt.setString(4, user1Id);
            
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    //========== METHODS FROM FRIEND'S CODE ==========
    
    public List<User> getSuggestionsForUser(String userId) {
        List<User> suggestions = new ArrayList<>();
        String sql = """
            SELECT * FROM users 
            WHERE user_id != ? 
            AND user_id NOT IN (SELECT user1_id FROM friendships WHERE user2_id = ?)
            AND user_id NOT IN (SELECT user2_id FROM friendships WHERE user1_id = ?)
            AND user_id NOT IN (SELECT to_user_id FROM friend_requests WHERE from_user_id = ? AND status = 'PENDING')
            AND user_id NOT IN (SELECT from_user_id FROM friend_requests WHERE to_user_id = ? AND status = 'PENDING')
            LIMIT 10
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            pstmt.setString(3, userId);
            pstmt.setString(4, userId);
            pstmt.setString(5, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                suggestions.add(userDAO.findById(rs.getString("user_id")));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return suggestions;
    }
    
    public int getFriendCount(String userId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user1_id = ? OR user2_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return 0;
    }

  
    public List<String> getFriendNames(String userId) {
        List<String> friendNames = new ArrayList<>();
        String sql = """
            SELECT u.username, u.full_name 
            FROM users u
            WHERE u.user_id IN (
                SELECT user1_id FROM friendships WHERE user2_id = ?
                UNION
                SELECT user2_id FROM friendships WHERE user1_id = ?
            )
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("full_name") != null ? 
                             rs.getString("full_name") : rs.getString("username");
                friendNames.add(name);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return friendNames;
    }
    
    public List<String> getPendingRequestNames(String userId) {
        List<String> requestNames = new ArrayList<>();
        String sql = """
            SELECT u.username, u.full_name 
            FROM users u
            WHERE u.user_id IN (
                SELECT from_user_id FROM friend_requests 
                WHERE to_user_id = ? AND status = 'PENDING'
            )
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("full_name") != null ? 
                             rs.getString("full_name") : rs.getString("username");
                requestNames.add(name);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return requestNames;
    }
    
    public void updateRequestStatusByUsers(String fromUserId, String toUserId, String status) {
        String sql = "UPDATE friend_requests SET status = ? WHERE from_user_id = ? AND to_user_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, fromUserId);
            pstmt.setString(3, toUserId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    //========== HELPER METHODS ==========
    
    private FriendRequest buildFriendRequestFromResultSet(ResultSet rs) throws SQLException {
        User fromUser = userDAO.findById(rs.getString("from_user_id"));
        User toUser = userDAO.findById(rs.getString("to_user_id"));
        
        FriendRequest request = new FriendRequest(
            rs.getString("request_id"),
            fromUser,
            toUser,
            FriendRequestStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("sent_at")
        );
        
        return request;
    }
    
    private Friendship buildFriendshipFromResultSet(ResultSet rs) throws SQLException {
        User user1 = new User(
            rs.getString("u1_id"),
            rs.getString("u1_username"),
            rs.getString("u1_email"),
            -1
        );
        user1.setFullName(rs.getString("u1_name"));
        
        User user2 = new User(
            rs.getString("u2_id"),
            rs.getString("u2_username"),
            rs.getString("u2_email"),
            -1
        );
        user2.setFullName(rs.getString("u2_name"));
        
        Friendship friendship = new Friendship(user1, user2);
        return friendship;
    }
    //Srishty
        public java.util.List<String> getFriends(String userId) {
    // Return a list of accepted friend names from SQL
    return new java.util.ArrayList<>();
}

public java.util.List<String> getPendingRequests(String userId) {
    // Return a list of pending request names from SQL
    return new java.util.ArrayList<>();
}

public java.util.List<String> getSuggestions(String userId) {
    // Return suggested friends from SQL
    return new java.util.ArrayList<>();
}
public void updateRequestStatus(String currentUserId, String targetName, String status) {
    // Execute SQL: UPDATE friend_requests SET status = ? WHERE ...
}
    
}