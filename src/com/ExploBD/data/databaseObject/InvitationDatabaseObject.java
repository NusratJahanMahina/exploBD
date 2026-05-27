package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.object.User;

import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.enums.InvitationStatus;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvitationDatabaseObject {

    private GroupDatabaseObject groupDAO;
    private UserDatabaseObject userDAO;

    public InvitationDatabaseObject() {
        this.groupDAO = new GroupDatabaseObject();
        this.userDAO = new UserDatabaseObject();
    }

    public void save(Invitation invitation) {
        String sql = """
            INSERT INTO invitations 
            (invitation_id, group_id, inviter_id, invitee_email, invitee_name,
             type, status, sent_date, expiry_date, needs_leader_approval, invitation_code)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, invitation.getInvitationId());
            pstmt.setString(2, invitation.getGroup().getId());
            pstmt.setString(3, invitation.getInviter().getUserId());
            pstmt.setString(4, invitation.getInviteeEmail());
            pstmt.setString(5, invitation.getInviteeName());
            pstmt.setString(6, invitation.getType());
            pstmt.setString(7, invitation.getStatus().name());
            pstmt.setString(8, invitation.getSentDate().toString());
            pstmt.setString(9, invitation.getExpiryDate().toString());
            pstmt.setBoolean(10, invitation.needsLeaderApproval());
            pstmt.setString(11, invitation.getInvitationCode());

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

    private Invitation buildInvitationFromResultSet(ResultSet rs) throws SQLException {
        String groupId = rs.getString("group_id");
        System.out.println("Loading group with ID: " + groupId);
        
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            System.out.println("ERROR: Group not found with ID: " + groupId);
            throw new SQLException("Group not found: " + groupId);
        }

        User inviter = userDAO.findById(rs.getString("inviter_id"));
        
        String type = rs.getString("type");
        String email = rs.getString("invitee_email");
        String name = rs.getString("invitee_name");
        String invitationId = rs.getString("invitation_id");
        String status = rs.getString("status");
        String invitationCode = rs.getString("invitation_code");
        String sentDateStr = rs.getString("sent_date");
        String expiryDateStr = rs.getString("expiry_date");
        boolean needsApproval = rs.getBoolean("needs_leader_approval");

        Invitation invitation;
        
        if ("EMAIL".equals(type)) {
            invitation = new Invitation(group, inviter, email);
        } else {
            invitation = Invitation.createPlaceholder(group, inviter, name, email);
        }
        
        invitation.setInvitationId(invitationId);
        invitation.setStatus(InvitationStatus.valueOf(status));
        invitation.setInvitationCode(invitationCode);
        invitation.setNeedsLeaderApproval(needsApproval);
        invitation.setSentDate(LocalDate.parse(sentDateStr));
        invitation.setExpiryDate(LocalDate.parse(expiryDateStr));
        
        System.out.println("Built invitation: " + invitationId + " for group: " + groupId);
        
        return invitation;
    }

public List<Invitation> findPendingForUser(String email, String name) {
    System.out.println("InvitationDAO: Looking for pending invitations for email=" + email + ", name=" + name);
    
    List<Invitation> invitations = new ArrayList<>();
    String sql = """
        SELECT * FROM invitations 
        WHERE status = 'PENDING' AND (invitee_email = ? OR invitee_name = ?)
        """;
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, email);
        pstmt.setString(2, name);
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            invitations.add(buildInvitationFromResultSet(rs));
        }
        
        System.out.println("InvitationDAO: Found " + invitations.size() + " invitations");
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return invitations;
}

    public List<Invitation> findPendingByEmail(String email) {
        List<Invitation> invitations = new ArrayList<>();
        String sql = "SELECT * FROM invitations WHERE invitee_email = ? AND status = 'PENDING'";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                invitations.add(buildInvitationFromResultSet(rs));
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
        return invitations;
    }

    public boolean updateStatus(String invitationId, InvitationStatus status) {
        String sql = "UPDATE invitations SET status = ? WHERE invitation_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status.name());
            pstmt.setString(2, invitationId);
            
            int rows = pstmt.executeUpdate();
            System.out.println("Updated invitation " + invitationId + ": " + rows + " rows");
            return rows > 0;
            
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

    public List<Invitation> findPlaceholdersByName(String name) {
        List<Invitation> placeholders = new ArrayList<>();
        String sql = "SELECT * FROM invitations WHERE type = 'PLACEHOLDER' AND invitee_name = ? AND status = 'PENDING'";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                placeholders.add(buildInvitationFromResultSet(rs));
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
        return placeholders;
    }

    public Invitation findByInvitationCode(String code) {
        String sql = "SELECT * FROM invitations WHERE invitation_code = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildInvitationFromResultSet(rs);
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
}