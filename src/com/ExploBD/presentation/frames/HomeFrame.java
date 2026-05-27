package com.ExploBD.presentation.frames;

import com.ExploBD.presentation.frames.module1.DivisionExploreFrame;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.object.User;
import com.ExploBD.service.*;

import com.ExploBD.Frame.RoundedPanel;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Tour;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.object.Place;
import com.ExploBD.presentation.components.ActiveTourMainPanel;
import com.ExploBD.presentation.components.ChecklistDialog;
import com.ExploBD.presentation.components.InvitationDialog;
import com.ExploBD.presentation.components.NotificationPanel;
//import com.ExploBD.presentation.components.NotificationPanel;

import com.ExploBD.presentation.frames.module2.CreateGroupFrame;
import com.ExploBD.presentation.frames.module2.MyGroupsFrame;

import com.ExploBD.session.UserSession;
import com.ExploBD.util.RefreshManager;
import java.awt.*;
import com.ExploBD.presentation.frames.EditUserProfile;
import com.ExploBD.service.ExpenseService;
import com.ExploBD.util.ProfilePhotoUtils;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import javax.swing.Timer;

public class HomeFrame extends javax.swing.JFrame implements RefreshManager.RefreshListener {

    private User currentUser;
    private ExpenseService expenseService;

    private boolean isProcessingInvitations = false;
    private String lastProcessedInvitationId = "";
    private JLabel welcomeLabel;
    private JButton createGroupBtn;

    private javax.swing.JPanel sidePanel;
    private boolean isSidebarOpen = false;
    private javax.swing.Timer sidebarTimer;
    private final int SIDEBAR_WIDTH = 260;

    private JLabel badgeLabel;

    public HomeFrame(User user) {
        this.currentUser = user;
        this.expenseService = new ExpenseService();
        initComponents();
        initSidebar();
// After all initialization
        updateNotificationBadge();
        this.pack();
        this.setLocationRelativeTo(null);

        updateWelcomeMessage();

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(15);
        // jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        jScrollPane2.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        Mytravelscrool.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        // tourScrool.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        jScrollPane1.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        mainpanel.setLayout(new javax.swing.BoxLayout(mainpanel, javax.swing.BoxLayout.Y_AXIS));
        refreshLayoutWithGaps(2);
        styleDivisionButtons();

        // Add this in constructor - MOST RELIABLE METHOD
        InputMap inputMap = jScrollPane1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = jScrollPane1.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "scrollDown");
        inputMap.put(KeyStroke.getKeyStroke("UP"), "scrollUp");

        // Clear placeholder text when focus gained
        // Setup search field placeholder behavior
        searchfor.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchfor.getText().equals("search for.....")) {
                    searchfor.setText("search for......");
                    searchfor.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchfor.getText().isEmpty()) {
                    searchfor.setText("search for.....");
                    searchfor.setForeground(Color.GRAY);
                }
            }
        });
        search.addActionListener(e -> performSearch());
        searchfor.addActionListener(e -> performSearch());
// Add action listener

        notification.addActionListener(e -> showNotificationPanel());
        actionMap.put("scrollDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JScrollBar vbar = jScrollPane1.getVerticalScrollBar();
                vbar.setValue(vbar.getValue() + 50);
            }
        });

        actionMap.put("scrollUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JScrollBar vbar = jScrollPane1.getVerticalScrollBar();
                vbar.setValue(vbar.getValue() - 50);
            }
        });

        RefreshManager.getInstance().register(this);

        AppConfig.applyAndTrack(this);

        startImageSlideshowOnPanel(pictureframe);

        setupActiveTour();

        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                RefreshManager.getInstance().refreshAll();
            }
        });

        Rajshahi.addActionListener(e -> new DivisionExploreFrame(currentUser, "Rajshahi", false).setVisible(true));
        rangpur.addActionListener(e -> new DivisionExploreFrame(currentUser, "Rangpur", false).setVisible(true));
        sylhet.addActionListener(e -> new DivisionExploreFrame(currentUser, "Sylhet", false).setVisible(true));
        kulna.addActionListener(e -> new DivisionExploreFrame(currentUser, "Khulna", false).setVisible(true));

    }

// Add this method to HomeFrame
    private void showNotificationPanel() {
        NotificationPanel panel = new NotificationPanel(
                this,
                currentUser,
                notification,
                () -> updateNotificationBadge()
        );
        panel.setVisible(true);
    }

    private void testCreateNotification() {
        NotificationService service = new NotificationService();
        service.createNotification(
                currentUser,
                NotificationType.GROUP_INVITATION,
                "Test Notification",
                "This is a test notification to check if system works",
                "test123"
        );
        System.out.println("Test notification created!");
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        NotificationService service = new NotificationService();
        int unreadCount = service.getUnreadCount(currentUser.getUserId());

        JLayeredPane layeredPane = getLayeredPane();

        if (badgeLabel == null) {
            badgeLabel = new JLabel();
            badgeLabel.setFont(new Font("Arial", Font.BOLD, 9));
            badgeLabel.setForeground(Color.WHITE);
            badgeLabel.setBackground(new Color(220, 53, 69));
            badgeLabel.setOpaque(true);
            badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            badgeLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

            layeredPane.add(badgeLabel, JLayeredPane.POPUP_LAYER);

            // Update badge position when scrolling
            jScrollPane1.getViewport().addChangeListener(e -> {
                if (badgeLabel != null && badgeLabel.isVisible()) {
                    positionBadgeOnButton();
                }
            });

            // Update badge position when window moves or resizes
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentMoved(java.awt.event.ComponentEvent e) {
                    if (badgeLabel != null && badgeLabel.isVisible()) {
                        positionBadgeOnButton();
                    }
                }

                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    if (badgeLabel != null && badgeLabel.isVisible()) {
                        positionBadgeOnButton();
                    }
                }
            });
        }

        if (unreadCount > 0) {
            badgeLabel.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
            badgeLabel.setVisible(true);
            positionBadgeOnButton();
        } else {
            badgeLabel.setVisible(false);
        }
    }

    private void positionBadgeOnButton() {
        try {
            if (badgeLabel == null || !badgeLabel.isVisible()) {
                return;
            }

            Point btnLoc = notification.getLocationOnScreen();
            Point frameLoc = getLocationOnScreen();

            // Position at top-right corner of the button
            int x = btnLoc.x - frameLoc.x + notification.getWidth() - 22;
            int y = btnLoc.y - frameLoc.y - 20;

            badgeLabel.setBounds(x, y, 18, 18);
        } catch (IllegalComponentStateException e) {
            // Component not ready yet
        }

    }

//private void updateNotificationBadge() {
//    // Don't run if frame is not visible yet
//    if (!isShowing()) {
//        return;
//    }
//    
//    NotificationService service = new NotificationService();
//    int unreadCount = service.getUnreadCount(currentUser.getUserId());
//    
//    JLayeredPane layeredPane = getLayeredPane();
//    
//    if (badgeLabel == null) {
//        badgeLabel = new JLabel();
//        badgeLabel.setFont(new Font("Arial", Font.BOLD, 9));
//        badgeLabel.setForeground(Color.WHITE);
//        badgeLabel.setBackground(new Color(220, 53, 69));
//        badgeLabel.setOpaque(true);
//        badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        badgeLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
//        
//        layeredPane.add(badgeLabel, JLayeredPane.POPUP_LAYER);
//    }
//    
//    if (unreadCount > 0) {
//        badgeLabel.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
//        badgeLabel.setVisible(true);
//        
//        try {
//            // Position ABOVE the button (5px higher)
//            Point btnLoc = notification.getLocationOnScreen();
//            Point frameLoc = getLocationOnScreen();
//            int x = btnLoc.x - frameLoc.x + notification.getWidth() - 15;
//            int y = btnLoc.y - frameLoc.y - 20;
//            badgeLabel.setBounds(x, y, 18, 18);
//        } catch (IllegalComponentStateException e) {
//            // Component not yet showing, schedule retry
//            SwingUtilities.invokeLater(() -> updateNotificationBadge());
//        }
//        
//    } else {
//        badgeLabel.setVisible(false);
//    }
//}
//private void updateNotificationBadge() {
//    NotificationService service = new NotificationService();
//    int unreadCount = service.getUnreadCount(currentUser.getUserId());
//    
//    // Get the parent layered pane
//    JLayeredPane layeredPane = getLayeredPane();
//    
//    if (badgeLabel == null) {
//        badgeLabel = new JLabel();
//        badgeLabel.setFont(new Font("Arial", Font.BOLD, 9));
//        badgeLabel.setForeground(Color.WHITE);
//        badgeLabel.setBackground(new Color(220, 53, 69));
//        badgeLabel.setOpaque(true);
//        badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        badgeLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
//        
//        // Add to layered pane with higher layer
//        layeredPane.add(badgeLabel, JLayeredPane.POPUP_LAYER);
//    }
//    
//    if (unreadCount > 0) {
//        badgeLabel.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
//        badgeLabel.setVisible(true);
//        
//        // Calculate position relative to the notification button
//        Point btnLoc = notification.getLocationOnScreen();
//        Point frameLoc = getLocationOnScreen();
//        int x = btnLoc.x - frameLoc.x + notification.getWidth() - 18;
//        int y = btnLoc.y - frameLoc.y - 5;
//        
//        badgeLabel.setBounds(x, y, 18, 18);
//        
//        // Reposition when window moves or resizes
//        addComponentListener(new java.awt.event.ComponentAdapter() {
//            @Override
//            public void componentMoved(java.awt.event.ComponentEvent e) {
//                repositionBadge();
//            }
//            @Override
//            public void componentResized(java.awt.event.ComponentEvent e) {
//                repositionBadge();
//            }
//        });
//        
//    } else {
//        badgeLabel.setVisible(false);
//    }
//}
//
//private void repositionBadge() {
//    if (badgeLabel != null && badgeLabel.isVisible()) {
//        Point btnLoc = notification.getLocationOnScreen();
//        Point frameLoc = getLocationOnScreen();
//        int x = btnLoc.x - frameLoc.x + notification.getWidth() - 18;
//        int y = btnLoc.y - frameLoc.y - 5;
//        badgeLabel.setBounds(x, y, 18, 18);
//    }
//}
//    private JButton createTourButton(String text) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Arial", Font.BOLD, 16));  // Slightly larger font
//        btn.setBackground(new Color(100, 86, 72));
//        btn.setForeground(new Color(255, 215, 0));
//        btn.setFocusPainted(false);
//        btn.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(180, 160, 140), 3), // Thicker border
//                BorderFactory.createEmptyBorder(15, 20, 15, 20) // More padding
//        ));
//
//        // Make buttons fill available space
//        btn.setPreferredSize(new Dimension(280, 240));  // Even wider
//        btn.setMinimumSize(new Dimension(250, 240));
//        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 240));
//
//        btn.setVerticalTextPosition(SwingConstants.CENTER);
//        btn.setHorizontalTextPosition(SwingConstants.CENTER);
//
//        btn.addMouseListener(new java.awt.event.MouseAdapter() {
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                btn.setBackground(new Color(120, 106, 92));
//            }
//
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                btn.setBackground(new Color(100, 86, 72));
//            }
//        });
//
//        return btn;
//    }
    //morning
    private JButton createTourButton(String text, String imagePath) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Bookman Old Style", Font.BOLD, 26));
        btn.setForeground(new Color(100, 66, 32));   // Light warm text
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setFocusPainted(false);

        // Load and set the icon
        try {
            java.net.URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                // Scale image to fit button
                Image scaledImage = originalIcon.getImage().getScaledInstance(260, 200, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
        }

        // Dark brown background with slight transparency to show image behind
        btn.setBackground(new Color(70, 50, 35, 200));  // Bright dark brown with opacity
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        // Border styling
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 140, 100), 3),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Keep original size constraints - slightly larger
        btn.setPreferredSize(new Dimension(280, 250));
        btn.setMinimumSize(new Dimension(260, 240));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 250));

        // Add hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(30, 20, 15));   // Lighter brown on hover
                btn.setForeground(new Color(70, 58, 47));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 190, 120), 3),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 50, 35, 200));
                btn.setForeground(new Color(30, 20, 15));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 140, 100), 3),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return btn;
    }

    private void setupActiveTour() {
        jPanel4.removeAll();
        jPanel4.setLayout(new BorderLayout(0, 0));
        jPanel4.setBackground(new Color(88, 74, 60));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(88, 74, 60));

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.fill = GridBagConstraints.BOTH;
        wrapperGbc.weighty = 1.0;
        wrapperGbc.anchor = GridBagConstraints.CENTER;

        // LEFT PANEL - Fixed width
        wrapperGbc.gridx = 0;
        wrapperGbc.weightx = 0.0;
        wrapperGbc.insets = new Insets(10, 10, 10, 20);

        ActiveTourMainPanel mainPanel = new ActiveTourMainPanel(currentUser);
        wrapper.add(mainPanel, wrapperGbc);

        // RIGHT PANEL - Takes all remaining space
        wrapperGbc.gridx = 1;
        wrapperGbc.weightx = 3.0;
        wrapperGbc.insets = new Insets(0, 0, 0, 0);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(88, 74, 60));

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.fill = GridBagConstraints.BOTH;
        buttonGbc.weighty = 1.0;
        buttonGbc.insets = new Insets(10, 20, 10, 20);

        JButton todoBtn = createTourButton("TO-DO", "/images/todolist.png");
        JButton bulletinBtn = createTourButton("Messages", "/images/message.png");
        JButton splitwiseBtn = createTourButton("SPLIT UP", "/images/spitup.png");

        todoBtn.addActionListener(e -> {
            Group activeGroup = findActiveGroup();

            if (activeGroup != null) {
                boolean isLeader = activeGroup.isLeader(currentUser);

                ChecklistManager<Object> manager = new ChecklistManager<>(
                        activeGroup.getId(), // tourId (using group ID)
                        currentUser,
                        isLeader
                );

                ChecklistDialog<Object> dialog = new ChecklistDialog<>(
                        this,
                        "TO-DO - " + activeGroup.getName(),
                        manager,
                        currentUser,
                        isLeader
                );
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No active tour found!");
            }
        });

        // BULLETIN Button - Opens Messages
        bulletinBtn.addActionListener(e -> {
            Group activeGroup = findActiveGroup();

            if (activeGroup != null) {
                try {
                    BulletinFrame bulletinFrame = new BulletinFrame(currentUser);
                    bulletinFrame.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error opening Bulletin: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No active tour found!\n\nPlease create or join a confirmed group first.",
                        "No Active Tour",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // SPLITWISE Button - Opens Expense Split
        splitwiseBtn.addActionListener(e -> {
            Group activeGroup = findActiveGroup();

            if (activeGroup != null) {
                try {
                    // First, check if expense tour exists for this group
                    ExpenseService expenseService = new ExpenseService();
                    Tour existingTour = expenseService.getActiveTour();

                    if (existingTour == null) {
                        // Ask if user wants to create expense tour
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "No expense tour found for this group.\n\n"
                                + "Would you like to create one now?",
                                "Create Expense Tour",
                                JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            String tourName = JOptionPane.showInputDialog(this,
                                    "Enter tour name:",
                                    activeGroup.getName() + " Expenses");
                            if (tourName != null && !tourName.trim().isEmpty()) {
                                expenseService.createTourFromGroup(activeGroup.getId(), tourName);
                            }
                        }
                    }

                    ExpenseSplitUI expenseUI = new ExpenseSplitUI(currentUser);
                    expenseUI.setVisible(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error opening Expense Split: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No active tour found!\n\nPlease create or join a confirmed group first.",
                        "No Active Tour",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add buttons to panel
        buttonGbc.gridx = 0;
        buttonGbc.weightx = 1.0;
        buttonPanel.add(todoBtn, buttonGbc);

        buttonGbc.gridx = 1;
        buttonGbc.weightx = 1.0;
        buttonPanel.add(bulletinBtn, buttonGbc);

        buttonGbc.gridx = 2;
        buttonGbc.weightx = 1.0;
        buttonPanel.add(splitwiseBtn, buttonGbc);

        wrapper.add(buttonPanel, wrapperGbc);

        jPanel4.add(wrapper, BorderLayout.CENTER);

        jPanel4.revalidate();
        jPanel4.repaint();

        SwingUtilities.invokeLater(() -> {
            if (tourScrool != null) {
                tourScrool.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                tourScrool.getHorizontalScrollBar().setUnitIncrement(30);
                tourScrool.revalidate();
                tourScrool.repaint();
            }
        });
    }

// Add this helper method to find the active group
    private Group findActiveGroup() {
        if (currentUser == null) {
            return null;
        }

        GroupDatabaseObject groupDAO = new GroupDatabaseObject();
        List<Group> myGroups = groupDAO.findByUser(currentUser.getUserId());

        if (myGroups == null || myGroups.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();

        // Priority 1: Ongoing confirmed trip (happening right now)
        for (Group g : myGroups) {
            if (g.getStatus() == GroupStatus.CONFIRMED && g.getStartDate() != null && g.getEndDate() != null) {
                if (!today.isBefore(g.getStartDate()) && !today.isAfter(g.getEndDate())) {
                    return g;
                }
            }
        }

        // Priority 2: Most recent confirmed trip (by start date)
        Group mostRecentConfirmed = null;
        LocalDate latestDate = null;

        for (Group g : myGroups) {
            if (g.getStatus() == GroupStatus.CONFIRMED && g.getStartDate() != null) {
                if (latestDate == null || g.getStartDate().isAfter(latestDate)) {
                    latestDate = g.getStartDate();
                    mostRecentConfirmed = g;
                }
            }
        }

        return mostRecentConfirmed;
    }

//    private void setupActiveTour() {
//        jPanel4.removeAll();
//        jPanel4.setLayout(new BorderLayout(0, 0));
//        jPanel4.setBackground(new Color(88, 74, 60));
//
//        JPanel wrapper = new JPanel(new GridBagLayout());
//        wrapper.setBackground(new Color(88, 74, 60));
//
//        GridBagConstraints wrapperGbc = new GridBagConstraints();
//        wrapperGbc.fill = GridBagConstraints.BOTH;
//        wrapperGbc.weighty = 1.0;
//        wrapperGbc.anchor = GridBagConstraints.CENTER;
//
//        // LEFT PANEL - Fixed width
//        wrapperGbc.gridx = 0;
//        wrapperGbc.weightx = 0.0;
//        wrapperGbc.insets = new Insets(10, 10, 10, 20);
//
//        ActiveTourMainPanel mainPanel = new ActiveTourMainPanel(currentUser);
//        wrapper.add(mainPanel, wrapperGbc);
//
//        // RIGHT PANEL - Takes all remaining space
//        wrapperGbc.gridx = 1;
//        wrapperGbc.weightx = 3.0;  // Give maximum space to buttons
//        wrapperGbc.insets = new Insets(0, 0, 0, 0);
//
//        JPanel buttonPanel = new JPanel(new GridBagLayout());
//        buttonPanel.setBackground(new Color(88, 74, 60));
//
//        GridBagConstraints buttonGbc = new GridBagConstraints();
//        buttonGbc.fill = GridBagConstraints.BOTH;
//        buttonGbc.weighty = 1.0;
//        buttonGbc.insets = new Insets(10, 20, 10, 20);  // Generous spacing
//
//        JButton todoBtn = createTourButton("TO-DO");
//        JButton bulletinBtn = createTourButton("BULLETIN");
//        JButton splitwiseBtn = createTourButton("SPLITWISE");
//
//        todoBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "To-Do List coming soon!"));
//        bulletinBtn.addActionListener(e -> {
//    try {
//        BulletinFrame bulletinFrame = new BulletinFrame(currentUser);
//        bulletinFrame.setVisible(true);
//    } catch (Exception ex) {
//        ex.printStackTrace();
//        JOptionPane.showMessageDialog(this, "Error opening Bulletin: " + ex.getMessage());
//    }
//});
//        splitwiseBtn.addActionListener(e -> {
//    try {
//        ExpenseSplitUI expenseUI = new ExpenseSplitUI(currentUser);
//        expenseUI.setVisible(true);
//    } catch (Exception ex) {
//        ex.printStackTrace();
//        JOptionPane.showMessageDialog(this, "Error opening Expense Split: " + ex.getMessage());
//    }
//});
//
//        // Each button gets equal weight
//        buttonGbc.gridx = 0;
//        buttonGbc.weightx = 1.0;
//        buttonPanel.add(todoBtn, buttonGbc);
//
//        buttonGbc.gridx = 1;
//        buttonGbc.weightx = 1.0;
//        buttonPanel.add(bulletinBtn, buttonGbc);
//
//        buttonGbc.gridx = 2;
//        buttonGbc.weightx = 1.0;
//        buttonPanel.add(splitwiseBtn, buttonGbc);
//
//        // No extra spacing - buttons take all space
//        wrapper.add(buttonPanel, wrapperGbc);
//
//        jPanel4.add(wrapper, BorderLayout.CENTER);
//
//        jPanel4.revalidate();
//        jPanel4.repaint();
//
//        SwingUtilities.invokeLater(() -> {
//            if (tourScrool != null) {
//                tourScrool.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//                tourScrool.getHorizontalScrollBar().setUnitIncrement(30);
//                tourScrool.revalidate();
//                tourScrool.repaint();
//            }
//        });
//    }
    public void onRefresh() {
        SwingUtilities.invokeLater(() -> {
            this.currentUser = UserSession.getInstance().getCurrentUser();
            updateWelcomeMessage();
            updateNotificationBadge();
            // checkPendingInvitations();
            setupActiveTour();
            revalidate();
            repaint();
        });
    }

    private void updateWelcomeMessage() {
        if (currentUser == null) {
            return;
        }

        if (currentUser.isProfileComplete()) {

            String[] messages = {
                "WELCOME BACK, " + currentUser.getUsername() + "! Where to today?",
                "READY FOR ADVENTURE, " + currentUser.getUsername() + "?",
                "HEY " + currentUser.getUsername() + "! Plan your next trip.",
                "GOOD TO SEE YOU, " + currentUser.getUsername() + "!",
                "WELCOME BACK! Your next adventure awaits."
            };

            String message = messages[(int) (Math.random() * messages.length)];
            jLabel4.setText(message);
        } else {

            jLabel4.setText("WELCOME, " + currentUser.getUsername() + "! Let's edit your profile .");
        }
    }

    public HomeFrame() {

        this.currentUser = null;
        initComponents();
        setLocationRelativeTo(null);
    }

    private void refreshLayoutWithGaps(int gapSize) {
        java.awt.Component[] components = mainpanel.getComponents();
        mainpanel.removeAll();
        mainpanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof javax.swing.JComponent) {
                javax.swing.JComponent jc = (javax.swing.JComponent) components[i];

                jc.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, jc.getPreferredSize().height));

                jc.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            }
            mainpanel.add(components[i]);
            if (i < components.length - 1) {
                mainpanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, gapSize)));
            }
        }
        mainpanel.revalidate();
        mainpanel.repaint();
    }

    public void startImageSlideshowOnPanel(JPanel panel) {
        List<ImageIcon> images = new ArrayList<>();

        images.add(new ImageIcon(getClass().getResource("/images/explo_panel_coxs.png")));
        images.add(new ImageIcon(getClass().getResource("/images/explo_panel_khulna.jpg")));
        images.add(new ImageIcon(getClass().getResource("/images/explo_panel_sylhet.jpg")));

        images.add(new ImageIcon(getClass().getResource("/images/explo_panel_dhaka.jpg")));

        images.add(new ImageIcon(getClass().getResource("/images/random1.jpeg")));
        images.add(new ImageIcon(getClass().getResource("/images/explo_panel_chabagan.jpg")));

        images.add(new ImageIcon(getClass().getResource("/images/random5.jpg")));

        final int[] currentIndex = {0};
        final float[] alpha = {0.0f};
        final boolean[] isCrossfading = {false};

        panel.removeAll();
        panel.setLayout(new BorderLayout());

        JLabel existingLabel = jLabel1;

        existingLabel.setOpaque(false);
        existingLabel.setForeground(new java.awt.Color(255, 153, 51));
        existingLabel.setFont(new java.awt.Font("Bookman Old Style", 1, 30));
        existingLabel.setText("Explore Bangladesh");

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!images.isEmpty() && images.size() > 1) {
                    Graphics2D g2d = (Graphics2D) g;

                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    int nextIndex = (currentIndex[0] + 1) % images.size();

                    Image currentImg = images.get(currentIndex[0]).getImage();
                    if (currentImg != null) {

                        g2d.drawImage(currentImg, 0, 0, getWidth(), getHeight(), this);
                    }

                    if (alpha[0] > 0) {
                        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]);
                        g2d.setComposite(alphaComposite);

                        Image nextImg = images.get(nextIndex).getImage();
                        if (nextImg != null) {

                            g2d.drawImage(nextImg, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                }
            }
        };

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        imagePanel.setBounds(0, 0, panel.getWidth(), panel.getHeight());

        existingLabel.setBounds(19, 50, 400, 70);

        layeredPane.add(imagePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(existingLabel, JLayeredPane.PALETTE_LAYER);

        layeredPane.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));

        panel.add(layeredPane, BorderLayout.CENTER);

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {

                int width = panel.getWidth();
                int height = panel.getHeight();

                imagePanel.setBounds(0, 0, width, height);

                layeredPane.setPreferredSize(new Dimension(width, height));
                layeredPane.revalidate();

                imagePanel.repaint();
            }
        });

        Timer crossfadeTimer = new Timer(50, e -> {
            if (isCrossfading[0]) {
                alpha[0] += 0.05f;

                if (alpha[0] >= 1.0f) {
                    alpha[0] = 1.0f;
                    currentIndex[0] = (currentIndex[0] + 1) % images.size();
                    alpha[0] = 0.0f;
                    isCrossfading[0] = false;
                }
                imagePanel.repaint();
            }
        });

        Timer triggerTimer = new Timer(2000, e -> {
            if (!isCrossfading[0]) {
                isCrossfading[0] = true;
                alpha[0] = 0.0f;
            }
        });

        crossfadeTimer.start();
        triggerTimer.start();

        panel.revalidate();
        panel.repaint();
    }

    private void styleDivisionButtons() {

        styleButton(Dhaka, "Dhaka");
        styleButton(chittagong, "Chittagong");
        styleButton(sylhet, "Sylhet");
        styleButton(kulna, "Khulna");
        styleButton(Rajshahi, "Rajshahi");
        styleButton(mymensigh_, "Mymensingh");
        styleButton(barishal, "Barishal");
        styleButton(rangpur, "Rangpur");
    }

    //////////////////// ////////////// nazifa
    
    
 //nazifa
  // Sidebar init
private void initSidebar() {

        sidePanel = new JPanel();
        sidePanel.setLayout(null);
        sidePanel.setBackground(new java.awt.Color(102, 51, 0)); // Dark coffee color
        sidePanel.setBounds(-SIDEBAR_WIDTH, 0, SIDEBAR_WIDTH, getHeight());

        int y = 50;

        // Close button (always visible in sidebar)
        JButton btnClose = new JButton("Close");
        btnClose.setBounds(20, 10, 200, 35);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> {
            if (isSidebarOpen) {
                toggleSidebar(); // close sidebar
            }
        });
        sidePanel.add(btnClose);

        // Menu buttons
        sidePanel.add(createButton("My Profile", y));
        y += 45;
        sidePanel.add(createButton("MY Groups", y));
        y += 45;
        sidePanel.add(createButton("TO-DO", y));
        y += 45;
        sidePanel.add(createButton("History", y));
        y += 45;
        sidePanel.add(createButton("Messages", y));
        y += 45;
        sidePanel.add(createButton("New Groups", y));
        y += 45;
        sidePanel.add(createButton("Expense Update", y));
        y += 45;
        sidePanel.add(createButton("Help-Line", y));
        y += 45;
        sidePanel.add(createButton("Settings", y));
        y += 45;
        sidePanel.add(createButton("Dev Mode", y));
        y += 45;
        sidePanel.add(createButton("Contact Us", y));
        y += 45;
        sidePanel.add(createButton("Logout", y));
        y += 45;

        getLayeredPane().add(sidePanel, JLayeredPane.POPUP_LAYER);
    }

    //Create button method
    private JButton createButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(20, y, 200, 35);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> handleMenuClick(text));
        return btn;
    }

// Handle button click
//    private void handleMenuClick(String name) {
//        //JOptionPane.showMessageDialog(this, name + " clicked");
//
//        // Example: open another frame
//        if (name.equals("My Profile")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Current Group")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Bulletin")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Help")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Settings")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Dev")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Dev")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Group Invites")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Expense Update")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("New Message")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Contact Us")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//        if (name.equals("Logout")) {
//            new UserProfile(currentUser).setVisible(true);
//        }
//
//    }
    private void handleMenuClick(String name) {
        switch (name) {
            case "My Profile":
                new UserProfile(currentUser).setVisible(true);
                break;

            case "MY Groups":
                new MyGroupsFrame(currentUser).setVisible(true);
                break;

            case "TO-DO":
                // Open To-Do Checklist
                Group activeGroup = findActiveGroup();
                if (activeGroup != null) {
                    boolean isLeader = activeGroup.isLeader(currentUser);
                    ChecklistManager<Object> manager = new ChecklistManager<>(
                            activeGroup.getId(), currentUser, isLeader
                    );
                    ChecklistDialog<Object> dialog = new ChecklistDialog<>(
                            this, "TO-DO - " + activeGroup.getName(), manager, currentUser, isLeader
                    );
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No active tour found!\n\nPlease create or join a confirmed group first.",
                            "To-Do", JOptionPane.WARNING_MESSAGE);
                }
                break;

            case "History":
                new CompletedToursFrame(currentUser).setVisible(true);
                break;

            case "Messages":
                new BulletinFrame(currentUser).setVisible(true);
                break;

            case "New Groups":
                new PublicGroupsFrame(currentUser).setVisible(true);
                break;

            case "Expense Update":
                new ExpenseSplitUI(currentUser).setVisible(true);
                break;

            case "Help-Line":
                showHelpDialog();
                break;

            case "Settings":
                new EditUserProfile(currentUser).setVisible(true);
                break;

            case "Dev Mode":
                showDeveloperDialog();
                break;

            case "Contact Us":
                showContactDialog();
                break;

            case "Logout":
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to logout?", "Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    UserSession.getInstance().logout();
                    new LoginFrame().setVisible(true);
                    dispose();
                }
                break;

            default:
                JOptionPane.showMessageDialog(this, name + " - Coming soon!");
                break;
        }
    }

    private void showHelpDialog() {
        JDialog helpDialog = new JDialog(this, "Emergency Helpline", true);
        helpDialog.setSize(400, 350);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 248, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("NATIONAL EMERGENCY HELPLINES");
        title.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        title.setForeground(new Color(70, 58, 47));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[][] helplines = {
            {"🚑 National Ambulance", "999"},
            {"🚒 Fire Service", "999"},
            {"👮 Police", "999"},
            {"🚨 National Emergency", "999"},
            {"🏥 Dhaka Medical", "10655"},
            {"💊 Poison Control", "16441"},
            {"👩‍⚕️ National Health Line", "16263"},
            {"🚸 Child Helpline", "1098"}
        };

        JPanel helplinePanel = new JPanel(new GridLayout(helplines.length, 2, 10, 8));
        helplinePanel.setBackground(new Color(250, 248, 245));
        helplinePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        for (String[] line : helplines) {
            JLabel nameLabel = new JLabel(line[0]);
            nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
            nameLabel.setForeground(new Color(70, 58, 47));

            JLabel numberLabel = new JLabel(line[1]);
            numberLabel.setFont(new Font("Arial", Font.BOLD, 14));
            numberLabel.setForeground(new Color(220, 53, 69));

            helplinePanel.add(nameLabel);
            helplinePanel.add(numberLabel);
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(new Color(70, 58, 47));
        closeBtn.setForeground(new Color(255, 215, 0));
        closeBtn.addActionListener(e -> helpDialog.dispose());

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(helplinePanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(closeBtn);

        helpDialog.add(panel, BorderLayout.CENTER);
        helpDialog.setVisible(true);
    }

    private void showDeveloperDialog() {
        JDialog devDialog = new JDialog(this, "🔐 Developer Mode", true);
        devDialog.setSize(500, 450);
        devDialog.setLocationRelativeTo(this);
        devDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 248, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("🔓 UNLOCK DEVELOPER MODE");
        title.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
        title.setForeground(new Color(220, 53, 69));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel puzzleLabel = new JLabel("Solve this riddle to unlock:");
        puzzleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        puzzleLabel.setForeground(new Color(70, 58, 47));
        puzzleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // HARD RIDDLE - Only a real coder can solve
        JTextArea riddleArea = new JTextArea();
        riddleArea.setEditable(false);
        riddleArea.setLineWrap(true);
        riddleArea.setWrapStyleWord(true);
        riddleArea.setBackground(new Color(250, 248, 245));
        riddleArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        riddleArea.setForeground(new Color(70, 58, 47));
        riddleArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        riddleArea.setMaximumSize(new Dimension(400, 120));

        String riddle
                = "I speak without a mouth and hear without ears.\n"
                + "I have no body, but I come alive with your tears.\n"
                + "I can be compiled, interpreted, or just in time.\n"
                + "I can be low-level or high-level, but always in line.\n"
                + "What am I?\n\n"
                + "HINT: Without me, your code is just text.\n"
                + "With me, it becomes magic.";

        riddleArea.setText(riddle);
        riddleArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel answerLabel = new JLabel("Your Answer:");
        answerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        answerLabel.setForeground(new Color(70, 58, 47));
        answerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField answerField = new JTextField(20);
        answerField.setMaximumSize(new Dimension(300, 35));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerField.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton submitBtn = new JButton("Unlock");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 12));
        submitBtn.setBackground(new Color(70, 58, 47));
        submitBtn.setForeground(new Color(255, 215, 0));
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(120, 35));

        JLabel resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // The answer is "CODE" or "PROGRAM" or "LANGUAGE"
        // But the trick is - the riddle describes "CODE" or "A PROGRAM"
        // However, the real developer answer is "COMPILER" or "INTERPRETER"
        // Let's make it "CODE" - simple but clever
        submitBtn.addActionListener(e -> {
            String answer = answerField.getText().trim().toLowerCase();

            // Multiple correct answers for flexibility
            boolean isCorrect = answer.equals("code")
                    || answer.equals("program")
                    || answer.equals("programming language")
                    || answer.equals("compiler")
                    || answer.equals("interpreter")
                    || answer.equals("language")
                    || answer.equals("source code");

            if (isCorrect) {
                resultLabel.setText("Access Granted! Welcome Developer!");
                resultLabel.setForeground(new Color(46, 125, 50));

                // Show developer features after correct answer
                Timer timer = new Timer(1500, ev -> {
                    JOptionPane.showMessageDialog(devDialog,
                            "🔧 DEVELOPER ACCESS GRANTED 🔧\n\n"
                            + "• System Architecture: MVC Pattern\n"
                            + "• Database Schema: SQLite with 20+ tables\n"
                            + "• Design Patterns Used:\n"
                            + "  - Singleton (UserSession, DatabaseConnection)\n"
                            + "  - Observer (RefreshManager)\n"
                            + "  - Factory (PlaceDatabaseObject)\n"
                            + "  - DAO Pattern (All DatabaseObjects)\n\n"
                            + "• Total Classes: 150+\n"
                            + "• Lines of Code: ~25,000\n\n"
                            + "🔮 Hidden Feature: Type 'EXPLOBD' in search 3 times!\n\n"
                            + "Keep coding! 🚀",
                            "Developer Access", JOptionPane.INFORMATION_MESSAGE);
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                resultLabel.setText("✗ Access Denied! Think like a coder!");
                resultLabel.setForeground(new Color(198, 40, 40));
                answerField.setText("");
                answerField.requestFocus();
            }
        });

        // Allow Enter key to submit
        answerField.addActionListener(e -> submitBtn.doClick());

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(puzzleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(riddleArea);
        panel.add(Box.createVerticalStrut(15));
        panel.add(answerLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(answerField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(submitBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(resultLabel);

        // Hint button (for desperate coders)
        JButton hintBtn = new JButton("Need a Hint?");
        hintBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        hintBtn.setBackground(new Color(200, 180, 160));
        hintBtn.setForeground(Color.WHITE);
        hintBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintBtn.setMaximumSize(new Dimension(100, 25));
        hintBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(devDialog,
                    "💡 HINT:\n\n"
                    + "Without me, you're just typing.\n"
                    + "With me, computers understand you.\n"
                    + "I am what you write every day.\n\n"
                    + "What am I? (4 letters)",
                    "Hint", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(Box.createVerticalStrut(5));
        panel.add(hintBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(new Color(70, 58, 47));
        closeBtn.setForeground(new Color(255, 215, 0));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setMaximumSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> devDialog.dispose());

        panel.add(Box.createVerticalStrut(15));
        panel.add(closeBtn);

        devDialog.add(panel, BorderLayout.CENTER);
        devDialog.setVisible(true);
    }

    private void showContactDialog() {
        JDialog contactDialog = new JDialog(this, "Contact Us", true);
        contactDialog.setSize(450, 400);
        contactDialog.setLocationRelativeTo(this);
        contactDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 248, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📞 CONTACT US");
        title.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
        title.setForeground(new Color(70, 58, 47));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("ExploBD Development Team");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitle.setForeground(new Color(150, 150, 150));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[][] contacts = {
            {"Lead Developer", "Nusrat Jahan Mahina ", "+880 1234567899"},
            {"Developer", "Sanzida  Afroz ", "+880 1598347588"},
            {"Developer", " Nazifa Rahman", "+880 1232343454"},
            {"📧 Email", "support@explobd.com", "-"},
            {"🌐 Website", "www.explobd.com", "-"},
            {"💬 Discord", "discord.gg/explobd", "-"}
        };

        JPanel contactPanel = new JPanel(new GridLayout(contacts.length, 3, 8, 10));
        contactPanel.setBackground(new Color(250, 248, 245));
        contactPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));

        for (String[] contact : contacts) {
            JLabel roleLabel = new JLabel(contact[0]);
            roleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 11));
            roleLabel.setForeground(new Color(70, 58, 47));

            JLabel nameLabel = new JLabel(contact[1]);
            nameLabel.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
            nameLabel.setForeground(new Color(100, 100, 100));

            JLabel numberLabel = new JLabel(contact[2]);
            numberLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            numberLabel.setForeground(new Color(255, 153, 51));

            contactPanel.add(roleLabel);
            contactPanel.add(nameLabel);
            contactPanel.add(numberLabel);
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(new Color(70, 58, 47));
        closeBtn.setForeground(new Color(255, 215, 0));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> contactDialog.dispose());

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(contactPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(closeBtn);

        contactDialog.add(panel, BorderLayout.CENTER);
        contactDialog.setVisible(true);
    }
    //nusrat end

    //Nazifa -start
    // Sidebar toggle animation
    private void toggleSidebar() {
        if (sidebarTimer != null && sidebarTimer.isRunning()) {
            return;
        }

        final int targetX = isSidebarOpen ? -SIDEBAR_WIDTH : 0;

        sidebarTimer = new Timer(2, null); // smooth
        sidebarTimer.addActionListener(e -> {
            int x = sidePanel.getX();
            if (!isSidebarOpen) { // opening
                if (x < targetX) {
                    sidePanel.setLocation(Math.min(x + 20, targetX), 0);
                } else {
                    sidePanel.setLocation(targetX, 0);
                    isSidebarOpen = true;
                    sidebarTimer.stop();
                }
            } else { // closing
                if (x > targetX) {
                    sidePanel.setLocation(Math.max(x - 20, targetX), 0);
                } else {
                    sidePanel.setLocation(targetX, 0);
                    isSidebarOpen = false;
                    sidebarTimer.stop();
                }
            }
        });
        sidebarTimer.start();
    }

//    private void checkPendingInvitations() {
//        if (currentUser == null || isProcessingInvitations) {
//            return;
//        }
//
//        try {
//            InvitationDatabaseObject invDAO = new InvitationDatabaseObject();
//
//            List<Invitation> pending = invDAO.findPendingForUser(
//                    currentUser.getEmail(),
//                    currentUser.getUsername()
//            );
//
//            System.out.println("Found " + pending.size() + " pending invitations");
//
//            pending.removeIf(inv -> inv.getInvitationId().equals(lastProcessedInvitationId));
//
//            if (!pending.isEmpty()) {
//
//                try {
//                    java.net.URL bellUrl = getClass().getResource("/images/bell_notification.png");
//                    if (bellUrl != null) {
//                        notification.setIcon(new ImageIcon(bellUrl));
//                        notification.setToolTipText("You have " + pending.size() + " new invitation(s)");
//                    }
//                } catch (Exception e) {
//                }
//
//                isProcessingInvitations = true;
//                processInvitation(pending, 0);
//
//            } else {
//
//                try {
//                    java.net.URL bellUrl = getClass().getResource("/images/bell 2.png");
//                    if (bellUrl != null) {
//                        notification.setIcon(new ImageIcon(bellUrl));
//                        notification.setToolTipText("Notifications");
//                    }
//                } catch (Exception e) {
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            isProcessingInvitations = false;
//        }
//    }
    @Override
    public void dispose() {
        RefreshManager.getInstance().unregister(this);
        super.dispose();
    }

    private void processInvitation(List<Invitation> invitations, int index) {
        if (index >= invitations.size()) {

            System.out.println("All " + invitations.size() + " invitations processed");
            isProcessingInvitations = false;
            return;
        }

        Invitation invitation = invitations.get(index);
        System.out.println("Showing invitation " + (index + 1) + " of " + invitations.size()
                + " - ID: " + invitation.getInvitationId());

        InvitationDialog dialog = new InvitationDialog(
                this,
                currentUser,
                invitation,
                () -> {

                    System.out.println("✓ Accepted invitation " + invitation.getInvitationId());
                    lastProcessedInvitationId = invitation.getInvitationId();
                    refreshData();

                    SwingUtilities.invokeLater(() -> {
                        processInvitation(invitations, index + 1);
                    });
                },
                () -> {

                    System.out.println("✗ Declined invitation " + invitation.getInvitationId());
                    lastProcessedInvitationId = invitation.getInvitationId();

                    SwingUtilities.invokeLater(() -> {
                        processInvitation(invitations, index + 1);
                    });
                }
        );

        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public void refreshData() {
        UserSession.getInstance().loadUserData();

        revalidate();
        repaint();
    }

    private void styleButton(javax.swing.JButton button, String text) {

        String capitalizedText = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        button.setText(capitalizedText);
        button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setForeground(new java.awt.Color(255, 255, 255));

        button.setFont(new java.awt.Font("Bookman Old Style", java.awt.Font.BOLD, 20));

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new java.awt.Color(255, 153, 51));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(new java.awt.Color(255, 255, 255));
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        mainpanel = new javax.swing.JPanel();
        Heading = new com.ExploBD.Frame.RoundedPanel();
        Navbar = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        searchfor = new javax.swing.JTextField();
        search = new javax.swing.JButton();
        notification = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        welcomemsg = new com.ExploBD.Frame.RoundedPanel();
        jLabel4 = new javax.swing.JLabel();
        explorbox = new com.ExploBD.Frame.RoundedPanel();
        pictureframe = new com.ExploBD.Frame.RoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        Dhaka = new javax.swing.JButton();
        chittagong = new javax.swing.JButton();
        Rajshahi = new javax.swing.JButton();
        rangpur = new javax.swing.JButton();
        sylhet = new javax.swing.JButton();
        kulna = new javax.swing.JButton();
        barishal = new javax.swing.JButton();
        mymensigh_ = new javax.swing.JButton();
        Mytravelbox = new com.ExploBD.Frame.RoundedPanel();
        roundedPanel1 = new com.ExploBD.Frame.RoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        roundedPanel2 = new com.ExploBD.Frame.RoundedPanel();
        Mytravelscrool = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        creategrp = new javax.swing.JButton();
        mygrp = new javax.swing.JButton();
        findgrp = new javax.swing.JButton();
        friends = new javax.swing.JButton();
        recommendation = new javax.swing.JButton();
        complete = new javax.swing.JButton();
        ActiveTravel = new com.ExploBD.Frame.RoundedPanel();
        roundedPanel4 = new com.ExploBD.Frame.RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        roundedPanel3 = new com.ExploBD.Frame.RoundedPanel();
        tourScrool = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(88, 74, 60));
        setMinimumSize(new java.awt.Dimension(840, 700));
        setPreferredSize(new java.awt.Dimension(832, 700));
        setSize(new java.awt.Dimension(840, 700));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(820, 32767));
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(820, 768));
        jScrollPane1.setViewportView(mainpanel);

        mainpanel.setBackground(new java.awt.Color(255, 255, 255));
        mainpanel.setMaximumSize(new java.awt.Dimension(800, 32767));
        mainpanel.setMinimumSize(new java.awt.Dimension(800, 1287));
        mainpanel.setPreferredSize(new java.awt.Dimension(800, 1287));
        mainpanel.setLayout(new javax.swing.BoxLayout(mainpanel, javax.swing.BoxLayout.Y_AXIS));

        Heading.setBackground(new java.awt.Color(88, 74, 60));
        Heading.setMaximumSize(new java.awt.Dimension(32767, 77));
        Heading.setMinimumSize(new java.awt.Dimension(781, 77));
        Heading.setPreferredSize(new java.awt.Dimension(781, 77));
        Heading.setLayout(new java.awt.GridBagLayout());

        Navbar.setBackground(new java.awt.Color(88, 74, 60));
        Navbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar.png"))); // NOI18N
        Navbar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0)));
        Navbar.addActionListener(this::NavbarActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 17;
        gridBagConstraints.ipady = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 16, 0, 0);
        Heading.add(Navbar, gridBagConstraints);

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo65.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 6, 0);
        Heading.add(logo, gridBagConstraints);

        searchfor.setFont(new java.awt.Font("Calisto MT", 2, 14)); // NOI18N
        searchfor.setText("\nsearch");
        searchfor.addActionListener(this::searchforActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 336;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(24, 18, 0, 0);
        Heading.add(searchfor, gridBagConstraints);

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Searchicon.png"))); // NOI18N
        search.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = -3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 6, 0, 0);
        Heading.add(search, gridBagConstraints);

        notification.setBackground(new java.awt.Color(88, 74, 60));
        notification.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bell 2.png"))); // NOI18N
        notification.setBorder(null);
        notification.addActionListener(this::notificationActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        Heading.add(notification, gridBagConstraints);

        jButton1.setBackground(new java.awt.Color(88, 74, 60));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/userprofile.png"))); // NOI18N
        jButton1.setBorder(null);
        jButton1.addActionListener(this::jButton1ActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 22;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 60);
        Heading.add(jButton1, gridBagConstraints);

        mainpanel.add(Heading);

        welcomemsg.setBackground(new java.awt.Color(255, 255, 255));
        welcomemsg.setMaximumSize(new java.awt.Dimension(32767, 65));
        welcomemsg.setMinimumSize(new java.awt.Dimension(781, 65));
        welcomemsg.setPreferredSize(new java.awt.Dimension(781, 65));

        jLabel4.setFont(new java.awt.Font("Bookman Old Style", 2, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 153, 0));
        jLabel4.setText("WELCOME , abc! Edit your profile ........");

        javax.swing.GroupLayout welcomemsgLayout = new javax.swing.GroupLayout(welcomemsg);
        welcomemsg.setLayout(welcomemsgLayout);
        welcomemsgLayout.setHorizontalGroup(
            welcomemsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomemsgLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 699, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );
        welcomemsgLayout.setVerticalGroup(
            welcomemsgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomemsgLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainpanel.add(welcomemsg);

        explorbox.setBackground(new java.awt.Color(88, 74, 60));
        explorbox.setMaximumSize(new java.awt.Dimension(32767, 500));
        explorbox.setMinimumSize(new java.awt.Dimension(781, 500));
        explorbox.setPreferredSize(new java.awt.Dimension(781, 500));

        pictureframe.setMaximumSize(new java.awt.Dimension(32767, 302));
        pictureframe.setMinimumSize(new java.awt.Dimension(767, 302));
        pictureframe.setName(""); // NOI18N
        pictureframe.setPreferredSize(new java.awt.Dimension(32767, 302));

        jLabel1.setFont(new java.awt.Font("Bookman Old Style", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 153, 51));
        jLabel1.setText("Explore Bangladesh");

        javax.swing.GroupLayout pictureframeLayout = new javax.swing.GroupLayout(pictureframe);
        pictureframe.setLayout(pictureframeLayout);
        pictureframeLayout.setHorizontalGroup(
            pictureframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pictureframeLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(399, Short.MAX_VALUE))
        );
        pictureframeLayout.setVerticalGroup(
            pictureframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pictureframeLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(198, Short.MAX_VALUE))
        );

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(500, 131));
        jScrollPane2.setOpaque(false);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(500, 131));

        jPanel1.setBackground(new java.awt.Color(88, 74, 60));
        jPanel1.setMaximumSize(new java.awt.Dimension(780, 32767));
        jPanel1.setMinimumSize(new java.awt.Dimension(780, 131));

        Dhaka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Dhaka.jpg"))); // NOI18N
        Dhaka.addActionListener(this::DhakaActionPerformed);

        chittagong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chittagong.jpg"))); // NOI18N
        chittagong.addActionListener(this::chittagongActionPerformed);

        Rajshahi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Rajshahi.jpg"))); // NOI18N

        rangpur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Rangpur.jpg"))); // NOI18N

        sylhet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sylhet.jpg"))); // NOI18N

        kulna.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Khulna.jpg"))); // NOI18N

        barishal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Barishal.jpg"))); // NOI18N
        barishal.addActionListener(this::barishalActionPerformed);

        mymensigh_.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Mymensing.jpg"))); // NOI18N
        mymensigh_.addActionListener(this::mymensigh_ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(Dhaka, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chittagong, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(sylhet, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(kulna, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Rajshahi, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(mymensigh_, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(barishal, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rangpur, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mymensigh_, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(rangpur, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(barishal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(chittagong, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(Dhaka, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sylhet, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(kulna, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(Rajshahi, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel1);

        javax.swing.GroupLayout explorboxLayout = new javax.swing.GroupLayout(explorbox);
        explorbox.setLayout(explorboxLayout);
        explorboxLayout.setHorizontalGroup(
            explorboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorboxLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pictureframe, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(explorboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        explorboxLayout.setVerticalGroup(
            explorboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(explorboxLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pictureframe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        mainpanel.add(explorbox);

        Mytravelbox.setBackground(new java.awt.Color(88, 74, 60));
        Mytravelbox.setMaximumSize(new java.awt.Dimension(32767, 276));
        Mytravelbox.setMinimumSize(new java.awt.Dimension(781, 276));
        Mytravelbox.setPreferredSize(new java.awt.Dimension(808, 276));

        roundedPanel1.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/MyTravel.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 26, 0, 0);
        roundedPanel1.add(jLabel2, gridBagConstraints);

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator1.setMinimumSize(new java.awt.Dimension(270, 10));
        jSeparator1.setPreferredSize(new java.awt.Dimension(270, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = -64;
        gridBagConstraints.ipady = -12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 26, 0, 0);
        roundedPanel1.add(jSeparator1, gridBagConstraints);

        jButton2.setFont(new java.awt.Font("Bookman Old Style", 1, 24)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 153, 51));
        jButton2.setText("MY Travel");
        jButton2.setBorder(null);
        jButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton2.setOpaque(true);
        jButton2.addActionListener(this::jButton2ActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 11;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        roundedPanel1.add(jButton2, gridBagConstraints);

        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));

        Mytravelscrool.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        Mytravelscrool.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Mytravelscrool.setMinimumSize(new java.awt.Dimension(500, 131));
        Mytravelscrool.setOpaque(false);
        Mytravelscrool.setPreferredSize(new java.awt.Dimension(500, 131));

        jPanel2.setBackground(new java.awt.Color(88, 74, 60));
        jPanel2.setMaximumSize(new java.awt.Dimension(780, 32767));
        jPanel2.setMinimumSize(new java.awt.Dimension(780, 131));

        creategrp.setBackground(new java.awt.Color(88, 74, 60));
        creategrp.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        creategrp.setForeground(new java.awt.Color(245, 180, 60));
        creategrp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/module2_11.jpeg"))); // NOI18N
        creategrp.setBorder(null);
        creategrp.setMaximumSize(new java.awt.Dimension(200, 150));
        creategrp.setMinimumSize(new java.awt.Dimension(200, 150));
        creategrp.setPreferredSize(new java.awt.Dimension(200, 150));
        creategrp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                creategrpMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                creategrpMouseExited(evt);
            }
        });
        creategrp.addActionListener(this::creategrpActionPerformed);

        mygrp.setBackground(new java.awt.Color(88, 74, 60));
        mygrp.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        mygrp.setForeground(new java.awt.Color(245, 180, 60));
        mygrp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/module3.jpeg"))); // NOI18N
        mygrp.setBorder(null);
        mygrp.setMaximumSize(new java.awt.Dimension(200, 150));
        mygrp.setMinimumSize(new java.awt.Dimension(200, 150));
        mygrp.setPreferredSize(new java.awt.Dimension(200, 150));
        mygrp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mygrpMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mygrpMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mygrpMouseExited(evt);
            }
        });
        mygrp.addActionListener(this::mygrpActionPerformed);

        findgrp.setBackground(new java.awt.Color(88, 74, 60));
        findgrp.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        findgrp.setForeground(new java.awt.Color(245, 180, 60));
        findgrp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/module2_3.jpeg"))); // NOI18N
        findgrp.setBorder(null);
        findgrp.setMaximumSize(new java.awt.Dimension(200, 150));
        findgrp.setMinimumSize(new java.awt.Dimension(200, 150));
        findgrp.setPreferredSize(new java.awt.Dimension(200, 150));
        findgrp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                findgrpMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                findgrpMouseExited(evt);
            }
        });
        findgrp.addActionListener(this::findgrpActionPerformed);

        friends.setBackground(new java.awt.Color(88, 74, 60));
        friends.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        friends.setForeground(new java.awt.Color(245, 180, 60));
        friends.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/module2_4.jpeg"))); // NOI18N
        friends.setBorder(null);
        friends.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                friendsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                friendsMouseExited(evt);
            }
        });
        friends.addActionListener(this::friendsActionPerformed);

        recommendation.setBackground(new java.awt.Color(88, 74, 60));
        recommendation.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        recommendation.setForeground(new java.awt.Color(245, 180, 60));
        recommendation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/module2_5.jpeg"))); // NOI18N
        recommendation.setBorder(null);
        recommendation.setMaximumSize(new java.awt.Dimension(200, 150));
        recommendation.setMinimumSize(new java.awt.Dimension(200, 150));
        recommendation.setPreferredSize(new java.awt.Dimension(200, 150));
        recommendation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                recommendationMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                recommendationMouseExited(evt);
            }
        });
        recommendation.addActionListener(this::recommendationActionPerformed);

        complete.setBackground(new java.awt.Color(88, 74, 60));
        complete.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        complete.setForeground(new java.awt.Color(245, 180, 60));
        complete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/module2_6.jpeg"))); // NOI18N
        complete.setBorder(null);
        complete.setMaximumSize(new java.awt.Dimension(200, 150));
        complete.setMinimumSize(new java.awt.Dimension(200, 150));
        complete.setPreferredSize(new java.awt.Dimension(200, 150));
        complete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                completeMouseExited(evt);
            }
        });
        complete.addActionListener(this::completeActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(creategrp, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(mygrp, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(findgrp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(friends, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(recommendation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(complete, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(complete, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recommendation, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(friends, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(findgrp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mygrp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(creategrp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );

        Mytravelscrool.setViewportView(jPanel2);

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(Mytravelscrool, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(Mytravelscrool, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 234;
        gridBagConstraints.ipady = -55;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        roundedPanel1.add(roundedPanel2, gridBagConstraints);

        javax.swing.GroupLayout MytravelboxLayout = new javax.swing.GroupLayout(Mytravelbox);
        Mytravelbox.setLayout(MytravelboxLayout);
        MytravelboxLayout.setHorizontalGroup(
            MytravelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MytravelboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        MytravelboxLayout.setVerticalGroup(
            MytravelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MytravelboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainpanel.add(Mytravelbox);

        ActiveTravel.setBackground(new java.awt.Color(88, 74, 60));
        ActiveTravel.setMaximumSize(new java.awt.Dimension(32767, 318));
        ActiveTravel.setMinimumSize(new java.awt.Dimension(781, 318));

        roundedPanel4.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel4.setMinimumSize(new java.awt.Dimension(1011, 306));
        roundedPanel4.setPreferredSize(new java.awt.Dimension(1011, 306));
        roundedPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/travel.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 31, 0, 0);
        roundedPanel4.add(jLabel3, gridBagConstraints);

        jButton3.setFont(new java.awt.Font("Bookman Old Style", 1, 24)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 153, 51));
        jButton3.setText("Active Tour");
        jButton3.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 8;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        roundedPanel4.add(jButton3, gridBagConstraints);

        roundedPanel3.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel3.setLayout(new java.awt.GridBagLayout());

        tourScrool.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        tourScrool.setMinimumSize(new java.awt.Dimension(500, 131));
        tourScrool.setOpaque(false);
        tourScrool.setPreferredSize(new java.awt.Dimension(500, 131));

        jPanel4.setBackground(new java.awt.Color(88, 74, 60));
        jPanel4.setMaximumSize(new java.awt.Dimension(780, 32767));
        jPanel4.setMinimumSize(new java.awt.Dimension(780, 131));
        jPanel4.setPreferredSize(new java.awt.Dimension(1416, 180));
        jPanel4.setLayout(new java.awt.GridLayout(1, 6, 15, 15));
        tourScrool.setViewportView(jPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 234;
        gridBagConstraints.ipady = 42;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(47, 6, 0, 17);
        roundedPanel3.add(tourScrool, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 234;
        gridBagConstraints.ipady = -55;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        roundedPanel4.add(roundedPanel3, gridBagConstraints);

        javax.swing.GroupLayout ActiveTravelLayout = new javax.swing.GroupLayout(ActiveTravel);
        ActiveTravel.setLayout(ActiveTravelLayout);
        ActiveTravelLayout.setHorizontalGroup(
            ActiveTravelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ActiveTravelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundedPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ActiveTravelLayout.setVerticalGroup(
            ActiveTravelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ActiveTravelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundedPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainpanel.add(ActiveTravel);

        jScrollPane1.setViewportView(mainpanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NavbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NavbarActionPerformed
        // TODO add your handling code here:

        //Nazifa
        toggleSidebar();

    }//GEN-LAST:event_NavbarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        UserProfile profileFrame = new UserProfile(currentUser);

        profileFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                RefreshManager.getInstance().refreshAll();
            }
        });

        profileFrame.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void barishalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barishalActionPerformed
        new DivisionExploreFrame(currentUser, "Barishal", false).setVisible(true);
    }//GEN-LAST:event_barishalActionPerformed

    private void chittagongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chittagongActionPerformed
        new DivisionExploreFrame(currentUser, "Chittagong", false).setVisible(true);
    }//GEN-LAST:event_chittagongActionPerformed

    private void mymensigh_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mymensigh_ActionPerformed
        new DivisionExploreFrame(currentUser, "Mymensingh", false).setVisible(true);
    }//GEN-LAST:event_mymensigh_ActionPerformed

    private void DhakaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DhakaActionPerformed
        new DivisionExploreFrame(currentUser, "Dhaka", false).setVisible(true);
    }//GEN-LAST:event_DhakaActionPerformed

    private void creategrpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_creategrpMouseEntered
        creategrp.setBackground(new Color(180, 165, 150));

    }//GEN-LAST:event_creategrpMouseEntered

    private void creategrpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_creategrpMouseExited
        creategrp.setBackground(new Color(70, 58, 47));
    }//GEN-LAST:event_creategrpMouseExited

    private void creategrpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_creategrpActionPerformed

        User currentUser = UserSession.getInstance().getCurrentUser();

        if (!currentUser.isProfileComplete()) {
            int response = JOptionPane.showConfirmDialog(this,
                    "Your profile is incomplete. Would you like to complete it now?\n\n"
                    + "You need to add your name and phone number to create groups.",
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
            return;
        }

        CreateGroupFrame createFrame = new CreateGroupFrame(currentUser);

        createFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                RefreshManager.getInstance().refreshAll();
            }
        });

        createFrame.setVisible(true);

//        User currentUser = UserSession.getInstance().getCurrentUser();
//
//        if (!currentUser.isProfileComplete()) {
//            int response = JOptionPane.showConfirmDialog(this,
//                    "Your profile is incomplete. Would you like to complete it now?\n\n"
//                    + "You need to add your name and phone number to create groups.",
//                    "Profile Incomplete",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.WARNING_MESSAGE);
//
//            if (response == JOptionPane.YES_OPTION) {
//                new EditUserProfile(currentUser).setVisible(true);
//            }
//            return;
//        }
//
//        new CreateGroupFrame(currentUser).setVisible(true);
    }//GEN-LAST:event_creategrpActionPerformed

    private void mygrpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mygrpMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_mygrpMouseClicked

    private void mygrpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mygrpMouseEntered
        mygrp.setBackground(new Color(180, 165, 150));
    }//GEN-LAST:event_mygrpMouseEntered

    private void mygrpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mygrpMouseExited
        mygrp.setBackground(new Color(70, 58, 47));
    }//GEN-LAST:event_mygrpMouseExited

    private void mygrpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mygrpActionPerformed

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            MyGroupsFrame myGroupsFrame = new MyGroupsFrame(currentUser);

            myGroupsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    RefreshManager.getInstance().refreshAll();
                }
            });

            myGroupsFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);

//        User currentUser = UserSession.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            new MyGroupsFrame(currentUser).setVisible(true);
//        } else {
//            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
        }    }//GEN-LAST:event_mygrpActionPerformed

    private void findgrpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_findgrpMouseEntered
        findgrp.setBackground(new Color(180, 165, 150));
    }//GEN-LAST:event_findgrpMouseEntered

    private void findgrpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_findgrpMouseExited
        findgrp.setBackground(new Color(70, 58, 47));
    }//GEN-LAST:event_findgrpMouseExited

    private void friendsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendsMouseEntered
        friends.setBackground(new Color(180, 165, 150));
    }//GEN-LAST:event_friendsMouseEntered

    private void friendsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendsMouseExited
        friends.setBackground(new Color(70, 58, 47));
    }//GEN-LAST:event_friendsMouseExited

    private void friendsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_friendsActionPerformed

        Friends friendsFrame = new Friends(this.currentUser);

        friendsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                RefreshManager.getInstance().refreshAll();
            }
        });

        friendsFrame.setVisible(true);

//        /* Friends friend = new Friends();
//
//        friend.setVisible(true);
//
//        friend.pack();
//
//        friend.setLocationRelativeTo(null);
//        this.dispose();
//        //new Friends(this.loggedInUserId).setVisible(true);
//        this.dispose();*/
//        new Friends(this.currentUser).setVisible(true);

    }//GEN-LAST:event_friendsActionPerformed

//Sristy did it
    // In your HomeFrame class, when user clicks on Friends button:
    private void openFriendsWindow() {
        // Assuming you have the current user stored
        Friends friendsWindow = new Friends(currentUser);
        friendsWindow.setVisible(true);
    }


    private void recommendationMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recommendationMouseEntered
        recommendation.setBackground(new Color(180, 165, 150));
    }//GEN-LAST:event_recommendationMouseEntered

    private void recommendationMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recommendationMouseExited
        recommendation.setBackground(new Color(70, 58, 47));
    }//GEN-LAST:event_recommendationMouseExited

    private void recommendationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recommendationActionPerformed
        RecommendationFrame recommendationFrame = new RecommendationFrame();
        recommendationFrame.setVisible(true);
        recommendationFrame.setLocationRelativeTo(this);
    }//GEN-LAST:event_recommendationActionPerformed

    private void completeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_completeMouseExited

    }//GEN-LAST:event_completeMouseExited

    private void completeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeActionPerformed
        System.out.println("Complete button clicked!");  // ← ADD THIS LINE
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            CompletedToursFrame frame = new CompletedToursFrame(user);
            frame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please login first");
        }
    }//GEN-LAST:event_completeActionPerformed

    private void findgrpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findgrpActionPerformed
        PublicGroupsFrame publicGroupsFrame = new PublicGroupsFrame(currentUser);

        publicGroupsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                RefreshManager.getInstance().refreshAll();
            }
        });

        publicGroupsFrame.setVisible(true);

//        new PublicGroupsFrame(currentUser).setVisible(true);
    }//GEN-LAST:event_findgrpActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void notificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notificationActionPerformed
        showNotificationPanel();
        // testCreateNotification();
    }//GEN-LAST:event_notificationActionPerformed

    private void searchforActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchforActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_searchforActionPerformed
    private void RajshahiActionPerformed(java.awt.event.ActionEvent evt) {
        new DivisionExploreFrame(currentUser, "Rajshahi", false).setVisible(true);
    }

    private void rangpurActionPerformed(java.awt.event.ActionEvent evt) {
        new DivisionExploreFrame(currentUser, "Rangpur", false).setVisible(true);
    }

    private void sylhetActionPerformed(java.awt.event.ActionEvent evt) {
        new DivisionExploreFrame(currentUser, "Sylhet", false).setVisible(true);
    }

    private void kulnaActionPerformed(java.awt.event.ActionEvent evt) {
        new DivisionExploreFrame(currentUser, "Khulna", false).setVisible(true);
    }

    public static void main(String args[]) {

    }

    private void performSearch() {
        String searchTerm = searchfor.getText().trim();

        // Clear the search field
        searchfor.setText("");

        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Place> matchingPlaces = searchPlaces(searchTerm);
        List<User> matchingUsers = searchUsers(searchTerm);

        if (matchingPlaces.isEmpty() && matchingUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results found for '" + searchTerm + "'", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        showSearchResultsDialog(searchTerm, matchingPlaces, matchingUsers);
    }

    private List<Place> searchPlaces(String searchTerm) {
        List<Place> results = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();

        try {
            // Use the existing static method getAllPlaces()
            java.util.List<Place> allPlaces = com.ExploBD.data.databaseObject.PlaceDatabaseObject.getAllPlaces();

            for (Place place : allPlaces) {
                if (place.getName().toLowerCase().contains(lowerSearchTerm)
                        || place.getDivision().toLowerCase().contains(lowerSearchTerm)
                        || place.getDistrict().toLowerCase().contains(lowerSearchTerm)) {
                    results.add(place);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private List<User> searchUsers(String searchTerm) {
        List<User> results = new ArrayList<>();

        try {
            UserDatabaseObject userDAO = new UserDatabaseObject();
            // Use the existing searchUsers method
            List<User> foundUsers = userDAO.searchUsers(searchTerm, currentUser.getUserId());
            results.addAll(foundUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private void showSearchResultsDialog(String searchTerm, List<Place> places, List<User> users) {
        JDialog resultDialog = new JDialog(this, "Search Results: " + searchTerm, true);
        resultDialog.setSize(550, 500);
        resultDialog.setLocationRelativeTo(this);
        resultDialog.setLayout(new BorderLayout());
        resultDialog.getContentPane().setBackground(new Color(250, 248, 245));

        // Create Tabbed Pane - NO EMOJIS
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(250, 248, 245));

        // Users Tab - NO EMOJI
        if (!users.isEmpty()) {
            tabbedPane.addTab("USERS (" + users.size() + ")", createUsersTab(users, resultDialog));
        } else {
            tabbedPane.addTab("USERS (0)", createEmptyTab("No users found"));
        }

        // Places Tab - NO EMOJI
        if (!places.isEmpty()) {
            tabbedPane.addTab("PLACES (" + places.size() + ")", createPlacesTab(places, resultDialog));
        } else {
            tabbedPane.addTab("PLACES (0)", createEmptyTab("No places found"));
        }

        // Close button panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(250, 248, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(new Color(70, 58, 47));
        closeBtn.setForeground(new Color(255, 215, 0));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> resultDialog.dispose());
        bottomPanel.add(closeBtn);

        resultDialog.add(tabbedPane, BorderLayout.CENTER);
        resultDialog.add(bottomPanel, BorderLayout.SOUTH);
        resultDialog.setVisible(true);
    }
//private void showSearchResultsDialog(String searchTerm, List<Place> places, List<User> users) {
//    JDialog resultDialog = new JDialog(this, "Search Results: " + searchTerm, true);
//    resultDialog.setSize(550, 500);
//    resultDialog.setLocationRelativeTo(this);
//    resultDialog.setLayout(new BorderLayout());
//    resultDialog.getContentPane().setBackground(new Color(250, 248, 245)); // Soft cream
//    
//    // Create Tabbed Pane
//    JTabbedPane tabbedPane = new JTabbedPane();
//    tabbedPane.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
//    tabbedPane.setBackground(new Color(250, 248, 245));
//    
//    // Users Tab
//    if (!users.isEmpty()) {
//        tabbedPane.addTab("👤 USERS (" + users.size() + ")", createUsersTab(users, resultDialog));
//    } else {
//        tabbedPane.addTab("👤 USERS (0)", createEmptyTab("No users found"));
//    }
//    
//    // Places Tab
//    if (!places.isEmpty()) {
//        tabbedPane.addTab("📍 PLACES (" + places.size() + ")", createPlacesTab(places, resultDialog));
//    } else {
//        tabbedPane.addTab("📍 PLACES (0)", createEmptyTab("No places found"));
//    }
//    
//    // Close button panel
//    JPanel bottomPanel = new JPanel();
//    bottomPanel.setBackground(new Color(250, 248, 245));
//    bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//    
//    JButton closeBtn = new JButton("Close");
//    closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
//    closeBtn.setBackground(new Color(70, 58, 47));
//    closeBtn.setForeground(new Color(255, 215, 0));
//    closeBtn.setFocusPainted(false);
//    closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
//    closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//    closeBtn.addActionListener(e -> resultDialog.dispose());
//    bottomPanel.add(closeBtn);
//    
//    resultDialog.add(tabbedPane, BorderLayout.CENTER);
//    resultDialog.add(bottomPanel, BorderLayout.SOUTH);
//    resultDialog.setVisible(true);
//}

    private JScrollPane createUsersTab(List<User> users, JDialog parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 248, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (User user : users) {
            panel.add(createUserResultCard(user, parent));
            panel.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Hide vertical scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JScrollPane createPlacesTab(List<Place> places, JDialog parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 248, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Place place : places) {
            panel.add(createPlaceResultCard(place, parent));
            panel.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Hide vertical scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JPanel createEmptyTab(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 248, 245));

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 14));
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPlaceResultCard(Place place, JDialog parent) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 210, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(500, 70));
        card.setPreferredSize(new Dimension(500, 70));
        card.setMinimumSize(new Dimension(400, 70));

        // Left - Place Image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(245, 242, 238));
        imagePanel.setPreferredSize(new Dimension(40, 40));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 190, 180), 1));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String imagePath = place.getImagePath();
        boolean imageLoaded = false;

        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                java.net.URL url = getClass().getResource(imagePath);
                if (url == null) {
                    url = getClass().getResource("/" + imagePath);
                }
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                    imageLoaded = true;
                }
            } catch (Exception e) {
            }
        }

        if (!imageLoaded) {
            String firstLetter = place.getName().substring(0, 1).toUpperCase();
            imageLabel.setText(firstLetter);
            imageLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
            imageLabel.setForeground(new Color(70, 58, 47));
        }

        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Center - Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));

        JLabel nameLabel = new JLabel(place.getName());
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(new Color(70, 58, 47));

        JLabel locationLabel = new JLabel(place.getDistrict() + ", " + place.getDivision());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        locationLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(locationLabel);

        // Right - Button
        JButton viewBtn = new JButton("View Place");
        viewBtn.setFont(new Font("Arial", Font.BOLD, 11));
        viewBtn.setBackground(new Color(70, 58, 47));
        viewBtn.setForeground(new Color(255, 215, 0));
        viewBtn.setFocusPainted(false);
        viewBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        viewBtn.setPreferredSize(new Dimension(100, 30));
        viewBtn.addActionListener(e -> {
            parent.dispose();
            new DivisionExploreFrame(currentUser, place.getDivision(), false).setVisible(true);
        });

        card.add(imagePanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(viewBtn, BorderLayout.EAST);

        return card;
    }

//private JPanel createPlaceResultCard(Place place, JDialog parent) {
//    JPanel card = new JPanel(new BorderLayout(10, 0));
//    card.setBackground(Color.WHITE);
//    card.setBorder(BorderFactory.createCompoundBorder(
//        BorderFactory.createLineBorder(new Color(220, 210, 200), 1),
//        BorderFactory.createEmptyBorder(10, 10, 10, 10)
//    ));
//    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
//    
//    // Left - Place Image
//    JPanel imagePanel = new JPanel(new BorderLayout());
//    imagePanel.setBackground(new Color(245, 242, 238));
//    imagePanel.setPreferredSize(new Dimension(50, 50));
//    imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 190, 180), 1));
//    
//    JLabel imageLabel = new JLabel();
//    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
//    
//    String imagePath = place.getImagePath();
//    boolean imageLoaded = false;
//    
//    if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
//        try {
//            java.net.URL url = getClass().getResource(imagePath);
//            if (url == null) url = getClass().getResource("/" + imagePath);
//            if (url != null) {
//                ImageIcon icon = new ImageIcon(url);
//                Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
//                imageLabel.setIcon(new ImageIcon(scaled));
//                imageLoaded = true;
//            }
//        } catch (Exception e) {}
//    }
//    
//    if (!imageLoaded) {
//        String firstLetter = place.getName().substring(0, 1).toUpperCase();
//        imageLabel.setText(firstLetter);
//        imageLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 22));
//        imageLabel.setForeground(new Color(70, 58, 47));
//    }
//    
//    imagePanel.add(imageLabel, BorderLayout.CENTER);
//    
//    // Center - Info
//    JPanel infoPanel = new JPanel();
//    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
//    infoPanel.setBackground(Color.WHITE);
//    infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
//    
//    JLabel nameLabel = new JLabel(place.getName());
//    nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
//    nameLabel.setForeground(new Color(70, 58, 47));
//    
//    JLabel locationLabel = new JLabel(place.getDistrict() + ", " + place.getDivision());
//    locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
//    locationLabel.setForeground(Color.GRAY);
//    
//    infoPanel.add(nameLabel);
//    infoPanel.add(Box.createVerticalStrut(3));
//    infoPanel.add(locationLabel);
//    
//    // Right - Button
//    JButton viewBtn = new JButton("View Place");
//    viewBtn.setFont(new Font("Arial", Font.BOLD, 11));
//    viewBtn.setBackground(new Color(70, 58, 47));
//    viewBtn.setForeground(new Color(255, 215, 0));
//    viewBtn.setFocusPainted(false);
//    viewBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
//    viewBtn.setPreferredSize(new Dimension(100, 32));
//    viewBtn.addActionListener(e -> {
//        parent.dispose();
//        new DivisionExploreFrame(currentUser, place.getDivision(), false).setVisible(true);
//    });
//    
//    card.add(imagePanel, BorderLayout.WEST);
//    card.add(infoPanel, BorderLayout.CENTER);
//    card.add(viewBtn, BorderLayout.EAST);
//    
//    return card;
//}
    private JPanel createUserResultCard(User user, JDialog parent) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 210, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(500, 70));
        card.setPreferredSize(new Dimension(500, 70));
        card.setMinimumSize(new Dimension(400, 70));

        // Left - Profile Photo
        ProfilePhotoUtils photoUtils = new ProfilePhotoUtils();
        JLabel photoLabel = photoUtils.createUserPhotoLabel(user, 40);
        photoLabel.setPreferredSize(new Dimension(40, 40));

        // Center - Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));

        String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName() : user.getUsername();

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
        nameLabel.setForeground(new Color(70, 58, 47));

        JLabel emailLabel = new JLabel(user.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(emailLabel);

        // Right - Button
        JButton viewBtn = new JButton("View Profile");
        viewBtn.setFont(new Font("Arial", Font.BOLD, 11));
        viewBtn.setBackground(new Color(70, 58, 47));
        viewBtn.setForeground(new Color(255, 215, 0));
        viewBtn.setFocusPainted(false);
        viewBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        viewBtn.setPreferredSize(new Dimension(100, 30));

        viewBtn.addActionListener(e -> {
            parent.dispose();
            new UserProfile(user, true).setVisible(true);
        });

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(viewBtn, BorderLayout.EAST);

        return card;
    }

//private JPanel createUserResultCard(User user, JDialog parent) {
//    JPanel card = new JPanel(new BorderLayout(10, 0));
//    card.setBackground(Color.WHITE);
//    card.setBorder(BorderFactory.createCompoundBorder(
//        BorderFactory.createLineBorder(new Color(220, 210, 200), 1),
//        BorderFactory.createEmptyBorder(10, 10, 10, 10)
//    ));
//    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
//    
//    // Left - Profile Photo
//    ProfilePhotoUtils photoUtils = new ProfilePhotoUtils();
//    JLabel photoLabel = photoUtils.createUserPhotoLabel(user, 45);
//    photoLabel.setPreferredSize(new Dimension(45, 45));
//    
//    // Center - Info
//    JPanel infoPanel = new JPanel();
//    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
//    infoPanel.setBackground(Color.WHITE);
//    infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
//    
//    String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
//            ? user.getFullName() : user.getUsername();
//    
//    JLabel nameLabel = new JLabel(displayName);
//    nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
//    nameLabel.setForeground(new Color(70, 58, 47));
//    
//    JLabel emailLabel = new JLabel(user.getEmail());
//    emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
//    emailLabel.setForeground(Color.GRAY);
//    
//    infoPanel.add(nameLabel);
//    infoPanel.add(Box.createVerticalStrut(3));
//    infoPanel.add(emailLabel);
//    
//    // Right - Button
//    JButton viewBtn = new JButton("View Profile");
//    viewBtn.setFont(new Font("Arial", Font.BOLD, 11));
//    viewBtn.setBackground(new Color(70, 58, 47));
//    viewBtn.setForeground(new Color(255, 215, 0));
//    viewBtn.setFocusPainted(false);
//    viewBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
//    viewBtn.setPreferredSize(new Dimension(100, 32));
//    viewBtn.addActionListener(e -> {
//        parent.dispose();
//        new UserProfile(user, true).setVisible(true);
//    });
//    
//    card.add(photoLabel, BorderLayout.WEST);
//    card.add(infoPanel, BorderLayout.CENTER);
//    card.add(viewBtn, BorderLayout.EAST);
//    
//    return card;
//}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.ExploBD.Frame.RoundedPanel ActiveTravel;
    private javax.swing.JButton Dhaka;
    private com.ExploBD.Frame.RoundedPanel Heading;
    private com.ExploBD.Frame.RoundedPanel Mytravelbox;
    private javax.swing.JScrollPane Mytravelscrool;
    private javax.swing.JButton Navbar;
    private javax.swing.JButton Rajshahi;
    private javax.swing.JButton barishal;
    private javax.swing.JButton chittagong;
    private javax.swing.JButton complete;
    private javax.swing.JButton creategrp;
    private com.ExploBD.Frame.RoundedPanel explorbox;
    private javax.swing.JButton findgrp;
    private javax.swing.JButton friends;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton kulna;
    private javax.swing.JLabel logo;
    private javax.swing.JPanel mainpanel;
    private javax.swing.JButton mygrp;
    private javax.swing.JButton mymensigh_;
    private javax.swing.JButton notification;
    private com.ExploBD.Frame.RoundedPanel pictureframe;
    private javax.swing.JButton rangpur;
    private javax.swing.JButton recommendation;
    private com.ExploBD.Frame.RoundedPanel roundedPanel1;
    private com.ExploBD.Frame.RoundedPanel roundedPanel2;
    private com.ExploBD.Frame.RoundedPanel roundedPanel3;
    private com.ExploBD.Frame.RoundedPanel roundedPanel4;
    private javax.swing.JButton search;
    private javax.swing.JTextField searchfor;
    private javax.swing.JButton sylhet;
    private javax.swing.JScrollPane tourScrool;
    private com.ExploBD.Frame.RoundedPanel welcomemsg;
    // End of variables declaration//GEN-END:variables
    private void loadCompletedTours() {
        try {
            List<Tour> completedTours = expenseService.getCompletedTours();
            System.out.println("==== COMPLETED TOURS ====");
            System.out.println("Found " + completedTours.size() + " completed tours");
        } catch (ExpenseService.ExpenseServiceException e) {
            e.printStackTrace();
        }
    }
}
