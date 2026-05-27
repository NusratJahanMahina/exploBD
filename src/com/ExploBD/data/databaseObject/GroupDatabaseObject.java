package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.domain.members.GuestMember;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.enums.MemberType;
import com.ExploBD.domain.valueobjects.BudgetRange;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class GroupDatabaseObject {

    private UserDatabaseObject userDAO;
    private PlaceDatabaseObject placeDAO;

    public GroupDatabaseObject() {
        this.userDAO = new UserDatabaseObject();
        this.placeDAO = new PlaceDatabaseObject();
    }

    /**
     * Saves a group to the database with full transaction support
     */
    public boolean saveGroup(Group group) {
        String sql = """
            INSERT OR REPLACE INTO groups 
            (group_id, name, description, leader_id, destination_name, 
             destination_division, destination_image_path, min_budget, max_budget, max_members,
             start_date, end_date, status, invitation_code, voting_start_time, voting_end_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, group.getId());
            pstmt.setString(2, group.getName());
            pstmt.setString(3, group.getDescription());
            pstmt.setString(4, group.getLeader().getId());
            pstmt.setString(5, group.getDestinationName());
            pstmt.setString(6, group.getDestinationDivision());
            pstmt.setString(7, group.getDestinationImagePath());

            if (group.getBudgetRange() != null) {
                pstmt.setInt(8, group.getBudgetRange().getMinPerPerson());
                pstmt.setInt(9, group.getBudgetRange().getMaxPerPerson());
            } else {
                pstmt.setNull(8, java.sql.Types.INTEGER);
                pstmt.setNull(9, java.sql.Types.INTEGER);
            }

            pstmt.setInt(10, group.getMaxMembers());
            pstmt.setString(11, group.getStartDate() != null ? group.getStartDate().toString() : null);
            pstmt.setString(12, group.getEndDate() != null ? group.getEndDate().toString() : null);
            pstmt.setString(13, group.getStatus().name());
            pstmt.setString(14, group.getInvitationCode());

            if (group.getVotingStartTime() != null) {
                pstmt.setString(15, group.getVotingStartTime().toString());
            } else {
                pstmt.setNull(15, java.sql.Types.VARCHAR);
            }

            if (group.getVotingEndTime() != null) {
                pstmt.setString(16, group.getVotingEndTime().toString());
            } else {
                pstmt.setNull(16, java.sql.Types.VARCHAR);
            }

            int result = pstmt.executeUpdate();

            saveAllGroupMembers(group.getId(), group.getAllMembers(), conn);
            saveVoters(group.getId(), group.getVoters(), conn);
            conn.commit();
            return result > 0;

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
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



public List<Group> getAllAvailableGroups(String userId) {
    List<Group> groups = new ArrayList<>();
    
    String sql = "SELECT * FROM groups WHERE status IN ('PLANNING', 'VOTING')";
    
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        conn = DatabaseConnection.getConnection();
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Group group = buildGroupFromResultSet(rs, conn);
            
            
            boolean isMember = false;
            for (GroupMember member : group.getAllMembers()) {
                if (member instanceof RegisteredMember) {
                    User memberUser = ((RegisteredMember) member).getUser();
                    if (memberUser.getUserId().equals(userId)) {
                        isMember = true;
                        break;
                    }
                }
            }
            
          
            if (!isMember) {
                groups.add(group);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
    
    return groups;
}
    /**
     * Saves all group members (replaces old members)
     */
    /**
     * Gets all public groups that the user is not a member of (This method was
     * incorrectly placed inside saveAllGroupMembers)
     */
    public List<Group> getPublicGroups(String excludeUserId) {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM groups WHERE status IN ('PLANNING', 'VOTING')";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Group group = buildGroupFromResultSet(rs, conn);

                
                boolean isMember = false;
                for (GroupMember member : group.getAllMembers()) {
                    if (member instanceof RegisteredMember) {
                        User memberUser = ((RegisteredMember) member).getUser();
                        if (memberUser.getUserId().equals(excludeUserId)) {
                            isMember = true;
                            break;
                        }
                    }
                }

                if (!isMember) {
                    groups.add(group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return groups;
    }

    private void saveAllGroupMembers(String groupId, List<GroupMember> members, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM group_members WHERE group_id = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(deleteSql);
            pstmt.setString(1, groupId);
            pstmt.executeUpdate();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String insertSql = """
            INSERT INTO group_members 
            (group_id, member_id, member_type, role, joined_at, invited_by, invitation_code)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        PreparedStatement insertStmt = null;
        try {
            insertStmt = conn.prepareStatement(insertSql);
            for (GroupMember member : members) {
                insertStmt.setString(1, groupId);
                insertStmt.setString(2, member.getId());
                insertStmt.setString(3, member.getType().name());
                insertStmt.setString(4, member.getRole().name());
                insertStmt.setString(5, java.time.LocalDate.now().toString());

                if (member instanceof GuestMember) {
                    insertStmt.setString(6, ((GuestMember) member).getInvitedBy());
                } else {
                    insertStmt.setNull(6, java.sql.Types.VARCHAR);
                }

                insertStmt.setString(7, member.getInvitationCode());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        } finally {
            try {
                if (insertStmt != null) {
                    insertStmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads a complete group with all members
     */
    public Group loadGroup(String groupId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM groups WHERE group_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildGroupFromResultSet(rs, conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Builds a Group object from database result set
     */
    private Group buildGroupFromResultSet(ResultSet rs, Connection conn) throws SQLException {
        String groupId = rs.getString("group_id");
        String leaderId = rs.getString("leader_id");
        User leaderUser = userDAO.findById(leaderId);
        if (leaderUser == null) {
            throw new SQLException("Leader user not found: " + leaderId);
        }

        RegisteredMember leader = RegisteredMember.asLeader(leaderUser);
        Group group = new Group(groupId, rs.getString("name"), leader);

        group.setDescription(rs.getString("description"));
        group.setDestinationName(rs.getString("destination_name"));
        group.setDestinationDivision(rs.getString("destination_division"));
        group.setDestinationImagePath(rs.getString("destination_image_path"));

        int minBudget = rs.getInt("min_budget");
        int maxBudget = rs.getInt("max_budget");
        if (!rs.wasNull()) {
            group.setBudgetRange(new BudgetRange(minBudget, maxBudget));
        }

        group.setMaxMembers(rs.getInt("max_members"));

        String startDateStr = rs.getString("start_date");
        if (startDateStr != null) {
            group.setStartDate(LocalDate.parse(startDateStr));
        }

        String endDateStr = rs.getString("end_date");
        if (endDateStr != null) {
            group.setEndDate(LocalDate.parse(endDateStr));
        }

        group.setStatus(GroupStatus.valueOf(rs.getString("status")));
        group.setInvitationCode(rs.getString("invitation_code"));

        String votingStartStr = rs.getString("voting_start_time");
        if (votingStartStr != null) {
            group.setVotingStartTime(LocalDateTime.parse(votingStartStr));
        }

        String votingEndStr = rs.getString("voting_end_time");
        if (votingEndStr != null) {
            group.setVotingEndTime(LocalDateTime.parse(votingEndStr));
        }

        loadGroupMembers(group, groupId, conn);
        loadGroupSuggestions(group, groupId, conn);
        loadVoters(group, groupId, conn);

        System.out.println("Loaded group " + groupId + " with " + group.getMemberCount() + " members");
        return group;
    }

    /**
     * Loads all members for a group
     */
    private void loadGroupMembers(Group group, String groupId, Connection conn) {
        String sql = "SELECT * FROM group_members WHERE group_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String memberId = rs.getString("member_id");
                String memberType = rs.getString("member_type");
                String role = rs.getString("role");
                String invitedBy = rs.getString("invited_by");

                if (memberId.equals(group.getLeader().getId())) {
                    continue;
                }

                GroupMember member = null;

                if ("REGISTERED".equals(memberType)) {
                    User user = userDAO.findById(memberId);
                    if (user != null) {
                        member = new RegisteredMember(user, MemberRole.valueOf(role));
                    }
                } else {
                    GuestMember guest = new GuestMember(memberId, invitedBy);
                    guest.setRole(MemberRole.valueOf(role));
                    member = guest;
                }

                if (member != null) {
                    group.addMember(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves voters
     */
    private void saveVoters(String groupId, Map<String, Set<String>> voters, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM suggestion_votes WHERE suggestion_id IN (SELECT message_id FROM messages WHERE group_id = ? AND message_type = 'SUGGESTION')";
        PreparedStatement deleteStmt = null;
        try {
            deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setString(1, groupId);
            deleteStmt.executeUpdate();
        } finally {
            try {
                if (deleteStmt != null) {
                    deleteStmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String insertSql = "INSERT INTO suggestion_votes (vote_id, suggestion_id, voter_id, vote_type, voted_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertStmt = null;

        try {
            insertStmt = conn.prepareStatement(insertSql);
            for (Map.Entry<String, Set<String>> entry : voters.entrySet()) {
                String suggestionId = entry.getKey();
                Set<String> voterIds = entry.getValue();

                for (String voterId : voterIds) {
                    String voteId = "VOTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    insertStmt.setString(1, voteId);
                    insertStmt.setString(2, suggestionId);
                    insertStmt.setString(3, voterId);
                    insertStmt.setString(4, "YES");
                    insertStmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    insertStmt.addBatch();
                }
            }
            insertStmt.executeBatch();
        } finally {
            try {
                if (insertStmt != null) {
                    insertStmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads voters
     */
    private void loadVoters(Group group, String groupId, Connection conn) {
        String sql = "SELECT suggestion_id, voter_id FROM suggestion_votes WHERE suggestion_id IN (SELECT message_id FROM messages WHERE group_id = ? AND message_type = 'SUGGESTION')";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            rs = pstmt.executeQuery();

            Map<String, Set<String>> voters = new HashMap<>();

            while (rs.next()) {
                String suggestionId = rs.getString("suggestion_id");
                String voterId = rs.getString("voter_id");

                Set<String> voterSet = voters.get(suggestionId);
                if (voterSet == null) {
                    voterSet = new HashSet<>();
                    voters.put(suggestionId, voterSet);
                }
                voterSet.add(voterId);
            }

            group.setVoters(voters);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads group suggestions
     */
    private void loadGroupSuggestions(Group group, String groupId, Connection conn) {
        String sql = "SELECT * FROM messages WHERE group_id = ? AND message_type = 'SUGGESTION' ORDER BY created_at ASC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String messageId = rs.getString("message_id");
                String placeId = rs.getString("place_id");
                String reason = rs.getString("reason");
                String senderId = rs.getString("sender_id");
                int upvotes = rs.getInt("upvotes");
                int downvotes = rs.getInt("downvotes");
                String status = rs.getString("status");
                String createdAt = rs.getString("created_at");

                Place place = placeDAO.findById(placeId);
                User suggester = userDAO.findById(senderId);

                if (place == null || suggester == null) {
                    continue;
                }

                InvitationStatus suggestionStatus = "APPROVED".equals(status)
                        ? InvitationStatus.APPROVED
                        : ("REJECTED".equals(status) ? InvitationStatus.DECLINED : InvitationStatus.PENDING);

                PlaceSuggestion suggestion = new PlaceSuggestion(
                        messageId,
                        place,
                        suggester,
                        reason,
                        suggestionStatus,
                        LocalDateTime.parse(createdAt),
                        upvotes,
                        downvotes
                );

                
                String voteSql = "SELECT voter_id, vote_type FROM suggestion_votes WHERE suggestion_id = ?";
                PreparedStatement voteStmt = null;
                ResultSet voteRs = null;
                try {
                    voteStmt = conn.prepareStatement(voteSql);
                    voteStmt.setString(1, messageId);
                    voteRs = voteStmt.executeQuery();

                    Map<String, Boolean> votes = new HashMap<>();
                    while (voteRs.next()) {
                        String voterId = voteRs.getString("voter_id");
                        String voteType = voteRs.getString("vote_type");
                        votes.put(voterId, voteType.equals("YES"));
                    }
                    suggestion.setVotes(votes);
                } finally {
                    try {
                        if (voteRs != null) {
                            voteRs.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (voteStmt != null) {
                            voteStmt.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                group.addSuggestion(suggestion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds all groups a user is a member of
     */
    public List<Group> findByUser(String userId) {
        List<Group> groups = new ArrayList<>();
        String sql = """
            SELECT g.* FROM groups g
            JOIN group_members gm ON g.group_id = gm.group_id
            WHERE gm.member_id = ? AND gm.member_type = 'REGISTERED'
            ORDER BY g.start_date DESC
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
                groups.add(buildGroupFromResultSet(rs, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return groups;
    }

    /**
     * Deletes a group and all related data
     */
    public boolean deleteGroup(String groupId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM groups WHERE group_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds a group by its invitation code
     */
    public Group findByInvitationCode(String code) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM groups WHERE invitation_code = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildGroupFromResultSet(rs, conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Gets all members of a group
     */
    public List<GroupMember> getGroupMembers(String groupId) {
        List<GroupMember> members = new ArrayList<>();
        String sql = "SELECT * FROM group_members WHERE group_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String memberId = rs.getString("member_id");
                String memberType = rs.getString("member_type");
                String role = rs.getString("role");
                String invitedBy = rs.getString("invited_by");

                if ("REGISTERED".equals(memberType)) {
                    User user = userDAO.findById(memberId);
                    if (user != null) {
                        RegisteredMember member = new RegisteredMember(user, MemberRole.valueOf(role));
                        members.add(member);
                    }
                } else {
                    GuestMember guest = new GuestMember(memberId, invitedBy);
                    guest.setRole(MemberRole.valueOf(role));
                    members.add(guest);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return members;
    }

    /**
     * Directly adds a member to a group
     */
    public boolean addMemberToGroup(String groupId, String userId, String invitedBy) {
        String sql = """
            INSERT INTO group_members (group_id, member_id, member_type, role, joined_at, invited_by)
            VALUES (?, ?, 'REGISTERED', 'MEMBER', ?, ?)
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, groupId);
            pstmt.setString(2, userId);
            pstmt.setString(3, java.time.LocalDate.now().toString());
            pstmt.setString(4, invitedBy);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes a member from a group
     */
    public boolean removeMemberFromGroup(String groupId, String memberId) {
        String sql = "DELETE FROM group_members WHERE group_id = ? AND member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            pstmt.setString(2, memberId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if a user is a member of a group
     */
    public boolean isMember(String groupId, String userId) {
        String sql = "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);
            pstmt.setString(2, userId);

            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets member count for a group
     */
    /**
 * Gets ALL groups that the user is NOT a member of
 * This includes PLANNING, VOTING, CONFIRMED - all statuses
 */
public List<Group> getAllGroupsUserNotIn(String userId) {
    List<Group> groups = new ArrayList<>();
    String sql = """
        SELECT g.* FROM groups g
        WHERE g.group_id NOT IN (
            SELECT gm.group_id FROM group_members gm 
            WHERE gm.member_id = ?
        )
        ORDER BY g.created_at DESC
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
            Group group = buildGroupFromResultSet(rs, conn);
            groups.add(group);
        }
        System.out.println("GroupDAO: Found " + groups.size() + " groups user is NOT in");
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
    
    return groups;
}
    public int getMemberCount(String groupId) {
        String sql = "SELECT COUNT(*) FROM group_members WHERE group_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, groupId);

            rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    }
}
