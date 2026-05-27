package com.ExploBD.presentation.frames.module2.data;

import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.domain.members.GuestMember;
import com.ExploBD.service.GroupService;
import com.ExploBD.service.InvitationService;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.NotificationDatabaseObject;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import com.ExploBD.presentation.components.PlaceInfoDialog;
import com.ExploBD.presentation.components.ViewRequestsDialog;
import com.ExploBD.presentation.frames.EditUserProfile;
import com.ExploBD.presentation.frames.module1.DivisionExploreFrame;
import com.ExploBD.session.UserSession;
import com.ExploBD.util.ProfilePhotoUtils;
import com.ExploBD.util.RefreshManager;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class GroupDataManager {

    private User currentUser;
    private GroupService groupService;
    private InvitationService invitationService;
    private GroupDatabaseObject groupDAO;
    private InvitationDatabaseObject invitationDAO;
    private PlaceDatabaseObject placeDAO;
    private ProfilePhotoUtils photoUtils;

    private List<Group> allUserGroups;
    private List<Invitation> pendingInvitations;
    private Group selectedGroup;
    private Group currentActiveGroup;
    private List<PlaceSuggestion> currentSuggestions;

    private List<Runnable> listeners = new ArrayList<>();

    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;

    private Color BROWN_BG = new Color(88, 74, 60);
    private Color ORANGE = new Color(255, 153, 51);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color GOLD = new Color(255, 215, 0);
    private Color CREAM = new Color(250, 245, 240);
    private Color LIGHT_RED = new Color(255, 235, 230);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color EXPIRED_GRAY = new Color(200, 200, 200);
    private Color ACTIVE_GREEN = new Color(46, 125, 50);
    private Color ACTIVE_LIGHT_GREEN = new Color(235, 255, 235);

    private DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM");

    public GroupDataManager(User currentUser) {
        this.currentUser = currentUser;
        this.groupService = new GroupService();
        this.invitationService = new InvitationService();
        this.groupDAO = new GroupDatabaseObject();
        this.invitationDAO = new InvitationDatabaseObject();
        this.placeDAO = new PlaceDatabaseObject();
        this.photoUtils = new ProfilePhotoUtils();

        this.allUserGroups = new ArrayList<>();
        this.pendingInvitations = new ArrayList<>();
        this.currentSuggestions = new ArrayList<>();
    }

    public void setSuggestFields(JTextField placeField, JTextField reasonField, JButton submitBtn) {
        this.suggestPlaceField = placeField;
        this.suggestReasonField = reasonField;
        this.suggestSubmitBtn = submitBtn;
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    public void loadData() {
        System.out.println("=== GroupDataManager.loadData() ===");

        // Remember which group was selected before (if any)
        String previousSelectedId = (selectedGroup != null) ? selectedGroup.getId() : null;

        List<Group> loadedGroups = groupService.getMyGroups();
        if (loadedGroups != null) {
            allUserGroups = loadedGroups;
        }

        cleanupPastGroups();

        loadedGroups = groupService.getMyGroups();
        if (loadedGroups != null) {
            allUserGroups = loadedGroups;
        }

        // Find the most recent group by start date
        findCurrentActiveGroup();

        // Try to restore the previously selected group
        if (previousSelectedId != null) {
            selectedGroup = null;
            for (Group g : allUserGroups) {
                if (g.getId().equals(previousSelectedId)) {
                    selectedGroup = g;
                    System.out.println("Restored previously selected group: " + selectedGroup.getName());
                    break;
                }
            }
        }

        // If no previous selection or it was deleted, use the most recent group
        if (selectedGroup == null) {
            if (currentActiveGroup != null) {
                selectedGroup = currentActiveGroup;
                System.out.println("Selected most recent group: " + selectedGroup.getName());
            } else if (!allUserGroups.isEmpty()) {
                selectedGroup = allUserGroups.get(0);
                System.out.println("Selected first group: " + selectedGroup.getName());
            } else {
                selectedGroup = null;
                System.out.println("No groups found");
            }
        }

        loadInvitations();

        if (selectedGroup != null) {
            selectedGroup = groupDAO.loadGroup(selectedGroup.getId());
            currentSuggestions = selectedGroup.getAllSuggestions();
        }

        System.out.println("Loaded " + allUserGroups.size() + " groups");
        notifyListeners();
    }

    public void refreshData() {
        String previousGroupId = (selectedGroup != null) ? selectedGroup.getId() : null;

        List<Group> loadedGroups = groupDAO.findByUser(currentUser.getUserId());
        if (loadedGroups != null) {
            allUserGroups = loadedGroups;
        }

        findCurrentActiveGroup();

        if (previousGroupId != null) {
            selectedGroup = null;
            for (Group g : allUserGroups) {
                if (g.getId().equals(previousGroupId)) {
                    selectedGroup = g;
                    break;
                }
            }
        }

        if (selectedGroup == null) {
            if (currentActiveGroup != null) {
                selectedGroup = currentActiveGroup;
            } else if (!allUserGroups.isEmpty()) {
                selectedGroup = allUserGroups.get(0);
            }
        }

        loadInvitations();

        if (selectedGroup != null) {
            selectedGroup = groupDAO.loadGroup(selectedGroup.getId());
            currentSuggestions = selectedGroup.getAllSuggestions();
        }

        notifyListeners();
    }

    private void cleanupPastGroups() {
        LocalDate today = LocalDate.now();
        List<Group> toRemove = new ArrayList<>();

        for (Group group : allUserGroups) {
            if (group.getEndDate() != null
                    && group.getEndDate().isBefore(today)
                    && (group.getStatus() == GroupStatus.PLANNING
                    || group.getStatus() == GroupStatus.VOTING)) {

                try {
                    groupDAO.deleteGroup(group.getId());
                    toRemove.add(group);
                    System.out.println("Auto-deleted past group: " + group.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        allUserGroups.removeAll(toRemove);
    }

    private void loadInvitations() {
        List<Invitation> allInvitations = invitationDAO.findPendingForUser(
                currentUser.getEmail(), currentUser.getUsername());

        pendingInvitations = new ArrayList<>();
        if (allInvitations != null) {
            for (Invitation inv : allInvitations) {
                if (isInvitationExpired(inv)) {
                    try {
                        invitationDAO.updateStatus(inv.getInvitationId(), InvitationStatus.DECLINED);
                        System.out.println("Auto-deleted expired invitation: " + inv.getInvitationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    pendingInvitations.add(inv);
                }
            }
        }
    }

    public boolean isInvitationExpired(Invitation inv) {
        if (inv.getExpiryDate() == null) {
            return false;
        }
        return LocalDate.now().isAfter(inv.getExpiryDate());
    }

    public void setSelectedGroup(Group group) {
        this.selectedGroup = group;
        if (group != null) {
            this.selectedGroup = groupDAO.loadGroup(group.getId());
            this.currentSuggestions = selectedGroup.getAllSuggestions();
        }
        notifyListeners();
    }

    public List<Group> getAllUserGroups() {
        return allUserGroups != null ? allUserGroups : new ArrayList<>();
    }

    public List<Invitation> getPendingInvitations() {
        return pendingInvitations != null ? pendingInvitations : new ArrayList<>();
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public Group getCurrentActiveGroup() {
        return currentActiveGroup;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<PlaceSuggestion> getCurrentSuggestions() {
        return currentSuggestions;
    }

    public List<Group> getOtherGroups() {
        List<Group> otherGroups = new ArrayList<>();
        if (allUserGroups == null) {
            return otherGroups;
        }

        for (Group g : allUserGroups) {
            if (selectedGroup == null || !g.getId().equals(selectedGroup.getId())) {
                otherGroups.add(g);
            }
        }
        return otherGroups;
    }

    public boolean isUserInAnyActiveGroup() {
        for (Group g : allUserGroups) {
            if (g.getStatus() == GroupStatus.PLANNING || g.getStatus() == GroupStatus.VOTING) {
                return true;
            }
        }
        return false;
    }

    public void acceptInvitation(Invitation invitation) {
        if (!checkProfileComplete()) {
            return;
        }
        if (isInvitationExpired(invitation)) {
            JOptionPane.showMessageDialog(null,
                    "This invitation has expired.",
                    "Expired Invitation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Group invitedGroup = invitation.getGroup();
        LocalDate today = LocalDate.now();

        if (checkIfInGroup(invitedGroup, currentUser.getEmail())) {
            JOptionPane.showMessageDialog(null,
                    "You are already a member of this group!",
                    "Already Member",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (invitedGroup.getStartDate() != null && invitedGroup.getStartDate().isBefore(today)) {
            JOptionPane.showMessageDialog(null,
                    "This group's start date (" + invitedGroup.getStartDate() + ") has already passed.\n"
                    + "You cannot join past trips.",
                    "Past Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate newStart = invitedGroup.getStartDate();
        LocalDate newEnd = invitedGroup.getEndDate();

        if (newStart != null && newEnd != null) {
            boolean hasDateConflict = false;
            String conflictingGroup = "";

            for (Group g : allUserGroups) {
                if (!g.getId().equals(invitedGroup.getId())
                        && g.getStartDate() != null && g.getEndDate() != null) {

                    if (!(newEnd.isBefore(g.getStartDate()) || newStart.isAfter(g.getEndDate()))) {
                        hasDateConflict = true;
                        conflictingGroup = g.getName() + " (" + g.getStartDate() + " to " + g.getEndDate() + ")";
                        break;
                    }
                }
            }

            if (hasDateConflict) {
                JOptionPane.showMessageDialog(null,
                        "This group's dates (" + newStart + " to " + newEnd + ") conflict with\n"
                        + "another group: " + conflictingGroup + "\n\n"
                        + "You cannot be in two groups with overlapping dates.",
                        "Date Conflict",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Accept invitation to join " + invitedGroup.getName() + "?\n\n"
                + "Dates: " + invitedGroup.getStartDate() + " to " + invitedGroup.getEndDate(),
                "Accept Invitation",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String updateInvSql = "UPDATE invitations SET status = ? WHERE invitation_id = ?";
            pstmt1 = conn.prepareStatement(updateInvSql);
            pstmt1.setString(1, InvitationStatus.ACCEPTED.name());
            pstmt1.setString(2, invitation.getInvitationId());
            pstmt1.executeUpdate();

            String insertSql = "INSERT INTO group_members (group_id, member_id, member_type, role, joined_at, invited_by) VALUES (?, ?, 'REGISTERED', 'MEMBER', ?, ?)";
            pstmt2 = conn.prepareStatement(insertSql);
            pstmt2.setString(1, invitedGroup.getId());
            pstmt2.setString(2, currentUser.getUserId());
            pstmt2.setString(3, new Timestamp(System.currentTimeMillis()).toString());
            pstmt2.setString(4, invitation.getInviter().getUserId());
            pstmt2.executeUpdate();

            conn.commit();

// ===== ADD NOTIFICATION FOR INVITER =====
            try {
                User inviter = invitation.getInviter();
                if (inviter != null && !inviter.getUserId().equals(currentUser.getUserId())) {
                    Notification notif = new Notification(
                            inviter,
                            NotificationType.MEMBER_JOINED,
                            "Member Joined",
                            currentUser.getDisplayName() + " accepted your invitation to " + invitedGroup.getName(),
                            invitedGroup.getId()
                    );
                    new NotificationDatabaseObject().save(notif);
                }
            } catch (Exception notifEx) {
                System.out.println("Failed to send notification: " + notifEx.getMessage());
            }
// ======================================

            JOptionPane.showMessageDialog(null,
                    "✓ You joined " + invitedGroup.getName() + "!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshData();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
                if (pstmt2 != null) {
                    pstmt2.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void declineInvitation(Invitation invitation) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Decline invitation from " + invitation.getInviter().getDisplayName() + "?",
                "Decline Invitation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE invitations SET status = ? WHERE invitation_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, InvitationStatus.DECLINED.name());
            pstmt.setString(2, invitation.getInvitationId());
            pstmt.executeUpdate();
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public void startVoting() {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.startVoting(selectedGroup.getId(), currentUser);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Voting started!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void endVoting() {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.endVoting(selectedGroup.getId(), currentUser);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null,
                    "Voting ended! We are now ready to go.",
                    "Voting Ended",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void confirmWinner(String suggestionId) {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.confirmDestination(selectedGroup.getId(), suggestionId, currentUser);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Destination confirmed!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void disbandGroup() {
        if (selectedGroup == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "⚠️ PERMANENT ACTION ⚠️\n\n"
                + "Are you sure you want to DELETE this group?\n\n"
                + "This will remove it for ALL members permanently!",
                "Disband Group",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String typeConfirm = JOptionPane.showInputDialog(null,
                "Type 'DELETE' to confirm permanent deletion:",
                "Final Confirmation",
                JOptionPane.WARNING_MESSAGE);

        if (typeConfirm == null || !typeConfirm.equals("DELETE")) {
            return;
        }

        try {
            JOptionPane.showMessageDialog(null,
                    "Deleting group...",
                    "Please wait",
                    JOptionPane.INFORMATION_MESSAGE);

            boolean deleted = groupService.disbandGroup(selectedGroup.getId(), currentUser);

            if (deleted) {
                JOptionPane.showMessageDialog(null,
                        "✓ Group disbanded successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                refreshData();

                if (allUserGroups.isEmpty()) {
                    ((JFrame) null).dispose();
                } else {
                    selectedGroup = allUserGroups.get(0);
                }

                UserSession.getInstance().loadUserData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error disbanding group: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void finalizeGroup() {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.finalizeGroup(selectedGroup.getId(), currentUser);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Trip finalized successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void leaveGroup() {
        if (selectedGroup == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Leave " + selectedGroup.getName() + "?",
                "Leave Group", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            selectedGroup.removeMember(currentUser.getUserId());
            groupDAO.saveGroup(selectedGroup);
            refreshData();
            JOptionPane.showMessageDialog(null, "You left the group.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void approveSuggestion(String suggestionId) {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.approveSuggestion(selectedGroup.getId(), suggestionId, currentUser);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Suggestion approved!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void rejectSuggestion(String suggestionId) {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.rejectSuggestion(selectedGroup.getId(), suggestionId, currentUser);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void castVote(String suggestionId, boolean approve) {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.castVote(selectedGroup.getId(), suggestionId, currentUser, approve);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Vote recorded!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void removeVote(String suggestionId) {
        if (selectedGroup == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Remove your vote?", "Remove Vote", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            groupService.removeVote(selectedGroup.getId(), suggestionId, currentUser);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Vote removed!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void cancelSuggestion(String suggestionId) {
        if (selectedGroup == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to cancel your suggestion?",
                "Cancel Suggestion",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            groupService.deleteSuggestion(selectedGroup.getId(), suggestionId, currentUser);
            JOptionPane.showMessageDialog(null,
                    "Your suggestion has been cancelled!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateGroupSettings(int maxMembers, LocalDate startDate, LocalDate endDate) {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.updateGroupSettings(selectedGroup.getId(), currentUser, maxMembers, startDate, endDate);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Group updated successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public List<User> searchUsersByEmail(String email) {
        return groupService.searchUsersByEmail(email);
    }

    public void createInvitationRequest(String name, String email, String type) {
        if (selectedGroup == null) {
            return;
        }
        try {
            groupService.createInvitationRequest(selectedGroup.getId(), currentUser, name, email, type);
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendInvitation(String email, boolean isGuest) {
        if (selectedGroup == null) {
            return;
        }
        try {
            invitationService.sendInvitation(selectedGroup.getId(), currentUser.getUserId(), email, isGuest);
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simpleRefresh() {
        System.out.println("=== Simple Refresh ===");

        String previousId = null;
        if (selectedGroup != null) {
            previousId = selectedGroup.getId();
        }

        allUserGroups = groupDAO.findByUser(currentUser.getUserId());

        findCurrentActiveGroup();

        if (previousId != null) {
            selectedGroup = null;
            for (int i = 0; i < allUserGroups.size(); i++) {
                Group g = allUserGroups.get(i);
                if (g.getId().equals(previousId)) {
                    selectedGroup = g;
                    System.out.println("Restored previous group: " + selectedGroup.getName());
                    break;
                }
            }
        }

        if (selectedGroup == null) {
            if (currentActiveGroup != null) {
                selectedGroup = currentActiveGroup;
            } else if (allUserGroups.size() > 0) {
                selectedGroup = allUserGroups.get(0);
            }
        }

        loadInvitations();

        if (selectedGroup != null) {
            selectedGroup = groupDAO.loadGroup(selectedGroup.getId());
            currentSuggestions = selectedGroup.getAllSuggestions();
        }

        notifyListeners();
    }

    public void suggestPlace(Place place, String reason) {
        if (selectedGroup == null || place == null) {
            return;
        }
        try {
            groupService.suggestPlace(selectedGroup.getId(), currentUser, place.getId(), reason);
            selectedGroup = groupService.getGroup(selectedGroup.getId());
            refreshData();
            JOptionPane.showMessageDialog(null, "Suggestion submitted!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public Place getDestinationPlace() {
        if (selectedGroup == null) {
            System.out.println("No selected group");
            return null;
        }

        String destName = selectedGroup.getDestinationName();
        if (destName == null || destName.isEmpty()) {
            System.out.println("No destination name set");
            return null;
        }

        System.out.println("Looking for destination: " + destName);

        List<PlaceSuggestion> suggestions = selectedGroup.getAllSuggestions();
        for (int i = 0; i < suggestions.size(); i++) {
            PlaceSuggestion s = suggestions.get(i);
            Place place = s.getPlace();
            if (place != null) {
                String placeName = place.getName();
                if (placeName != null && placeName.equalsIgnoreCase(destName)) {
                    System.out.println("Found place in suggestions: " + placeName);
                    return place;
                }
            }
        }

        String division = selectedGroup.getDestinationDivision();
        if (division != null && !division.isEmpty()) {
            List<Place> places = placeDAO.findAllByDivision(division);
            for (int i = 0; i < places.size(); i++) {
                Place place = places.get(i);
                String placeName = place.getName();
                if (placeName != null && placeName.equalsIgnoreCase(destName)) {
                    System.out.println("Found place in database: " + placeName);
                    return place;
                }
            }
        }

        System.out.println("Could not find place: " + destName);
        return null;
    }

    public boolean canSuggest() {
        if (selectedGroup == null) {
            return false;
        }

        if (selectedGroup.getStatus() == GroupStatus.VOTING) {
            JOptionPane.showMessageDialog(null,
                    "Voting has already started. No more suggestions allowed.",
                    "Cannot Suggest",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (selectedGroup.getStatus() == GroupStatus.CONFIRMED) {
            JOptionPane.showMessageDialog(null,
                    "This trip is already confirmed. Cannot add new suggestions.",
                    "Cannot Suggest",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public Place findPlaceWithImages(String placeName, String division) {
        if (placeName == null || placeName.isEmpty()) {
            return null;
        }

        System.out.println("Searching for place: " + placeName + " in division: " + division);

        List<Place> placesInDivision = placeDAO.findAllByDivision(division);

        for (Place place : placesInDivision) {
            if (place.getName().equalsIgnoreCase(placeName)) {
                System.out.println("Found place in database: " + place.getName());
                return place;
            }
        }

        for (Place place : placesInDivision) {
            if (place.getName().toLowerCase().contains(placeName.toLowerCase())
                    || placeName.toLowerCase().contains(place.getName().toLowerCase())) {
                System.out.println("Found place by partial match: " + place.getName());
                return place;
            }
        }

        System.out.println("Could not find place: " + placeName);
        return null;
    }

    public boolean checkIfInGroup(String email) {
        if (selectedGroup == null) {
            return false;
        }
        for (GroupMember member : selectedGroup.getAllMembers()) {
            if (member instanceof RegisteredMember) {
                User memberUser = ((RegisteredMember) member).getUser();
                if (memberUser.getEmail().equalsIgnoreCase(email)) {
                    return true;
                }
            } else if (member instanceof GuestMember) {
                GuestMember guest = (GuestMember) member;
                if (guest.getEmail() != null && guest.getEmail().equalsIgnoreCase(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkIfAlreadyInvited(String email) {
        if (checkIfInGroup(email)) {
            return true;
        }

        try {
            List<Invitation> pending = invitationDAO.findPendingByEmail(email);
            for (Invitation inv : pending) {
                if (inv.getGroup().getId().equals(selectedGroup.getId())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkIfAlreadyRequested(String email) {
        return false;
    }

    private boolean checkIfInGroup(Group group, String email) {
        for (GroupMember member : group.getAllMembers()) {
            if (member instanceof RegisteredMember) {
                User memberUser = ((RegisteredMember) member).getUser();
                if (memberUser.getEmail().equalsIgnoreCase(email)) {
                    return true;
                }
            } else if (member instanceof GuestMember) {
                GuestMember guest = (GuestMember) member;
                if (guest.getEmail() != null && guest.getEmail().equalsIgnoreCase(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showEditGroupDialog(JFrame parent) {
        if (selectedGroup == null) {
            return;
        }

        JDialog dialog = new JDialog(parent, "Edit Group Settings", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SOFT_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("EDIT GROUP: " + selectedGroup.getName());
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        titleLabel.setForeground(DARK_BROWN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        JPanel membersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        membersPanel.setBackground(SOFT_WHITE);

        JLabel membersLabel = new JLabel("Max Members:");
        membersLabel.setFont(new Font("Arial", Font.BOLD, 12));
        membersLabel.setPreferredSize(new Dimension(100, 25));

        JSpinner membersSpinner = new JSpinner(new SpinnerNumberModel(
                selectedGroup.getMaxMembers(),
                selectedGroup.getMemberCount(),
                20, 1));
        membersSpinner.setPreferredSize(new Dimension(80, 25));

        membersPanel.add(membersLabel);
        membersPanel.add(membersSpinner);
        panel.add(membersPanel);
        panel.add(Box.createVerticalStrut(15));

        JLabel datesTitle = new JLabel("TRIP DATES");
        datesTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        datesTitle.setForeground(DARK_BROWN);
        datesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(datesTitle);
        panel.add(Box.createVerticalStrut(5));

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        startPanel.setBackground(SOFT_WHITE);

        JLabel startLabel = new JLabel("Start:");
        startLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        startLabel.setPreferredSize(new Dimension(40, 25));

        JTextField startField = new JTextField(10);
        if (selectedGroup.getStartDate() != null) {
            startField.setText(selectedGroup.getStartDate().toString());
        }
        startField.setToolTipText("YYYY-MM-DD");

        startPanel.add(startLabel);
        startPanel.add(startField);
        panel.add(startPanel);

        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        endPanel.setBackground(SOFT_WHITE);

        JLabel endLabel = new JLabel("End:");
        endLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        endLabel.setPreferredSize(new Dimension(40, 25));

        JTextField endField = new JTextField(10);
        if (selectedGroup.getEndDate() != null) {
            endField.setText(selectedGroup.getEndDate().toString());
        }
        endField.setToolTipText("YYYY-MM-DD");

        endPanel.add(endLabel);
        endPanel.add(endField);
        panel.add(endPanel);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(SOFT_WHITE);

        JButton saveBtn = new JButton("SAVE CHANGES");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        saveBtn.setBackground(SUCCESS_GREEN);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newMaxMembers = (int) membersSpinner.getValue();
                    LocalDate newStartDate = LocalDate.parse(startField.getText());
                    LocalDate newEndDate = LocalDate.parse(endField.getText());

                    updateGroupSettings(newMaxMembers, newStartDate, newEndDate);

                    JOptionPane.showMessageDialog(dialog, "Group updated successfully!");
                    dialog.dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }
        });

        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public void showMemberProfile(GroupMember member, JFrame parent) {
        JDialog dialog = new JDialog(parent, "Member Profile", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SOFT_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel photoLabel = photoUtils.createMemberPhotoLabel(member, 100);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 3));
        panel.add(photoLabel);
        panel.add(Box.createVerticalStrut(15));

        String displayName;
        String email = "";
        String phone = "Not shared";
        String gender = "Not shared";
        String nationality = "Not shared";
        String memberSince = "Unknown";
        int totalGroups = 0;

        if (member instanceof RegisteredMember) {
            User user = ((RegisteredMember) member).getUser();
            displayName = user.getDisplayName();
            email = user.getEmail();

            phone = user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Not shared";
            gender = user.getGender() != null && !user.getGender().isEmpty() && !user.getGender().equals("Select Gender")
                    ? user.getGender() : "Not shared";
            nationality = user.getNationality() != null && !user.getNationality().isEmpty()
                    ? user.getNationality() : "Not shared";

            if (user.getCreatedAt() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
                memberSince = sdf.format(user.getCreatedAt());
            }

            totalGroups = allUserGroups.size();

        } else {
            GuestMember guest = (GuestMember) member;
            displayName = guest.getDisplayName();
            email = guest.getEmail() != null ? guest.getEmail() : "No email";
        }

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 22));
        nameLabel.setForeground(DARK_BROWN);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));

        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        emailLabel.setForeground(Color.GRAY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(15));

        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 10, 8));
        infoGrid.setBackground(SOFT_WHITE);
        infoGrid.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        addInfoRow(infoGrid, "Member Since:", memberSince, DARK_BROWN);
        addInfoRow(infoGrid, "Nationality:", nationality, DARK_BROWN);
        addInfoRow(infoGrid, "Gender:", gender, DARK_BROWN);
        addInfoRow(infoGrid, "Phone:", phone, DARK_BROWN);
        addInfoRow(infoGrid, "Total Groups:", String.valueOf(totalGroups), DARK_BROWN);

        panel.add(infoGrid);
        panel.add(Box.createVerticalStrut(20));

        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setBackground(DARK_BROWN);
        closeBtn.setForeground(GOLD);
        closeBtn.setFocusPainted(false);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setBackground(ORANGE);
                closeBtn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setBackground(DARK_BROWN);
                closeBtn.setForeground(GOLD);
            }
        });

        panel.add(closeBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void addInfoRow(JPanel grid, String label, String value, Color valueColor) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        labelComp.setForeground(DARK_BROWN);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));
        valueComp.setForeground(valueColor);

        grid.add(labelComp);
        grid.add(valueComp);
    }

    public void showPlaceDetails(Place place, JFrame parent) {
        try {
            new PlaceInfoDialog(parent, place).setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Error showing place details: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showRequestsDialog(JFrame parent) {
        if (selectedGroup == null) {
            return;
        }
        ViewRequestsDialog dialog = new ViewRequestsDialog(parent, selectedGroup, currentUser, this::refreshData);
        dialog.setVisible(true);
    }

    public void showInviteUserDialog(JFrame parent) {
        showSearchDialog(parent, "Invite User", false);
    }

    public void showInviteGuestDialog(JFrame parent) {
        showGuestDialog(parent, "Invite Guest", false);
    }

    public void showRequestUserDialog(JFrame parent) {
        showSearchDialog(parent, "Request User", true);
    }

    public void showRequestGuestDialog(JFrame parent) {
        showGuestDialog(parent, "Request Guest", true);
    }

    public void showFriendDialog(JFrame parent) {
        JOptionPane.showMessageDialog(parent,
                "Friend list coming soon!\nYou will be able to invite friends directly.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showFriendRequestDialog(JFrame parent) {
        JOptionPane.showMessageDialog(parent,
                "Friend requests coming soon!\nYou will be able to request friends to join.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showInviteOptionsDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Invite to Group", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Choose invitation type:");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        title.setForeground(DARK_BROWN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        JButton userBtn = createDialogButton("Invite User", DARK_BROWN, GOLD);
        JButton guestBtn = createDialogButton("Invite Guest", new Color(100, 86, 72), Color.WHITE);
        JButton friendBtn = createDialogButton("Invite Friend", ORANGE, Color.WHITE);

        userBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInviteUserDialog(parent);
                dialog.dispose();
            }
        });

        guestBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInviteGuestDialog(parent);
                dialog.dispose();
            }
        });

        friendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFriendDialog(parent);
                dialog.dispose();
            }
        });

        panel.add(userBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(guestBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(friendBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JButton createDialogButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        return button;
    }

    private void showSearchDialog(JFrame parent, String title, boolean isRequest) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel searchLabel = new JLabel("Search by email or name:");
        searchLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        searchLabel.setForeground(DARK_BROWN);

        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 0));
        searchInputPanel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ORANGE),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 11));
        searchBtn.setBackground(DARK_BROWN);
        searchBtn.setForeground(GOLD);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        searchInputPanel.add(searchField, BorderLayout.CENTER);
        searchInputPanel.add(searchBtn, BorderLayout.EAST);

        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(searchInputPanel, BorderLayout.SOUTH);

        panel.add(searchPanel, BorderLayout.NORTH);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);

        JScrollPane resultsScroll = new JScrollPane(resultsPanel);
        resultsScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 140)),
                "SEARCH RESULTS",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, 11),
                DARK_BROWN
        ));

        panel.add(resultsScroll, BorderLayout.CENTER);

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                if (!searchTerm.isEmpty()) {
                    resultsPanel.removeAll();

                    List<User> results = searchUsersByEmail(searchTerm);

                    if (results.isEmpty()) {
                        JLabel notFoundLabel = new JLabel("No users found.");
                        notFoundLabel.setFont(new Font("Bookman Old Style", Font.ITALIC, 12));
                        notFoundLabel.setForeground(Color.GRAY);
                        notFoundLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
                        notFoundLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        resultsPanel.add(notFoundLabel);
                    } else {
                        for (User user : results) {
                            if (!user.getUserId().equals(currentUser.getUserId())) {
                                if (isRequest) {
                                    resultsPanel.add(createRequestUserResultCard(user, dialog));
                                } else {
                                    resultsPanel.add(createInviteUserResultCard(user, dialog));
                                }
                                resultsPanel.add(Box.createVerticalStrut(5));
                            }
                        }
                    }

                    resultsPanel.revalidate();
                    resultsPanel.repaint();
                }
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBtn.doClick();
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel createInviteUserResultCard(User user, JDialog dialog) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        card.setMaximumSize(new Dimension(380, 80));
        card.setPreferredSize(new Dimension(380, 80));

        JLabel photoLabel = photoUtils.createUserPhotoLabel(user, 45);
        photoLabel.setPreferredSize(new Dimension(45, 45));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName() : user.getUsername();

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(DARK_BROWN);

        JLabel emailLabel = new JLabel(user.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(emailLabel);

        boolean isSelf = user.getUserId().equals(currentUser.getUserId());
        boolean alreadyInGroup = checkIfInGroup(user.getEmail());
        boolean alreadyInvited = checkIfAlreadyInvited(user.getEmail());

        JButton actionBtn = new JButton();
        actionBtn.setFont(new Font("Arial", Font.BOLD, 12));
        actionBtn.setPreferredSize(new Dimension(110, 40));
        actionBtn.setFocusPainted(false);
        actionBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        if (isSelf) {
            actionBtn.setText("Yourself");
            actionBtn.setEnabled(false);
            actionBtn.setBackground(Color.GRAY);
            actionBtn.setForeground(Color.WHITE);
        } else if (alreadyInGroup) {
            actionBtn.setText("In Group");
            actionBtn.setEnabled(false);
            actionBtn.setBackground(Color.GRAY);
            actionBtn.setForeground(Color.WHITE);
        } else if (alreadyInvited) {
            actionBtn.setText("Invited");
            actionBtn.setEnabled(false);
            actionBtn.setBackground(Color.GRAY);
            actionBtn.setForeground(Color.WHITE);
        } else {
            actionBtn.setText("INVITE");
            actionBtn.setBackground(DARK_BROWN);
            actionBtn.setForeground(GOLD);
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        sendInvitation(user.getEmail(), false);
                        JOptionPane.showMessageDialog(dialog,
                                "Invitation sent to " + user.getEmail() + "!");
                        dialog.dispose();
                        refreshData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Error: " + ex.getMessage());
                    }
                }
            });
        }

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionBtn, BorderLayout.EAST);

        return card;
    }

    private JPanel createRequestUserResultCard(User user, JDialog dialog) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        card.setMaximumSize(new Dimension(380, 80));
        card.setPreferredSize(new Dimension(380, 80));

        JLabel photoLabel = photoUtils.createUserPhotoLabel(user, 45);
        photoLabel.setPreferredSize(new Dimension(45, 45));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName() : user.getUsername();

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(DARK_BROWN);

        JLabel emailLabel = new JLabel(user.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(emailLabel);

        boolean isSelf = user.getUserId().equals(currentUser.getUserId());
        boolean alreadyInGroup = checkIfInGroup(user.getEmail());
        boolean alreadyRequested = checkIfAlreadyRequested(user.getEmail());

        JButton actionBtn = new JButton();
        actionBtn.setFont(new Font("Arial", Font.BOLD, 12));
        actionBtn.setPreferredSize(new Dimension(110, 40));
        actionBtn.setFocusPainted(false);
        actionBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        if (isSelf) {
            actionBtn.setText("Yourself");
            actionBtn.setEnabled(false);
            actionBtn.setBackground(Color.GRAY);
            actionBtn.setForeground(Color.WHITE);
        } else if (alreadyInGroup) {
            actionBtn.setText("In Group");
            actionBtn.setEnabled(false);
            actionBtn.setBackground(Color.GRAY);
            actionBtn.setForeground(Color.WHITE);
        } else if (alreadyRequested) {
            actionBtn.setText("Requested");
            actionBtn.setEnabled(false);
            actionBtn.setBackground(Color.GRAY);
            actionBtn.setForeground(Color.WHITE);
        } else {
            actionBtn.setText("REQUEST");
            actionBtn.setBackground(ORANGE);
            actionBtn.setForeground(Color.WHITE);
            actionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        createInvitationRequest(
                                displayName,
                                user.getEmail(),
                                "USER"
                        );

                        JOptionPane.showMessageDialog(dialog,
                                "✓ Request sent to leader for approval!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        dialog.dispose();
                        refreshData();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Error: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionBtn, BorderLayout.EAST);

        return card;
    }

    private void showGuestDialog(JFrame parent, String title, boolean isRequest) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        nameLabel.setForeground(DARK_BROWN);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        emailLabel.setForeground(DARK_BROWN);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField emailField = new JTextField(15);
        emailField.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        panel.add(emailField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 5, 5, 5);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 12));
        submitBtn.setBackground(isRequest ? ORANGE : DARK_BROWN);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name and Email are required");
                    return;
                }

                try {
                    if (isRequest) {
                        createInvitationRequest(name, email, "GUEST");
                        JOptionPane.showMessageDialog(dialog,
                                "✓ Request sent to leader for approval!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        sendInvitation(email, true);
                        JOptionPane.showMessageDialog(dialog,
                                "✓ Invitation sent to " + email + "!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                    dialog.dispose();
                    refreshData();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public void browseDestination(JFrame parent, JTextField suggestPlaceField) {
        if (selectedGroup == null || selectedGroup.getDestinationDivision() == null) {
            JOptionPane.showMessageDialog(parent, "No destination division set for this group");
            return;
        }

        DivisionExploreFrame browser = new DivisionExploreFrame(
                currentUser,
                selectedGroup.getDestinationDivision(),
                true
        );

        browser.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Place selected = browser.getSelectedPlace();
                if (selected != null) {
                    boolean alreadySuggested = false;
                    for (PlaceSuggestion s : selectedGroup.getAllSuggestions()) {
                        if (s.getPlace().getId().equals(selected.getId())) {
                            alreadySuggested = true;
                            break;
                        }
                    }

                    if (alreadySuggested) {
                        JOptionPane.showMessageDialog(parent,
                                "This place has already been suggested!",
                                "Already Suggested",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (selected.getDivision().equals(selectedGroup.getDestinationDivision())) {
                        suggestPlaceField.setText(selected.getName());
                        suggestPlaceField.putClientProperty("selectedPlace", selected);
                        JOptionPane.showMessageDialog(parent,
                                "Selected: " + selected.getName() + "\nNow add a reason and click Submit!");
                    } else {
                        JOptionPane.showMessageDialog(parent,
                                "This place is not in " + selectedGroup.getDestinationDivision()
                                + " division. Please select a place in the same division.",
                                "Invalid Destination",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        browser.setVisible(true);
    }

    private void findCurrentActiveGroup() {
        currentActiveGroup = null;
        LocalDate latestDate = null;

        // Check ALL groups (any status) and find the one with the latest start date
        for (Group g : allUserGroups) {
            if (g.getStartDate() != null) {
                if (latestDate == null || g.getStartDate().isAfter(latestDate)) {
                    latestDate = g.getStartDate();
                    currentActiveGroup = g;
                }
            }
        }

        // If no group has a start date, use the most recently created
        if (currentActiveGroup == null && !allUserGroups.isEmpty()) {
            currentActiveGroup = allUserGroups.get(0);
            for (Group g : allUserGroups) {
                if (g.getCreatedAt() != null && currentActiveGroup.getCreatedAt() != null) {
                    if (g.getCreatedAt().after(currentActiveGroup.getCreatedAt())) {
                        currentActiveGroup = g;
                    }
                }
            }
        }

        if (currentActiveGroup != null) {
            System.out.println("GroupDataManager: Most recent group by start date: "
                    + currentActiveGroup.getName()
                    + " | Start: " + currentActiveGroup.getStartDate()
                    + " | Status: " + currentActiveGroup.getStatus());
        }
    }

    private boolean checkProfileComplete() {
        if (!currentUser.isProfileComplete()) {
            int response = JOptionPane.showConfirmDialog(null,
                    "Your profile is incomplete. Would you like to complete it now?\n\n"
                    + "You need to add your name and phone number to join groups.",
                    "Profile Incomplete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                EditUserProfile editProfile = new EditUserProfile(currentUser);
                editProfile.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        RefreshManager.getInstance().refreshAll();
                    }
                });
                editProfile.setVisible(true);
            }
            return false;
        }
        return true;
    }
}
