package com.ExploBD.presentation.components;

import com.ExploBD.object.User;
import com.ExploBD.service.FriendService;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FriendSelectionDialog extends JDialog {
    
    private User currentUser;
    private FriendService friendService;
    private FriendSelectedListener listener;
    private JPanel friendsContainer;
    
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color GOLD = new Color(255, 215, 0);
    
    public interface FriendSelectedListener {
        void onFriendSelected(User selectedFriend);
    }
    
    public FriendSelectionDialog(JFrame parent, User user, FriendSelectedListener listener) {
        super(parent, "Select a Friend to Invite", true);
        this.currentUser = user;
        this.friendService = new FriendService(user);
        this.listener = listener;
        
        // Make dialog undecorated (no title bar, no X button)
        setUndecorated(true);
        
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 248, 245));
        
        initComponents();
        loadFriends();
        
        // Close when clicking outside (losing focus)
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });
    }
    
    private void initComponents() {
        // Simple header - no close button
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK_BROWN);
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("SELECT A FRIEND TO INVITE");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        titleLabel.setForeground(GOLD);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(titleLabel, BorderLayout.CENTER);
        
        // Friends container
        friendsContainer = new JPanel();
        friendsContainer.setLayout(new BoxLayout(friendsContainer, BoxLayout.Y_AXIS));
        friendsContainer.setBackground(new Color(250, 248, 245));
        friendsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(friendsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Message when no friends
        JLabel noFriendsLabel = new JLabel("You don't have any friends yet.\nAdd friends from the Friends tab first!", SwingConstants.CENTER);
        noFriendsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noFriendsLabel.setForeground(Color.GRAY);
        noFriendsLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(250, 248, 245));
        messagePanel.add(noFriendsLabel, BorderLayout.CENTER);
        
        // Check if user has friends
        List<User> friends = friendService.getFriends();
        if (friends.isEmpty()) {
            add(messagePanel, BorderLayout.CENTER);
        } else {
            add(header, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }
        
        // Bottom button for no friends case
        if (friends.isEmpty()) {
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(new Color(250, 248, 245));
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JButton goToFriendsBtn = new JButton("Go to Friends Tab");
            goToFriendsBtn.setFont(new Font("Arial", Font.BOLD, 12));
            goToFriendsBtn.setBackground(ORANGE);
            goToFriendsBtn.setForeground(Color.WHITE);
            goToFriendsBtn.setFocusPainted(false);
            goToFriendsBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            goToFriendsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            goToFriendsBtn.addActionListener(e -> {
                dispose();
                new com.ExploBD.presentation.frames.Friends(currentUser).setVisible(true);
            });
            bottomPanel.add(goToFriendsBtn);
            add(bottomPanel, BorderLayout.SOUTH);
        }
    }
    
    private void loadFriends() {
        List<User> friends = friendService.getFriends();
        
        for (User friend : friends) {
            friendsContainer.add(createFriendCard(friend));
            friendsContainer.add(Box.createVerticalStrut(5));
        }
        
        friendsContainer.revalidate();
        friendsContainer.repaint();
    }
    
    private JPanel createFriendCard(User friend) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 210, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(400, 70));
        card.setPreferredSize(new Dimension(400, 70));
        
        // Profile photo
        ProfilePhotoUtils photoUtils = new ProfilePhotoUtils();
        JLabel photoLabel = photoUtils.createUserPhotoLabel(friend, 45);
        photoLabel.setPreferredSize(new Dimension(45, 45));
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        
        String displayName = friend.getFullName() != null && !friend.getFullName().isEmpty()
                ? friend.getFullName() : friend.getUsername();
        
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(DARK_BROWN);
        
        JLabel emailLabel = new JLabel(friend.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailLabel.setForeground(Color.GRAY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(emailLabel);
        
        // Invite button
        JButton inviteBtn = new JButton("Invite to Group");
        inviteBtn.setFont(new Font("Arial", Font.BOLD, 11));
        inviteBtn.setBackground(new Color(46, 125, 50));
        inviteBtn.setForeground(Color.WHITE);
        inviteBtn.setFocusPainted(false);
        inviteBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        inviteBtn.setPreferredSize(new Dimension(110, 32));
        inviteBtn.addActionListener(e -> {
            if (listener != null) {
                listener.onFriendSelected(friend);
            }
            dispose();
        });
        
        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(inviteBtn, BorderLayout.EAST);
        
        return card;
    }
}