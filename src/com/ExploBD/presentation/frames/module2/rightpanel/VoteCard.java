package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VoteCard extends JPanel {
    
    private PlaceSuggestion suggestion;
    private Group group;
    private User currentUser;
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private boolean isLeader;
    private boolean userVoted;
    private String votedSuggestionId;
    
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color GOLD = new Color(255, 215, 0);
    
    public VoteCard(PlaceSuggestion suggestion, boolean isLeader, boolean userVoted, 
                    String votedSuggestionId, Group group, User currentUser, 
                    GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.suggestion = suggestion;
        this.isLeader = isLeader;
        this.userVoted = userVoted;
        this.votedSuggestionId = votedSuggestionId;
        this.group = group;
        this.currentUser = currentUser;
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel nameLabel = new JLabel(suggestion.getPlace().getName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        nameLabel.setForeground(DARK_BROWN);
        
        JLabel locationLabel = new JLabel(suggestion.getPlace().getDistrict() + ", " + suggestion.getPlace().getDivision());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        locationLabel.setForeground(Color.GRAY);
        
        JLabel suggesterLabel = new JLabel("suggested by " + suggestion.getSuggester().getDisplayName());
        suggesterLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        suggesterLabel.setForeground(new Color(120, 120, 120));
        
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        namePanel.setBackground(Color.WHITE);
        namePanel.add(nameLabel);
        namePanel.add(suggesterLabel);
        
        topRow.add(namePanel, BorderLayout.WEST);
        topRow.add(locationLabel, BorderLayout.EAST);
        
        add(topRow);
        add(Box.createVerticalStrut(8));
        
        JPanel middleRow = new JPanel(new BorderLayout(10, 0));
        middleRow.setBackground(Color.WHITE);
        middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        int totalMembers = group.getMemberCount();
        int yesVotes = suggestion.getApproveCount();
        int percentage = totalMembers > 0 ? (yesVotes * 100) / totalMembers : 0;
        
        JLabel percentageLabel = new JLabel(yesVotes + "/" + totalMembers + " votes (" + percentage + "%)");
        percentageLabel.setFont(new Font("Arial", Font.BOLD, 12));
        percentageLabel.setForeground(DARK_BROWN);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(percentage);
        progressBar.setString(percentage + "%");
        progressBar.setStringPainted(true);
        progressBar.setForeground(SUCCESS_GREEN);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setFont(new Font("Arial", Font.BOLD, 9));
        progressBar.setPreferredSize(new Dimension(150, 18));
        
        middleRow.add(percentageLabel, BorderLayout.WEST);
        middleRow.add(progressBar, BorderLayout.CENTER);
        
        add(middleRow);
        add(Box.createVerticalStrut(8));
        
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        boolean votedForThis = userVoted && votedSuggestionId != null && votedSuggestionId.equals(suggestion.getSuggestionId());
        
        if (!isLeader) {
            if (!votedForThis && !userVoted) {
                JButton voteBtn = new JButton("VOTE");
                voteBtn.setFont(new Font("Arial", Font.BOLD, 11));
                voteBtn.setBackground(SUCCESS_GREEN);
                voteBtn.setForeground(Color.WHITE);
                voteBtn.setFocusPainted(false);
                voteBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                voteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                voteBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dataManager.castVote(suggestion.getSuggestionId(), true);
                    }
                });
                bottomRow.add(voteBtn);
                
            } else if (votedForThis) {
                JPanel votedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                votedPanel.setBackground(Color.WHITE);
                
                JLabel votedLabel = new JLabel("YOU VOTED");
                votedLabel.setFont(new Font("Arial", Font.BOLD, 11));
                votedLabel.setForeground(SUCCESS_GREEN);
                votedLabel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_GREEN, 1),
                        BorderFactory.createEmptyBorder(3, 8, 3, 8)
                ));
                votedPanel.add(votedLabel);
                
                JButton removeBtn = new JButton("REMOVE");
                removeBtn.setFont(new Font("Arial", Font.BOLD, 9));
                removeBtn.setBackground(ERROR_RED);
                removeBtn.setForeground(Color.WHITE);
                removeBtn.setFocusPainted(false);
                removeBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                removeBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dataManager.removeVote(suggestion.getSuggestionId());
                    }
                });
                votedPanel.add(removeBtn);
                
                bottomRow.add(votedPanel);
            }
        } else {
            JLabel leaderMsg = new JLabel("Leader observes voting");
            leaderMsg.setFont(new Font("Arial", Font.ITALIC, 10));
            leaderMsg.setForeground(Color.GRAY);
            leaderMsg.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            bottomRow.add(leaderMsg);
        }
        
        JButton viewBtn = new JButton("Details");
        viewBtn.setFont(new Font("Arial", Font.BOLD, 10));
        viewBtn.setBackground(DARK_BROWN);
        viewBtn.setForeground(GOLD);
        viewBtn.setFocusPainted(false);
        viewBtn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataManager.showPlaceDetails(suggestion.getPlace(), (JFrame) SwingUtilities.getWindowAncestor(VoteCard.this));
            }
        });
        bottomRow.add(viewBtn);
        
        add(bottomRow);
    }
}