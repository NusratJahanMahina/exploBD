package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDatabaseObject {

    public static final String[] SECURITY_QUESTIONS = DatabaseConnection.SECURITY_QUESTIONS;

    public UserDatabaseObject() {
    }

    public String generateUserId() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as count FROM users";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            int count = 1;
            if (rs.next()) {
                count = rs.getInt("count") + 1;
            }

            String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            return String.format("EXP-%06d-%s", count, randomSuffix);
        } catch (Exception e) {
            return "EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean registerUser(String username, String email, String password,
            int securityQuestionIndex, String securityAnswer) {
        
        if (securityQuestionIndex < 0 || securityQuestionIndex >= SECURITY_QUESTIONS.length) {
            return false;
        }
        
        String cleanAnswer = securityAnswer.trim().toLowerCase();
        String questionText = SECURITY_QUESTIONS[securityQuestionIndex];
        String userId = generateUserId();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            if (emailExists(conn, email)) {
                return false;
            }

            String sql = """
                INSERT INTO users (user_id, username, email, password, security_question, security_answer, 
                                  full_name, nationality, min_budget, max_budget, profile_image_path) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, questionText);
            pstmt.setString(6, cleanAnswer);
            pstmt.setString(7, username);
            pstmt.setString(8, "Bangladeshi");
            pstmt.setInt(9, 500);
            pstmt.setInt(10, 10000);
            pstmt.setString(11, "/images/nophoto.jpg");

            int rows = pstmt.executeUpdate();
            conn.commit();
            return rows > 0;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public boolean saveUserProfile(User user) {
        String sql = """
            UPDATE users SET 
                username = ?,
                email = ?,
                full_name = ?,
                nationality = ?,
                phone = ?,
                emergency_contact = ?,
                nid_passport = ?,
                date_of_birth = ?,
                gender = ?,
                min_budget = ?,
                max_budget = ?,
                profile_image_path = ?,
                travel_styles = ?,
                is_leader = ?,
                last_updated = CURRENT_TIMESTAMP
            WHERE user_id = ?
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getNationality());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getEmergencyContact());
            pstmt.setString(7, user.getNidPassport());
            pstmt.setString(8, user.getDateOfBirth());
            pstmt.setString(9, user.getGender());
            pstmt.setInt(10, user.getMinBudget());
            pstmt.setInt(11, user.getMaxBudget());
            pstmt.setString(12, user.getProfileImagePath());
            pstmt.setString(13, user.getTravelStylesAsString());
            pstmt.setBoolean(14, user.isLeader());
            pstmt.setString(15, user.getUserId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public User findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String questionText = rs.getString("security_question");
        int questionIndex = -1;
        for (int i = 0; i < SECURITY_QUESTIONS.length; i++) {
            if (SECURITY_QUESTIONS[i].equals(questionText)) {
                questionIndex = i;
                break;
            }
        }

        User user = new User(
            rs.getString("user_id"),
            rs.getString("username"),
            rs.getString("email"),
            questionIndex
        );

        loadUserProfileData(user, rs);
        return user;
    }

    private void loadUserProfileData(User user, ResultSet rs) throws SQLException {
        user.setFullName(rs.getString("full_name"));
        user.setNationality(rs.getString("nationality"));
        user.setPhone(rs.getString("phone"));
        user.setEmergencyContact(rs.getString("emergency_contact"));
        user.setNidPassport(rs.getString("nid_passport"));
        user.setDateOfBirth(rs.getString("date_of_birth"));
        user.setGender(rs.getString("gender"));
        user.setMinBudget(rs.getInt("min_budget"));
        user.setMaxBudget(rs.getInt("max_budget"));
        user.setProfileImagePath(rs.getString("profile_image_path"));
        user.setLeader(rs.getBoolean("is_leader"));

        String travelStylesStr = rs.getString("travel_styles");
        if (travelStylesStr != null && !travelStylesStr.isEmpty()) {
            user.setTravelStylesFromString(travelStylesStr);
        }

        String favoritesStr = rs.getString("favorite_places");
        if (favoritesStr != null && !favoritesStr.isEmpty()) {
            user.setFavoritePlacesFromString(favoritesStr);
        }

        String wishlistStr = rs.getString("wishlist_places");
        if (wishlistStr != null && !wishlistStr.isEmpty()) {
            user.setWishlistPlacesFromString(wishlistStr);
        }

        boolean isComplete = (user.getFullName() != null && !user.getFullName().isEmpty()) &&
                            (user.getPhone() != null && !user.getPhone().isEmpty()) &&
                            (user.getGender() != null && !user.getGender().isEmpty() &&
                             !user.getGender().equals("Select Gender"));
        user.setProfileComplete(isComplete);
    }

    public boolean updateLeaderStatus(String userId, boolean isLeader) {
        String sql = "UPDATE users SET is_leader = ?, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, isLeader);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public String[][] getUserStatistics(String userId) {
        String[][] stats = new String[6][2];
        stats[0][0] = "Total Travels"; stats[0][1] = "0";
        stats[1][0] = "Total Spent"; stats[1][1] = "0 tk";
        stats[2][0] = "Active Tours"; stats[2][1] = "0";
        stats[3][0] = "Places Visited"; stats[3][1] = "0";
        stats[4][0] = "Last Travel"; stats[4][1] = "Never";
        stats[5][0] = "Wishlist"; stats[5][1] = "0 places";

        Connection conn = null;
        PreparedStatement favStmt = null;
        PreparedStatement wishStmt = null;
        ResultSet favRs = null;
        ResultSet wishRs = null;

        try {
            conn = DatabaseConnection.getConnection();
            
            String favoritesSql = "SELECT COUNT(*) FROM user_preferences WHERE user_id = ? AND preference_type = 'FAVORITE'";
            favStmt = conn.prepareStatement(favoritesSql);
            favStmt.setString(1, userId);
            favRs = favStmt.executeQuery();
            if (favRs.next()) {
                stats[5][1] = favRs.getInt(1) + " places";
            }

            String wishlistSql = "SELECT COUNT(*) FROM user_preferences WHERE user_id = ? AND preference_type = 'WISHLIST'";
            wishStmt = conn.prepareStatement(wishlistSql);
            wishStmt.setString(1, userId);
            wishRs = wishStmt.executeQuery();
            if (wishRs.next()) {
                stats[5][1] = wishRs.getInt(1) + " places";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (favRs != null) favRs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (wishRs != null) wishRs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (favStmt != null) favStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (wishStmt != null) wishStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return stats;
    }

    public boolean usernameExists(String username, String excludeUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id != ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, excludeUserId);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean emailExists(String email, String excludeUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, excludeUserId);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean emailExists(Connection conn, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<User> getAllUsersForGroup(String excludeUserId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name FROM users WHERE user_id != ? ORDER BY username";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, excludeUserId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    "",
                    0
                );
                user.setFullName(rs.getString("full_name"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return users;
    }

    public List<User> searchUsers(String searchTerm, String excludeUserId) {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT user_id, username, email, full_name, profile_image_path 
            FROM users 
            WHERE user_id != ? AND 
                  (username LIKE ? OR full_name LIKE ? OR email LIKE ?)
            ORDER BY username
            """;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, excludeUserId);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    0
                );
                user.setFullName(rs.getString("full_name"));
                user.setProfileImagePath(rs.getString("profile_image_path"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return users;
    }

    public boolean resetPassword(String email, int securityQuestionIndex,
            String securityAnswer, String newPassword) {
        
        if (securityQuestionIndex < 0 || securityQuestionIndex >= SECURITY_QUESTIONS.length) {
            return false;
        }

        String cleanAnswer = securityAnswer.trim().toLowerCase();
        String questionText = SECURITY_QUESTIONS[securityQuestionIndex];
        
        String sql = "UPDATE users SET password = ? WHERE email = ? AND security_question = ? AND security_answer = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            pstmt.setString(3, questionText);
            pstmt.setString(4, cleanAnswer);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public String getSecurityQuestion(String email) {
        String sql = "SELECT security_question FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("security_question");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }
        // Add to UserDatabaseObject.java
public User findByEmailOrName(String searchTerm) {
    String sql = "SELECT * FROM users WHERE email = ? OR username = ? OR full_name = ?";
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        conn = DatabaseConnection.getConnection();
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, searchTerm);
        pstmt.setString(2, searchTerm);
        pstmt.setString(3, searchTerm);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return createUserFromResultSet(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return null;
}
    
}