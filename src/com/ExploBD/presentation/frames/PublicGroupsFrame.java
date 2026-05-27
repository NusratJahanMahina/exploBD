package com.ExploBD.presentation.frames;

import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.service.GroupService;
import com.ExploBD.service.InvitationService;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.presentation.components.PlaceInfoDialog;
import com.ExploBD.util.ProfilePhotoUtils;
import com.ExploBD.util.RefreshManager;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class PublicGroupsFrame extends JFrame implements RefreshManager.RefreshListener {

    private User currentUser;
    private GroupService groupService;
    private InvitationService invitationService;
    private GroupDatabaseObject groupDAO;
    private InvitationDatabaseObject invitationDAO;
    private PlaceDatabaseObject placeDAO;
    private ProfilePhotoUtils photoUtils;

    private List<Group> allAvailableGroups;
    private List<Group> currentUserGroups;
    private List<Invitation> pendingRequests;

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel searchPanel;
    private JTextField searchField;
    private JButton searchBtn;
    private JButton clearSearchBtn;
    private JButton codeBtn;
    private JPanel resultsContainer;
    private JLabel resultCountLabel;

    private Color BROWN_BG = new Color(88, 74, 60);
    private Color ORANGE = new Color(255, 153, 51);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color GOLD = new Color(255, 215, 0);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color CARD_BORDER = new Color(200, 180, 160);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color PENDING_ORANGE = new Color(255, 165, 0);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color INFO_BLUE = new Color(33, 150, 243);
    private Color SEARCH_BG = new Color(245, 240, 235);

    private int baseTitleSize = 18;
    private int baseNormalSize = 12;
    private int baseSmallSize = 11;
    private int baseButtonSize = 11;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public PublicGroupsFrame(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        this.currentUser = user;
        this.groupService = new GroupService();
        this.invitationService = new InvitationService();
        this.groupDAO = new GroupDatabaseObject();
        this.invitationDAO = new InvitationDatabaseObject();
        this.placeDAO = new PlaceDatabaseObject();
        this.photoUtils = new ProfilePhotoUtils();
setUndecorated(true);
        setTitle("Discover Groups - ExploBD");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        
        initComponents();

        
        refreshData();

        
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                System.out.println("PublicGroupsFrame: Window gained focus - refreshing");
                refreshData();
            }
        });

        
        RefreshManager.getInstance().register(this);
        AppConfig.applyAndTrack(this);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                adjustForResize();
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            System.out.println("PublicGroupsFrame: Becoming visible - refreshing");
            refreshData();
        }
        super.setVisible(visible);
    }

    @Override
    public void onRefresh() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("PublicGroupsFrame: RefreshManager triggered refresh");
            refreshData();
        });
    }

    private void refreshData() {
        searchField.setText("");
        clearSearchBtn.setVisible(false);

        loadData();

        updateResultsPanel(allAvailableGroups);
        resultCountLabel.setText(allAvailableGroups.size() + " groups available");

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private void loadData() {
        this.currentUserGroups = groupDAO.findByUser(currentUser.getUserId());
        if (this.currentUserGroups == null) {
            this.currentUserGroups = new ArrayList<>();
        }

        this.allAvailableGroups = groupDAO.getAllGroupsUserNotIn(currentUser.getUserId());
        if (this.allAvailableGroups == null) {
            this.allAvailableGroups = new ArrayList<>();
        }

        List<Invitation> allInvitations = invitationDAO.findPendingForUser(
                currentUser.getEmail(), currentUser.getUsername()
        );

        this.pendingRequests = new ArrayList<>();
        if (allInvitations != null) {
            for (Invitation inv : allInvitations) {
                if (!inv.isExpired()) {  
                    this.pendingRequests.add(inv);
                }
            }
        }
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(BROWN_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        createTopPanel();
        createSearchPanel();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(SOFT_WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(createResultsScrollPane(), BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BROWN_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ORANGE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, baseButtonSize));
        backButton.setBackground(ORANGE);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        backButton.addActionListener(e -> dispose());

        JLabel titleLabel = new JLabel("DISCOVER GROUPS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, baseTitleSize));
        titleLabel.setForeground(ORANGE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, baseButtonSize));
        refreshButton.setBackground(DARK_BROWN);
        refreshButton.setForeground(GOLD);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        refreshButton.addActionListener(e -> refreshData());

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);
    }

    private void createSearchPanel() {
        searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(SEARCH_BG);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(SEARCH_BG);

        JLabel searchLabel = new JLabel("Search groups:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, baseNormalSize));
        searchLabel.setForeground(DARK_BROWN);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, baseNormalSize));
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        searchField.setPreferredSize(new Dimension(300, 32));

        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, baseButtonSize));
        searchBtn.setBackground(INFO_BLUE);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        searchBtn.setPreferredSize(new Dimension(80, 32));
        searchBtn.addActionListener(e -> performSearch());

        clearSearchBtn = new JButton("Clear");
        clearSearchBtn.setFont(new Font("Arial", Font.BOLD, baseButtonSize));
        clearSearchBtn.setBackground(Color.GRAY);
        clearSearchBtn.setForeground(Color.WHITE);
        clearSearchBtn.setFocusPainted(false);
        clearSearchBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        clearSearchBtn.setPreferredSize(new Dimension(70, 32));
        clearSearchBtn.setVisible(false);
        clearSearchBtn.addActionListener(e -> clearSearch());

        codeBtn = new JButton("Have Invitation ?");
        codeBtn.setFont(new Font("Arial", Font.BOLD, baseButtonSize));
        codeBtn.setBackground(ORANGE);
        codeBtn.setForeground(Color.WHITE);
        codeBtn.setFocusPainted(false);
        codeBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        codeBtn.setPreferredSize(new Dimension(150, 32));
        codeBtn.addActionListener(e -> showInvitationCodeDialog());

        leftPanel.add(searchLabel);
        leftPanel.add(searchField);
        leftPanel.add(searchBtn);
        leftPanel.add(clearSearchBtn);
        leftPanel.add(codeBtn);

        resultCountLabel = new JLabel();
        resultCountLabel.setFont(new Font("Arial", Font.ITALIC, baseSmallSize));
        resultCountLabel.setForeground(DARK_BROWN);

        searchPanel.add(leftPanel, BorderLayout.WEST);
        searchPanel.add(resultCountLabel, BorderLayout.EAST);

        searchField.addActionListener(e -> searchBtn.doClick());
    }

    private JScrollPane createResultsScrollPane() {
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(SOFT_WHITE);

        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getVerticalScrollBar().setBackground(SOFT_WHITE);

        return scrollPane;
    }

    private void updateResultsPanel(List<Group> groupsToShow) {
        resultsContainer.removeAll();

        if (groupsToShow == null || groupsToShow.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setBackground(SOFT_WHITE);
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

            String message = searchField.getText().trim().isEmpty()
                    ? "No groups available to join"
                    : "No groups match your search";

            JLabel emptyLabel = new JLabel(message, SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, baseNormalSize));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyLabel);
            resultsContainer.add(emptyPanel);
        } else {
            for (Group group : groupsToShow) {
                PublicGroupCard card = new PublicGroupCard(group, currentUser, this);
                resultsContainer.add(card);
                resultsContainer.add(Box.createVerticalStrut(10));
            }
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        List<Group> results = new ArrayList<>();

        if (searchTerm.isEmpty()) {
            results = allAvailableGroups;
            clearSearchBtn.setVisible(false);
        } else {
            clearSearchBtn.setVisible(true);

            String[] searchWords = searchTerm.split("\\s+");

            for (Group group : allAvailableGroups) {
                boolean matches = false;

                StringBuilder searchableText = new StringBuilder();
                searchableText.append(group.getName().toLowerCase()).append(" ");

                if (group.getDestinationName() != null) {
                    searchableText.append(group.getDestinationName().toLowerCase()).append(" ");
                }

                if (group.getDestinationDivision() != null) {
                    searchableText.append(group.getDestinationDivision().toLowerCase()).append(" ");
                }

                User leader = getGroupLeader(group);
                if (leader != null) {
                    searchableText.append(leader.getDisplayName().toLowerCase()).append(" ");
                }

                String fullText = searchableText.toString();

                boolean allWordsFound = true;
                for (String word : searchWords) {
                    if (!fullText.contains(word)) {
                        allWordsFound = false;
                        break;
                    }
                }

                if (allWordsFound) {
                    results.add(group);
                }
            }
        }

        resultCountLabel.setText(results.size() + " groups found");
        updateResultsPanel(results);
    }

    private User getGroupLeader(Group group) {
        for (GroupMember member : group.getAllMembers()) {
            if (member instanceof RegisteredMember && member.getRole().toString().equals("LEADER")) {
                return ((RegisteredMember) member).getUser();
            }
        }
        return null;
    }

    private void clearSearch() {
        searchField.setText("");
        clearSearchBtn.setVisible(false);
        resultCountLabel.setText(allAvailableGroups.size() + " groups available");
        updateResultsPanel(allAvailableGroups);
    }

    private void adjustForResize() {
        int width = getWidth();
        int height = getHeight();

        double scale = Math.min(width / 800.0, height / 600.0);
        scale = Math.max(0.8, Math.min(scale, 1.5));

        baseTitleSize = (int) (18 * scale);
        baseNormalSize = (int) (12 * scale);
        baseSmallSize = (int) (11 * scale);
        baseButtonSize = (int) (11 * scale);

        baseTitleSize = Math.max(baseTitleSize, 16);
        baseNormalSize = Math.max(baseNormalSize, 11);
        baseSmallSize = Math.max(baseSmallSize, 10);
        baseButtonSize = Math.max(baseButtonSize, 10);

        revalidate();
        repaint();
    }

    private void showInvitationCodeDialog() {
        JDialog dialog = new JDialog(this, "Join by Invitation Code", true);
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Enter Invitation Code:");
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(DARK_BROWN);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));

        JTextField codeField = new JTextField(15);
        codeField.setFont(new Font("Arial", Font.PLAIN, 12));
        codeField.setMaximumSize(new Dimension(200, 30));
        codeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(codeField);
        panel.add(Box.createVerticalStrut(15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton joinBtn = new JButton("Join Group");
        joinBtn.setFont(new Font("Arial", Font.BOLD, 11));
        joinBtn.setBackground(SUCCESS_GREEN);
        joinBtn.setForeground(Color.WHITE);
        joinBtn.setFocusPainted(false);
        joinBtn.addActionListener(e -> {
            String code = codeField.getText().trim().toUpperCase();
            if (!code.isEmpty()) {
                joinByInvitationCode(code);
                dialog.dispose();
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 11));
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(joinBtn);
        buttonPanel.add(cancelBtn);

        panel.add(buttonPanel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    
    public boolean isGroupJoined(Group group) {
        for (Group g : currentUserGroups) {
            if (g.getId().equals(group.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPendingRequest(Group group) {
        for (Invitation inv : pendingRequests) {
            if (inv.getGroup().getId().equals(group.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean canUserJoinGroup(Group group) {
        if (isGroupJoined(group)) {
            return false;
        }
        if (hasPendingRequest(group)) {
            return false;
        }

        LocalDate newStart = group.getStartDate();
        LocalDate newEnd = group.getEndDate();

        if (newStart != null && newEnd != null) {
            for (Group g : currentUserGroups) {
                if (g.getStartDate() != null && g.getEndDate() != null) {
                    if (!(newEnd.isBefore(g.getStartDate()) || newStart.isAfter(g.getEndDate()))) {
                        return false;
                    }
                }
            }
        }

        if (group.getMemberCount() >= group.getMaxMembers()) {
            return false;
        }

        return true;
    }

    public String getActionButtonStatus(Group group) {
        
        if (group.getEndDate() != null && group.getEndDate().isBefore(LocalDate.now())) {
            return "PAST";
        }

        if (isGroupJoined(group)) {
            return "VIEW_GROUP";
        }
        if (hasPendingRequest(group)) {
            return "CANCEL_REQUEST";
        }
        if (group.getMemberCount() >= group.getMaxMembers()) {
            return "FULL";
        }

        LocalDate newStart = group.getStartDate();
        LocalDate newEnd = group.getEndDate();

        if (newStart != null && newEnd != null) {
            for (Group g : currentUserGroups) {
                if (g.getStartDate() != null && g.getEndDate() != null) {
                    if (!(newEnd.isBefore(g.getStartDate()) || newStart.isAfter(g.getEndDate()))) {
                        return "CONFLICT";
                    }
                }
            }
        }

        return group.isPublic() ? "JOIN_PUBLIC" : "REQUEST_PRIVATE";
    }

    public String getStatusReason(Group group) {
        if (group.getEndDate() != null && group.getEndDate().isBefore(LocalDate.now())) {
            return "This trip has already ended";
        }

        if (group.getMemberCount() >= group.getMaxMembers()) {
            return "Group is full (" + group.getMemberCount() + "/" + group.getMaxMembers() + ")";
        }

        LocalDate newStart = group.getStartDate();
        LocalDate newEnd = group.getEndDate();

        if (newStart != null && newEnd != null) {
            for (Group g : currentUserGroups) {
                if (g.getStartDate() != null && g.getEndDate() != null) {
                    if (!(newEnd.isBefore(g.getStartDate()) || newStart.isAfter(g.getEndDate()))) {
                        return "Date conflict with: " + g.getName();
                    }
                }
            }
        }

        return "";
    }

    public void joinPublicGroup(Group group) {
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
                    // After profile is complete, try joining again
                    joinPublicGroup(group);
                }
            });
            editProfile.setVisible(true);
        }
        return;
    }
    // ===== END PROFILE CHECK =====
    
    String confirmMessage = "Join " + group.getName() + "?\n\n"
            + "Destination: " + (group.getDestinationName() != null ? group.getDestinationName() : "Not set") + "\n"
            + "Dates: " + (group.getStartDate() != null ? group.getStartDate().format(dateFormatter) : "TBD")
            + " - " + (group.getEndDate() != null ? group.getEndDate().format(dateFormatter) : "TBD") + "\n"
            + "Members: " + group.getMemberCount() + "/" + group.getMaxMembers();

    int confirm = JOptionPane.showConfirmDialog(this,
            confirmMessage,
            "Confirm Join",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = groupService.joinPublicGroup(group.getId(), currentUser);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "You have joined " + group.getName() + "!\n\n"
                    + "You can now view this group in My Groups.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            RefreshManager.getInstance().refreshAll();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to join group. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    
    public void requestToJoinPrivateGroup(Group group) {
    // ===== ADD PROFILE COMPLETENESS CHECK =====
    if (!currentUser.isProfileComplete()) {
        int response = JOptionPane.showConfirmDialog(this,
            "Your profile is incomplete. Would you like to complete it now?\n\n" +
            "You need to add your name and phone number to request joining groups.",
            "Profile Incomplete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            EditUserProfile editProfile = new EditUserProfile(currentUser);
            editProfile.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    RefreshManager.getInstance().refreshAll();
                    requestToJoinPrivateGroup(group);
                }
            });
            editProfile.setVisible(true);
        }
        return;
    }
    // ===== END PROFILE CHECK =====
    
    String confirmMessage = "Send request to join " + group.getName() + "?\n\n"
            + "Destination: " + (group.getDestinationName() != null ? group.getDestinationName() : "Not set") + "\n"
            + "Dates: " + (group.getStartDate() != null ? group.getStartDate().format(dateFormatter) : "TBD")
            + " - " + (group.getEndDate() != null ? group.getEndDate().format(dateFormatter) : "TBD") + "\n"
            + "Members: " + group.getMemberCount() + "/" + group.getMaxMembers() + "\n\n"
            + "The group leader will review your request.";

    int confirm = JOptionPane.showConfirmDialog(this,
            confirmMessage,
            "Confirm Request",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            groupService.createInvitationRequest(
                    group.getId(),
                    currentUser,
                    currentUser.getDisplayName(),
                    currentUser.getEmail(),
                    "JOIN_REQUEST"
            );
            JOptionPane.showMessageDialog(this,
                    "Request sent to group leader!\n\n"
                    + "You'll be notified when it's approved.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    
    
//    public void joinPublicGroup(Group group) {
//        String confirmMessage = "Join " + group.getName() + "?\n\n"
//                + "Destination: " + (group.getDestinationName() != null ? group.getDestinationName() : "Not set") + "\n"
//                + "Dates: " + (group.getStartDate() != null ? group.getStartDate().format(dateFormatter) : "TBD")
//                + " - " + (group.getEndDate() != null ? group.getEndDate().format(dateFormatter) : "TBD") + "\n"
//                + "Members: " + group.getMemberCount() + "/" + group.getMaxMembers();
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//                confirmMessage,
//                "Confirm Join",
//                JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            boolean success = groupService.joinPublicGroup(group.getId(), currentUser);
//            if (success) {
//                JOptionPane.showMessageDialog(this,
//                        "You have joined " + group.getName() + "!\n\n"
//                        + "You can now view this group in My Groups.",
//                        "Success",
//                        JOptionPane.INFORMATION_MESSAGE);
//                refreshData();
//                RefreshManager.getInstance().refreshAll();
//            } else {
//                JOptionPane.showMessageDialog(this,
//                        "Failed to join group. Please try again.",
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }

//    public void requestToJoinPrivateGroup(Group group) {
//        String confirmMessage = "Send request to join " + group.getName() + "?\n\n"
//                + "Destination: " + (group.getDestinationName() != null ? group.getDestinationName() : "Not set") + "\n"
//                + "Dates: " + (group.getStartDate() != null ? group.getStartDate().format(dateFormatter) : "TBD")
//                + " - " + (group.getEndDate() != null ? group.getEndDate().format(dateFormatter) : "TBD") + "\n"
//                + "Members: " + group.getMemberCount() + "/" + group.getMaxMembers() + "\n\n"
//                + "The group leader will review your request.";
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//                confirmMessage,
//                "Confirm Request",
//                JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            try {
//                groupService.createInvitationRequest(
//                        group.getId(),
//                        currentUser,
//                        currentUser.getDisplayName(),
//                        currentUser.getEmail(),
//                        "JOIN_REQUEST"
//                );
//                JOptionPane.showMessageDialog(this,
//                        "Request sent to group leader!\n\n"
//                        + "You'll be notified when it's approved.",
//                        "Success",
//                        JOptionPane.INFORMATION_MESSAGE);
//                refreshData();
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(this,
//                        "Error: " + e.getMessage(),
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }

    public void cancelJoinRequest(Group group) {
        try {
            for (Invitation inv : pendingRequests) {
                if (inv.getGroup().getId().equals(group.getId())) {
                    invitationService.updateStatus(inv.getInvitationId(), InvitationStatus.DECLINED);
                    break;
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Request cancelled.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public void joinByInvitationCode(String code) {
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
                    joinByInvitationCode(code);
                }
            });
            editProfile.setVisible(true);
        }
        return;
    }
    // ===== END PROFILE CHECK =====
    
    Group group = groupDAO.findByInvitationCode(code);

    if (group == null) {
        JOptionPane.showMessageDialog(this,
                "No group found with invitation code: " + code,
                "Invalid Code",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (isGroupJoined(group)) {
        JOptionPane.showMessageDialog(this,
                "You are already a member of: " + group.getName(),
                "Already Member",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    if (group.getMemberCount() >= group.getMaxMembers()) {
        JOptionPane.showMessageDialog(this,
                "Cannot join: Group is full (" + group.getMemberCount() + "/" + group.getMaxMembers() + ")",
                "Group Full",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    String message = "Join " + group.getName() + " using invitation code?\n\n"
            + "Destination: " + (group.getDestinationName() != null ? group.getDestinationName() : "Not set") + "\n"
            + "Dates: " + (group.getStartDate() != null ? group.getStartDate().format(dateFormatter) : "TBD")
            + " - " + (group.getEndDate() != null ? group.getEndDate().format(dateFormatter) : "TBD") + "\n"
            + "Members: " + group.getMemberCount() + "/" + group.getMaxMembers();

    int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Confirm Join by Code",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = groupService.joinPublicGroup(group.getId(), currentUser);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Successfully joined " + group.getName() + "!\n\n"
                    + "You can now view this group in My Groups.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            RefreshManager.getInstance().refreshAll();
        }
    }
}
    
//
//    public void joinByInvitationCode(String code) {
//        Group group = groupDAO.findByInvitationCode(code);
//
//        if (group == null) {
//            JOptionPane.showMessageDialog(this,
//                    "No group found with invitation code: " + code,
//                    "Invalid Code",
//                    JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        if (isGroupJoined(group)) {
//            JOptionPane.showMessageDialog(this,
//                    "You are already a member of: " + group.getName(),
//                    "Already Member",
//                    JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
//
//        if (group.getMemberCount() >= group.getMaxMembers()) {
//            JOptionPane.showMessageDialog(this,
//                    "Cannot join: Group is full (" + group.getMemberCount() + "/" + group.getMaxMembers() + ")",
//                    "Group Full",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        String message = "Join " + group.getName() + " using invitation code?\n\n"
//                + "Destination: " + (group.getDestinationName() != null ? group.getDestinationName() : "Not set") + "\n"
//                + "Dates: " + (group.getStartDate() != null ? group.getStartDate().format(dateFormatter) : "TBD")
//                + " - " + (group.getEndDate() != null ? group.getEndDate().format(dateFormatter) : "TBD") + "\n"
//                + "Members: " + group.getMemberCount() + "/" + group.getMaxMembers();
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//                message,
//                "Confirm Join by Code",
//                JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            boolean success = groupService.joinPublicGroup(group.getId(), currentUser);
//            if (success) {
//                JOptionPane.showMessageDialog(this,
//                        "Successfully joined " + group.getName() + "!\n\n"
//                        + "You can now view this group in My Groups.",
//                        "Success",
//                        JOptionPane.INFORMATION_MESSAGE);
//                refreshData();
//                RefreshManager.getInstance().refreshAll();
//            }
//        }
//    }

    public Place getGroupDestinationPlace(Group group) {
        if (group == null || group.getDestinationName() == null) {
            return null;
        }

        List<PlaceSuggestion> suggestions = group.getAllSuggestions();
        for (int i = 0; i < suggestions.size(); i++) {
            PlaceSuggestion s = suggestions.get(i);
            Place place = s.getPlace();
            if (place != null && place.getName().equalsIgnoreCase(group.getDestinationName())) {
                return place;
            }
        }

        String division = group.getDestinationDivision();
        if (division != null && !division.isEmpty()) {
            List<Place> places = placeDAO.findAllByDivision(division);
            for (int i = 0; i < places.size(); i++) {
                Place place = places.get(i);
                if (place.getName().equalsIgnoreCase(group.getDestinationName())) {
                    return place;
                }
            }
        }

        return null;
    }

    @Override
    public void dispose() {
        RefreshManager.getInstance().unregister(this);
        super.dispose();
    }
}
