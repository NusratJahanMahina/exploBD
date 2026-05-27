package com.ExploBD.presentation.frames.module2;

import com.ExploBD.object.User;
import com.ExploBD.object.Place;

import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.enums.MemberType;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.domain.members.GuestMember;
import com.ExploBD.service.GroupService;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.exceptions .GroupException;
import com.ExploBD.presentation.components.FriendSelectionDialog;
import com.ExploBD.presentation.frames.module1.DestinationBrowserFrame;
import com.ExploBD.service.FriendService;
import com.ExploBD.session.UserSession;
import com.ExploBD.util.ProfilePhotoUtils;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class CreateGroupFrame extends JFrame {

    private User currentUser;

    private GroupDatabaseObject groupDAO;
    private InvitationDatabaseObject invitationDAO;

    private JTextField groupNameField;
    private JTextArea descriptionArea;
    private JLabel destinationLabel;
    private String selectedDestinationId;
    private String selectedDestinationName;
    private String selectedDestinationDivision;
    private Place selectedPlace;
    private JButton browseDestinationBtn;
    private JTextArea planArea;
    private JComboBox<String> privacyCombo;
    private JSpinner membersSpinner;
    private JSpinner daysSpinner;
    private JComboBox<String> startDayCombo;
    private JComboBox<String> startMonthCombo;
    private JComboBox<String> startYearCombo;
    private JComboBox<String> minBudgetCombo;
    private JComboBox<String> maxBudgetCombo;
    private JLabel charCountLabel;
    private JButton createGroupBtn;
    private ProfilePhotoUtils photoUtils;

    private GroupService groupService;

    private JPanel membersPanel;
    private JButton inviteEmailBtn;
    private JButton inviteFriendBtn;
    private JButton inviteGuestBtn;
    private JPanel pendingInvitesPanel;

    private List<GroupMember> pendingMembers;
    private List<PendingInvite> pendingInvitesList;
    private JLabel validationLabel;

    private int[] budgetValues = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000,
        1200, 1400, 1600, 1800, 2000, 2500, 3000, 3500, 4000,
        4500, 5000, 6000, 7000, 8000, 9000, 10000, 12000,
        14000, 16000, 18000, 20000};

    private int baseFontSizeLabels = 13;
    private int baseFontSizeFields = 13;
    private int baseFontSizeButtons = 11;
    private int currentWidth = 800;
    private int currentHeight = 600;
    private Timer resizeTimer;

    private class PendingInvite {

        String name;
        String email;
        String type;
        User user;

        PendingInvite(String name, String email, String type, User user) {
            this.name = name;
            this.email = email;
            this.type = type;
            this.user = user;
        }
    }
public CreateGroupFrame(User user) {
    // Get fresh user from session instead of using passed user
    this.currentUser = UserSession.getInstance().getCurrentUser();
    
    // Fallback to passed user if session is null
    if (this.currentUser == null) {
        this.currentUser = user;
    }
     this.photoUtils = new ProfilePhotoUtils();
    this.groupDAO = new GroupDatabaseObject();
    this.invitationDAO = new InvitationDatabaseObject();
    this.pendingMembers = new ArrayList<>();
    this.pendingInvitesList = new ArrayList<>();
    this.selectedDestinationId = null;
    this.selectedDestinationName = null;
    this.selectedDestinationDivision = null;
    this.selectedPlace = null;
    this.groupService = new GroupService();
    this.photoUtils = new ProfilePhotoUtils();
setUndecorated(true);
    initComponents();

    setPreferredSize(new Dimension(800, 600));
    setSize(800, 600);

    setupResponsiveListener();
    setupEnterKeyNavigation();
    setLocationRelativeTo(null);
    setVisible(true);
}

    public CreateGroupFrame(User user, Place preSelectedPlace) {
        this(user);
        if (preSelectedPlace != null) {
            this.selectedPlace = preSelectedPlace;
            this.selectedDestinationName = preSelectedPlace.getName();
            this.selectedDestinationDivision = preSelectedPlace.getDivision();
            destinationLabel.setText(preSelectedPlace.getName() + " - " + preSelectedPlace.getDistrict());
            destinationLabel.setForeground(new Color(70, 58, 47));
        }
    }

    private void initComponents() {
        setTitle("Create Travel Group - ExploBD");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(1000, 700));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(88, 74, 60));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(88, 74, 60));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(255, 153, 51)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, baseFontSizeButtons));
        backButton.setBackground(new Color(255, 153, 51));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 120, 30), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> dispose());

        JLabel titleLabel = new JLabel("CREATE TRAVEL GROUP");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(88, 74, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        leftPanel.setPreferredSize(new Dimension(480, 520));

        JPanel namePanel = new JPanel(new BorderLayout(8, 0));
        namePanel.setBackground(Color.WHITE);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        namePanel.setPreferredSize(new Dimension(450, 40));

        JLabel nameLabel = new JLabel("Group Name:");
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseFontSizeLabels));
        nameLabel.setForeground(new Color(70, 58, 47));
        nameLabel.setPreferredSize(new Dimension(110, 28));

        groupNameField = new JTextField();
        groupNameField.setFont(new Font("Bookman Old Style", Font.PLAIN, baseFontSizeFields));
        groupNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 160, 140)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(groupNameField, BorderLayout.CENTER);
        leftPanel.add(namePanel);
        leftPanel.add(Box.createVerticalStrut(6));

        JPanel descPanel = new JPanel(new BorderLayout(8, 0));
        descPanel.setBackground(Color.WHITE);
        descPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        descPanel.setPreferredSize(new Dimension(450, 75));

        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseFontSizeLabels));
        descLabel.setForeground(new Color(70, 58, 47));
        descLabel.setPreferredSize(new Dimension(100, 28));

        descriptionArea = new JTextArea(2, 20);
        descriptionArea.setFont(new Font("Bookman Old Style", Font.PLAIN, baseFontSizeFields));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(320, 55));

        descPanel.add(descLabel, BorderLayout.WEST);
        descPanel.add(descScroll, BorderLayout.CENTER);
        leftPanel.add(descPanel);
        leftPanel.add(Box.createVerticalStrut(6));

        JPanel destPanel = new JPanel(new BorderLayout(8, 0));
        destPanel.setBackground(Color.WHITE);
        destPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        destPanel.setPreferredSize(new Dimension(450, 40));

        JLabel destLabel = new JLabel("Destination:");
        destLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseFontSizeLabels));
        destLabel.setForeground(new Color(70, 58, 47));
        destLabel.setPreferredSize(new Dimension(100, 28));

        destinationLabel = new JLabel("No destination selected");
        destinationLabel.setFont(new Font("Bookman Old Style", Font.ITALIC, baseFontSizeFields - 1));
        destinationLabel.setForeground(new Color(150, 150, 150));

        browseDestinationBtn = new JButton("Browse");
        browseDestinationBtn.setFont(new Font("Arial", Font.BOLD, 11));
        browseDestinationBtn.setBackground(new Color(70, 58, 47));
        browseDestinationBtn.setForeground(new Color(255, 215, 0));
        browseDestinationBtn.setFocusPainted(false);
        browseDestinationBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        browseDestinationBtn.addActionListener(e -> {
            DestinationBrowserFrame browser = new DestinationBrowserFrame(currentUser);
            browser.setVisible(true);

            browser.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    Place selected = browser.getSelectedPlace();
                    if (selected != null) {
                        selectedPlace = selected;
                        selectedDestinationName = selected.getName();
                        selectedDestinationDivision = selected.getDivision();
                        destinationLabel.setText(selected.getName() + " - " + selected.getDistrict());
                        destinationLabel.setForeground(new Color(70, 58, 47));

                    }
                }
            });
        });

        JPanel destInnerPanel = new JPanel(new BorderLayout(8, 0));
        destInnerPanel.setBackground(Color.WHITE);
        destInnerPanel.add(destinationLabel, BorderLayout.CENTER);
        destInnerPanel.add(browseDestinationBtn, BorderLayout.EAST);

        destPanel.add(destLabel, BorderLayout.WEST);
        destPanel.add(destInnerPanel, BorderLayout.CENTER);
        leftPanel.add(destPanel);
        leftPanel.add(Box.createVerticalStrut(6));

        JPanel planPanel = new JPanel(new BorderLayout(8, 0));
        planPanel.setBackground(Color.WHITE);
        planPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        planPanel.setPreferredSize(new Dimension(450, 125));

        JLabel planLabel = new JLabel("My Plan:");
        planLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseFontSizeLabels));
        planLabel.setForeground(new Color(70, 58, 47));
        planLabel.setPreferredSize(new Dimension(100, 28));

        planArea = new JTextArea(4, 25);
        planArea.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
        planArea.setLineWrap(true);
        planArea.setWrapStyleWord(true);
        planArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JScrollPane planScroll = new JScrollPane(planArea);
        planScroll.setPreferredSize(new Dimension(320, 90));

        charCountLabel = new JLabel("0/500");
        charCountLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 10));
        charCountLabel.setForeground(new Color(150, 150, 150));
        charCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        planArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateCounter();
            }

            public void removeUpdate(DocumentEvent e) {
                updateCounter();
            }

            public void insertUpdate(DocumentEvent e) {
                updateCounter();
            }

            private void updateCounter() {
                int len = planArea.getText().length();
                charCountLabel.setText(len + "/500");
                charCountLabel.setForeground(len > 500 ? Color.RED : new Color(150, 150, 150));
            }
        });

        JPanel planWithCount = new JPanel(new BorderLayout());
        planWithCount.setBackground(Color.WHITE);
        planWithCount.add(planScroll, BorderLayout.CENTER);
        planWithCount.add(charCountLabel, BorderLayout.SOUTH);

        planPanel.add(planLabel, BorderLayout.WEST);
        planPanel.add(planWithCount, BorderLayout.CENTER);
        leftPanel.add(planPanel);
        leftPanel.add(Box.createVerticalStrut(6));

        JPanel privacyPanel = new JPanel(new BorderLayout(8, 0));
        privacyPanel.setBackground(Color.WHITE);
        privacyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        privacyPanel.setPreferredSize(new Dimension(450, 40));

        JLabel privacyLabel = new JLabel("Privacy:");
        privacyLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseFontSizeLabels));
        privacyLabel.setForeground(new Color(70, 58, 47));
        privacyLabel.setPreferredSize(new Dimension(100, 28));

        privacyCombo = new JComboBox<>(new String[]{"PUBLIC", "PRIVATE"});
        privacyCombo.setFont(new Font("Bookman Old Style", Font.PLAIN, baseFontSizeFields));
        privacyCombo.setBackground(Color.WHITE);

        privacyPanel.add(privacyLabel, BorderLayout.WEST);
        privacyPanel.add(privacyCombo, BorderLayout.CENTER);
        leftPanel.add(privacyPanel);
        leftPanel.add(Box.createVerticalStrut(6));

        JPanel memberDurationPanel = new JPanel(new GridBagLayout());
        memberDurationPanel.setBackground(Color.WHITE);
        memberDurationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        memberDurationPanel.setPreferredSize(new Dimension(450, 40));

        GridBagConstraints mdGbc = new GridBagConstraints();
        mdGbc.fill = GridBagConstraints.HORIZONTAL;
        mdGbc.insets = new Insets(0, 5, 0, 5);

        mdGbc.gridx = 0;
        mdGbc.gridy = 0;
        mdGbc.weightx = 0.5;
        JPanel memberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        memberPanel.setBackground(Color.WHITE);
        JLabel memberLabel = new JLabel("Members:");
        memberLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        memberLabel.setForeground(new Color(70, 58, 47));
        membersSpinner = new JSpinner(new SpinnerNumberModel(4, 2, 20, 1));
        membersSpinner.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        membersSpinner.setPreferredSize(new Dimension(65, 28));
        memberPanel.add(memberLabel);
        memberPanel.add(membersSpinner);
        memberDurationPanel.add(memberPanel, mdGbc);

        mdGbc.gridx = 1;
        mdGbc.weightx = 0.5;
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        durationPanel.setBackground(Color.WHITE);
        JLabel daysLabel = new JLabel("Days:");
        daysLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        daysLabel.setForeground(new Color(70, 58, 47));
        daysSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));
        daysSpinner.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        daysSpinner.setPreferredSize(new Dimension(65, 28));
        durationPanel.add(daysLabel);
        durationPanel.add(daysSpinner);
        memberDurationPanel.add(durationPanel, mdGbc);

        leftPanel.add(memberDurationPanel);
        leftPanel.add(Box.createVerticalStrut(6));

        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = String.valueOf(i + 1);
        }
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int currentYear = LocalDate.now().getYear();
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear + i);
        }

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        datePanel.setBackground(Color.WHITE);
        datePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                "START DATE",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, 11),
                new Color(70, 58, 47)
        ));
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        datePanel.setPreferredSize(new Dimension(450, 55));

        startDayCombo = new JComboBox<>(days);
        startDayCombo.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
        startDayCombo.setPreferredSize(new Dimension(65, 26));

        startMonthCombo = new JComboBox<>(months);
        startMonthCombo.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
        startMonthCombo.setPreferredSize(new Dimension(75, 26));

        startYearCombo = new JComboBox<>(years);
        startYearCombo.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
        startYearCombo.setPreferredSize(new Dimension(80, 26));

        LocalDate today = LocalDate.now();
        startDayCombo.setSelectedItem(String.valueOf(today.getDayOfMonth()));
        startMonthCombo.setSelectedIndex(today.getMonthValue() - 1);
        startYearCombo.setSelectedItem(String.valueOf(today.getYear()));

        datePanel.add(startDayCombo);
        datePanel.add(startMonthCombo);
        datePanel.add(startYearCombo);
        leftPanel.add(datePanel);
        leftPanel.add(Box.createVerticalStrut(6));

        JPanel budgetPanel = new JPanel(new GridBagLayout());
        budgetPanel.setBackground(Color.WHITE);
        budgetPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                "BUDGET PER PERSON",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, 11),
                new Color(70, 58, 47)
        ));
        budgetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        budgetPanel.setPreferredSize(new Dimension(450, 60));

        GridBagConstraints budgetGbc = new GridBagConstraints();
        budgetGbc.fill = GridBagConstraints.HORIZONTAL;
        budgetGbc.insets = new Insets(5, 10, 5, 10);
        budgetGbc.weightx = 0.5;

        String[] budgetItems = new String[budgetValues.length];
        for (int i = 0; i < budgetValues.length; i++) {
            budgetItems[i] = formatBudget(budgetValues[i]) + " tk";
        }

        
        budgetGbc.gridx = 0;
        JPanel minPanel = new JPanel(new BorderLayout(8, 0));
        minPanel.setBackground(Color.WHITE);
        JLabel minLabel = new JLabel("Min:");
        minLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        minLabel.setPreferredSize(new Dimension(40, 28));

        minBudgetCombo = new JComboBox<>(budgetItems);
        minBudgetCombo.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        minBudgetCombo.setBackground(Color.WHITE);
        minBudgetCombo.setSelectedIndex(4);

        minPanel.add(minLabel, BorderLayout.WEST);
        minPanel.add(minBudgetCombo, BorderLayout.CENTER);
        budgetPanel.add(minPanel, budgetGbc);

        
        budgetGbc.gridx = 1;
        JPanel maxPanel = new JPanel(new BorderLayout(8, 0));
        maxPanel.setBackground(Color.WHITE);
        JLabel maxLabel = new JLabel("Max:");
        maxLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        maxLabel.setPreferredSize(new Dimension(40, 28));

        maxBudgetCombo = new JComboBox<>(budgetItems);
        maxBudgetCombo.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        maxBudgetCombo.setBackground(Color.WHITE);
        for (int i = 0; i < budgetValues.length; i++) {
            if (budgetValues[i] == 5000) {
                maxBudgetCombo.setSelectedIndex(i);
                break;
            }
        }

        maxPanel.add(maxLabel, BorderLayout.WEST);
        maxPanel.add(maxBudgetCombo, BorderLayout.CENTER);
        budgetPanel.add(maxPanel, budgetGbc);

        minBudgetCombo.addActionListener(e -> validateBudget());
        maxBudgetCombo.addActionListener(e -> validateBudget());

        leftPanel.add(budgetPanel);

        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.fill = GridBagConstraints.HORIZONTAL;
        rGbc.insets = new Insets(5, 5, 5, 5);
        rGbc.weightx = 1.0;

        
        rGbc.gridy = 0;
        rGbc.weighty = 0.0;
        JLabel membersTitle = new JLabel("GROUP MEMBERS");
        membersTitle.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        membersTitle.setForeground(new Color(70, 58, 47));
        rightPanel.add(membersTitle, rGbc);

       
        rGbc.gridy = 1;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 153, 51));
        rightPanel.add(sep, rGbc);

        
        rGbc.gridy = 2;
        rGbc.weighty = 0.0;
        rGbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(createLeaderCard(), rGbc);

       
        rGbc.gridy = 3;
        rGbc.weighty = 0.0;
        rGbc.fill = GridBagConstraints.HORIZONTAL;
        inviteEmailBtn = createStyledButton("Invite Member by Email", new Color(70, 58, 47), new Color(255, 215, 0));
        inviteEmailBtn.addActionListener(e -> showInviteUserDialog());
        rightPanel.add(inviteEmailBtn, rGbc);

        
        rGbc.gridy = 4;
        inviteFriendBtn = createStyledButton("Invite Friend", new Color(70, 58, 47), new Color(255, 215, 0));
        inviteFriendBtn.addActionListener(e -> showInviteFriendDialog());
        rightPanel.add(inviteFriendBtn, rGbc);

        
        rGbc.gridy = 5;
        inviteGuestBtn = createStyledButton("Invite Guest", new Color(100, 86, 72), Color.WHITE);
        inviteGuestBtn.addActionListener(e -> showInviteGuestDialog());
        rightPanel.add(inviteGuestBtn, rGbc);

        
        rGbc.gridy = 6;
        rGbc.insets = new Insets(15, 5, 5, 5);
        JLabel pendingLabel = new JLabel("PENDING INVITES");
        pendingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        pendingLabel.setForeground(new Color(70, 58, 47));
        rightPanel.add(pendingLabel, rGbc);

        
        rGbc.gridy = 7;
        rGbc.weighty = 1.0;
        rGbc.fill = GridBagConstraints.BOTH;
        rGbc.insets = new Insets(5, 5, 5, 5);

        pendingInvitesPanel = new JPanel();
        pendingInvitesPanel.setLayout(new BoxLayout(pendingInvitesPanel, BoxLayout.Y_AXIS));
        pendingInvitesPanel.setBackground(Color.WHITE);

        JScrollPane pendingScroll = new JScrollPane(pendingInvitesPanel);
        pendingScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        pendingScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        pendingScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pendingScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        rightPanel.add(pendingScroll, rGbc);

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        contentPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        contentPanel.add(rightPanel, gbc);

        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(new Color(88, 74, 60));

        validationLabel = new JLabel(" ");
        validationLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        validationLabel.setForeground(new Color(255, 200, 200));

        createGroupBtn = new JButton("CREATE GROUP");
        createGroupBtn.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        createGroupBtn.setBackground(new Color(255, 153, 51));
        createGroupBtn.setForeground(Color.WHITE);
        createGroupBtn.setFocusPainted(false);
        createGroupBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        createGroupBtn.addActionListener(e -> createGroup());

        bottomPanel.add(validationLabel);
        bottomPanel.add(createGroupBtn);

        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
    }

private JPanel createLeaderCard() {
    JPanel card = new JPanel(new GridBagLayout());
    card.setBackground(new Color(250, 245, 240));
    card.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140)));

    // Get fresh user from session
    User freshUser = UserSession.getInstance().getCurrentUser();
    if (freshUser == null) {
        freshUser = currentUser;
    }

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(8, 8, 8, 8);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.weightx = 0.0;
    gbc.weighty = 1.0;
    JLabel photoLabel = photoUtils.createUserPhotoLabel(freshUser, 48);
    photoLabel.setPreferredSize(new Dimension(48, 48));
    card.add(photoLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 0.5;
    gbc.insets = new Insets(8, 5, 2, 8);
    String displayName = freshUser.getFullName() != null ? freshUser.getFullName() : freshUser.getUsername();
    JLabel nameLabel = new JLabel(displayName);
    nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
    nameLabel.setForeground(new Color(70, 58, 47));
    card.add(nameLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weighty = 0.5;
    gbc.insets = new Insets(2, 5, 8, 8);
    JLabel emailLabel = new JLabel(freshUser.getEmail());
    emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    emailLabel.setForeground(new Color(150, 150, 150));
    card.add(emailLabel, gbc);

    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.weightx = 0.0;
    gbc.insets = new Insets(8, 5, 8, 8);
    JLabel roleLabel = new JLabel("LEADER");
    roleLabel.setFont(new Font("Arial", Font.BOLD, 10));
    roleLabel.setForeground(Color.WHITE);
    roleLabel.setBackground(new Color(255, 153, 51));
    roleLabel.setOpaque(true);
    roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    roleLabel.setPreferredSize(new Dimension(70, 25));
    card.add(roleLabel, gbc);

    return card;
}

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void validateBudget() {
        int minIndex = minBudgetCombo.getSelectedIndex();
        int maxIndex = maxBudgetCombo.getSelectedIndex();

        if (minIndex > maxIndex) {
            maxBudgetCombo.setSelectedIndex(minIndex);
            JOptionPane.showMessageDialog(this,
                    "Maximum budget cannot be less than minimum budget.\nMax has been adjusted.",
                    "Budget Adjustment",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
private void checkGroupData() throws GroupException {
    
    try {
        System.out.println("Checking group data...");
        
        try {
            if (groupNameField.getText().trim().isEmpty()) {
                throw new GroupException(GroupException.nameRequired, 
                    "Please enter a group name");
            }
        } catch (GroupException e) {
            JOptionPane.showMessageDialog(this, 
                "Name Error: " + e.getMessage(), 
                "Cannot Create Group", 
                JOptionPane.WARNING_MESSAGE);
            groupNameField.requestFocus();
            throw e;
        }
        
        try {
            if (selectedPlace == null) {
                throw new GroupException(GroupException.destinationRequired, 
                    "Please select a destination first");
            }
        } catch (GroupException e) {
            JOptionPane.showMessageDialog(this, 
                "Destination Error: " + e.getMessage(), 
                "Cannot Create Group", 
                JOptionPane.WARNING_MESSAGE);
            throw e;
        }
        
        try {
            if (planArea.getText().length() > 500) {
                throw new GroupException(GroupException.planTooLong, 
                    "Your travel plan is too long. Maximum 500 characters allowed");
            }
        } catch (GroupException e) {
            JOptionPane.showMessageDialog(this, 
                "Plan Error: " + e.getMessage(), 
                "Cannot Create Group", 
                JOptionPane.WARNING_MESSAGE);
            planArea.requestFocus();
            throw e;
        }
        
        try {
            int minBudget = getSelectedMinBudget();
            int maxBudget = getSelectedMaxBudget();
            
            if (minBudget > maxBudget) {
                throw new GroupException(GroupException.budgetInvalid, 
                    "Minimum budget cannot be greater than maximum budget");
            }
        } catch (GroupException e) {
            JOptionPane.showMessageDialog(this, 
                "Budget Error: " + e.getMessage(), 
                "Cannot Create Group", 
                JOptionPane.WARNING_MESSAGE);
            throw e;
        }
        
        try {
            int day = Integer.parseInt((String) startDayCombo.getSelectedItem());
            int month = startMonthCombo.getSelectedIndex() + 1;
            int year = Integer.parseInt((String) startYearCombo.getSelectedItem());
            
            LocalDate startDate = LocalDate.of(year, month, day);
            LocalDate endDate = startDate.plusDays((Integer) daysSpinner.getValue() - 1);
            
            if (startDate.isBefore(LocalDate.now())) {
               
                throw new GroupException(GroupException.pastDate, 
                    "Start date cannot be in the past");
            }
            
            
            try {
                boolean hasConflict = groupService.hasDateConflictWithAnyGroup(
                    currentUser.getUserId(), startDate, endDate);
                
                if (hasConflict) {
                    
                    throw new GroupException(GroupException.dateConflict, 
                        "You already have another trip planned during these dates");
                }
                
            } catch (Exception ex) {
               
                throw new GroupException(GroupException.systemError, 
                    "Could not check date availability. Please try again.");
            }
            
        } catch (NumberFormatException ex) {
           
            throw new GroupException(GroupException.dateInvalid, 
                "Please select valid dates");
        } catch (GroupException e) {
            JOptionPane.showMessageDialog(this, 
                "Date Error: " + e.getMessage(), 
                "Cannot Create Group", 
                JOptionPane.WARNING_MESSAGE);
            throw e;
        }
        
        System.out.println("All group data is valid");
        
    } catch (GroupException e) {
        throw e;
    }
}
   
    private void setupEnterKeyNavigation() {
        javax.swing.Action nextFocusAction = new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ((java.awt.Component) e.getSource()).transferFocus();
            }
        };

        groupNameField.addActionListener(nextFocusAction);

        descriptionArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        descriptionArea.append("\n");
                    } else {
                        e.consume();
                        descriptionArea.transferFocus();
                    }
                }
            }
        });

        planArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        planArea.append("\n");
                    } else {
                        e.consume();
                        planArea.transferFocus();
                    }
                }
            }
        });
    }

    
    private String formatBudget(int value) {
        if (value >= 1000) {
            return String.format("%,d", value);
        }
        return String.valueOf(value);
    }


    private JLabel createProfilePhotoLabel(User user, int size) {
        return photoUtils.createUserPhotoLabel(user, size);  
    }

    private void addPendingInvite(PendingInvite invite) {
        JPanel invitePanel = new JPanel(new BorderLayout(8, 0));
        invitePanel.setBackground(Color.WHITE);
        invitePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        invitePanel.setMaximumSize(new Dimension(250, 60));
        invitePanel.setPreferredSize(new Dimension(250, 60));
        invitePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel photoLabel;

        if (invite.type.equals("USER") && invite.user != null) {
            photoLabel = photoUtils.createUserPhotoLabel(invite.user, 40);
        } else {
            photoLabel = new JLabel("👤❓");
            photoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            photoLabel.setPreferredSize(new Dimension(40, 40));
            photoLabel.setMinimumSize(new Dimension(40, 40));
            photoLabel.setMaximumSize(new Dimension(40, 40));
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            photoLabel.setBackground(new Color(240, 240, 240));
            photoLabel.setOpaque(true);
            photoLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140)));
        }

       
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

        
        JLabel nameLabel = new JLabel(invite.name);
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 12)); 
        nameLabel.setForeground(new Color(70, 58, 47));

        JLabel detailLabel;
        if (invite.email != null && !invite.email.isEmpty()) {
            detailLabel = new JLabel(invite.email);
            detailLabel.setFont(new Font("Arial", Font.PLAIN, 9)); 
            detailLabel.setForeground(new Color(150, 150, 150));
        } else {
            detailLabel = new JLabel("No email");
            detailLabel.setFont(new Font("Arial", Font.ITALIC, 9));
            detailLabel.setForeground(new Color(180, 180, 180));
        }

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(detailLabel);

        JLabel statusLabel;
        if (invite.type.equals("GUEST")) {
            statusLabel = new JLabel(invite.email != null ? "GUEST+EMAIL" : "GUEST");
            statusLabel.setBackground(new Color(100, 86, 72));
        } else {
            statusLabel = new JLabel("USER");
            statusLabel.setBackground(new Color(255, 153, 51));
        }
        statusLabel.setFont(new Font("Arial", Font.BOLD, 9));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8)); 

        invitePanel.add(photoLabel, BorderLayout.WEST);
        invitePanel.add(infoPanel, BorderLayout.CENTER);
        invitePanel.add(statusLabel, BorderLayout.EAST);

        pendingInvitesPanel.add(invitePanel);
        pendingInvitesPanel.add(Box.createVerticalStrut(5)); 
        pendingInvitesPanel.revalidate();
        pendingInvitesPanel.repaint();
    }

    private void showInviteUserDialog() {
        JDialog dialog = new JDialog(this, "Invite Member", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel searchLabel = new JLabel("Search by email:");
        searchLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 153, 51)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 11));
        searchBtn.setBackground(new Color(70, 58, 47));
        searchBtn.setForeground(new Color(255, 215, 0));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        searchPanel.add(searchLabel, BorderLayout.NORTH);

        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 0));
        searchInputPanel.setBackground(Color.WHITE);
        searchInputPanel.add(searchField, BorderLayout.CENTER);
        searchInputPanel.add(searchBtn, BorderLayout.EAST);
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
                new Color(70, 58, 47)
        ));

        panel.add(resultsScroll, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String email = searchField.getText().trim();
            if (!email.isEmpty()) {
                resultsPanel.removeAll();

                List<User> results = groupService.searchUsersByEmail(email);

                if (results.isEmpty()) {
                    JLabel notFoundLabel = new JLabel("No user found with this email.");
                    notFoundLabel.setFont(new Font("Bookman Old Style", Font.ITALIC, 12));
                    notFoundLabel.setForeground(new Color(150, 150, 150));
                    notFoundLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
                    notFoundLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    resultsPanel.add(notFoundLabel);
                } else {
                    for (User user : results) {
                        boolean alreadyInGroup = false;
                        for (PendingInvite invite : pendingInvitesList) {
                            if (invite.user != null && invite.user.getUserId().equals(user.getUserId())) {
                                alreadyInGroup = true;
                                break;
                            }
                        }

                        if (!alreadyInGroup && !user.getUserId().equals(currentUser.getUserId())) {
                            resultsPanel.add(createUserResultCard(user, dialog));
                            resultsPanel.add(Box.createVerticalStrut(5));
                        }
                    }
                }

                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        });

        searchField.addActionListener(e -> searchBtn.doClick());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel createUserResultCard(User user, JDialog dialog) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        card.setMaximumSize(new Dimension(380, 70));
        card.setPreferredSize(new Dimension(380, 70));

        JLabel photoLabel = photoUtils.createUserPhotoLabel(user, 40);
        photoLabel.setPreferredSize(new Dimension(40, 40));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName() : user.getUsername();

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        nameLabel.setForeground(new Color(70, 58, 47));

        JLabel emailLabel = new JLabel(user.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        emailLabel.setForeground(new Color(150, 150, 150));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(emailLabel);

        JButton inviteBtn = new JButton("Invite");
        inviteBtn.setFont(new Font("Arial", Font.BOLD, 10));
        inviteBtn.setBackground(new Color(70, 58, 47));
        inviteBtn.setForeground(new Color(255, 215, 0));
        inviteBtn.setFocusPainted(false);
        inviteBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        inviteBtn.setPreferredSize(new Dimension(70, 30));
        inviteBtn.addActionListener(e -> {
            PendingInvite invite = new PendingInvite(
                    displayName,
                    user.getEmail(),
                    "USER",
                    user
            );
            pendingInvitesList.add(invite);
            addPendingInvite(invite);
            dialog.dispose();
        });

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(inviteBtn, BorderLayout.EAST);

        return card;
    }

private void showInviteFriendDialog() {
    FriendService friendService = new FriendService(currentUser);
    List<User> friends = friendService.getFriends();
    
    if (friends.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "You don't have any friends yet.\n\n" +
                "Go to Friends tab to add friends first!",
                "No Friends",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    // Open the new FriendSelectionDialog
    FriendSelectionDialog dialog = new FriendSelectionDialog(this, currentUser, selectedFriend -> {
        // Check if already invited
        boolean alreadyInvited = false;
        for (PendingInvite invite : pendingInvitesList) {
            if (invite.user != null && invite.user.getUserId().equals(selectedFriend.getUserId())) {
                alreadyInvited = true;
                break;
            }
        }
        
        if (!alreadyInvited) {
            String displayName = selectedFriend.getFullName() != null && !selectedFriend.getFullName().isEmpty()
                    ? selectedFriend.getFullName() : selectedFriend.getUsername();
            
            PendingInvite invite = new PendingInvite(
                    displayName,
                    selectedFriend.getEmail(),
                    "USER",
                    selectedFriend
            );
            pendingInvitesList.add(invite);
            addPendingInvite(invite);
            
            JOptionPane.showMessageDialog(this,
                    selectedFriend.getDisplayName() + " added to invite list!",
                    "Friend Added",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    selectedFriend.getDisplayName() + " is already invited!",
                    "Already Invited",
                    JOptionPane.WARNING_MESSAGE);
        }
    });
    
    dialog.setVisible(true);
}

private JPanel createFriendCard(User friend, JDialog dialog) {
    JPanel card = new JPanel(new BorderLayout(8, 0));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
    ));
    card.setMaximumSize(new Dimension(380, 70));
    card.setPreferredSize(new Dimension(380, 70));
    
    // Profile photo
    JLabel photoLabel = photoUtils.createUserPhotoLabel(friend, 45);
    photoLabel.setPreferredSize(new Dimension(45, 45));
    
    // Info panel
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);
    infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    
    String displayName = friend.getFullName() != null && !friend.getFullName().isEmpty()
            ? friend.getFullName() : friend.getUsername();
    
    JLabel nameLabel = new JLabel(displayName);
    nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
    nameLabel.setForeground(new Color(70, 58, 47)); // DARK_BROWN
    
    JLabel emailLabel = new JLabel(friend.getEmail());
    emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    emailLabel.setForeground(Color.GRAY);
    
    infoPanel.add(nameLabel);
    infoPanel.add(Box.createVerticalStrut(3));
    infoPanel.add(emailLabel);
    
    // Invite button
    JButton inviteBtn = new JButton("Invite");
    inviteBtn.setFont(new Font("Arial", Font.BOLD, 11));
    inviteBtn.setBackground(new Color(255, 153, 51)); // ORANGE
    inviteBtn.setForeground(Color.WHITE);
    inviteBtn.setFocusPainted(false);
    inviteBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
    inviteBtn.setPreferredSize(new Dimension(70, 32));
    
    inviteBtn.addActionListener(e -> {
        // Add friend to pending invites
        PendingInvite pendingInvite = new PendingInvite(
                displayName,
                friend.getEmail(),
                "USER",
                friend
        );
        pendingInvitesList.add(pendingInvite);
        addPendingInvite(pendingInvite);
        
        JOptionPane.showMessageDialog(dialog,
                "Invitation sent to " + displayName + "!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
    });
    
    card.add(photoLabel, BorderLayout.WEST);
    card.add(infoPanel, BorderLayout.CENTER);
    card.add(inviteBtn, BorderLayout.EAST);
    
    return card;
}

    private void showInviteGuestDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JLabel hintLabel = new JLabel("<html><i>Email is optional. If provided, user will get invitation when they register with this email.</i></html>");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        hintLabel.setForeground(Color.GRAY);
        panel.add(hintLabel, gbc);

        
        int result = JOptionPane.showConfirmDialog(this,
                panel,
                "Invite Guest",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            
            if (name.isEmpty() && email.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter at least a name or an email.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (name.isEmpty() && !email.isEmpty()) {
            }

            if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address or leave it empty.",
                        "Invalid Email",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (PendingInvite invite : pendingInvitesList) {
                if (invite.email != null && !invite.email.isEmpty()
                        && invite.email.equalsIgnoreCase(email)) {
                    JOptionPane.showMessageDialog(this,
                            "This guest has already been invited with this email.",
                            "Already Invited",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (invite.name != null && !invite.name.isEmpty()
                        && invite.name.equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(this,
                            "A guest with this name has already been invited.",
                            "Already Invited",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            PendingInvite invite = new PendingInvite(
                    name, 
                    email.isEmpty() ? null : email, 
                    "GUEST",
                    null
            );

            pendingInvitesList.add(invite);
            addPendingInvite(invite);

            System.out.println("Guest invited: " + name + (email.isEmpty() ? "" : " (" + email + ")"));
        }
    }

    private int getSelectedMinBudget() {
        int index = minBudgetCombo.getSelectedIndex();
        return budgetValues[index];
    }

    private int getSelectedMaxBudget() {
        int index = maxBudgetCombo.getSelectedIndex();
        return budgetValues[index];
    }

private void createGroup() {
    try {
        checkGroupData();
        
        int day = Integer.parseInt((String) startDayCombo.getSelectedItem());
        int month = startMonthCombo.getSelectedIndex() + 1;
        int year = Integer.parseInt((String) startYearCombo.getSelectedItem());
        LocalDate startDate = LocalDate.of(year, month, day);
        LocalDate endDate = startDate.plusDays((Integer) daysSpinner.getValue() - 1);
        
        Group group = groupService.createGroup(
            groupNameField.getText().trim(),
            currentUser,
            descriptionArea.getText().trim(),
            startDate,
            endDate,
            getSelectedMinBudget(),
            getSelectedMaxBudget(),
            (Integer) membersSpinner.getValue()
        );
        
        group.setDestinationName(selectedDestinationName);
        group.setDestinationDivision(selectedDestinationDivision);
        
        boolean saved = groupDAO.saveGroup(group);
        if (!saved) {
            throw new GroupException(GroupException.systemError, 
                "Failed to save group to database");
        }
        
        // ===== ADD THIS BACK - SAVE INVITATIONS =====
        int inviteCount = 0;
        for (PendingInvite pending : pendingInvitesList) {
            try {
                Invitation invitation = null;

                if (pending.type.equals("GUEST")) {
                    if (pending.email != null && !pending.email.isEmpty()) {
                        invitation = Invitation.createPlaceholderWithEmail(
                                group,
                                currentUser,
                                pending.name,
                                pending.email
                        );
                        invitation.setInvitationCode(group.getInvitationCode());
                    } else {
                        invitation = Invitation.createPlaceholder(
                                group,
                                currentUser,
                                pending.name
                        );
                        invitation.setInvitationCode(group.getInvitationCode());
                    }
                } else if (pending.type.equals("USER") && pending.user != null) {
                    invitation = new Invitation(
                            group,
                            currentUser,
                            pending.user.getEmail()
                    );
                    invitation.setInviteeName(pending.name);
                    invitation.setInvitationCode(group.getInvitationCode());
                }

                if (invitation != null) {
                    invitationDAO.save(invitation);
                    inviteCount++;
                    System.out.println("Invitation saved for: " + pending.name);
                }

            } catch (Exception e) {
                System.out.println("Failed to invite " + pending.name + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        // ===== END OF INVITATION CODE =====
        
        String message = "Group created successfully!\n\n" +
            "Group Name: " + group.getName() + "\n" +
            "Destination: " + selectedDestinationName + "\n" +
            "Invitations sent: " + inviteCount + " out of " + pendingInvitesList.size();
        
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
        
    } catch (GroupException e) {
        System.out.println("Group creation cancelled: " + e.getMessage());
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "An unexpected error occurred: " + e.getMessage(),
            "System Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

    private void setupResponsiveListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                currentWidth = getWidth();
                currentHeight = getHeight();
                adjustForResize();
            }
        });
    }

    private void adjustForResize() {
        double scale = Math.min(currentWidth / 800.0, currentHeight / 600.0);
        scale = Math.min(scale, 1.5);
        scale = Math.max(scale, 0.9);

        int newLabelSize = Math.max(11, (int) (baseFontSizeLabels * scale));
        int newFieldSize = Math.max(11, (int) (baseFontSizeFields * scale));
        int newButtonSize = Math.max(10, (int) (baseFontSizeButtons * scale));

        updateComponentFonts(this, newLabelSize, newFieldSize, newButtonSize);
    }

    private void updateComponentFonts(Container container, int labelSize, int fieldSize, int buttonSize) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getFont().getFontName().contains("Comic")) {
                    label.setFont(new Font("Comic Sans MS", label.getFont().getStyle(), labelSize));
                }
            } else if (comp instanceof JTextField || comp instanceof JTextArea) {
                comp.setFont(new Font("Bookman Old Style", Font.PLAIN, fieldSize));
            } else if (comp instanceof JComboBox) {
                comp.setFont(new Font("Bookman Old Style", Font.PLAIN, fieldSize - 1));
            } else if (comp instanceof JSpinner) {
                comp.setFont(new Font("Bookman Old Style", Font.PLAIN, fieldSize - 1));
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (!button.getText().contains("←")) {
                    button.setFont(new Font("Arial", Font.BOLD, buttonSize));
                }
            } else if (comp instanceof Container) {
                updateComponentFonts((Container) comp, labelSize, fieldSize, buttonSize);
            }
        }
    }
}
