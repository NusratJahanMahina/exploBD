//package com.ExploBD.presentation.components;
//
//import com.ExploBD.domain.entities.Notification;
//import com.ExploBD.domain.enums.NotificationType;
//import com.ExploBD.object.User;
//import com.ExploBD.presentation.frames.Friends;
//import com.ExploBD.presentation.frames.module2.MyGroupsFrame;
//import com.ExploBD.presentation.frames.BulletinFrame;
//import com.ExploBD.presentation.frames.ExpenseSplitUI;
//import com.ExploBD.presentation.frames.HomeFrame;
//import com.ExploBD.service.NotificationService;
//import com.ExploBD.domain.entities.Invitation;
//import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//public class NotificationPanel extends JDialog {
//    
//    private User currentUser;
//    private NotificationService notificationService;
//    private JPanel notificationsContainer;
//    private JLabel headerBadge;
//    private Runnable onNotificationRead;
//    private JButton bellButton;
//    private HomeFrame homeFrame;
//    
//    // Colors
//    private Color DARK_BROWN = new Color(70, 58, 47);
//    private Color ORANGE = new Color(255, 153, 51);
//    private Color GOLD = new Color(255, 215, 0);
//    private Color BG_WHITE = new Color(255, 255, 255);
//    private Color BORDER_LIGHT = new Color(220, 220, 220);
//    private Color UNREAD_BG = new Color(240, 248, 255);
//    private Color READ_BG = new Color(255, 255, 255);
//    private Color HOVER_COLOR = new Color(250, 250, 250);
//    
//    public NotificationPanel(HomeFrame parent, User user, JButton bellButton, Runnable onNotificationRead) {
//        super(parent, "Notifications", false);
//        this.currentUser = user;
//        this.homeFrame = parent;
//        this.notificationService = new NotificationService();
//        this.onNotificationRead = onNotificationRead;
//        this.bellButton = bellButton;
//        
//        setUndecorated(true);
//        setLayout(new BorderLayout());
//        
//        setSize(380, 480);
//        setPreferredSize(new Dimension(380, 480));
//        
//        // Position below the bell button
//        Point buttonLocation = bellButton.getLocationOnScreen();
//        setLocation(buttonLocation.x - 300, buttonLocation.y + bellButton.getHeight());
//        
//        initComponents();
//        loadNotifications();
//        
//        addWindowFocusListener(new WindowAdapter() {
//            public void windowLostFocus(WindowEvent e) {
//                dispose();
//            }
//        });
//    }
//    
//    private void initComponents() {
//        setBackground(BG_WHITE);
//        
//        JPanel headerPanel = createHeaderPanel();
//        add(headerPanel, BorderLayout.NORTH);
//        
//        notificationsContainer = new JPanel();
//        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
//        notificationsContainer.setBackground(BG_WHITE);
//        
//        JScrollPane scrollPane = new JScrollPane(notificationsContainer);
//        scrollPane.setBorder(BorderFactory.createEmptyBorder());
//        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        add(scrollPane, BorderLayout.CENTER);
//        
//        JPanel footerPanel = createFooterPanel();
//        add(footerPanel, BorderLayout.SOUTH);
//    }
//    
//    private JPanel createHeaderPanel() {
//        JPanel header = new JPanel(new BorderLayout());
//        header.setBackground(DARK_BROWN);
//        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
//        header.setPreferredSize(new Dimension(380, 45));
//        
//        JLabel titleLabel = new JLabel("NOTIFICATIONS");
//        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
//        titleLabel.setForeground(GOLD);
//        
//        int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
//        headerBadge = new JLabel(String.valueOf(unreadCount));
//        headerBadge.setFont(new Font("Arial", Font.BOLD, 10));
//        headerBadge.setForeground(Color.WHITE);
//        headerBadge.setBackground(ORANGE);
//        headerBadge.setOpaque(true);
//        headerBadge.setHorizontalAlignment(SwingConstants.CENTER);
//        headerBadge.setPreferredSize(new Dimension(22, 22));
//        headerBadge.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
//        headerBadge.setVisible(unreadCount > 0);
//        
//        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
//        rightPanel.setBackground(DARK_BROWN);
//        rightPanel.add(headerBadge);
//        
//        header.add(titleLabel, BorderLayout.WEST);
//        header.add(rightPanel, BorderLayout.EAST);
//        
//        return header;
//    }
//    
//    private JPanel createFooterPanel() {
//        JPanel footer = new JPanel();
//        footer.setBackground(new Color(248, 248, 248));
//        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT));
//        footer.setPreferredSize(new Dimension(380, 38));
//        
//        JButton markAllReadBtn = new JButton("Mark all as read");
//        markAllReadBtn.setFont(new Font("Arial", Font.PLAIN, 11));
//        markAllReadBtn.setForeground(DARK_BROWN);
//        markAllReadBtn.setBackground(new Color(248, 248, 248));
//        markAllReadBtn.setFocusPainted(false);
//        markAllReadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        markAllReadBtn.addActionListener(e -> markAllAsRead());
//        
//        footer.add(markAllReadBtn);
//        
//        return footer;
//    }
//    
//    private void loadNotifications() {
//        notificationsContainer.removeAll();
//        
//        List<Notification> notifications = notificationService.getUserNotifications(currentUser.getUserId());
//        
//        if (notifications.isEmpty()) {
//            JPanel emptyPanel = new JPanel();
//            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
//            emptyPanel.setBackground(BG_WHITE);
//            emptyPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
//            
//            JLabel emptyIcon = new JLabel("📭");
//            emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
//            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
//            
//            JLabel emptyLabel = new JLabel("No notifications");
//            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//            emptyLabel.setForeground(Color.GRAY);
//            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//            
//            emptyPanel.add(emptyIcon);
//            emptyPanel.add(Box.createVerticalStrut(8));
//            emptyPanel.add(emptyLabel);
//            
//            notificationsContainer.add(emptyPanel);
//        } else {
//            for (Notification notification : notifications) {
//                notificationsContainer.add(createNotificationCard(notification));
//                notificationsContainer.add(Box.createVerticalStrut(1));
//            }
//        }
//        
//        notificationsContainer.revalidate();
//        notificationsContainer.repaint();
//        
//        int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
//        if (headerBadge != null) {
//            headerBadge.setText(String.valueOf(unreadCount));
//            headerBadge.setVisible(unreadCount > 0);
//        }
//        
//        if (onNotificationRead != null) {
//            onNotificationRead.run();
//        }
//    }
//    
//    private JPanel createNotificationCard(Notification notification) {
//        boolean isUnread = !notification.isRead();
//        
//        JPanel card = new JPanel(new BorderLayout(10, 0));
//        card.setBackground(isUnread ? UNREAD_BG : READ_BG);
//        card.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
//            BorderFactory.createEmptyBorder(10, 12, 10, 12)
//        ));
//        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        
//        // Icon
//        JLabel iconLabel = new JLabel(getIconForType(notification.getType()));
//        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
//        iconLabel.setPreferredSize(new Dimension(35, 35));
//        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        
//        // Content panel
//        JPanel contentPanel = new JPanel();
//        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
//        contentPanel.setBackground(card.getBackground());
//        
//        JLabel titleLabel = new JLabel(notification.getTitle());
//        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 12));
//        titleLabel.setForeground(DARK_BROWN);
//        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        
//        JLabel messageLabel = new JLabel(notification.getMessage());
//        messageLabel.setFont(new Font("Arial", Font.PLAIN, 10));
//        messageLabel.setForeground(new Color(100, 100, 100));
//        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        
//        JLabel timeLabel = new JLabel(getTimeAgo(notification.getCreatedAt()));
//        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
//        timeLabel.setForeground(new Color(150, 150, 150));
//        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        
//        contentPanel.add(titleLabel);
//        contentPanel.add(Box.createVerticalStrut(2));
//        contentPanel.add(messageLabel);
//        contentPanel.add(Box.createVerticalStrut(3));
//        contentPanel.add(timeLabel);
//        
//        card.add(iconLabel, BorderLayout.WEST);
//        card.add(contentPanel, BorderLayout.CENTER);
//        
//        // Unread indicator
//        if (isUnread) {
//            JLabel unreadDot = new JLabel("●");
//            unreadDot.setFont(new Font("Arial", Font.BOLD, 10));
//            unreadDot.setForeground(ORANGE);
//            unreadDot.setPreferredSize(new Dimension(12, 12));
//            card.add(unreadDot, BorderLayout.EAST);
//        }
//        
//        // Click handler
//        card.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (isUnread) {
//                    notificationService.markAsRead(notification.getNotificationId());
//                    loadNotifications();
//                }
//                handleNotificationClick(notification);
//                dispose();
//            }
//            
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                card.setBackground(HOVER_COLOR);
//            }
//            
//            @Override
//            public void mouseExited(MouseEvent e) {
//                card.setBackground(isUnread ? UNREAD_BG : READ_BG);
//            }
//        });
//        
//        return card;
//    }
//    
//    private String getIconForType(NotificationType type) {
//        switch (type) {
//            case FRIEND_REQUEST: return "👤";
//            case GROUP_INVITATION: return "👥";
//            case INVITATION_APPROVED: return "✓";
//            case MEMBER_JOINED: return "➕";
//            case VOTE_STARTED: return "🗳️";
//            case DESTINATION_CONFIRMED: return "📍";
//            case GROUP_CREATED: return "✨";
//            case EXPENSE_ADDED: return "💰";
//            case SETTLEMENT_REMINDER: return "💸";
//            case NEW_SUGGESTION: return "💡";
//            case JOIN_REQUEST: return "📨";
//            default: return "📌";
//        }
//    }
//    
//    private String getTimeAgo(java.util.Date date) {
//        LocalDateTime then = date.toInstant()
//            .atZone(java.time.ZoneId.systemDefault())
//            .toLocalDateTime();
//        LocalDateTime now = LocalDateTime.now();
//        
//        long seconds = ChronoUnit.SECONDS.between(then, now);
//        long minutes = ChronoUnit.MINUTES.between(then, now);
//        long hours = ChronoUnit.HOURS.between(then, now);
//        long days = ChronoUnit.DAYS.between(then, now);
//        
//        if (seconds < 60) {
//            return "Just now";
//        } else if (minutes < 60) {
//            return minutes + " min ago";
//        } else if (hours < 24) {
//            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
//        } else {
//            return days + " day" + (days > 1 ? "s" : "") + " ago";
//        }
//    }
//    
//    private void handleNotificationClick(Notification notification) {
//        switch (notification.getType()) {
//            case FRIEND_REQUEST:
//                new Friends(currentUser).setVisible(true);
//                break;
//                
//            case GROUP_INVITATION:
//                InvitationDatabaseObject invDAO = new InvitationDatabaseObject();
//                Invitation invitation = invDAO.findByInvitationCode(notification.getRelatedId());
//                if (invitation != null && homeFrame != null) {
//                    new InvitationDialog(homeFrame, currentUser, invitation).setVisible(true);
//                }
//                break;
//                
//            case MEMBER_JOINED:
//            case VOTE_STARTED:
//            case DESTINATION_CONFIRMED:
//            case GROUP_CREATED:
//            case JOIN_REQUEST:
//            case NEW_SUGGESTION:
//                new MyGroupsFrame(currentUser).setVisible(true);
//                break;
//                
//            case EXPENSE_ADDED:
//            case SETTLEMENT_REMINDER:
//                new ExpenseSplitUI(currentUser).setVisible(true);
//                break;
//                
//            default:
//                new BulletinFrame(currentUser).setVisible(true);
//                break;
//        }
//    }
//    
//    private void markAllAsRead() {
//        notificationService.markAllAsRead(currentUser.getUserId());
//        loadNotifications();
//    }
//}
package com.ExploBD.presentation.components;

import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.object.User;
import com.ExploBD.presentation.frames.Friends;
import com.ExploBD.presentation.frames.module2.MyGroupsFrame;
import com.ExploBD.presentation.frames.BulletinFrame;
import com.ExploBD.presentation.frames.ExpenseSplitUI;
import com.ExploBD.presentation.frames.HomeFrame;
import com.ExploBD.service.NotificationService;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.presentation.frames.PublicGroupsFrame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.presentation.frames.HomeFrame;

public class NotificationPanel extends JDialog {

    private User currentUser;
    private NotificationService notificationService;

    private JPanel notificationsContainer;
    private JLabel headerBadge;
    private Runnable onNotificationRead;
    private JButton bellButton;
    private HomeFrame homeFrame;

    // Enhanced color palette
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color WARM_ORANGE = new Color(255, 140, 26);
    private Color GOLD = new Color(255, 215, 0);
    private Color BG_WHITE = new Color(255, 255, 255);
    private Color BORDER_LIGHT = new Color(220, 220, 220);
    private Color UNREAD_BG = new Color(255, 248, 240);      // Warm cream for unread
    private Color READ_BG = new Color(255, 255, 255);
    private Color HOVER_COLOR = new Color(255, 240, 230);     // Soft orange hover
    private Color HEADER_BG = new Color(88, 74, 60);          // Brown header
    private Color CARD_BORDER = new Color(255, 200, 150);     // Orange border for cards

    public NotificationPanel(HomeFrame parent, User user, JButton bellButton, Runnable onNotificationRead) {
        super(parent, "Notifications", false);
        this.currentUser = user;
        this.homeFrame = parent;
        this.notificationService = new NotificationService();
        this.onNotificationRead = onNotificationRead;
        this.bellButton = bellButton;

        setUndecorated(true);
        setLayout(new BorderLayout());

        setSize(380, 480);
        setPreferredSize(new Dimension(380, 480));

        // Position below the bell button
        Point buttonLocation = bellButton.getLocationOnScreen();
        setLocation(buttonLocation.x - 300, buttonLocation.y + bellButton.getHeight());

        initComponents();
        loadNotifications();

        addWindowFocusListener(new WindowAdapter() {
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });
    }

    private void initComponents() {
        setBackground(BG_WHITE);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setBackground(new Color(250, 248, 245));

        JScrollPane scrollPane = new JScrollPane(notificationsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(new Color(250, 248, 245));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        header.setPreferredSize(new Dimension(380, 45));

        JLabel titleLabel = new JLabel("NOTIFICATIONS");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        titleLabel.setForeground(GOLD);

        int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
        headerBadge = new JLabel(String.valueOf(unreadCount));
        headerBadge.setFont(new Font("Arial", Font.BOLD, 10));
        headerBadge.setForeground(Color.WHITE);
        headerBadge.setBackground(WARM_ORANGE);
        headerBadge.setOpaque(true);
        headerBadge.setHorizontalAlignment(SwingConstants.CENTER);
        headerBadge.setPreferredSize(new Dimension(22, 22));
        headerBadge.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        headerBadge.setVisible(unreadCount > 0);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setBackground(HEADER_BG);
        rightPanel.add(headerBadge);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(245, 240, 235));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ORANGE));
        footer.setPreferredSize(new Dimension(380, 38));

        JButton markAllReadBtn = new JButton("✓ Mark all as read");
        markAllReadBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        markAllReadBtn.setForeground(DARK_BROWN);
        markAllReadBtn.setBackground(new Color(245, 240, 235));
        markAllReadBtn.setFocusPainted(false);
        markAllReadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        markAllReadBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        markAllReadBtn.addActionListener(e -> markAllAsRead());

        footer.add(markAllReadBtn);

        return footer;
    }

    private void loadNotifications() {
        notificationsContainer.removeAll();

        List<Notification> notifications = notificationService.getUserNotifications(currentUser.getUserId());

        if (notifications.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setBackground(new Color(250, 248, 245));
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

            JLabel emptyIcon = new JLabel("📭");
            emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyLabel = new JLabel("No notifications");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(8));
            emptyPanel.add(emptyLabel);

            notificationsContainer.add(emptyPanel);
        } else {
            for (Notification notification : notifications) {
                notificationsContainer.add(createNotificationCard(notification));
                notificationsContainer.add(Box.createVerticalStrut(1));
            }
        }

        notificationsContainer.revalidate();
        notificationsContainer.repaint();

        int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
        if (headerBadge != null) {
            headerBadge.setText(String.valueOf(unreadCount));
            headerBadge.setVisible(unreadCount > 0);
        }

        if (onNotificationRead != null) {
            onNotificationRead.run();
        }
    }

    private JPanel createNotificationCard(Notification notification) {
        boolean isUnread = !notification.isRead();

        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(isUnread ? UNREAD_BG : READ_BG);

        // Fixed height - doesn't stretch
        card.setMaximumSize(new Dimension(380, 85));
        card.setPreferredSize(new Dimension(380, 85));
        card.setMinimumSize(new Dimension(380, 85));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Left: Icon with colored background for unread
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(true);
        iconPanel.setBackground(isUnread ? new Color(255, 220, 200) : new Color(240, 240, 240));
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setBorder(BorderFactory.createLineBorder(isUnread ? ORANGE : new Color(200, 200, 200), 1));

        JLabel iconLabel = new JLabel(getIconForType(notification.getType()));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        // Center: Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(card.getBackground());

        JLabel titleLabel = new JLabel(notification.getTitle());
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 12));
        titleLabel.setForeground(DARK_BROWN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel messageLabel = new JLabel(notification.getMessage());
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        messageLabel.setForeground(new Color(100, 100, 100));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel timeLabel = new JLabel(getTimeAgo(notification.getCreatedAt()));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        timeLabel.setForeground(ORANGE);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(timeLabel);

        // Right: Unread indicator
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(20, 40));

        if (isUnread) {
            JLabel unreadDot = new JLabel("●");
            unreadDot.setFont(new Font("Arial", Font.BOLD, 10));
            unreadDot.setForeground(WARM_ORANGE);
            rightPanel.add(unreadDot, BorderLayout.NORTH);
        }

        card.add(iconPanel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        // Click handler
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isUnread) {
                    notificationService.markAsRead(notification.getNotificationId());
                    loadNotifications();
                }
                handleNotificationClick(notification);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER_COLOR);
                contentPanel.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(isUnread ? UNREAD_BG : READ_BG);
                contentPanel.setBackground(isUnread ? UNREAD_BG : READ_BG);
            }
        });

        return card;
    }

    private String getIconForType(NotificationType type) {
        switch (type) {
            case FRIEND_REQUEST:
                return "👤";
            case GROUP_INVITATION:
                return "👥";
            case INVITATION_APPROVED:
                return "✓";
            case MEMBER_JOINED:
                return "➕";
            case VOTE_STARTED:
                return "🗳️";
            case DESTINATION_CONFIRMED:
                return "📍";
            case GROUP_CREATED:
                return "✨";
            case EXPENSE_ADDED:
                return "💰";
            case SETTLEMENT_REMINDER:
                return "💸";
            case NEW_SUGGESTION:
                return "💡";
            case JOIN_REQUEST:
                return "📨";
            default:
                return "📌";
        }
    }

    private String getTimeAgo(java.util.Date date) {
        LocalDateTime then = date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        long seconds = ChronoUnit.SECONDS.between(then, now);
        long minutes = ChronoUnit.MINUTES.between(then, now);
        long hours = ChronoUnit.HOURS.between(then, now);
        long days = ChronoUnit.DAYS.between(then, now);

        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
    }

    private void handleNotificationClick(Notification notification) {
        switch (notification.getType()) {
            case FRIEND_REQUEST:
                Friends friendsReqFrame = new Friends(currentUser);
                friendsReqFrame.setVisible(true);
                friendsReqFrame.showRequestsTab();
                break;

            case FRIEND_REQUEST_ACCEPTED:
                Friends friendsAccFrame = new Friends(currentUser);
                friendsAccFrame.setVisible(true);
                friendsAccFrame.showFriendsTab();
                break;

            case GROUP_INVITATION:
                try {
                    InvitationDatabaseObject invDAO = new InvitationDatabaseObject();
                    Invitation invitation = invDAO.findByInvitationCode(notification.getRelatedId());
                    if (invitation != null && homeFrame != null) {
                        InvitationDialog dialog = new InvitationDialog(
                                homeFrame,
                                currentUser,
                                invitation,
                                () -> {
                                    if (onNotificationRead != null) {
                                        onNotificationRead.run();
                                    }
                                },
                                () -> {
                                    if (onNotificationRead != null) {
                                        onNotificationRead.run();
                                    }
                                }
                        );
                        dialog.setVisible(true);
                    } else {
                        new PublicGroupsFrame(currentUser).setVisible(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new PublicGroupsFrame(currentUser).setVisible(true);
                }
                break;

            case MEMBER_JOINED:
                // Open MyGroupsFrame and show Members tab (tab index 1)
                MyGroupsFrame memberFrame = new MyGroupsFrame(currentUser);
                memberFrame.setVisible(true);
                memberFrame.showMembersTab();
                break;

            case VOTE_STARTED:
                // Open MyGroupsFrame and show Vote tab (tab index 3)
                MyGroupsFrame voteFrame = new MyGroupsFrame(currentUser);
                voteFrame.setVisible(true);
                voteFrame.showVoteTab();
                break;

            case JOIN_REQUEST:
                // Open MyGroupsFrame and show Members tab (to see pending requests)
                MyGroupsFrame requestFrame = new MyGroupsFrame(currentUser);
                requestFrame.setVisible(true);
                requestFrame.showMembersTab();
                break;

            case NEW_SUGGESTION:
                // Open MyGroupsFrame and show Suggest tab (tab index 2)
                MyGroupsFrame suggestFrame = new MyGroupsFrame(currentUser);
                suggestFrame.setVisible(true);
                suggestFrame.showSuggestTab();
                break;

            case DESTINATION_CONFIRMED:
                // Open MyGroupsFrame default (Plan tab)
                MyGroupsFrame destFrame = new MyGroupsFrame(currentUser);
                destFrame.setVisible(true);
                break;

            case GROUP_CREATED:
                // Open MyGroupsFrame default
                MyGroupsFrame groupFrame = new MyGroupsFrame(currentUser);
                groupFrame.setVisible(true);
                break;

            case EXPENSE_ADDED:
            case SETTLEMENT_REMINDER:
                new ExpenseSplitUI(currentUser).setVisible(true);
                break;

            default:
                new BulletinFrame(currentUser).setVisible(true);
                break;
        }
    }

//    private void handleNotificationClick(Notification notification) {
//        switch (notification.getType()) {
//            case FRIEND_REQUEST:
//                new Friends(currentUser).setVisible(true);
//                break;
//                
//            case GROUP_INVITATION:
//                InvitationDatabaseObject invDAO = new InvitationDatabaseObject();
//                Invitation invitation = invDAO.findByInvitationCode(notification.getRelatedId());
//                if (invitation != null && homeFrame != null) {
//                    new InvitationDialog(homeFrame, currentUser, invitation).setVisible(true);
//                }
//                break;
//                
//            case MEMBER_JOINED:
//            case VOTE_STARTED:
//            case DESTINATION_CONFIRMED:
//            case GROUP_CREATED:
//            case JOIN_REQUEST:
//            case NEW_SUGGESTION:
//                new MyGroupsFrame(currentUser).setVisible(true);
//                break;
//                
//            case EXPENSE_ADDED:
//            case SETTLEMENT_REMINDER:
//                new ExpenseSplitUI(currentUser).setVisible(true);
//                break;
//                
//            default:
//                new BulletinFrame(currentUser).setVisible(true);
//                break;
//        }
//    }
    private void markAllAsRead() {
        notificationService.markAllAsRead(currentUser.getUserId());
        loadNotifications();
    }
}
