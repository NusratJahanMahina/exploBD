package com.ExploBD.presentation.frames;

import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.presentation.components.PlaceInfoDialog;
import com.ExploBD.service.FriendService;
import com.ExploBD.util.PlacePhotoPanel;
import com.ExploBD.util.ProfilePhotoUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PublicGroupCard extends JPanel {
    
    private Group group;
    private User currentUser;
    private PublicGroupsFrame parentFrame;
    private ProfilePhotoUtils photoUtils;
    private FriendService friendService;
    
    private Color BROWN_DARK = new Color(70, 58, 47);
    private Color BROWN_MEDIUM = new Color(88, 74, 60);
    private Color ORANGE = new Color(255, 153, 51);
    private Color ORANGE_LIGHT = new Color(255, 183, 77);
    private Color GOLD = new Color(255, 215, 0);
    private Color GREEN_SUCCESS = new Color(46, 125, 50);  // Added this
    private Color GREEN_LIGHT = new Color(76, 175, 80);
    private Color ORANGE_WARNING = new Color(255, 165, 0);
    private Color RED_ERROR = new Color(198, 40, 40);
    private Color GRAY_TEXT = new Color(80, 80, 80);
    private Color GRAY_LIGHT = new Color(150, 150, 150);
    private Color BORDER_LIGHT = new Color(200, 180, 160);
    private Color BG_WHITE = Color.WHITE;
    private Color BG_LIGHT = new Color(250, 245, 240);
    private Color BUTTON_BROWN = new Color(102, 85, 70); 
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    
    public PublicGroupCard(Group group, User currentUser, PublicGroupsFrame parentFrame) {
        this.group = group;
        this.currentUser = currentUser;
        this.parentFrame = parentFrame;
        this.photoUtils = new ProfilePhotoUtils();
        this.friendService = new FriendService(currentUser); 
        
        setLayout(new BorderLayout(20, 0)); 
        setBackground(BG_WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 160)); 
        setPreferredSize(new Dimension(800, 160));
        
        
        add(createImagePanel(), BorderLayout.WEST);
        
       
        add(createInfoPanel(), BorderLayout.CENTER);
        
        
        add(createButtonPanel(), BorderLayout.EAST);
    }
    
    private JPanel createImagePanel() {
        Place place = parentFrame.getGroupDestinationPlace(group);
        if (place != null) {
            return new PlacePhotoPanel(place, 180, 130); 
        }
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setPreferredSize(new Dimension(180, 130)); 
        panel.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT));
        
        JLabel noImageLabel = new JLabel("NO IMAGE", SwingConstants.CENTER);
        noImageLabel.setFont(new Font("Arial", Font.BOLD, 14)); 
        noImageLabel.setForeground(GRAY_LIGHT);
        panel.add(noImageLabel, BorderLayout.CENTER);
        
        return panel;
    }
  
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
        
        // Group name
        JLabel nameLabel = new JLabel(group.getName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        nameLabel.setForeground(BROWN_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        
        // Privacy badge
        String privacyText = group.isPublic() ? "PUBLIC" : "PRIVATE";
        Color badgeColor = group.isPublic() ? GREEN_SUCCESS : ORANGE_WARNING;
        JLabel badge = new JLabel(privacyText);
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBackground(badgeColor);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(badge);
        panel.add(Box.createVerticalStrut(8));
        
        // Destination
        String destText = group.getDestinationName() != null 
            ? group.getDestinationName() : "Destination not set";
        JLabel destLabel = new JLabel("Destination: " + destText);
        destLabel.setFont(new Font("Arial", Font.BOLD, 14));
        destLabel.setForeground(ORANGE);
        destLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(destLabel);
        panel.add(Box.createVerticalStrut(5));
        
        // Dates
        String dateText;
        if (group.getStartDate() != null && group.getEndDate() != null) {
            dateText = "Dates: " + group.getStartDate().format(dateFormatter) + 
                      " - " + group.getEndDate().format(dateFormatter);
        } else {
            dateText = "Dates: Not set";
        }
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(GRAY_TEXT);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dateLabel);
        panel.add(Box.createVerticalStrut(3));
        
        // Stats row (members, status)
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsRow.setBackground(BG_WHITE);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel memberLabel = new JLabel("Members: " + group.getMemberCount() + "/" + group.getMaxMembers());
        memberLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        memberLabel.setForeground(GRAY_TEXT);
        
        JLabel statusLabel = new JLabel("Status: " + group.getStatus().toString());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(getStatusColor(group.getStatus()));
        
        statsRow.add(memberLabel);
        statsRow.add(statusLabel);
        panel.add(statsRow);
        panel.add(Box.createVerticalStrut(8));
        
        // ADD FRIENDS IN GROUP SECTION
        List<User> friendsInGroup = getFriendsInGroup();
        if (!friendsInGroup.isEmpty()) {
            JPanel friendsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            friendsPanel.setBackground(BG_WHITE);
            friendsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel friendsIcon = new JLabel("👥");
            friendsIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            friendsIcon.setForeground(GREEN_SUCCESS);
            
            String friendsText = "Friends in this group: ";
            for (int i = 0; i < friendsInGroup.size(); i++) {
                if (i > 0) friendsText += ", ";
                friendsText += friendsInGroup.get(i).getDisplayName();
            }
            
            JLabel friendsLabel = new JLabel(friendsText);
            friendsLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            friendsLabel.setForeground(GREEN_SUCCESS);
            
            friendsPanel.add(friendsIcon);
            friendsPanel.add(friendsLabel);
            panel.add(friendsPanel);
            panel.add(Box.createVerticalStrut(5));
        }
        
        return panel;
    }
    
    private List<User> getFriendsInGroup() {
        List<User> friendsInGroup = new ArrayList<>();
        
        // Get all user's friends
        List<User> userFriends = friendService.getFriends();
        
        // Get all members of the group
        List<GroupMember> groupMembers = group.getAllMembers();
        
        // Check which friends are in the group
        for (User friend : userFriends) {
            for (GroupMember member : groupMembers) {
                if (member instanceof RegisteredMember) {
                    User memberUser = ((RegisteredMember) member).getUser();
                    if (memberUser.getUserId().equals(friend.getUserId())) {
                        friendsInGroup.add(friend);
                        break;
                    }
                }
            }
        }
        
        return friendsInGroup;
    }    
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        panel.setPreferredSize(new Dimension(150, 130));
        
        String status = parentFrame.getActionButtonStatus(group);
        
        JButton detailsBtn = createButton("View Details", BUTTON_BROWN, GOLD, 130, 32);
        detailsBtn.addActionListener(e -> showPlaceDetails());
        panel.add(detailsBtn);
        panel.add(Box.createVerticalStrut(6));
        
        JButton membersBtn = createButton("View Members", BUTTON_BROWN, GOLD, 130, 32);
        membersBtn.addActionListener(e -> showMembersDialog());
        panel.add(membersBtn);
        panel.add(Box.createVerticalStrut(6));
        
        JButton actionBtn = createActionButton(status);
        if (actionBtn != null) {
            panel.add(actionBtn);
        }
        
        if (status.equals("FULL") || status.equals("CONFLICT") || status.equals("PAST")) {
            panel.add(Box.createVerticalStrut(5));
            String reason = parentFrame.getStatusReason(group);
            JLabel reasonLabel = new JLabel("<html><center>" + reason + "</center></html>");
            reasonLabel.setFont(new Font("Arial", Font.BOLD, 10)); 
            reasonLabel.setForeground(RED_ERROR);
            reasonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(reasonLabel);
        }
        
        return panel;
    }
    
    private JButton createButton(String text, Color bgColor, Color fgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(width, height));
        button.setPreferredSize(new Dimension(width, height));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
    
    private JButton createActionButton(String status) {
        JButton button = null;
        
        if (status.equals("VIEW_GROUP")) {
            button = createButton("View Group", GREEN_LIGHT, Color.WHITE, 130, 35);
            button.addActionListener(e -> {
                new com.ExploBD.presentation.frames.module2.MyGroupsFrame(currentUser).setVisible(true);
                SwingUtilities.getWindowAncestor(this).dispose();
            });
        }
        else if (status.equals("CANCEL_REQUEST")) {
            button = createButton("Cancel Request", RED_ERROR, Color.WHITE, 130, 35);
            button.addActionListener(e -> parentFrame.cancelJoinRequest(group));
        }
        else if (status.equals("FULL") || status.equals("CONFLICT") || status.equals("PAST")) {
            button = createButton("Cannot Join", GRAY_LIGHT, Color.WHITE, 130, 35);
            button.setEnabled(false);
        }
        else if (status.equals("JOIN_PUBLIC")) {
            button = createButton("Join Now", GREEN_LIGHT, Color.WHITE, 130, 35);
            button.addActionListener(e -> parentFrame.joinPublicGroup(group));
        }
        else if (status.equals("REQUEST_PRIVATE")) {
            button = createButton("Request Join", ORANGE_LIGHT, Color.WHITE, 130, 35);
            button.addActionListener(e -> parentFrame.requestToJoinPrivateGroup(group));
        }
        
        return button;
    }
    
    private Color getStatusColor(GroupStatus status) {
        switch (status) {
            case PLANNING: return BROWN_DARK;
            case VOTING: return ORANGE_WARNING;
            case CONFIRMED: return GREEN_SUCCESS;
            default: return GRAY_TEXT;
        }
    }
    
    private void showPlaceDetails() {
        Place place = parentFrame.getGroupDestinationPlace(group);
        if (place != null) {
            new PlaceInfoDialog((JFrame) SwingUtilities.getWindowAncestor(this), place).setVisible(true);
        }
    }
    
    private void showMembersDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Members - " + group.getName(), true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_WHITE);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BROWN_MEDIUM);
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel("Members of " + group.getName());
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        titleLabel.setForeground(GOLD);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(ORANGE);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);
        
        ViewOnlyMembersTab membersTab = new ViewOnlyMembersTab(group, currentUser, photoUtils, dialog);
        membersTab.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(membersTab, BorderLayout.CENTER);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}