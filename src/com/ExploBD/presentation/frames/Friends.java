//package com.ExploBD.presentation.frames;
package com.ExploBD.presentation.frames;
import com.ExploBD.data.databaseObject.FriendDatabaseObject;
import com.ExploBD.domain.entities.FriendRequest;
import com.ExploBD.domain.enums.FriendRequestStatus;
import com.ExploBD.object.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import com.ExploBD.presentation.components.UserCard;
import com.ExploBD.service.FriendService;
public class Friends extends JFrame {
    
    private String currentUserId;
    private User currentUser;
    private FriendService friendService;
    
    Color ABS_BG = new Color(150, 138, 127);
    Color TOP_BG = new Color(88, 74, 60);
    Color FRIENDS_COLOR = new Color(245, 180, 60);
    Color X_BG = new Color(255, 192, 0);

    JPanel contentPanel;
    CardLayout cardLayout;
    
    // Lists to store data
    private List<String> friendNames = new ArrayList<>();
    private List<String> requestNames = new ArrayList<>();
    private List<String> suggestionNames = new ArrayList<>();
    private List<String> requestIds = new ArrayList<>(); // To store IDs for accept/decline

    public Friends(User user) {
        this.currentUser = user;
        this.currentUserId = user.getUserId();
        this.friendService = new FriendService(user);
        friendService.debugCheckRequests();
        setTitle("Friends - " + user.getDisplayName());
        setSize(800, 600);
        setPreferredSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Don't exit, just close
        
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(ABS_BG);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(createTopBar(), BorderLayout.NORTH);
        northContainer.add(createTabBar(), BorderLayout.SOUTH);
        mainWrapper.add(northContainer, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Load data from database
        loadData();
        
        
        
        setUndecorated(true);//added by nusrat
        
        
        
        // Create panels with real data
        refreshUI();
        
        mainWrapper.add(contentPanel, BorderLayout.CENTER);
        add(mainWrapper);
        setVisible(true);
    }
 
    private JScrollPane createScrollableList(String type) {
    JPanel listContainer = new JPanel();
    listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

    if (type.equals("friend")) {
        List<User> friends = friendService.getFriends();
        for (User friend : friends) {
            // Use the 5-parameter constructor for friends
            listContainer.add(new UserCard(friend, "friend", currentUser, friendService, this));
            listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }
    } 
    else if (type.equals("request")) {
        List<FriendRequest> pendingRequests = friendService.getPendingRequests();
        System.out.println("Found " + pendingRequests.size() + " pending requests"); // Debug
        
        for (FriendRequest request : pendingRequests) {
            // IMPORTANT: Use the 6-parameter constructor with request ID
            listContainer.add(new UserCard(
                request.getFrom(),           // user
                "request",                   // cardType
                currentUser,                 // currentUser
                friendService,               // friendService
                this,                        // parentFrame
                request.getRequestId()       // requestId - THIS IS THE KEY!
            ));
            listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }
    } 
    else if (type.equals("suggestion")) {
        List<User> suggestions = friendService.getSuggestionUsers();
        for (User suggestion : suggestions) {
            // Use the 5-parameter constructor for suggestions
            listContainer.add(new UserCard(suggestion, "suggestion", currentUser, friendService, this));
            listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }
    }

    if (listContainer.getComponentCount() == 0) {
        JLabel emptyLabel = new JLabel("  No items to display");
        emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        listContainer.add(emptyLabel);
    }

    return new JScrollPane(listContainer);
}
   
    private void loadData() {
         System.out.println("=== LOADING FRIENDS DATA ===");
     System.out.println("Current User: " + currentUser.getDisplayName());
    System.out.println("User ID: " + currentUser.getUserId());
    // Load friends
    friendNames = friendService.getFriendNames();
    System.out.println("FriendNames loaded: " + friendNames.size());
    
    List<User> friends = friendService.getFriends();
    System.out.println("Friends objects loaded: " + friends.size());
    for (User f : friends) {
        System.out.println("  - " + f.getDisplayName());
    }
    System.out.println("=== LOADING FRIENDS DATA ===");
    System.out.println("Current User ID: " + currentUser.getUserId());
    
    // Load friends
    friendNames = friendService.getFriendNames();
    System.out.println("Friends count: " + friendNames.size());
    
    
    // Load friends
    friendNames = friendService.getFriendNames();
    System.out.println("friendNames list size: " + friendNames.size());
    System.out.println("friendNames contents: " + friendNames);
    // Load pending requests - THIS IS THE IMPORTANT PART
    List<FriendRequest> pendingRequests = friendService.getPendingRequests();
    System.out.println("Pending requests from service: " + pendingRequests.size());
    
    requestNames.clear();
    requestIds.clear();
    for (FriendRequest request : pendingRequests) {
        System.out.println("  Request from: " + request.getFrom().getDisplayName() + ", ID: " + request.getRequestId());
        requestNames.add(request.getFrom().getDisplayName());
        requestIds.add(request.getRequestId());
    }
    
    // Load suggestions
    suggestionNames = friendService.getSuggestionNames();
    System.out.println("Suggestions count: " + suggestionNames.size());
}
    public void refreshUI() {
        contentPanel.removeAll();
        
        // Reload data from database
        loadData();
        
        // Create panels with updated data
        contentPanel.add(createMainPanel("Total Friends: (" + friendNames.size() + ")", "friend"), "friends");
        contentPanel.add(createMainPanel("Friend Requests (" + requestNames.size() + ")", "request"), "requests");
        contentPanel.add(createMainPanel("Suggestions (" + suggestionNames.size() + ")", "suggestion"), "suggestions");
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

 

    private JPanel createTabBar() {
        JPanel tabBar = new JPanel(new GridLayout(1, 3, 0, 0));
        tabBar.setPreferredSize(new Dimension(800, 50));
        
        JButton b1 = createTabButton("Friends List");
        JButton b2 = createTabButton("Requests");
        JButton b3 = createTabButton("Suggestions");

        b1.addActionListener(e -> cardLayout.show(contentPanel, "friends"));
        b2.addActionListener(e -> cardLayout.show(contentPanel, "requests"));
        b3.addActionListener(e -> cardLayout.show(contentPanel, "suggestions"));

        tabBar.add(b1);
        tabBar.add(b2);
        tabBar.add(b3);
        return tabBar;
    }

    private JButton createTabButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ABS_BG);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return btn;
    }

    private JPanel createMainPanel(String titleText, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.add(title, BorderLayout.NORTH);

        panel.add(createScrollableList(type), BorderLayout.CENTER);
        return panel;
    }

  

// Add search functionality to the top bar
private JPanel createTopBar() {
    JPanel top = new JPanel(new BorderLayout());
    top.setBackground(TOP_BG);
    top.setBorder(new LineBorder(Color.BLACK, 2));

    JLabel title = new JLabel("FRIENDS", SwingConstants.CENTER);
    title.setForeground(FRIENDS_COLOR);
    title.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
    top.add(title, BorderLayout.NORTH);

    JPanel row = new JPanel(new BorderLayout(10, 0));
    row.setBackground(TOP_BG);
    row.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

    JTextField searchField = new JTextField();
    searchField.setPreferredSize(new Dimension(200, 30));
    searchField.putClientProperty("JTextField.placeholderText", "Search users...");
    
    JButton searchBtn = new JButton("🔍");
    searchBtn.addActionListener(e -> {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            showSearchResults(searchTerm);
        }
    });
    
    // Also search on Enter key
    searchField.addActionListener(e -> {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            showSearchResults(searchTerm);
        }
    });

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    rightPanel.setOpaque(false);
    JButton closeBtn = new JButton("X");
    closeBtn.setBackground(X_BG);
    closeBtn.setForeground(Color.BLACK);
    closeBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
    closeBtn.setFocusPainted(false);

    closeBtn.addActionListener(e -> this.dispose());
    
    //rightPanel.add(searchBtn);
    //JButton searchBtn = new JButton("🔍");
searchBtn.addActionListener(e -> {
    String searchTerm = searchField.getText().trim();
    if (!searchTerm.isEmpty() && !searchTerm.equals("Search...")) {
        // Show search results dialog
        List<User> results = friendService.searchUsers(searchTerm);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users found matching '" + searchTerm + "'");
        } else {
            showSearchResults(results);
        }
    }
});
rightPanel.add(searchBtn);
    rightPanel.add(closeBtn);

    row.add(searchField, BorderLayout.CENTER);
    row.add(rightPanel, BorderLayout.EAST);
    top.add(row, BorderLayout.SOUTH);
    return top;
}

// Add this method to show search results
private void showSearchResults(String searchTerm) {
    List<FriendService.SearchResult> results = friendService.searchUsersWithDetails(searchTerm);
    
    if (results.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No users found matching '" + searchTerm + "'");
        return;
    }
    
    // Create a dialog to show search results
    JDialog searchDialog = new JDialog(this, "Search Results", true);
    searchDialog.setSize(500, 400);
    searchDialog.setLocationRelativeTo(this);
    
    JPanel resultsPanel = new JPanel();
    resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
    
    for (FriendService.SearchResult result : results) {
        User user = result.getUser();
        if (result.isFriend()) {
            // Already a friend
            JLabel friendLabel = new JLabel("  👤 " + user.getDisplayName() + " (Already a friend)");
            friendLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            resultsPanel.add(friendLabel);
        } else if (result.hasPendingRequest()) {
            // Request already sent
            JLabel pendingLabel = new JLabel("  ⏳ " + user.getDisplayName() + " (Request pending)");
            pendingLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            resultsPanel.add(pendingLabel);
        } else {
            // Can send request
            resultsPanel.add(new UserCard(user, "search", currentUser, friendService, this));
        }
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    JScrollPane scrollPane = new JScrollPane(resultsPanel);
    searchDialog.add(scrollPane);
    
    JButton closeSearchBtn = new JButton("Close");
    closeSearchBtn.addActionListener(e -> searchDialog.dispose());
    
    JPanel bottomPanel = new JPanel();
    bottomPanel.add(closeSearchBtn);
    searchDialog.add(bottomPanel, BorderLayout.SOUTH);
    
    searchDialog.setVisible(true);
}
  
    private JPanel createUserCard(String name, String type, String requestId, int index) {
    JPanel card = new JPanel(new BorderLayout());
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); 
    card.setPreferredSize(new Dimension(700, 60));
    card.setBackground(TOP_BG);
    card.setBorder(new LineBorder(FRIENDS_COLOR, 2));

    // LEFT SIDE: Profile picture and name
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    leftPanel.setOpaque(false);
    
    // Profile picture placeholder (you'll replace with actual image later)
    JLabel picLabel = new JLabel();
    picLabel.setPreferredSize(new Dimension(40, 40));
    picLabel.setBackground(ABS_BG);
    picLabel.setOpaque(true);
    picLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
    picLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    // Get first letter for avatar
    String firstLetter = name.substring(0, 1).toUpperCase();
    picLabel.setText(firstLetter);
    picLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
    picLabel.setForeground(Color.WHITE);
    
    leftPanel.add(picLabel);
    
    JLabel nameLbl = new JLabel(name);
    nameLbl.setForeground(Color.WHITE);
    nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
    leftPanel.add(nameLbl);

    // RIGHT SIDE: Buttons
    JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
    btns.setOpaque(false);

    if (type.equals("request")) {
        JButton acceptBtn = new JButton("Accept");
        JButton rejectBtn = new JButton("Reject");
        
        acceptBtn.addActionListener(e -> {
            boolean success = friendService.acceptFriendRequest("", requestId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Friend Request Accepted!");
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to accept request");
            }
        });

        rejectBtn.addActionListener(e -> {
            boolean success = friendService.declineFriendRequest(requestId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Friend Request Declined");
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to decline request");
            }
        });

        btns.add(acceptBtn);
        btns.add(rejectBtn);

    } else if (type.equals("suggestion")) {
        JButton addBtn = new JButton("Add Friend");
        
        addBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Send friend request to " + name + "?", 
                "Confirm", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Get user ID from suggestions list
                User suggestedUser = friendService.getSuggestionUsers().get(index);
                boolean success = friendService.sendFriendRequest(suggestedUser.getUserId());
                
                if (success) {
                    addBtn.setText("Request Sent");
                    addBtn.setEnabled(false);
                    addBtn.setBackground(Color.GRAY);
                    JOptionPane.showMessageDialog(this, "Friend request sent to " + name);
                    refreshUI();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not send request");
                }
            }
        });

        btns.add(addBtn);

    } else { // Friends tab
        JButton profileBtn = new JButton("Profile");
        JButton removeBtn = new JButton("Remove");
        
        profileBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "View profile for " + name);
        });
        
        removeBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Remove " + name + " from friends?", 
                "Confirm", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                User friend = friendService.getFriends().get(index);
                boolean success = friendService.removeFriend(friend.getUserId());
                if (success) {
                    JOptionPane.showMessageDialog(this, name + " removed from friends");
                    refreshUI();
                }
            }
        });
        
        btns.add(profileBtn);
        btns.add(removeBtn);
    }

    card.add(leftPanel, BorderLayout.WEST);
    card.add(btns, BorderLayout.EAST);
    return card;
}
    private void showSearchResults(List<User> results) {
    JDialog searchDialog = new JDialog(this, "Search Results", true);
    searchDialog.setSize(500, 400);
    searchDialog.setLocationRelativeTo(this);
    
    JPanel resultsPanel = new JPanel();
    resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
    
    FriendDatabaseObject fdb = new FriendDatabaseObject();
    
    for (User user : results) {
        JPanel userRow = new JPanel(new BorderLayout());
        userRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        userRow.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel nameLabel = new JLabel("👤 " + user.getDisplayName() + " (" + user.getEmail() + ")");
        
        // Check if already friends
        boolean areFriends = fdb.areFriends(currentUser.getUserId(), user.getUserId());
        // Check if request pending
        FriendRequest existing = fdb.findFriendRequest(currentUser.getUserId(), user.getUserId());
        
        JButton actionBtn;
        if (areFriends) {
            actionBtn = new JButton("Already Friends");
            actionBtn.setEnabled(false);
        } else if (existing != null) {
            actionBtn = new JButton("Request Pending");
            actionBtn.setEnabled(false);
        } else {
            actionBtn = new JButton("Add Friend");
            actionBtn.addActionListener(e -> {
                boolean success = friendService.sendFriendRequest(user.getUserId());
                if (success) {
                    JOptionPane.showMessageDialog(searchDialog, "Friend request sent!");
                    searchDialog.dispose();
                    refreshUI();
                }
            });
        }
        
        userRow.add(nameLabel, BorderLayout.WEST);
        userRow.add(actionBtn, BorderLayout.EAST);
        resultsPanel.add(userRow);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    JScrollPane scrollPane = new JScrollPane(resultsPanel);
    searchDialog.add(scrollPane);
    
    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(e -> searchDialog.dispose());
    
    JPanel bottomPanel = new JPanel();
    bottomPanel.add(closeBtn);
    searchDialog.add(bottomPanel, BorderLayout.SOUTH);
    
    searchDialog.setVisible(true);
}
    // For testing only - remove this in production
    public static void main(String[] args) {
        // For testing, create a dummy user
        User testUser = new User("test123", "testuser", "test@email.com", 0);
        testUser.setFullName("Test User");
        
        SwingUtilities.invokeLater(() -> new Friends(testUser));
    }
    // Add this method to Friends.java
public void viewUserProfile(User user) {
    
     new UserProfile(user, true).setVisible(true);//add by nusrat
    
    JDialog profileDialog = new JDialog(this, user.getDisplayName() + "'s Profile", true);
    profileDialog.setSize(400, 500);
    profileDialog.setLocationRelativeTo(this);
    profileDialog.setLayout(new BorderLayout());
    
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(250, 245, 240));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Profile Picture
    JLabel photoLabel = new JLabel();
    photoLabel.setPreferredSize(new Dimension(100, 100));
    photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    photoLabel.setVerticalAlignment(SwingConstants.CENTER);
    photoLabel.setBackground(new Color(200, 180, 160));
    photoLabel.setOpaque(true);
    photoLabel.setBorder(BorderFactory.createLineBorder(new Color(245, 180, 60), 2));
    
    String displayName = user.getDisplayName();
    String firstLetter = displayName != null && displayName.length() > 0 ? 
                        displayName.substring(0, 1).toUpperCase() : "?";
    photoLabel.setText(firstLetter);
    photoLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
    photoLabel.setForeground(new Color(88, 74, 60));
    
    // Try to load profile image
    String imagePath = user.getProfileImagePath();
    if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("images/nophoto.jpg")) {
        try {
            java.net.URL url = getClass().getResource(imagePath);
            if (url == null && imagePath.startsWith("/")) {
                url = getClass().getResource(imagePath);
            }
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
                photoLabel.setText("");
            }
        } catch (Exception e) {
            // Use default avatar
        }
    }
    
    JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    photoPanel.setBackground(new Color(250, 245, 240));
    photoPanel.add(photoLabel);
    panel.add(photoPanel);
    panel.add(Box.createVerticalStrut(15));
    
    // Name
    JLabel nameLabel = new JLabel(user.getDisplayName());
    nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
    nameLabel.setForeground(new Color(70, 58, 47));
    nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(nameLabel);
    panel.add(Box.createVerticalStrut(5));
    
    // Email
    JLabel emailLabel = new JLabel(user.getEmail());
    emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    emailLabel.setForeground(Color.GRAY);
    emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(emailLabel);
    panel.add(Box.createVerticalStrut(15));
    
    // Additional Info
    JPanel infoGrid = new JPanel(new GridLayout(0, 2, 10, 8));
    infoGrid.setBackground(new Color(250, 245, 240));
    infoGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    
    addInfoRow(infoGrid, "User ID:", user.getUserId());
    addInfoRow(infoGrid, "Full Name:", user.getFullName() != null ? user.getFullName() : "Not set");
    addInfoRow(infoGrid, "Gender:", user.getGender() != null && !user.getGender().equals("Select Gender") ? 
              user.getGender() : "Not set");
    addInfoRow(infoGrid, "Nationality:", user.getNationality() != null ? user.getNationality() : "Not set");
    addInfoRow(infoGrid, "Budget:", user.getMinBudget() + " - " + user.getMaxBudget() + " tk");
    
    String travelStyles = user.getTravelStylesAsString();
    addInfoRow(infoGrid, "Travel Style:", travelStyles != null && !travelStyles.isEmpty() ? 
              travelStyles : "Not set");
    
    panel.add(infoGrid);
    panel.add(Box.createVerticalStrut(15));
    
    // Close button
    JButton closeBtn = new JButton("Close");
    closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
    closeBtn.setBackground(new Color(88, 74, 60));
    closeBtn.setForeground(new Color(255, 215, 0));
    closeBtn.setFocusPainted(false);
    closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    closeBtn.addActionListener(e -> profileDialog.dispose());
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBackground(new Color(250, 245, 240));
    buttonPanel.add(closeBtn);
    panel.add(buttonPanel);
    
    profileDialog.add(panel, BorderLayout.CENTER);
    profileDialog.setVisible(true);
}

private void addInfoRow(JPanel grid, String label, String value) {
    JLabel labelComp = new JLabel(label);
    labelComp.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
    labelComp.setForeground(new Color(70, 58, 47));
    
    JLabel valueComp = new JLabel(value != null && !value.isEmpty() ? value : "Not set");
    valueComp.setFont(new Font("Arial", Font.PLAIN, 11));
    valueComp.setForeground(new Color(100, 100, 100));
    
    grid.add(labelComp);
    grid.add(valueComp);
}











//nusrat


public void showRequestsTab() {
    // Switch to the Requests tab
    if (cardLayout != null && contentPanel != null) {
        cardLayout.show(contentPanel, "requests");
    }
}

public void showFriendsTab() {
    // Switch to the Friends List tab
    if (cardLayout != null && contentPanel != null) {
        cardLayout.show(contentPanel, "friends");
    }
}


//till this
}