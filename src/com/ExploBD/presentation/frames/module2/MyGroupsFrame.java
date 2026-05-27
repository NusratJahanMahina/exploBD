package com.ExploBD.presentation.frames.module2;

import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.service.GroupService;
import com.ExploBD.service.InvitationService;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import com.ExploBD.presentation.components.PlaceInfoDialog;
import com.ExploBD.presentation.components.ViewRequestsDialog;
import com.ExploBD.presentation.frames.AppConfig;
import com.ExploBD.presentation.frames.module1.DivisionExploreFrame;
import com.ExploBD.Frame.RoundedPanel;
import com.ExploBD.session.UserSession;
import com.ExploBD.util.ProfilePhotoUtils;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.presentation.frames.module2.leftpanel.LeftPanel;
import com.ExploBD.presentation.frames.module2.rightpanel.RightPanel;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MyGroupsFrame extends JFrame {

    private User currentUser;
    private GroupDataManager dataManager;

    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;

    private JPanel mainPanel;
    private JPanel topPanel;
    private JSplitPane splitPane;

    private LeftPanel leftPanel;
    private RightPanel rightPanel;

    private Color BROWN_BG = new Color(88, 74, 60);
    private Color ORANGE = new Color(255, 153, 51);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color GOLD = new Color(255, 215, 0);
    private Color CREAM = new Color(250, 245, 240);
    private Color LIGHT_RED = new Color(255, 235, 230);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color EXPIRED_GRAY = new Color(200, 200, 200);
    private Color ACTIVE_GREEN = new Color(46, 125, 50);
    private Color ACTIVE_LIGHT_GREEN = new Color(235, 255, 235);

    private int baseTitleSize = 18;
    private int baseSubtitleSize = 14;
    private int baseNormalSize = 12;
    private int baseSmallSize = 11;
    private int baseButtonSize = 11;

    private DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM");

    private ProfilePhotoUtils photoUtils = new ProfilePhotoUtils();

    public MyGroupsFrame(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        this.currentUser = user;
        this.dataManager = new GroupDataManager(currentUser);

        initComponents();

        dataManager.loadData();

        rightPanel.setGroup(dataManager.getSelectedGroup());

        dataManager.setSuggestFields(suggestPlaceField, suggestReasonField, suggestSubmitBtn);
        dataManager.addListener(this::refreshAll);

        setTitle("My Groups - ExploBD");
        setSize(900, 650);
        setUndecorated(true);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        AppConfig.applyAndTrack(this);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustForResize();
            }
        });
    }

    private void adjustForResize() {
        int width = getWidth();
        int height = getHeight();

        double widthScale = width / 900.0;
        double heightScale = height / 650.0;
        double scale = Math.min(widthScale, heightScale);
        scale = Math.max(0.8, Math.min(scale, 1.5));

        baseTitleSize = (int) (18 * scale);
        baseSubtitleSize = (int) (14 * scale);
        baseNormalSize = (int) (12 * scale);
        baseSmallSize = (int) (11 * scale);
        baseButtonSize = (int) (11 * scale);

        baseTitleSize = Math.max(baseTitleSize, 16);
        baseSubtitleSize = Math.max(baseSubtitleSize, 12);
        baseNormalSize = Math.max(baseNormalSize, 11);
        baseSmallSize = Math.max(baseSmallSize, 10);
        baseButtonSize = Math.max(baseButtonSize, 10);

        if (rightPanel != null) {
            rightPanel.refresh();
        }
        if (leftPanel != null) {
            leftPanel.refresh();
        }
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(BROWN_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        createTopPanel();
        createSplitPane();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BROWN_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ORANGE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, baseButtonSize));
        backButton.setBackground(ORANGE);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JLabel titleLabel = new JLabel("MY GROUPS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, baseTitleSize));
        titleLabel.setForeground(ORANGE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, baseButtonSize));
        refreshButton.setBackground(DARK_BROWN);
        refreshButton.setForeground(GOLD);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataManager.refreshData();
            }
        });

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);
    }

    private void createSplitPane() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(BROWN_BG);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);
        splitPane.setResizeWeight(0.4);

        leftPanel = new LeftPanel(dataManager, photoUtils);
        rightPanel = new RightPanel(dataManager, photoUtils);

        leftPanel.setSuggestFields(suggestPlaceField, suggestReasonField, suggestSubmitBtn);
        rightPanel.setSuggestFields(suggestPlaceField, suggestReasonField, suggestSubmitBtn);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
    }

    private void refreshAll() {
        if (leftPanel != null) {
            leftPanel.refresh();
        }
        if (rightPanel != null) {
            rightPanel.setGroup(dataManager.getSelectedGroup());
            rightPanel.refresh();
        }
    }

    public void showMembersTab() {
        if (rightPanel != null && rightPanel.tabbedPane != null) {
            rightPanel.tabbedPane.setSelectedIndex(1);  // Members tab index
        }
    }

    public void showVoteTab() {
        if (rightPanel != null && rightPanel.tabbedPane != null) {
            rightPanel.tabbedPane.setSelectedIndex(3);  // Vote tab index
        }
    }

    public void showSuggestTab() {
        if (rightPanel != null && rightPanel.tabbedPane != null) {
            rightPanel.tabbedPane.setSelectedIndex(2);  // Suggest tab index
        }
    }
}
