package com.ExploBD.service;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.data.databaseObject.NotificationDatabaseObject;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.object.User;
import java.sql.*;
import java.util.List;

public class InvitationService {

    private InvitationDatabaseObject invitationDAO;
    private GroupDatabaseObject groupDAO;
    private UserDatabaseObject userDAO;

    public InvitationService() {
        this.invitationDAO = new InvitationDatabaseObject();
        this.groupDAO = new GroupDatabaseObject();
        this.userDAO = new UserDatabaseObject();
    }

    public void acceptInvitation(String invitationId, String userId, String groupId, String inviterId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND member_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, groupId);
                checkStmt.setString(2, userId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    String updateInvSql = "UPDATE invitations SET status = ? WHERE invitation_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateInvSql)) {
                        pstmt.setString(1, InvitationStatus.ACCEPTED.name());
                        pstmt.setString(2, invitationId);
                        pstmt.executeUpdate();
                    }
                    conn.commit();

//                // ===== NOTIFICATION FOR ALREADY MEMBER CASE =====
//                try {
//                    User inviter = userDAO.findById(inviterId);
//                    User newMember = userDAO.findById(userId);
//                    Group group = groupDAO.loadGroup(groupId);
//
//                    if (inviter != null && newMember != null && group != null) {
//                        Notification notif = new Notification(
//                            inviter,
//                            NotificationType.MEMBER_JOINED,
//                            "Member Joined",
//                            newMember.getDisplayName() + " joined your group " + group.getName(),
//                            groupId
//                        );
//                        new NotificationDatabaseObject().save(notif);
//                    }
//                } catch (Exception notifEx) {
//                    System.out.println("Failed to send notification: " + notifEx.getMessage());
//                }
                    conn.commit();

// ===== NOTIFICATION: MEMBER_JOINED =====
                    try {
                        User inviter = userDAO.findById(inviterId);
                        User joiner = userDAO.findById(userId);
                        if (inviter != null && joiner != null && !inviter.getUserId().equals(joiner.getUserId())) {
                            Notification notif = new Notification(
                                    inviter,
                                    NotificationType.MEMBER_JOINED,
                                    "Member Joined Your Group",
                                    joiner.getDisplayName() + " accepted your invitation to the group",
                                    groupId
                            );
                            new NotificationDatabaseObject().save(notif);
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to send MEMBER_JOINED notification: " + e.getMessage());
                    }
// ======================================
                    // ===== END NOTIFICATION =====
                    return;
                }
            }

            String updateInvSql = "UPDATE invitations SET status = ? WHERE invitation_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateInvSql)) {
                pstmt.setString(1, InvitationStatus.ACCEPTED.name());
                pstmt.setString(2, invitationId);
                int invRows = pstmt.executeUpdate();
                if (invRows == 0) {
                    throw new SQLException("Failed to update invitation status");
                }
            }

            String insertSql = """
            INSERT INTO group_members (group_id, member_id, member_type, role, joined_at, invited_by)
            VALUES (?, ?, 'REGISTERED', 'MEMBER', ?, ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, groupId);
                pstmt.setString(2, userId);
                pstmt.setString(3, new Timestamp(System.currentTimeMillis()).toString());
                pstmt.setString(4, inviterId);

                int memberRows = pstmt.executeUpdate();
                if (memberRows == 0) {
                    throw new SQLException("Failed to add to group members");
                }
            }

            conn.commit();

            // ===== NOTIFICATION FOR NEW MEMBER CASE =====
            try {
                User inviter = userDAO.findById(inviterId);
                User newMember = userDAO.findById(userId);
                Group group = groupDAO.loadGroup(groupId);

                if (inviter != null && newMember != null && group != null) {
                    Notification notif = new Notification(
                            inviter,
                            NotificationType.MEMBER_JOINED,
                            "Member Joined",
                            newMember.getDisplayName() + " joined your group " + group.getName(),
                            groupId
                    );
                    new NotificationDatabaseObject().save(notif);
                }
            } catch (Exception notifEx) {
                System.out.println("Failed to send notification: " + notifEx.getMessage());
            }
            // ===== END NOTIFICATION =====

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Failed to accept invitation: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void declineInvitation(String invitationId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE invitations SET status = ? WHERE invitation_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, InvitationStatus.DECLINED.name());
                pstmt.setString(2, invitationId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to decline invitation: " + e.getMessage(), e);
        }
    }

    public void sendInvitation(String groupId, String inviterId, String email, boolean isGuest) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found with ID: " + groupId);
        }

        User inviter = userDAO.findById(inviterId);
        if (inviter == null) {
            throw new IllegalArgumentException("Inviter not found");
        }

        Invitation invitation;

        User existingUser = userDAO.findByEmail(email);

        if (existingUser != null) {
            invitation = new Invitation(group, inviter, email);
            String userName = existingUser.getFullName();
            if (userName != null && !userName.isEmpty()) {
                invitation.setInviteeName(userName);
            }
        } else {
            String name = isGuest ? email.split("@")[0] : "Guest";
            invitation = Invitation.createPlaceholderWithEmail(group, inviter, name, email);
        }

        invitation.setInvitationCode(group.getInvitationCode());
        invitationDAO.save(invitation);

        //new
        if (existingUser != null) {
            Notification notif = new Notification(
                    existingUser,
                    NotificationType.GROUP_INVITATION,
                    "Group Invitation",
                    inviter.getDisplayName() + " invited you to join " + group.getName(),
                    group.getInvitationCode()
            );
            new NotificationDatabaseObject().save(notif);
        }

        //till this
    }

    public List<Invitation> getPendingInvitations(String email) {
        return invitationDAO.findPendingByEmail(email);
    }

    public List<Invitation> getPendingInvitationsForUser(String email, String name) {
        return invitationDAO.findPendingForUser(email, name);
    }

    public Invitation findByInvitationCode(String code) {
        return invitationDAO.findByInvitationCode(code);
    }

    public void updateStatus(String invitationId, InvitationStatus status) {
        invitationDAO.updateStatus(invitationId, status);
    }
}
