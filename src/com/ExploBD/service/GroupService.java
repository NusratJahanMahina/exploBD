package com.ExploBD.service;

import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.data.databaseObject.MessageDatabaseObject;
import com.ExploBD.data.databaseObject.NotificationDatabaseObject;
import com.ExploBD.domain.entities.*;
import com.ExploBD.domain.enums.*;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.domain.members.GuestMember;
import com.ExploBD.domain.valueobjects.BudgetRange;
import com.ExploBD.session.UserSession;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupService {

    private GroupDatabaseObject groupDAO;
    private InvitationDatabaseObject invitationDAO;
    private MessageDatabaseObject messageRepo;
    private UserDatabaseObject userDAO;
    private PlaceDatabaseObject placeDAO;

    public GroupService() {
        this.groupDAO = new GroupDatabaseObject();
        this.invitationDAO = new InvitationDatabaseObject();
        this.messageRepo = new MessageDatabaseObject();
        this.userDAO = new UserDatabaseObject();
        this.placeDAO = new PlaceDatabaseObject();
    }

    private void checkDateConflicts(User user, LocalDate startDate, LocalDate endDate) {
        List<Group> userGroups = groupDAO.findByUser(user.getUserId());

        Group tempGroup = new Group("temp", RegisteredMember.asLeader(user));
        tempGroup.setStartDate(startDate);
        tempGroup.setEndDate(endDate);

        for (Group existingGroup : userGroups) {
            if (existingGroup.getStatus() == GroupStatus.PLANNING
                    || existingGroup.getStatus() == GroupStatus.VOTING) {
                if (tempGroup.conflictsWith(existingGroup)) {
                    throw new IllegalStateException(
                            "You already have an active group planned from "
                            + existingGroup.getStartDate() + " to " + existingGroup.getEndDate()
                    );
                }
            }
        }
    }

    public void updateGroupSettings(String groupId, User leader,
            Integer maxMembers,
            LocalDate startDate,
            LocalDate endDate) {

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Group group = groupDAO.loadGroup(groupId);
            if (group == null) {
                throw new IllegalArgumentException("Group not found");
            }

            if (!group.isLeader(leader)) {
                throw new IllegalStateException("Only leader can edit group settings");
            }

            if (!group.canEdit()) {
                throw new IllegalStateException("Cannot edit group at this stage");
            }

            if (maxMembers != null && maxMembers > 0) {
                if (maxMembers < group.getMemberCount()) {
                    throw new IllegalArgumentException(
                            "Cannot reduce max members below current member count ("
                            + group.getMemberCount() + ")"
                    );
                }
                group.setMaxMembers(maxMembers);
            }

            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    throw new IllegalArgumentException("Start date must be before end date");
                }

                List<Group> userGroups = groupDAO.findByUser(leader.getUserId());
                for (Group other : userGroups) {
                    if (other.getId().equals(groupId)) {
                        continue;
                    }
                    if (other.getStatus() == GroupStatus.PLANNING
                            || other.getStatus() == GroupStatus.VOTING
                            || (other.getStatus() == GroupStatus.CONFIRMED
                            && other.getEndDate() != null
                            && other.getEndDate().isAfter(LocalDate.now()))) {

                        if (!(endDate.isBefore(other.getStartDate())
                                || startDate.isAfter(other.getEndDate()))) {
                            throw new IllegalStateException(
                                    "Date conflict with group: " + other.getName()
                            );
                        }
                    }
                }

                group.setStartDate(startDate);
                group.setEndDate(endDate);
            }

            groupDAO.saveGroup(group);

            conn.commit();
            System.out.println("Group settings updated: " + groupId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to update group settings", e);
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

    /**
     * Get all public groups for browsing
     */
    public List<Group> getPublicGroups() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return groupDAO.getPublicGroups(currentUser.getUserId());
    }

    /**
     * Join a public group directly
     */
    public boolean joinPublicGroup(String groupId, User user) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT COUNT(*) FROM group_members WHERE group_id = ? AND member_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, groupId);
                pstmt.setString(2, user.getUserId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }

            Group group = groupDAO.loadGroup(groupId);
            if (group != null && group.getMemberCount() >= group.getMaxMembers()) {
                return false;
            }

            String sql = "INSERT INTO group_members (group_id, member_id, member_type, role, joined_at) "
                    + "VALUES (?, ?, 'REGISTERED', 'MEMBER', ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, groupId);
                pstmt.setString(2, user.getUserId());
                pstmt.setString(3, new Timestamp(System.currentTimeMillis()).toString());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

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

    public boolean disbandGroup(String groupId, User leader) {
        Connection conn = null;
        try {
            Group group = groupDAO.loadGroup(groupId);
            if (group == null) {
                throw new IllegalArgumentException("Group not found");
            }

            if (!group.isLeader(leader)) {
                throw new IllegalStateException("Only leader can disband the group");
            }

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM suggestion_votes WHERE suggestion_id IN (SELECT message_id FROM messages WHERE group_id = ?)")) {
                pstmt.setString(1, groupId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM messages WHERE group_id = ?")) {
                pstmt.setString(1, groupId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM invitations WHERE group_id = ?")) {
                pstmt.setString(1, groupId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM group_members WHERE group_id = ?")) {
                pstmt.setString(1, groupId);
                pstmt.executeUpdate();
            }

            int deleted;
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM groups WHERE group_id = ?")) {
                pstmt.setString(1, groupId);
                deleted = pstmt.executeUpdate();
            }

            conn.commit();

            UserSession session = UserSession.getInstance();
            session.loadUserData();

            return deleted > 0;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to disband group: " + e.getMessage());
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

    public void finalizeGroup(String groupId, User leader) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        if (!group.isLeader(leader)) {
            throw new IllegalStateException("Only leader can finalize group");
        }

        if (group.getStatus() != GroupStatus.VOTING
                && group.getStatus() != GroupStatus.PLANNING) {
            throw new IllegalStateException("Group cannot be finalized at this stage");
        }

        if (group.getSelectedDestination() == null) {
            PlaceSuggestion winner = group.getWinningSuggestion();
            if (winner == null) {
                throw new IllegalStateException("No destination selected for this group");
            }
            group.confirmDestination(winner.getSuggestionId(), leader);
        }

        group.setStatus(GroupStatus.CONFIRMED);
        groupDAO.saveGroup(group);
    }

    public Group createGroup(String name, User creator, String description,
            LocalDate startDate, LocalDate endDate,
            int minBudget, int maxBudget, int maxMembers) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required");
        }

        if (!creator.canCreateGroup()) {
            throw new IllegalStateException("You cannot create a group at this time");
        }

        checkDateConflicts(creator, startDate, endDate);

        RegisteredMember leader = RegisteredMember.asLeader(creator);
        Group group = new Group(name, leader);

        group.setDescription(description);
        group.setStartDate(startDate);
        group.setEndDate(endDate);
        group.setBudgetRange(new BudgetRange(minBudget, maxBudget));
        group.setMaxMembers(maxMembers);
        group.setStatus(GroupStatus.PLANNING);

        boolean saved = groupDAO.saveGroup(group);
        if (!saved) {
            throw new RuntimeException("Failed to save group");
        }

        return group;
    }

    public boolean isUserInAnyActiveGroup(String userId) {
        List<Group> userGroups = groupDAO.findByUser(userId);
        for (Group g : userGroups) {
            if (g.getStatus() == GroupStatus.PLANNING || g.getStatus() == GroupStatus.VOTING) {
                return true;
            }
        }
        return false;
    }

    public List<Group> getUserActiveGroups(String userId) {
        List<Group> userGroups = groupDAO.findByUser(userId);
        List<Group> activeGroups = new ArrayList<>();
        for (Group g : userGroups) {
            if (g.getStatus() == GroupStatus.PLANNING || g.getStatus() == GroupStatus.VOTING) {
                activeGroups.add(g);
            }
        }
        return activeGroups;
    }

    public boolean hasDateConflictWithAnyGroup(String userId, LocalDate startDate, LocalDate endDate) {
        List<Group> activeGroups = getUserActiveGroups(userId);

        for (Group group : activeGroups) {
            if (group.getStartDate() == null || group.getEndDate() == null) {
                continue;
            }

            if (!(endDate.isBefore(group.getStartDate())
                    || startDate.isAfter(group.getEndDate()))) {
                return true;
            }
        }
        return false;
    }

    public Group getMostRecentActiveGroup(String userId) {
        List<Group> activeGroups = getUserActiveGroups(userId);
        Group mostRecent = null;
        LocalDate latestDate = null;

        for (Group g : activeGroups) {
            if (g.getStartDate() != null) {
                if (latestDate == null || g.getStartDate().isAfter(latestDate)) {
                    latestDate = g.getStartDate();
                    mostRecent = g;
                }
            }
        }
        return mostRecent;
    }

    public Group getGroup(String groupId) {
        return groupDAO.loadGroup(groupId);
    }

    public GroupMember inviteRegistered(String groupId, User inviter, String email) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        GroupMember inviterMember = group.getMember(inviter.getUserId());
        if (inviterMember == null || !inviterMember.canInvite()) {
            throw new IllegalStateException("You don't have permission to invite");
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No user found with this email");
        }

        if (group.hasMember(user.getUserId())) {
            throw new IllegalStateException("User already in group");
        }

        RegisteredMember member = RegisteredMember.asMember(user);
        group.addMember(member);
        groupDAO.saveGroup(group);

        return member;
    }

    public GuestMember inviteGuest(String groupId, User inviter, String email) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        GroupMember inviterMember = group.getMember(inviter.getUserId());
        if (inviterMember == null || !inviterMember.canInvite()) {
            throw new IllegalStateException("You don't have permission to invite");
        }

        if (group.hasMember(email)) {
            throw new IllegalStateException("This email already has a pending invitation");
        }

        GuestMember guest = new GuestMember(email, inviter.getUserId());
        group.addMember(guest);
        groupDAO.saveGroup(group);

        return guest;
    }

public InviteRequestMessage createInvitationRequest(String groupId, User requester,
        String inviteeName, String inviteeEmail,
        String requestType) {
    
    Group group = groupDAO.loadGroup(groupId);  // Load group FIRST
    if (group == null) {
        throw new IllegalArgumentException("Group not found");
    }

    if (!group.hasMember(requester)) {
        throw new IllegalArgumentException("You are not a member of this group");
    }

    List<Message> pendingRequests = messageRepo.getPendingRequests(groupId);
    boolean alreadyRequested = false;
    for (Message m : pendingRequests) {
        if (m instanceof InviteRequestMessage) {
            InviteRequestMessage req = (InviteRequestMessage) m;
            if (req.getInviteeEmail().equalsIgnoreCase(inviteeEmail)
                    && req.getStatus() == MessageStatus.PENDING) {
                alreadyRequested = true;
                break;
            }
        }
    }
    if (alreadyRequested) {
        throw new IllegalStateException("Already have a pending request for this email");
    }

    InviteRequestMessage request = new InviteRequestMessage(
            requester, groupId, inviteeName, inviteeEmail, requestType
    );

    messageRepo.save(request);
    
    // ===== ADD NOTIFICATION FOR LEADER (AFTER group is loaded) =====
    try {
        GroupMember leader = group.getLeader();
        if (leader instanceof RegisteredMember) {
            User leaderUser = ((RegisteredMember) leader).getUser();
            if (!leaderUser.getUserId().equals(requester.getUserId())) {
                Notification notif = new Notification(
                    leaderUser,
                    NotificationType.JOIN_REQUEST,
                    "Join Request",
                    requester.getDisplayName() + " requested to join the group",
                    groupId
                );
                new NotificationDatabaseObject().save(notif);
            }
        }
    } catch (Exception notifEx) {
        System.out.println("Failed to send notification: " + notifEx.getMessage());
    }
    // ===== END OF NOTIFICATION CODE =====

    return request;
}
    public void approveInvitationRequest(String groupId, String messageId, User leader) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        if (!group.isLeader(leader)) {
            throw new IllegalStateException("Only leader can approve requests");
        }

        if (group.getMemberCount() >= group.getMaxMembers()) {
            throw new IllegalStateException("Group has reached maximum members");
        }

        List<Message> allMessages = messageRepo.getGroupMessages(groupId);

        Message foundMessage = null;
        for (Message m : allMessages) {
            if (m.getMessageId().equals(messageId)) {
                foundMessage = m;
                break;
            }
        }

        if (foundMessage == null) {
            throw new IllegalArgumentException("Message not found with ID: " + messageId);
        }

        if (foundMessage.getType() != MessageType.INVITE_REQUEST) {
            throw new IllegalArgumentException("Message is not an invitation request");
        }

        if (!(foundMessage instanceof InviteRequestMessage)) {
            throw new IllegalArgumentException("Message class mismatch");
        }

        InviteRequestMessage request = (InviteRequestMessage) foundMessage;

        request.approve(leader);
        messageRepo.updateStatus(messageId, MessageStatus.APPROVED, leader.getUserId());

        User existingUser = userDAO.findByEmail(request.getInviteeEmail());
        boolean isRegistered = existingUser != null;

        Invitation invitation;
        if (isRegistered) {
            invitation = new Invitation(group, leader, request.getInviteeEmail());
            invitation.setInviteeName(request.getInviteeName());
        } else {
            invitation = Invitation.createPlaceholderWithEmail(
                    group, leader, request.getInviteeName(), request.getInviteeEmail()
            );
        }
        invitation.setInvitationCode(group.getInvitationCode());
        invitationDAO.save(invitation);
    }

    private String getAllMessageIds(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        for (Message msg : messages) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(msg.getMessageId());
        }
        return sb.toString();
    }

    public void rejectInvitationRequest(String groupId, String messageId, User leader) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        if (!group.isLeader(leader)) {
            throw new IllegalStateException("Only leader can reject requests");
        }

        Message message = messageRepo.getGroupMessages(groupId).stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst()
                .orElse(null);

        if (!(message instanceof InviteRequestMessage)) {
            throw new IllegalArgumentException("Message is not an invitation request");
        }

        InviteRequestMessage request = (InviteRequestMessage) message;
        request.reject(leader);
        messageRepo.updateStatus(messageId, MessageStatus.REJECTED, leader.getUserId());
    }

  public PlaceSuggestion suggestPlace(String groupId, User suggester, String placeId, String reason) {
    Group group = groupDAO.loadGroup(groupId);
    if (group == null) {
        throw new IllegalArgumentException("Group not found");
    }

    if (!group.canSuggest(suggester)) {
        throw new IllegalStateException("You cannot suggest places at this time");
    }

    Place place = placeDAO.findById(placeId);
    if (place == null) {
        throw new IllegalArgumentException("Place not found. Please select a place using the Browse button.");
    }

    if (!place.getDivision().equalsIgnoreCase(group.getDestinationDivision())) {
        throw new IllegalArgumentException("This place is in " + place.getDivision()
                + " division, but group destination is in " + group.getDestinationDivision());
    }

    SuggestionMessage suggestionMsg = new SuggestionMessage(
            suggester,
            groupId,
            place.getId(),
            place.getName(),
            reason
    );
    messageRepo.save(suggestionMsg);
    System.out.println("Saved suggestion to messages table with ID: " + suggestionMsg.getMessageId());

    group.suggestPlace(place, suggester, reason);

    boolean saved = groupDAO.saveGroup(group);
    if (!saved) {
        throw new RuntimeException("Failed to save group");
    }

    // ===== ADD NOTIFICATION FOR LEADER =====
    try {
        // DON'T redeclare Group group - use the existing one
        // DON'T redeclare Place place - use the existing one
        GroupMember leader = group.getLeader();
        if (leader instanceof RegisteredMember) {
            User leaderUser = ((RegisteredMember) leader).getUser();
            if (!leaderUser.getUserId().equals(suggester.getUserId())) {
                // Use the existing 'place' variable, don't reload it
                Notification notif = new Notification(
                        leaderUser,
                        NotificationType.NEW_SUGGESTION,
                        "New Suggestion",
                        suggester.getDisplayName() + " suggested a new place: " + place.getName(),
                        groupId
                );
                new NotificationDatabaseObject().save(notif);
            }
        }
    } catch (Exception notifEx) {
        System.out.println("Failed to send notification: " + notifEx.getMessage());
    }
    // ===== END OF NOTIFICATION CODE =====

    List<PlaceSuggestion> suggestions = group.getAllSuggestions();
    return suggestions.isEmpty() ? null : suggestions.get(suggestions.size() - 1);
}

    public void approveSuggestion(String groupId, String suggestionId, User leader) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Group group = groupDAO.loadGroup(groupId);
            if (group == null) {
                throw new IllegalArgumentException("Group not found");
            }

            if (!group.isLeader(leader)) {
                throw new IllegalStateException("Only leader can approve suggestions");
            }

            group.approveSuggestion(suggestionId, leader);

            //new
            PlaceSuggestion suggestion = null;
            for (PlaceSuggestion s : group.getAllSuggestions()) {
                if (s.getSuggestionId().equals(suggestionId)) {
                    suggestion = s;
                    break;
                }
            }

            if (suggestion != null && suggestion.getSuggester() != null) {
                Notification notif = new Notification(
                        suggestion.getSuggester(),
                        NotificationType.VOTE_STARTED,
                        "Suggestion Approved",
                        "Your suggestion for " + suggestion.getPlace().getName() + " has been approved for voting!",
                        groupId
                );
                new NotificationDatabaseObject().save(notif);
            }

            //till this
            String updateSql = "UPDATE messages SET status = ?, reviewed_by = ?, reviewed_at = ? WHERE message_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, MessageStatus.APPROVED.name());
                pstmt.setString(2, leader.getUserId());
                pstmt.setString(3, LocalDateTime.now().toString());
                pstmt.setString(4, suggestionId);
                pstmt.executeUpdate();
            }

            saveGroupWithConnection(group, conn);

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to approve suggestion", e);
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

    private void saveGroupWithConnection(Group group, Connection conn) throws SQLException {
        String sql = """
            UPDATE groups SET 
                name = ?, description = ?, leader_id = ?, destination_name = ?, 
                destination_division = ?, destination_image_path = ?, min_budget = ?, 
                max_budget = ?, max_members = ?, start_date = ?, end_date = ?, 
                status = ?, invitation_code = ?, voting_start_time = ?, voting_end_time = ?
            WHERE group_id = ?
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, group.getName());
            pstmt.setString(2, group.getDescription());
            pstmt.setString(3, group.getLeader().getId());
            pstmt.setString(4, group.getDestinationName());
            pstmt.setString(5, group.getDestinationDivision());
            pstmt.setString(6, group.getDestinationImagePath());

            if (group.getBudgetRange() != null) {
                pstmt.setInt(7, group.getBudgetRange().getMinPerPerson());
                pstmt.setInt(8, group.getBudgetRange().getMaxPerPerson());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
                pstmt.setNull(8, java.sql.Types.INTEGER);
            }

            pstmt.setInt(9, group.getMaxMembers());
            pstmt.setString(10, group.getStartDate() != null ? group.getStartDate().toString() : null);
            pstmt.setString(11, group.getEndDate() != null ? group.getEndDate().toString() : null);
            pstmt.setString(12, group.getStatus().name());
            pstmt.setString(13, group.getInvitationCode());
            pstmt.setString(14, group.getVotingStartTime() != null ? group.getVotingStartTime().toString() : null);
            pstmt.setString(15, group.getVotingEndTime() != null ? group.getVotingEndTime().toString() : null);
            pstmt.setString(16, group.getId());

            pstmt.executeUpdate();
        }
    }

    public void rejectSuggestion(String groupId, String suggestionId, User leader) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        if (!group.isLeader(leader)) {
            throw new IllegalStateException("Only leader can reject suggestions");
        }

        group.rejectSuggestion(suggestionId, leader);

        messageRepo.updateStatus(suggestionId, MessageStatus.REJECTED, leader.getUserId());

        groupDAO.saveGroup(group);
    }

    public void startVoting(String groupId, User leader) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        group.startVoting(leader);

        //from this
        for (GroupMember member : group.getAllMembers()) {
            if (member instanceof RegisteredMember) {
                User memberUser = ((RegisteredMember) member).getUser();
                if (!memberUser.getUserId().equals(leader.getUserId())) { // Don't notify leader
                    Notification notif = new Notification(
                            memberUser,
                            NotificationType.VOTE_STARTED,
                            "Voting Started",
                            "Voting has started for " + group.getName() + "! Cast your vote now.",
                            groupId
                    );
                    new NotificationDatabaseObject().save(notif);
                }
            }
        }

        //till this
        groupDAO.saveGroup(group);
    }

    public void castVote(String groupId, String suggestionId, User voter, boolean isApprove) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT suggestion_id FROM suggestion_votes WHERE voter_id = ? AND suggestion_id IN (SELECT message_id FROM messages WHERE group_id = ? AND message_type = 'SUGGESTION')";
            String oldSuggestionId = null;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, voter.getUserId());
                checkStmt.setString(2, groupId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    oldSuggestionId = rs.getString("suggestion_id");
                }
            }

            if (oldSuggestionId != null && oldSuggestionId.equals(suggestionId)) {
                conn.rollback();
                throw new IllegalStateException("You have already voted for this place");
            }

            if (oldSuggestionId != null) {
                String deleteSql = "DELETE FROM suggestion_votes WHERE suggestion_id = ? AND voter_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, oldSuggestionId);
                    deleteStmt.setString(2, voter.getUserId());
                    deleteStmt.executeUpdate();
                }
            }

            String insertSql = "INSERT INTO suggestion_votes (vote_id, suggestion_id, voter_id, vote_type) VALUES (?, ?, ?, ?)";
            String voteId = "VOTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, voteId);
                insertStmt.setString(2, suggestionId);
                insertStmt.setString(3, voter.getUserId());
                insertStmt.setString(4, isApprove ? "YES" : "NO");
                insertStmt.executeUpdate();
            }

            updateAllSuggestionVotes(conn, groupId);

            conn.commit();

            group = groupDAO.loadGroup(groupId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to save vote", e);
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

    public void removeVote(String groupId, String suggestionId, User voter) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String deleteSql = "DELETE FROM suggestion_votes WHERE suggestion_id = ? AND voter_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, suggestionId);
                deleteStmt.setString(2, voter.getUserId());
                deleteStmt.executeUpdate();
            }

            updateAllSuggestionVotes(conn, groupId);

            conn.commit();

            group.removeVote(suggestionId, voter);
            groupDAO.saveGroup(group);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to remove vote", e);
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

    private void updateAllSuggestionVotes(Connection conn, String groupId) throws SQLException {
        String suggestionsSql = "SELECT message_id FROM messages WHERE group_id = ? AND message_type = 'SUGGESTION'";

        try (PreparedStatement suggestStmt = conn.prepareStatement(suggestionsSql)) {
            suggestStmt.setString(1, groupId);
            ResultSet rs = suggestStmt.executeQuery();

            while (rs.next()) {
                String suggestionId = rs.getString("message_id");

                String countYesSql = "SELECT COUNT(*) FROM suggestion_votes WHERE suggestion_id = ? AND vote_type = 'YES'";
                int yesCount = 0;
                try (PreparedStatement countStmt = conn.prepareStatement(countYesSql)) {
                    countStmt.setString(1, suggestionId);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        yesCount = countRs.getInt(1);
                    }
                }

                String countNoSql = "SELECT COUNT(*) FROM suggestion_votes WHERE suggestion_id = ? AND vote_type = 'NO'";
                int noCount = 0;
                try (PreparedStatement countStmt = conn.prepareStatement(countNoSql)) {
                    countStmt.setString(1, suggestionId);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        noCount = countRs.getInt(1);
                    }
                }

                String updateSql = "UPDATE messages SET upvotes = ?, downvotes = ? WHERE message_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, yesCount);
                    updateStmt.setInt(2, noCount);
                    updateStmt.setString(3, suggestionId);
                    updateStmt.executeUpdate();
                }
            }
        }
    }

    public void castVote(String groupId, String suggestionId, User voter) {
        castVote(groupId, suggestionId, voter, true);
    }

    public void endVoting(String groupId, User leader) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        group.endVoting(leader);
        groupDAO.saveGroup(group);
    }

    public void confirmDestination(String groupId, String suggestionId, User leader) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        group.confirmDestination(suggestionId, leader);

        //new
        for (GroupMember member : group.getAllMembers()) {
            if (member instanceof RegisteredMember) {
                User memberUser = ((RegisteredMember) member).getUser();
                if (!memberUser.getUserId().equals(leader.getUserId())) { // Don't notify leader
                    Notification notif = new Notification(
                            memberUser,
                            NotificationType.DESTINATION_CONFIRMED,
                            "Destination Confirmed",
                            "Destination confirmed for " + group.getName() + ": " + group.getDestinationName(),
                            groupId
                    );
                    new NotificationDatabaseObject().save(notif);
                }
            }
        }

        //tiillt his
        groupDAO.saveGroup(group);
    }

    public List<Group> getMyGroups() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in");
        }
        return groupDAO.findByUser(currentUser.getUserId());
    }

    public List<Group> getActiveGroups(User user) {
        List<Group> allGroups = groupDAO.findByUser(user.getUserId());
        List<Group> activeGroups = new ArrayList<>();
        for (Group g : allGroups) {
            if (g.getStatus() == GroupStatus.PLANNING || g.getStatus() == GroupStatus.VOTING) {
                activeGroups.add(g);
            }
        }
        return activeGroups;
    }

    public Group getCurrentActiveGroup(User user) {
        List<Group> userGroups = groupDAO.findByUser(user.getUserId());
        Group current = null;
        LocalDate latestDate = null;

        for (Group g : userGroups) {
            if (g.getStatus() == GroupStatus.PLANNING || g.getStatus() == GroupStatus.VOTING) {
                if (g.getStartDate() != null) {
                    if (latestDate == null || g.getStartDate().isAfter(latestDate)) {
                        latestDate = g.getStartDate();
                        current = g;
                    }
                } else if (current == null) {
                    current = g;
                }
            }
        }
        return current;
    }

    public List<LocalDate> getConflictingDates(User user, LocalDate startDate, LocalDate endDate) {
        List<Group> userGroups = groupDAO.findByUser(user.getUserId());
        List<LocalDate> conflicts = new ArrayList<>();

        for (Group group : userGroups) {
            if (group.getStatus() == GroupStatus.PLANNING
                    || group.getStatus() == GroupStatus.VOTING) {

                LocalDate groupStart = group.getStartDate();
                LocalDate groupEnd = group.getEndDate();

                if (groupStart == null || groupEnd == null) {
                    continue;
                }

                if (!(endDate.isBefore(groupStart) || startDate.isAfter(groupEnd))) {
                    LocalDate date = startDate.isBefore(groupStart) ? groupStart : startDate;
                    LocalDate end = endDate.isAfter(groupEnd) ? groupEnd : endDate;

                    while (!date.isAfter(end)) {
                        conflicts.add(date);
                        date = date.plusDays(1);
                    }
                }
            }
        }
        return conflicts;
    }

    public boolean canUserCreateGroup(User user, LocalDate startDate, LocalDate endDate) {
        List<Group> userGroups = groupDAO.findByUser(user.getUserId());

        for (Group group : userGroups) {
            if (group.getStatus() == GroupStatus.PLANNING
                    || group.getStatus() == GroupStatus.VOTING) {

                if (group.getStartDate() == null || group.getEndDate() == null) {
                    continue;
                }

                if (!(endDate.isBefore(group.getStartDate())
                        || startDate.isAfter(group.getEndDate()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<User> searchUsersByEmail(String email) {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }

        return userDAO.searchUsers(email, currentUser.getUserId());
    }

    public List<User> getFriends(User user) {
        return user.getFriends();
    }

    public void leaveGroup(String groupId, User user) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        if (group.isLeader(user)) {
            throw new IllegalStateException("Leader cannot leave group. Transfer leadership first or delete group.");
        }

        group.removeMember(user.getUserId());
        groupDAO.saveGroup(group);
    }

    public void deleteSuggestion(String groupId, String suggestionId, User user) {
        Group group = groupDAO.loadGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        PlaceSuggestion suggestion = null;
        for (PlaceSuggestion s : group.getAllSuggestions()) {
            if (s.getSuggestionId().equals(suggestionId)) {
                suggestion = s;
                break;
            }
        }

        if (suggestion == null) {
            throw new IllegalArgumentException("Suggestion not found");
        }

        if (!suggestion.getSuggester().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("You can only delete your own suggestions");
        }

        if (suggestion.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalStateException("Can only delete pending suggestions");
        }

        List<PlaceSuggestion> updatedSuggestions = new ArrayList<>();
        for (PlaceSuggestion s : group.getAllSuggestions()) {
            if (!s.getSuggestionId().equals(suggestionId)) {
                updatedSuggestions.add(s);
            }
        }

        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, suggestionId);
            int deleted = pstmt.executeUpdate();
            System.out.println("Deleted suggestion from messages table: " + suggestionId + " - Rows affected: " + deleted);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete suggestion from database", e);
        }

        groupDAO.saveGroup(group);

        System.out.println("Suggestion deleted successfully");
    }
}
