package com.ExploBD.presentation.components;

import com.ExploBD.object.User;
import com.ExploBD.presentation.frames.Friends;
import com.ExploBD.domain.entities.FriendRequest;
import com.ExploBD.presentation.frames.UserProfile;
import com.ExploBD.service.FriendService;
import javax.swing.*;
import java.awt.*;

public class UserCard extends JPanel {
    
    private User user;
    private String cardType;
    private FriendService friendService;
    private User currentUser;
    private Friends parentFrame;
    private String requestId;  // ADD THIS FIELD - stores the request ID for request cards
    
    // Constructor for FRIEND and SUGGESTION cards (no requestId)
    public UserCard(User user, String cardType, User currentUser, FriendService friendService, Friends parentFrame) {
        this.user = user;
        this.cardType = cardType;
        this.currentUser = currentUser;
        this.friendService = friendService;
        this.parentFrame = parentFrame;
        this.requestId = null;  // No request ID needed
        setupUI();
    }
    
    // NEW CONSTRUCTOR for REQUEST cards (with requestId)
    public UserCard(User user, String cardType, User currentUser, FriendService friendService, 
                    Friends parentFrame, String requestId) {
        this.user = user;
        this.cardType = cardType;
        this.currentUser = currentUser;
        this.friendService = friendService;
        this.parentFrame = parentFrame;
        this.requestId = requestId;  // Store the request ID
        setupUI();
    }
 private void setupUI() {
        setLayout(new BorderLayout(10, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        setPreferredSize(new Dimension(700, 70));
        setBackground(new Color(88, 74, 60));
        setBorder(BorderFactory.createLineBorder(new Color(245, 180, 60), 2));
        JLabel nameLbl = new JLabel(user.getDisplayName());
        // LEFT SIDE: Profile picture and name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(nameLbl); 
        // Profile picture placeholder
        JLabel picLabel = new JLabel();
        picLabel.setPreferredSize(new Dimension(40, 40));
        picLabel.setBackground(new Color(150, 138, 127));
        picLabel.setOpaque(true);
        picLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        picLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        String displayName = user.getDisplayName();
        String firstLetter = displayName != null && displayName.length() > 0 ? 
                            displayName.substring(0, 1).toUpperCase() : "?";
        picLabel.setText(firstLetter);
        picLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        picLabel.setForeground(Color.WHITE);
        
        leftPanel.add(picLabel);
        
        //JLabel nameLbl = new JLabel(displayName);
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        leftPanel.add(nameLbl);
        
        // RIGHT SIDE: Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        btns.setOpaque(false);
        
        if (cardType.equals("request")) {
            JButton acceptBtn = new JButton("Accept");
            JButton rejectBtn = new JButton("Reject");
            
            acceptBtn.addActionListener(e -> {
                // FIXED: Pass both user ID and request ID
                boolean success = friendService.acceptFriendRequest(user.getUserId(), requestId);
                if (success) {
                    JOptionPane.showMessageDialog(UserCard.this, "Friend Request Accepted!");
                    if (parentFrame != null) {
                        parentFrame.refreshUI();
                    }
                } else {
                    JOptionPane.showMessageDialog(UserCard.this, "Failed to accept request", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            rejectBtn.addActionListener(e -> {
                // FIXED: Pass request ID for rejection
                boolean success = friendService.declineFriendRequest(requestId);
                if (success) {
                    JOptionPane.showMessageDialog(UserCard.this, "Friend Request Declined");
                    if (parentFrame != null) {
                        parentFrame.refreshUI();
                    }
                } else {
                    JOptionPane.showMessageDialog(UserCard.this, "Failed to decline request", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btns.add(acceptBtn);
            btns.add(rejectBtn);
            
        } else if (cardType.equals("suggestion") || cardType.equals("search")) {
            JButton addBtn = new JButton("Add Friend");
            
            addBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(UserCard.this, 
                    "Send friend request to " + user.getDisplayName() + "?", 
                    "Confirm", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = friendService.sendFriendRequest(user.getUserId());
                    if (success) {
                        addBtn.setText("Request Sent");
                        addBtn.setEnabled(false);
                        addBtn.setBackground(Color.GRAY);
                        if (parentFrame != null) {
                            parentFrame.refreshUI();
                        }
                    }
                }
            });
            
            btns.add(addBtn);
            
        } else if (cardType.equals("friend")) {
            JButton profileBtn = new JButton("Profile");
            JButton removeBtn = new JButton("Remove");
            
               profileBtn.addActionListener(e -> {
                   
                   
                   
                   
    // Open friend's profile
    
    
      //new UserProfile(user).setVisible(true);
     new UserProfile(user, true).setVisible(true);  ///changed by nusrat
});
            
            /*removeBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(UserCard.this, 
                    "Remove " + user.getDisplayName() + " from friends?", 
                    "Confirm", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = friendService.removeFriend(user.getUserId());
                    if (success) {
                        if (parentFrame != null) {
                            parentFrame.refreshUI();
                        }
                    }
                }
            });*/
            removeBtn.addActionListener(e -> {
    int confirm = JOptionPane.showConfirmDialog(UserCard.this, 
        "Remove " + user.getDisplayName() + " from friends?", 
        "Confirm", 
        JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        // Call the remove friend method
        boolean success = friendService.removeFriend(user.getUserId());
        
        if (success) {
            // Refresh the parent Friends window
            if (parentFrame != null) {
                parentFrame.refreshUI();
            }
            JOptionPane.showMessageDialog(UserCard.this, 
                user.getDisplayName() + " removed from friends");
        } else {
            JOptionPane.showMessageDialog(UserCard.this, 
                "Failed to remove friend", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
});
            btns.add(profileBtn);
            btns.add(removeBtn);
        }
        
        add(leftPanel, BorderLayout.WEST);
        add(btns, BorderLayout.EAST);
    }

    
}