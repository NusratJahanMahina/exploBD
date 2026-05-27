package com.ExploBD.presentation.components;

import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.presentation.frames.EditUserProfile;
import com.ExploBD.presentation.frames.HomeFrame;
import com.ExploBD.service.GroupService;
import com.ExploBD.service.InvitationService;
import com.ExploBD.util.RefreshManager;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class InvitationDialog extends JDialog {
    
    private User currentUser;
    private Invitation invitation;
    private Group group;
    private HomeFrame homeFrame;
    private Runnable onAcceptCallback;
    private Runnable onDeclineCallback;
    private Runnable onLaterCallback;
    private boolean processed = false;
    private Place destinationPlace;
    private InvitationService invitationService;
    private GroupService groupService;
    private PlaceDatabaseObject placeDAO;
    
    
    private Color BROWN_BG = new Color(88, 74, 60);
    private Color ORANGE = new Color(255, 153, 51);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color GOLD = new Color(255, 215, 0);
    private Color LIGHT_CREAM = new Color(250, 245, 240);
    
    public InvitationDialog(HomeFrame parent, User user, Invitation invitation) {
        super(parent, "Group Invitation", true);
        this.currentUser = user;
        this.invitation = invitation;
        this.group = invitation.getGroup();
        this.homeFrame = parent;
        this.invitationService = new InvitationService();
        this.groupService = new GroupService();
        this.placeDAO = new PlaceDatabaseObject();
        
        loadDestinationPlace();
        initComponents();
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    public InvitationDialog(HomeFrame parent, User user, Invitation invitation, 
                            Runnable onAccept, Runnable onDecline) {
        this(parent, user, invitation);
        this.onAcceptCallback = onAccept;
        this.onDeclineCallback = onDecline;
        this.onLaterCallback = onDecline;
    }
    
    public InvitationDialog(HomeFrame parent, User user, Invitation invitation, 
                            Runnable onAccept, Runnable onDecline, Runnable onLater) {
        this(parent, user, invitation);
        this.onAcceptCallback = onAccept;
        this.onDeclineCallback = onDecline;
        this.onLaterCallback = onLater;
    }
    
    private void loadDestinationPlace() {
        String destName = group.getDestinationName();
        if (destName != null && !destName.isEmpty()) {
            List<Place> places = placeDAO.findAllByDivision(
                group.getDestinationDivision() != null ? group.getDestinationDivision() : "");
            for (Place place : places) {
                if (place.getName().equalsIgnoreCase(destName)) {
                    destinationPlace = place;
                    break;
                }
            }
        }
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BROWN_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 180, 160), 2),
            BorderFactory.createEmptyBorder(5, 10, 10, 10)
        ));
        
        contentPanel.add(createImagePanel());
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(createDestinationNamePanel());
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(createDateRowPanel());
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createInviterCardPanel());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createButtonPanel());
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_CREAM);
        panel.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 2));
        panel.setPreferredSize(new Dimension(360, 170));
        panel.setMaximumSize(new Dimension(360, 170));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        boolean imageLoaded = false;
        if (destinationPlace != null) {
            String imagePath = destinationPlace.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    java.net.URL url = getClass().getResource(imagePath);
                    if (url == null) url = getClass().getResource("/" + imagePath);
                    if (url != null) {
                        ImageIcon icon = new ImageIcon(url);
                        Image scaled = icon.getImage().getScaledInstance(360, 190, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(scaled));
                        imageLoaded = true;
                    }
                } catch (Exception e) {}
            }
        }
        
        if (!imageLoaded) {
            String destName = group.getDestinationName() != null ? group.getDestinationName() : "DESTINATION";
            imageLabel.setText(destName);
            imageLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            imageLabel.setForeground(DARK_BROWN);
            imageLabel.setOpaque(true);
            imageLabel.setBackground(LIGHT_CREAM);
        }
        
        panel.add(imageLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createDestinationNamePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String destName = group.getDestinationName() != null ? group.getDestinationName() : "Destination";
        JLabel nameLabel = new JLabel(destName.toUpperCase());
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        nameLabel.setForeground(DARK_BROWN);
        
        panel.add(nameLabel);
        return panel;
    }
    
    private JPanel createDateRowPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        String startStr = group.getStartDate() != null ? group.getStartDate().format(fmt) : "TBD";
        JLabel startLabel = new JLabel("Starts: " + startStr);
        startLabel.setFont(new Font("Arial", Font.BOLD, 12));
        startLabel.setForeground(DARK_BROWN);
        
        LocalDate joinBy = group.getStartDate() != null ? 
            group.getStartDate().minusDays(2) : invitation.getExpiryDate();
        String joinStr = joinBy.format(fmt);
        
        JLabel joinLabel = new JLabel("Join before " + joinStr);
        joinLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        joinLabel.setForeground(ORANGE);
        
        panel.add(startLabel);
        panel.add(joinLabel);
        
        return panel;
    }
    
    private JPanel createInviterCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(LIGHT_CREAM);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(360, 55));
        
        User inviter = invitation.getInviter();
        
        JLabel photoLabel = createProfilePhotoLabel(inviter, 38);
        photoLabel.setPreferredSize(new Dimension(38, 38));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(LIGHT_CREAM);
        
        JLabel nameLabel = new JLabel(inviter.getDisplayName());
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(DARK_BROWN);
        
        JLabel emailLabel = new JLabel(inviter.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        emailLabel.setForeground(Color.GRAY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(1));
        infoPanel.add(emailLabel);
        
        String type = invitation.getType().equals("EMAIL") ? "MEMBER" : "GUEST";
        JLabel badgeLabel = new JLabel(type);
        badgeLabel.setFont(new Font("Arial", Font.BOLD, 8));
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setBackground(invitation.getType().equals("EMAIL") ? ORANGE : new Color(100, 86, 72));
        badgeLabel.setOpaque(true);
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        
        panel.add(photoLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(badgeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JLabel createProfilePhotoLabel(User user, int size) {
        JLabel photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(size, size));
        photoLabel.setMinimumSize(new Dimension(size, size));
        photoLabel.setMaximumSize(new Dimension(size, size));
        photoLabel.setBackground(new Color(240, 240, 240));
        photoLabel.setOpaque(true);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140)));

        boolean imageLoaded = false;
        String imagePath = user.getProfileImagePath();

        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("images/nophoto.jpg")) {
            try {
                java.net.URL url = null;
                if (imagePath.startsWith("/")) {
                    url = getClass().getResource(imagePath);
                } else {
                    url = getClass().getResource("/" + imagePath);
                }
                
                if (url == null) {
                    java.io.File file = new java.io.File(imagePath);
                    if (file.exists()) {
                        ImageIcon icon = new ImageIcon(imagePath);
                        if (icon.getIconWidth() > 0) {
                            Image image = icon.getImage();
                            int imgWidth = icon.getIconWidth();
                            int imgHeight = icon.getIconHeight();
                            double scale = Math.min((double) (size - 4) / imgWidth, (double) (size - 4) / imgHeight);
                            int scaledWidth = (int) (imgWidth * scale);
                            int scaledHeight = (int) (imgHeight * scale);
                            Image scaled = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                            photoLabel.setIcon(new ImageIcon(scaled));
                            photoLabel.setText("");
                            photoLabel.setBackground(new Color(250, 245, 240));
                            imageLoaded = true;
                        }
                    }
                } else {
                    ImageIcon icon = new ImageIcon(url);
                    if (icon.getIconWidth() > 0) {
                        Image image = icon.getImage();
                        int imgWidth = icon.getIconWidth();
                        int imgHeight = icon.getIconHeight();
                        double scale = Math.min((double) (size - 4) / imgWidth, (double) (size - 4) / imgHeight);
                        int scaledWidth = (int) (imgWidth * scale);
                        int scaledHeight = (int) (imgHeight * scale);
                        Image scaled = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        photoLabel.setIcon(new ImageIcon(scaled));
                        photoLabel.setText("");
                        photoLabel.setBackground(Color.WHITE);
                        imageLoaded = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!imageLoaded) {
            String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
                    ? user.getFullName() : user.getUsername();
            if (displayName != null && displayName.length() > 0) {
                photoLabel.setText(displayName.substring(0, 1).toUpperCase());
                photoLabel.setFont(new Font("Comic Sans MS", Font.BOLD, size / 2));
                photoLabel.setForeground(DARK_BROWN);
                photoLabel.setIcon(null);
            } else {
                photoLabel.setText("?");
                photoLabel.setFont(new Font("Comic Sans MS", Font.BOLD, size / 2));
            }
        }

        return photoLabel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton acceptBtn = createButton("ACCEPT", new Color(46, 125, 50), Color.WHITE);
        JButton declineBtn = createButton("DECLINE", new Color(198, 40, 40), Color.WHITE);
        JButton laterBtn = createButton("LATER", DARK_BROWN, GOLD);
        
        acceptBtn.addActionListener(e -> acceptInvitation());
        declineBtn.addActionListener(e -> declineInvitation());
        laterBtn.addActionListener(e -> laterInvitation());
        
        panel.add(acceptBtn);
        panel.add(declineBtn);
        panel.add(laterBtn);
        
        return panel;
    }
    
    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void acceptInvitation() {
    if (processed) return;

    // ===== ADD PROFILE COMPLETENESS CHECK =====
    if (!currentUser.isProfileComplete()) {
        int response = JOptionPane.showConfirmDialog(this,
            "Your profile is incomplete. Would you like to complete it now?\n\n" +
            "You need to add your name and phone number to join groups.",
            "Profile Incomplete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            EditUserProfile editProfile = new EditUserProfile(currentUser);
            editProfile.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    RefreshManager.getInstance().refreshAll();
                    // Retry accepting after profile is complete
                    acceptInvitation();
                }
            });
            editProfile.setVisible(true);
        }
        return;
    }
    // ===== END PROFILE CHECK =====

    boolean hasActiveGroup = groupService.isUserInAnyActiveGroup(currentUser.getUserId());
    
    if (hasActiveGroup) {
        JOptionPane.showMessageDialog(this,
            "You already have an active group.\nPlease complete or leave that group first.",
            "Cannot Join",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
        "Accept invitation to join " + group.getName() + "?",
        "Accept Invitation",
        JOptionPane.YES_NO_OPTION);
        
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }
    
    processed = true;
    
    try {
        invitationService.acceptInvitation(
            invitation.getInvitationId(),
            currentUser.getUserId(),
            group.getId(),
            invitation.getInviter().getUserId()
        );
        
        JOptionPane.showMessageDialog(this, 
            "You have joined " + group.getName() + "!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        
        if (onAcceptCallback != null) {
            onAcceptCallback.run();
        }
        
        dispose();
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        processed = false;
    }
}
    
    
    
    
    
    
    
    
    
    
    
//    private void acceptInvitation() {
//        if (processed) return;
//
//        boolean hasActiveGroup = groupService.isUserInAnyActiveGroup(currentUser.getUserId());
//        
//        if (hasActiveGroup) {
//            JOptionPane.showMessageDialog(this,
//                "You already have an active group.\nPlease complete or leave that group first.",
//                "Cannot Join",
//                JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//            "Accept invitation to join " + group.getName() + "?",
//            "Accept Invitation",
//            JOptionPane.YES_NO_OPTION);
//            
//        if (confirm != JOptionPane.YES_OPTION) {
//            return;
//        }
//        
//        processed = true;
//        
//        try {
//            invitationService.acceptInvitation(
//                invitation.getInvitationId(),
//                currentUser.getUserId(),
//                group.getId(),
//                invitation.getInviter().getUserId()
//            );
//            
//            JOptionPane.showMessageDialog(this, 
//                "You have joined " + group.getName() + "!",
//                "Success",
//                JOptionPane.INFORMATION_MESSAGE);
//            
//            if (onAcceptCallback != null) {
//                onAcceptCallback.run();
//            }
//            
//            dispose();
//            
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this,
//                "Error: " + e.getMessage(),
//                "Error",
//                JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//            processed = false;
//        }
//    }

    private void declineInvitation() {
        if (processed) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Decline invitation from " + invitation.getInviter().getDisplayName() + "?", 
            "Decline Invitation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        processed = true;
        
        try {
            invitationService.declineInvitation(invitation.getInvitationId());
            
            JOptionPane.showMessageDialog(this, 
                "Invitation declined.", 
                "Declined", 
                JOptionPane.INFORMATION_MESSAGE);
            
            if (onDeclineCallback != null) {
                onDeclineCallback.run();
            }
            
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            processed = false;
        }
    }
    
    private void laterInvitation() {
        if (processed) return;
        processed = true;
        
        if (onLaterCallback != null) {
            onLaterCallback.run();
        } else if (onDeclineCallback != null) {
            onDeclineCallback.run();
        }
        
        dispose();
    }
}