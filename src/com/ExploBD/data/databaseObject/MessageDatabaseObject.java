package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.User;
import com.ExploBD.domain.entities.*;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.enums.MessageStatus;
import com.ExploBD.domain.enums.MessageType;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseObject {

    private UserDatabaseObject userDAO;

    public MessageDatabaseObject() {
        this.userDAO = new UserDatabaseObject();
    }

    public void save(Message message) {
        String sql = """
            INSERT INTO messages (
                message_id, group_id, sender_id, message_type, status, 
                title, content, like_count, created_at,
                place_id, place_name, upvotes, downvotes, reason,
                invitee_name, invitee_email, request_type, is_important
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            // Common columns — same for every message type
            pstmt.setString(1, message.getMessageId());
            pstmt.setString(2, message.getGroupId());
            pstmt.setString(3, message.getSender().getUserId());
            pstmt.setString(4, message.getType().name());
            pstmt.setString(5, message.getStatus().name());
            pstmt.setString(6, message.getTitle());
            pstmt.setString(7, message.getContent());
            pstmt.setInt(8, message.getLikeCount());
            pstmt.setString(9, message.getCreatedAt().toString());

            // Type-specific columns — each subclass knows its own data
            message.fillStatement(pstmt);

            pstmt.executeUpdate();
            // After saving the message, send notification for leader bulletins
            if (message.getType() == MessageType.BULLETIN) {
                try {
                    GroupDatabaseObject groupDAO = new GroupDatabaseObject();
                    Group group = groupDAO.loadGroup(message.getGroupId());
                    if (group != null && group.isLeader(message.getSender())) {
                        // Leader posted a bulletin – notify all members
                        for (GroupMember member : group.getAllMembers()) {
                            if (member instanceof RegisteredMember) {
                                User memberUser = ((RegisteredMember) member).getUser();
                                if (!memberUser.getUserId().equals(message.getSender().getUserId())) {
                                    Notification notif = new Notification(
                                            memberUser,
                                            NotificationType.NEW_MESSAGE, // or create BULLETIN_ANNOUNCEMENT
                                            "Important Bulletin",
                                            "Leader " + message.getSender().getDisplayName() + " posted: " + message.getTitle(),
                                            message.getMessageId()
                                    );
                                    new NotificationDatabaseObject().save(notif);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Failed to send bulletin notification: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Message> getGroupMessages(String groupId) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT m.*, u.user_id, u.username, u.email, u.full_name
            FROM messages m
            JOIN users u ON m.sender_id = u.user_id
            WHERE m.group_id = ?
            ORDER BY m.created_at DESC
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(mapToMessage(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }

    /**
     * Fetch messages for a group filtered by type (e.g. CHAT, BULLETIN).
     * Returns oldest first so the UI can display them top-to-bottom naturally.
     */
    public List<Message> getByType(String groupId, MessageType type) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT m.*, u.user_id, u.username, u.email, u.full_name
            FROM messages m
            JOIN users u ON m.sender_id = u.user_id
            WHERE m.group_id = ? AND m.message_type = ?
            ORDER BY m.created_at ASC
            """;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            pstmt.setString(2, type.name());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Message m = mapToMessage(rs);
                if (m != null) {
                    messages.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    public List<Message> getPendingRequests(String groupId) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT m.*, u.user_id, u.username, u.email, u.full_name
            FROM messages m
            JOIN users u ON m.sender_id = u.user_id
            WHERE m.group_id = ? AND m.status = 'PENDING'
            ORDER BY m.created_at ASC
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(mapToMessage(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }

    public void updateStatus(String messageId, MessageStatus status, String reviewerId) {
        String sql = "UPDATE messages SET status = ?, reviewed_by = ?, reviewed_at = ? WHERE message_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, status.name());
            pstmt.setString(2, reviewerId);
            pstmt.setString(3, LocalDateTime.now().toString());
            pstmt.setString(4, messageId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Message mapToMessage(ResultSet rs) throws SQLException {
        User sender = userDAO.findById(rs.getString("sender_id"));
        String groupId = rs.getString("group_id");
        String messageType = rs.getString("message_type");
        String messageId = rs.getString("message_id");

        Message message = null;

        if ("SUGGESTION".equals(messageType)) {
            message = new SuggestionMessage(
                    messageId,
                    sender,
                    groupId,
                    rs.getString("place_id"),
                    rs.getString("place_name"),
                    rs.getString("reason")
            );
        } else if ("CHAT".equals(messageType)) {
            message = new ChatMessage(
                    messageId,
                    sender,
                    groupId,
                    rs.getString("content")
            );
        } else if ("BULLETIN".equals(messageType)) {
            message = new BulletinMessage(
                    messageId,
                    sender,
                    groupId,
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getBoolean("is_important")
            );
        } else if ("INVITE_REQUEST".equals(messageType)) {
            message = new InviteRequestMessage(
                    messageId,
                    sender,
                    groupId,
                    rs.getString("invitee_name"),
                    rs.getString("invitee_email"),
                    rs.getString("request_type")
            );
        }

        if (message != null) {
            message.setStatus(MessageStatus.valueOf(rs.getString("status")));
            message.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
            message.setLikeCount(rs.getInt("like_count"));
        }

        return message;
    }
    // Add this method to MessageDatabaseObject class

    public List<Message> getGroupMessagesFiltered(String groupId) {
        List<Message> messages = new ArrayList<>();

        // Calculate cutoff dates
        LocalDateTime chatCutoff = LocalDateTime.now().minusDays(1);
        LocalDateTime bulletinCutoff = LocalDateTime.now().minusDays(5);

        String sql = """
        SELECT m.*, u.user_id, u.username, u.email, u.full_name
        FROM messages m
        JOIN users u ON m.sender_id = u.user_id
        WHERE m.group_id = ?
        ORDER BY m.created_at ASC
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String messageType = rs.getString("message_type");
                LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));

                // Filter based on message type
                if ("CHAT".equals(messageType)) {
                    if (createdAt.isAfter(chatCutoff)) {
                        messages.add(mapToMessage(rs));
                    }
                } else if ("BULLETIN".equals(messageType)) {
                    if (createdAt.isAfter(bulletinCutoff)) {
                        messages.add(mapToMessage(rs));
                    }
                } else {
                    // Other message types (SUGGESTION, INVITE_REQUEST) - show all
                    messages.add(mapToMessage(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
