package com.ExploBD.presentation.frames;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.MessageDatabaseObject;
import com.ExploBD.domain.entities.BulletinMessage;
import com.ExploBD.domain.entities.ChatMessage;
import com.ExploBD.domain.entities.Message;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.object.User;
import com.ExploBD.util.ProfilePhotoUtils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class BulletinFrame extends JFrame {

    private static final Color BROWN_BG   = new Color(88, 74, 60);
    private static final Color DARK_BROWN = new Color(70, 58, 47);
    private static final Color ORANGE     = new Color(255, 153, 51);
    private static final Color GOLD       = new Color(255, 215, 0);
    private static final Color BORDER_CLR = new Color(200, 180, 160);
    private static final Color LEADER_BOX = new Color(88, 74, 60);
    private static final Color CHAT_BUBBLE_ME = new Color(220, 248, 198);
    private static final Color CHAT_BUBBLE_OTHER = Color.WHITE;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM");

    private final User currentUser;
    private Group activeGroup;
    private boolean isLeader = false;
    private final MessageDatabaseObject messageDAO = new MessageDatabaseObject();
    private final GroupDatabaseObject groupDAO = new GroupDatabaseObject();
    private final ProfilePhotoUtils photoUtils = new ProfilePhotoUtils();
    
    // Cache for user photos to avoid reloading
    private final Map<String, ImageIcon> photoCache = new HashMap<>();

    private JPanel chatListPanel;
    private JPanel bulletinListPanel;
    private JTextField chatInput;
    private JTextField bulletinTitleInput;
    private JTextField bulletinContentInput;
    private JScrollPane chatScrollPane;
    private JScrollPane bulletinScrollPane;
    
    private JLabel loadingLabel;

    public BulletinFrame(User user) {
        this.currentUser = user;
        ensureTableExists();
        ensureIndexesExist(); // Add indexes for performance
        loadActiveGroup();
        
         setUndecorated(true);
         
        setTitle("Messages - " + (activeGroup != null ? activeGroup.getName() : "ExploBD"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 650);
        setMinimumSize(new Dimension(800, 700));
        setLocationRelativeTo(null);
        buildUI();
        
        // Load messages in background thread
        loadMessagesAsync();
    }
    
    private void ensureIndexesExist() {
        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_messages_group_id ON messages(group_id)",
            "CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at)",
            "CREATE INDEX IF NOT EXISTS idx_messages_group_created ON messages(group_id, created_at)"
        };
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String indexSql : indexes) {
                stmt.execute(indexSql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS messages ("
                + "message_id TEXT PRIMARY KEY, group_id TEXT NOT NULL, "
                + "sender_id TEXT NOT NULL, message_type TEXT, status TEXT, "
                + "title TEXT, content TEXT, like_count INTEGER DEFAULT 0, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "place_id TEXT, place_name TEXT, upvotes INTEGER DEFAULT 0, "
                + "downvotes INTEGER DEFAULT 0, reason TEXT, "
                + "invitee_name TEXT, invitee_email TEXT, request_type TEXT, "
                + "is_important BOOLEAN DEFAULT 0, reviewed_by TEXT, reviewed_at TIMESTAMP, "
                + "FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE)";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadActiveGroup() {
        List<Group> groups = groupDAO.findByUser(currentUser.getUserId());
        if (groups == null || groups.isEmpty()) return;
        
        LocalDate today = LocalDate.now();
        
        for (Group g : groups) {
            if (g.getStatus() == GroupStatus.CONFIRMED
                    && g.getStartDate() != null && g.getEndDate() != null
                    && !today.isBefore(g.getStartDate())
                    && !today.isAfter(g.getEndDate())) {
                activeGroup = g;
                break;
            }
        }
        
        if (activeGroup == null) {
            for (Group g : groups) {
                if (g.getStatus() == GroupStatus.CONFIRMED && g.getStartDate() != null) {
                    if (activeGroup == null || g.getStartDate().isAfter(activeGroup.getStartDate())) {
                        activeGroup = g;
                    }
                }
            }
        }
        
        if (activeGroup == null) {
            for (Group g : groups) {
                if (g.getStatus() == GroupStatus.PLANNING || g.getStatus() == GroupStatus.VOTING) {
                    activeGroup = g;
                    break;
                }
            }
        }
        
        if (activeGroup != null) {
            for (GroupMember m : activeGroup.getAllMembers()) {
                if (m.getId().equals(currentUser.getUserId()) && m.getRole() == MemberRole.LEADER) {
                    isLeader = true;
                    break;
                }
            }
        }
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BROWN_BG);
        main.add(buildTopBar(), BorderLayout.NORTH);
        main.add(buildCenter(), BorderLayout.CENTER);
        add(main);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BROWN_BG);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ORANGE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setBackground(ORANGE);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        backBtn.addActionListener(e -> dispose());

        String groupName = (activeGroup != null) ? activeGroup.getName() : "No Active Tour";
        JLabel titleLabel = new JLabel("MESSAGES  -  " + groupName.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        titleLabel.setForeground(ORANGE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(BROWN_BG);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 11));
        refreshBtn.setBackground(new Color(100, 85, 70));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        refreshBtn.addActionListener(e -> {
            // Clear and reload messages
            chatListPanel.removeAll();
            bulletinListPanel.removeAll();
            loadMessagesAsync();
        });
        
        JLabel roleLabel = new JLabel(isLeader ? "LEADER" : "MEMBER");
        roleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        roleLabel.setForeground(isLeader ? GOLD : new Color(200, 185, 165));
        
        rightPanel.add(refreshBtn);
        rightPanel.add(roleLabel);

        top.add(backBtn, BorderLayout.WEST);
        top.add(titleLabel, BorderLayout.CENTER);
        top.add(rightPanel, BorderLayout.EAST);
        return top;
    }

    private JPanel buildCenter() {
        if (activeGroup == null) return buildNoTourPanel();

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BROWN_BG);
        center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 5, 0, 5);

        gbc.gridx = 0;
        gbc.weightx = 0.55;  // Reduced from 0.65 to 0.55 (100px less)
        center.add(buildChatPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.45;  // Increased from 0.35 to 0.45
        center.add(buildBulletinPanel(), gbc);

        return center;
    }

// ==================== CHAT PANEL ====================
private JPanel buildChatPanel() {
    JPanel card = new JPanel(new BorderLayout(0, 5));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));

    JLabel header = new JLabel("GROUP CHAT");
    header.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
    header.setForeground(DARK_BROWN);
    header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_CLR));
    card.add(header, BorderLayout.NORTH);

    // Create a panel that will contain messages and scroll horizontally
    JPanel messagesWrapper = new JPanel();
    messagesWrapper.setLayout(new BoxLayout(messagesWrapper, BoxLayout.X_AXIS));
    messagesWrapper.setBackground(Color.WHITE);
    
    chatListPanel = new JPanel();
    chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
    chatListPanel.setBackground(Color.WHITE);
    
    messagesWrapper.add(chatListPanel);
    messagesWrapper.add(Box.createHorizontalGlue());
    
    // HORIZONTAL SCROLLING ONLY - NO VERTICAL SCROLLBAR
    chatScrollPane = new JScrollPane(messagesWrapper);
    chatScrollPane.setBorder(null);
    chatScrollPane.getHorizontalScrollBar().setUnitIncrement(20);
    // Disable vertical scrolling completely
    chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    // Make vertical scrollbar invisible and disabled
    chatScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    chatScrollPane.getVerticalScrollBar().setEnabled(false);
    // Adjust size
    chatScrollPane.setPreferredSize(new Dimension(450, 500));
    chatScrollPane.setMinimumSize(new Dimension(400, 450));
    
    // Add loading indicator
    loadingLabel = new JLabel("Loading messages...", SwingConstants.CENTER);
    loadingLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    loadingLabel.setForeground(Color.GRAY);
    messagesWrapper.add(loadingLabel);
    
    card.add(chatScrollPane, BorderLayout.CENTER);
    card.add(buildChatInputRow(), BorderLayout.SOUTH);
    return card;
}

    private JPanel buildChatInputRow() {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                BorderFactory.createEmptyBorder(8, 0, 4, 0)));

        chatInput = new JTextField();
        chatInput.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
        chatInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        chatInput.addActionListener(e -> sendChat());

        JButton sendBtn = makeSmallButton("Send");
        sendBtn.addActionListener(e -> sendChat());

        row.add(chatInput, BorderLayout.CENTER);
        row.add(sendBtn, BorderLayout.EAST);
        return row;
    }
    
// ==================== BULLETIN PANEL ====================
private JPanel buildBulletinPanel() {
    JPanel card = new JPanel(new BorderLayout(0, 5));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));

    JLabel header = new JLabel("LEADER BULLETIN");
    header.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
    header.setForeground(DARK_BROWN);
    header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_CLR));
    card.add(header, BorderLayout.NORTH);

    bulletinListPanel = new JPanel();
    bulletinListPanel.setLayout(new BoxLayout(bulletinListPanel, BoxLayout.Y_AXIS));
    bulletinListPanel.setBackground(Color.WHITE);
    
    bulletinScrollPane = new JScrollPane(bulletinListPanel);
    bulletinScrollPane.setBorder(null);
      bulletinScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
    bulletinScrollPane.getVerticalScrollBar().setUnitIncrement(16);
     bulletinScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      bulletinScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    bulletinScrollPane.setPreferredSize(new Dimension(350, 500));
    bulletinScrollPane.setMinimumSize(new Dimension(300, 450));
    card.add(bulletinScrollPane, BorderLayout.CENTER);

    if (isLeader) {
        card.add(buildBulletinInputPanel(), BorderLayout.SOUTH);
    } else {
        JLabel info = new JLabel("Only leader can post");
        info.setFont(new Font("Arial", Font.ITALIC, 11));
        info.setForeground(Color.GRAY);
        info.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(info, BorderLayout.SOUTH);
    }
    return card;
}

 private JPanel buildBulletinInputPanel() {
    JPanel col = new JPanel();
    col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
    col.setBackground(Color.WHITE);
    col.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));
    col.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

    JPanel titleRow = new JPanel(new BorderLayout(5, 0));
    titleRow.setBackground(Color.WHITE);
    titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

    JLabel titleLbl = new JLabel("Title:");
    titleLbl.setFont(new Font("Arial", Font.BOLD, 11));
    titleLbl.setForeground(DARK_BROWN);
    titleLbl.setPreferredSize(new Dimension(35, 24));

    bulletinTitleInput = new JTextField();
    bulletinTitleInput.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
    bulletinTitleInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));

    titleRow.add(titleLbl, BorderLayout.WEST);
    titleRow.add(bulletinTitleInput, BorderLayout.CENTER);

    JPanel msgRow = new JPanel(new BorderLayout(5, 0));
    msgRow.setBackground(Color.WHITE);
    msgRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
    msgRow.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

    JLabel msgLbl = new JLabel("Msg:");
    msgLbl.setFont(new Font("Arial", Font.BOLD, 11));
    msgLbl.setForeground(DARK_BROWN);
    msgLbl.setPreferredSize(new Dimension(35, 24));

    bulletinContentInput = new JTextField();
    bulletinContentInput.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
    bulletinContentInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    bulletinContentInput.addActionListener(e -> sendBulletin());

    JButton postBtn = makeSmallButton("Post");
    postBtn.addActionListener(e -> sendBulletin());

    msgRow.add(msgLbl, BorderLayout.WEST);
    msgRow.add(bulletinContentInput, BorderLayout.CENTER);
    msgRow.add(postBtn, BorderLayout.EAST);

    col.add(titleRow);
    col.add(msgRow);
    return col;
}

    // ==================== NO TOUR PANEL ====================
    private JPanel buildNoTourPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        JLabel msg = new JLabel("NO ACTIVE TOUR FOUND", SwingConstants.CENTER);
        msg.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        msg.setForeground(DARK_BROWN);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Create or join a group and confirm a destination");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalStrut(20));
        p.add(msg);
        p.add(Box.createVerticalStrut(5));
        p.add(sub);
        return p;
    }

    // ==================== SEND MESSAGES ====================
    private void sendChat() {
        String text = chatInput.getText().trim();
        if (text.isEmpty()) return;
        
        ChatMessage msg = new ChatMessage(currentUser, activeGroup.getId(), text);
        messageDAO.save(msg);
        chatInput.setText("");
        addChatRow(msg);
        
        // Scroll to show new message
        SwingUtilities.invokeLater(() -> scrollToBottom(chatScrollPane));
    }

    private void sendBulletin() {
        String title = bulletinTitleInput.getText().trim();
        String content = bulletinContentInput.getText().trim();
        
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both Title and Message.",
                    "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BulletinMessage msg = new BulletinMessage(currentUser, activeGroup.getId(), title, content);
        messageDAO.save(msg);
        bulletinTitleInput.setText("");
        bulletinContentInput.setText("");
        addBulletinRow(msg);
        
        // Scroll to show new bulletin
        SwingUtilities.invokeLater(() -> scrollToBottom(bulletinScrollPane));
    }

// ==================== LOAD MESSAGES ASYNC ====================
private void loadMessagesAsync() {
    if (activeGroup == null) return;
    
    // Show loading indicator
    SwingUtilities.invokeLater(() -> {
        if (loadingLabel != null) {
            loadingLabel.setText("Loading messages...");
            loadingLabel.setVisible(true);
        }
    });
    
    // Load in background thread
    CompletableFuture.supplyAsync(() -> {
        try {
            // Get messages directly with optimized query
          List<Message> allMessages = messageDAO.getGroupMessagesFiltered(activeGroup.getId());
            
            // Separate and sort in memory (fast after DB query)
            List<ChatMessage> chatMessages = new ArrayList<>();
            List<BulletinMessage> bulletinMessages = new ArrayList<>();
            
            for (Message m : allMessages) {
                if (m instanceof ChatMessage) {
                    chatMessages.add((ChatMessage) m);
                } else if (m instanceof BulletinMessage) {
                    bulletinMessages.add((BulletinMessage) m);
                }
            }
            
            // Sort by creation time (oldest first)
            chatMessages.sort((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
            bulletinMessages.sort((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
            
            return new Object[]{chatMessages, bulletinMessages};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }).thenAccept(result -> {
        if (result != null) {
            @SuppressWarnings("unchecked")
            List<ChatMessage> chatMessages = (List<ChatMessage>) result[0];
            @SuppressWarnings("unchecked")
            List<BulletinMessage> bulletinMessages = (List<BulletinMessage>) result[1];
            
            // Update UI on EDT
            SwingUtilities.invokeLater(() -> {
                chatListPanel.removeAll();
                bulletinListPanel.removeAll();
                
                // Batch add chat messages
                for (ChatMessage msg : chatMessages) {
                    addChatRow(msg);
                }
                
                // Batch add bulletin messages
                for (BulletinMessage msg : bulletinMessages) {
                    addBulletinRow(msg);
                }
                
                // Refresh panels
                chatListPanel.revalidate();
                chatListPanel.repaint();
                bulletinListPanel.revalidate();
                bulletinListPanel.repaint();
                
                // Scroll to bottom
                scrollToBottom(chatScrollPane);
                scrollToBottom(bulletinScrollPane);
                
                // Hide loading indicator
                if (loadingLabel != null) {
                    loadingLabel.setVisible(false);
                }
            });
        }
    });
}

// ==================== CHAT ROW - Profile picture on right for current user ====================
private void addChatRow(ChatMessage msg) {
    boolean mine = msg.getSender().getUserId().equals(currentUser.getUserId());
    
    JPanel row = new JPanel(new BorderLayout());
    row.setBackground(Color.WHITE);
    row.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
    row.setMaximumSize(new Dimension(500, 80));
    row.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Profile Picture - with caching
    String senderId = msg.getSender().getUserId();
    ImageIcon photoIcon = photoCache.get(senderId);
    if (photoIcon == null) {
        // Use the existing createUserPhotoLabel method but get the icon
        JLabel photoLabelTemp = photoUtils.createUserPhotoLabel(msg.getSender(), 36);
        if (photoLabelTemp.getIcon() != null) {
            photoIcon = (ImageIcon) photoLabelTemp.getIcon();
            photoCache.put(senderId, photoIcon);
        } else {
            // Create a default icon if none exists
            photoIcon = createDefaultPhotoIcon(36);
            photoCache.put(senderId, photoIcon);
        }
    }
    
    JLabel photoLabel = new JLabel(photoIcon);
    photoLabel.setPreferredSize(new Dimension(36, 36));
    
    // Message Bubble
    JPanel bubble = new JPanel();
    bubble.setLayout(new BorderLayout(5, 3));
    bubble.setBackground(mine ? CHAT_BUBBLE_ME : CHAT_BUBBLE_OTHER);
    bubble.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    
    // Sender name (only for others)
    if (!mine) {
        JLabel nameLabel = new JLabel(msg.getSender().getDisplayName());
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
        nameLabel.setForeground(ORANGE);
        bubble.add(nameLabel, BorderLayout.NORTH);
    }
    
    // Message content
    JTextArea contentArea = new JTextArea(msg.getContent());
    contentArea.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
    contentArea.setLineWrap(true);
    contentArea.setWrapStyleWord(true);
    contentArea.setEditable(false);
    contentArea.setBackground(bubble.getBackground());
    contentArea.setForeground(DARK_BROWN);
    contentArea.setBorder(null);
    contentArea.setRows(2);
    contentArea.setColumns(28);
    bubble.add(contentArea, BorderLayout.CENTER);
    
    // Time stamp
    JLabel timeLabel = new JLabel(formatTime(msg.getCreatedAt()));
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
    timeLabel.setForeground(Color.GRAY);
    bubble.add(timeLabel, BorderLayout.SOUTH);
    
    // Layout - Profile picture on RIGHT for current user's messages, aligned to far right
    if (mine) {
        JPanel rightAlignedWrapper = new JPanel(new BorderLayout());
        rightAlignedWrapper.setBackground(Color.WHITE);
        
        JPanel contentWithPhoto = new JPanel(new BorderLayout(8, 0));
        contentWithPhoto.setBackground(Color.WHITE);
        contentWithPhoto.add(bubble, BorderLayout.CENTER);
        contentWithPhoto.add(photoLabel, BorderLayout.EAST);
        
        rightAlignedWrapper.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        rightAlignedWrapper.add(contentWithPhoto, BorderLayout.EAST);
        
        row.add(rightAlignedWrapper, BorderLayout.CENTER);
    } else {
        JPanel leftWrapper = new JPanel(new BorderLayout(8, 0));
        leftWrapper.setBackground(Color.WHITE);
        leftWrapper.add(photoLabel, BorderLayout.WEST);
        leftWrapper.add(bubble, BorderLayout.CENTER);
        row.add(leftWrapper, BorderLayout.WEST);
        row.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
    }
    
    chatListPanel.add(row);
    chatListPanel.add(Box.createVerticalStrut(2));
}

// Helper method to create default photo icon
private ImageIcon createDefaultPhotoIcon(int size) {
    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = img.createGraphics();
    g2d.setColor(new Color(200, 180, 160));
    g2d.fillOval(0, 0, size, size);
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
    g2d.drawString("?", size / 3, size / 2 + size / 6);
    g2d.dispose();
    return new ImageIcon(img);
}

// ==================== BULLETIN ROW ====================
private void addBulletinRow(BulletinMessage msg) {
    JPanel row = new JPanel(new BorderLayout(6, 4));
    row.setBackground(LEADER_BOX);
    row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ORANGE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    row.setMaximumSize(new Dimension(380, 90));
    row.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(LEADER_BOX);
    
    JLabel titleLbl = new JLabel(msg.getTitle());
    titleLbl.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
    titleLbl.setForeground(GOLD);
    titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JTextArea contentArea = new JTextArea(msg.getContent());
    contentArea.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
    contentArea.setLineWrap(true);
    contentArea.setWrapStyleWord(true);
    contentArea.setEditable(false);
    contentArea.setBackground(LEADER_BOX);
    contentArea.setForeground(Color.WHITE);
    contentArea.setBorder(null);
    contentArea.setRows(2);
    contentArea.setColumns(25);
    contentArea.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    infoRow.setBackground(LEADER_BOX);
    
    JLabel senderLbl = new JLabel(msg.getSender().getDisplayName());
    senderLbl.setFont(new Font("Arial", Font.ITALIC, 10));
    senderLbl.setForeground(new Color(200, 185, 165));
    
    JLabel timeLbl = new JLabel(formatTime(msg.getCreatedAt()));
    timeLbl.setFont(new Font("Arial", Font.PLAIN, 9));
    timeLbl.setForeground(new Color(180, 165, 145));
    
    infoRow.add(senderLbl);
    infoRow.add(timeLbl);
    
    contentPanel.add(titleLbl);
    contentPanel.add(Box.createVerticalStrut(3));
    contentPanel.add(contentArea);
    contentPanel.add(Box.createVerticalStrut(3));
    contentPanel.add(infoRow);
    
    row.add(contentPanel, BorderLayout.CENTER);
    
    bulletinListPanel.add(row);
    bulletinListPanel.add(Box.createVerticalStrut(4));
}

    // ==================== HELPERS ====================
    private JButton makeSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(ORANGE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String formatTime(LocalDateTime dt) {
        if (dt == null) return "";
        LocalDate now = LocalDate.now();
        LocalDate msgDate = dt.toLocalDate();
        
        if (msgDate.equals(now)) {
            return dt.format(TIME_FMT);
        } else if (msgDate.equals(now.minusDays(1))) {
            return "Yesterday";
        } else {
            return msgDate.format(DATE_FMT);
        }
    }

    private void scrollToBottom(JScrollPane scrollPane) {
        if (scrollPane != null && scrollPane.getVerticalScrollBar() != null) {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        } else if (scrollPane != null && scrollPane.getHorizontalScrollBar() != null) {
            JScrollBar bar = scrollPane.getHorizontalScrollBar();
            bar.setValue(bar.getMaximum());
        }
    }
    
    private void scrollToRight(JScrollPane scrollPane) {
        if (scrollPane != null && scrollPane.getHorizontalScrollBar() != null) {
            JScrollBar bar = scrollPane.getHorizontalScrollBar();
            bar.setValue(bar.getMaximum());
        }
    }
}