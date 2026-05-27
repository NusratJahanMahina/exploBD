package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.service.GroupService;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.presentation.frames.module1.DivisionExploreFrame;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class SuggestTab extends JPanel {
    
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private Group currentGroup;
    private GroupService groupService;
    
    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;
    
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color GOLD = new Color(255, 215, 0);
    
    private int baseSmallSize = 11;
    private int baseNormalSize = 12;
    
    public SuggestTab(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        this.groupService = new GroupService();
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SOFT_WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    public void setSuggestFields(JTextField placeField, JTextField reasonField, JButton submitBtn) {
        this.suggestPlaceField = placeField;
        this.suggestReasonField = reasonField;
        this.suggestSubmitBtn = submitBtn;
    }
    
    public void setGroup(Group group) {
        this.currentGroup = group;
        refresh();
    }
    
    public void refresh() {
        removeAll();
        
        if (currentGroup == null) return;
        
        User currentUser = dataManager.getCurrentUser();
        boolean isLeader = currentGroup.isLeader(currentUser);
        boolean isVotingEnded = (currentGroup.getStatus() == GroupStatus.VOTING && !currentGroup.isVotingActive());
        boolean isVotingActive = (currentGroup.getStatus() == GroupStatus.VOTING && currentGroup.isVotingActive());
        boolean isConfirmed = (currentGroup.getStatus() == GroupStatus.CONFIRMED);
        
        if (isVotingEnded) {
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            messagePanel.setBackground(SOFT_WHITE);
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ORANGE, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel titleLabel = new JLabel("VOTING ENDED", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(ORANGE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagePanel.add(titleLabel);
            
            messagePanel.add(Box.createVerticalStrut(5));
            
            JLabel thanksLabel = new JLabel("Thanks for voting! Go to Vote tab to see results.", SwingConstants.CENTER);
            thanksLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            thanksLabel.setForeground(Color.GRAY);
            thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagePanel.add(thanksLabel);
            
            add(messagePanel);
            add(Box.createVerticalStrut(10));
        }
        
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(SOFT_WHITE);
        topSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                "SUGGESTIONS",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, baseSmallSize),
                DARK_BROWN
        ));
        
        if (isVotingActive) {
            JLabel votingActiveLabel = new JLabel("VOTING IN PROGRESS - Go to Vote tab");
            votingActiveLabel.setFont(new Font("Arial", Font.BOLD, 11));
            votingActiveLabel.setForeground(ORANGE);
            votingActiveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            topSection.add(votingActiveLabel);
            topSection.add(Box.createVerticalStrut(8));
        }
        
        List<PlaceSuggestion> suggestions = currentGroup.getAllSuggestions();
        
        List<PlaceSuggestion> pendingList = new ArrayList<>();
        List<PlaceSuggestion> approvedList = new ArrayList<>();
        List<PlaceSuggestion> rejectedList = new ArrayList<>();
        
        for (PlaceSuggestion s : suggestions) {
            if (s.getStatus() == InvitationStatus.PENDING) {
                pendingList.add(s);
            } else if (s.getStatus() == InvitationStatus.APPROVED) {
                approvedList.add(s);
            } else if (s.getStatus() == InvitationStatus.DECLINED) {
                rejectedList.add(s);
            }
        }
        
        PlaceSuggestion userExistingSuggestion = null;
        String userExistingSuggestionStatus = "";
        
        for (PlaceSuggestion s : pendingList) {
            if (s.getSuggester().getUserId().equals(currentUser.getUserId())) {
                userExistingSuggestion = s;
                userExistingSuggestionStatus = "PENDING";
                break;
            }
        }
        if (userExistingSuggestion == null) {
            for (PlaceSuggestion s : approvedList) {
                if (s.getSuggester().getUserId().equals(currentUser.getUserId())) {
                    userExistingSuggestion = s;
                    userExistingSuggestionStatus = "APPROVED";
                    break;
                }
            }
        }
        boolean userHasExistingSuggestion = userExistingSuggestion != null;
        
        JPanel suggestionsContainer = new JPanel();
        suggestionsContainer.setLayout(new BoxLayout(suggestionsContainer, BoxLayout.Y_AXIS));
        suggestionsContainer.setBackground(SOFT_WHITE);
        
        if (!pendingList.isEmpty()) {
            String pendingTitle = "PENDING REVIEW (" + pendingList.size() + ")";
            JLabel pendingTitleLabel = new JLabel(pendingTitle);
            pendingTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseSmallSize));
            pendingTitleLabel.setForeground(ORANGE);
            pendingTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            suggestionsContainer.add(pendingTitleLabel);
            suggestionsContainer.add(Box.createVerticalStrut(4));
            
            for (PlaceSuggestion s : pendingList) {
                SuggestionCard card = new SuggestionCard(s, isLeader, currentUser, currentGroup, dataManager, photoUtils);
                suggestionsContainer.add(card);
                suggestionsContainer.add(Box.createVerticalStrut(6));
            }
        }
        
        if (!approvedList.isEmpty()) {
            if (!pendingList.isEmpty()) {
                suggestionsContainer.add(Box.createVerticalStrut(4));
            }
            
            String approvedTitle = "APPROVED FOR VOTING (" + approvedList.size() + ")";
            JLabel approvedTitleLabel = new JLabel(approvedTitle);
            approvedTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseSmallSize));
            approvedTitleLabel.setForeground(SUCCESS_GREEN);
            approvedTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            suggestionsContainer.add(approvedTitleLabel);
            suggestionsContainer.add(Box.createVerticalStrut(4));
            
            for (PlaceSuggestion s : approvedList) {
                SuggestionCard card = new SuggestionCard(s, isLeader, currentUser, currentGroup, dataManager, photoUtils);
                suggestionsContainer.add(card);
                suggestionsContainer.add(Box.createVerticalStrut(4));
            }
        }
        
        if (!rejectedList.isEmpty()) {
            if (!pendingList.isEmpty() || !approvedList.isEmpty()) {
                suggestionsContainer.add(Box.createVerticalStrut(4));
            }
            
            String rejectedTitle = "REJECTED (" + rejectedList.size() + ")";
            JLabel rejectedTitleLabel = new JLabel(rejectedTitle);
            rejectedTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseSmallSize));
            rejectedTitleLabel.setForeground(ERROR_RED);
            rejectedTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            suggestionsContainer.add(rejectedTitleLabel);
            suggestionsContainer.add(Box.createVerticalStrut(4));
            
            for (PlaceSuggestion s : rejectedList) {
                SuggestionCard card = new SuggestionCard(s, isLeader, currentUser, currentGroup, dataManager, photoUtils);
                suggestionsContainer.add(card);
                suggestionsContainer.add(Box.createVerticalStrut(4));
            }
        }
        
        if (suggestions.isEmpty()) {
            JLabel noSuggestions = new JLabel("No suggestions yet.");
            noSuggestions.setFont(new Font("Arial", Font.ITALIC, baseNormalSize));
            noSuggestions.setForeground(Color.GRAY);
            noSuggestions.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
            noSuggestions.setAlignmentX(Component.LEFT_ALIGNMENT);
            suggestionsContainer.add(noSuggestions);
        }
        
        JScrollPane suggestionsScroll = new JScrollPane(suggestionsContainer);
        suggestionsScroll.setBorder(null);
        suggestionsScroll.getVerticalScrollBar().setUnitIncrement(16);
        suggestionsScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        suggestionsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        suggestionsScroll.setPreferredSize(new Dimension(400, 250));
        
        topSection.add(suggestionsScroll);
        
        if (isLeader && !approvedList.isEmpty() && currentGroup.getStatus() == GroupStatus.PLANNING) {
            topSection.add(Box.createVerticalStrut(8));
            JButton startVotingBtn = new JButton("START VOTING");
            startVotingBtn.setFont(new Font("Arial", Font.BOLD, 11));
            startVotingBtn.setBackground(ORANGE);
            startVotingBtn.setForeground(Color.WHITE);
            startVotingBtn.setFocusPainted(false);
            startVotingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            startVotingBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            startVotingBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.startVoting();
                }
            });
            topSection.add(startVotingBtn);
        }
        
        add(topSection);
        add(Box.createVerticalStrut(10));
        
        if (!isVotingEnded && !isConfirmed && !isLeader) {
            if (userHasExistingSuggestion) {
                JPanel existingSuggestionPanel = new JPanel();
                existingSuggestionPanel.setLayout(new BoxLayout(existingSuggestionPanel, BoxLayout.Y_AXIS));
                existingSuggestionPanel.setBackground(SOFT_WHITE);
                existingSuggestionPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                        "YOUR SUGGESTION",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Comic Sans MS", Font.BOLD, baseSmallSize),
                        DARK_BROWN
                ));
                
                JPanel messageInnerPanel = new JPanel();
                messageInnerPanel.setLayout(new BoxLayout(messageInnerPanel, BoxLayout.Y_AXIS));
                messageInnerPanel.setBackground(SOFT_WHITE);
                messageInnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JLabel placeNameLabel = new JLabel(userExistingSuggestion.getPlace().getName());
                placeNameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 13));
                placeNameLabel.setForeground(ORANGE);
                placeNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                messageInnerPanel.add(placeNameLabel);
                messageInnerPanel.add(Box.createVerticalStrut(5));
                
                String statusMessage = userExistingSuggestionStatus.equals("PENDING")
                        ? "Waiting for leader approval."
                        : "Approved for voting.";
                JLabel statusLabel = new JLabel(statusMessage);
                statusLabel.setFont(new Font("Arial", Font.ITALIC, 10));
                statusLabel.setForeground(userExistingSuggestionStatus.equals("PENDING") ? Color.GRAY : SUCCESS_GREEN);
                statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                messageInnerPanel.add(statusLabel);
                
                if (userExistingSuggestionStatus.equals("PENDING")) {
                    messageInnerPanel.add(Box.createVerticalStrut(10));
                    JButton cancelBtn = new JButton("CANCEL");
                    cancelBtn.setFont(new Font("Arial", Font.BOLD, 9));
                    cancelBtn.setBackground(ERROR_RED);
                    cancelBtn.setForeground(Color.WHITE);
                    cancelBtn.setFocusPainted(false);
                    cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                    cancelBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                    
                    final String suggestionIdToCancel = userExistingSuggestion.getSuggestionId();
                    cancelBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dataManager.cancelSuggestion(suggestionIdToCancel);
                        }
                    });
                    
                    messageInnerPanel.add(cancelBtn);
                }
                
                existingSuggestionPanel.add(messageInnerPanel);
                add(existingSuggestionPanel);
                
            } else if (!isVotingActive) {
                add(createSuggestionForm());
            }
        } else if (isLeader && !isVotingEnded && !isConfirmed) {
            JPanel leaderSection = new JPanel();
            leaderSection.setBackground(SOFT_WHITE);
            leaderSection.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                    "LEADER",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Comic Sans MS", Font.BOLD, baseSmallSize),
                    DARK_BROWN
            ));
            
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            messagePanel.setBackground(SOFT_WHITE);
            messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel leaderMsg = new JLabel("Approve or reject suggestions above.");
            leaderMsg.setFont(new Font("Arial", Font.ITALIC, 11));
            leaderMsg.setForeground(DARK_BROWN);
            leaderMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagePanel.add(leaderMsg);
            
            leaderSection.add(messagePanel);
            add(leaderSection);
        }
        
        revalidate();
        repaint();
    }
    
    private void browseDestination() {
        if (currentGroup == null || currentGroup.getDestinationDivision() == null) {
            JOptionPane.showMessageDialog(SuggestTab.this, "No destination division set for this group");
            return;
        }
        
        DivisionExploreFrame browser = new DivisionExploreFrame(
                dataManager.getCurrentUser(),
                currentGroup.getDestinationDivision(),
                true
        );
        
        browser.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Place selected = browser.getSelectedPlace();
                if (selected != null) {
                    boolean alreadySuggested = false;
                    for (PlaceSuggestion s : currentGroup.getAllSuggestions()) {
                        if (s.getPlace().getId().equals(selected.getId())) {
                            alreadySuggested = true;
                            break;
                        }
                    }
                    
                    if (alreadySuggested) {
                        JOptionPane.showMessageDialog(SuggestTab.this,
                                "This place has already been suggested!",
                                "Already Suggested",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    if (selected.getDivision().equals(currentGroup.getDestinationDivision())) {
                        suggestPlaceField.setText(selected.getName());
                        suggestPlaceField.putClientProperty("selectedPlace", selected);
                        JOptionPane.showMessageDialog(SuggestTab.this,
                                "Selected: " + selected.getName() + "\nNow add a reason and click Submit!");
                    } else {
                        JOptionPane.showMessageDialog(SuggestTab.this,
                                "This place is not in " + currentGroup.getDestinationDivision()
                                + " division. Please select a place in the same division.",
                                "Invalid Destination",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        
        browser.setVisible(true);
    }
    
    private void submitSuggestion() {
        Place selectedPlace = (Place) suggestPlaceField.getClientProperty("selectedPlace");
        String reason = suggestReasonField.getText().trim();
        
        if (selectedPlace == null) {
            JOptionPane.showMessageDialog(SuggestTab.this, "Please select a place first");
            return;
        }
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(SuggestTab.this, "Please enter a reason");
            return;
        }
        
        try {
            groupService.suggestPlace(
                    currentGroup.getId(),
                    dataManager.getCurrentUser(),
                    selectedPlace.getId(),
                    reason
            );
            
            JOptionPane.showMessageDialog(SuggestTab.this, "Suggestion submitted!");
            
            suggestPlaceField.setText("");
            suggestReasonField.setText("");
            suggestPlaceField.putClientProperty("selectedPlace", null);
            
            dataManager.refreshData();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(SuggestTab.this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private JPanel createSuggestionForm() {
        JPanel bottomSection = new JPanel();
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
        bottomSection.setBackground(SOFT_WHITE);
        bottomSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                "SUGGEST A PLACE",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, baseSmallSize),
                DARK_BROWN
        ));
        
        JPanel browseRow = new JPanel(new BorderLayout(5, 0));
        browseRow.setBackground(SOFT_WHITE);
        browseRow.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        
        JButton browseBtn = new JButton("Browse");
        browseBtn.setFont(new Font("Arial", Font.BOLD, 10));
        browseBtn.setBackground(DARK_BROWN);
        browseBtn.setForeground(GOLD);
        browseBtn.setFocusPainted(false);
        browseBtn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        browseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseDestination();
            }
        });
        
        suggestPlaceField = new JTextField();
        suggestPlaceField.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
        suggestPlaceField.setEditable(false);
        suggestPlaceField.setBackground(SOFT_WHITE);
        suggestPlaceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        
        browseRow.add(browseBtn, BorderLayout.WEST);
        browseRow.add(suggestPlaceField, BorderLayout.CENTER);
        
        JPanel reasonRow = new JPanel(new BorderLayout(5, 0));
        reasonRow.setBackground(SOFT_WHITE);
        reasonRow.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        
        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 10));
        reasonLabel.setForeground(DARK_BROWN);
        reasonLabel.setPreferredSize(new Dimension(50, 25));
        
        suggestReasonField = new JTextField();
        suggestReasonField.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
        suggestReasonField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        
        suggestSubmitBtn = new JButton("SUBMIT");
        suggestSubmitBtn.setFont(new Font("Arial", Font.BOLD, 10));
        suggestSubmitBtn.setBackground(ORANGE);
        suggestSubmitBtn.setForeground(Color.WHITE);
        suggestSubmitBtn.setFocusPainted(false);
        suggestSubmitBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        suggestSubmitBtn.setPreferredSize(new Dimension(70, 30));
        
        suggestSubmitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitSuggestion();
            }
        });
        
        JPanel reasonInputPanel = new JPanel(new BorderLayout(5, 0));
        reasonInputPanel.setBackground(SOFT_WHITE);
        reasonInputPanel.add(reasonLabel, BorderLayout.WEST);
        reasonInputPanel.add(suggestReasonField, BorderLayout.CENTER);
        
        reasonRow.add(reasonInputPanel, BorderLayout.CENTER);
        reasonRow.add(suggestSubmitBtn, BorderLayout.EAST);
        
        bottomSection.add(browseRow);
        bottomSection.add(reasonRow);
        
        return bottomSection;
    }
}