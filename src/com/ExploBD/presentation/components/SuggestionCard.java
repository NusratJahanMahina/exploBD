package com.ExploBD.presentation.components;

import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.object.User;
import com.ExploBD.domain.enums.InvitationStatus;
import javax.swing.*;
import java.awt.*;

public class SuggestionCard extends BaseCard<PlaceSuggestion> {
    
    private User currentUser;
    private boolean isLeader;
    private SuggestionActionListener listener;
    
    private Color VOTE_GREEN = new Color(50, 205, 50);
    private Color VOTE_RED = new Color(220, 20, 60);
    
    public interface SuggestionActionListener {
        void onApproveByLeader(PlaceSuggestion suggestion);
        void onRejectByLeader(PlaceSuggestion suggestion);
        void onVote(PlaceSuggestion suggestion, boolean approve);
        void onViewDetails(PlaceSuggestion suggestion);
    }
    
    public SuggestionCard(PlaceSuggestion suggestion, User currentUser, 
                          boolean isLeader, SuggestionActionListener listener) {
        super(suggestion);
        this.currentUser = currentUser;
        this.isLeader = isLeader;
        this.listener = listener;
        setPreferredSize(new Dimension(500, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    }
    
    @Override
    protected JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(50, 50));
        
        
        String placeName = data.getPlace().getName();
        String initial = placeName != null && placeName.length() > 0 ? 
                        placeName.substring(0, 1).toUpperCase() : "📍";
        
        JLabel iconLabel = new JLabel(initial);
        iconLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 24));
        iconLabel.setForeground(DARK_BROWN);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(LIGHT_CREAM);
        iconLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140)));
        
        panel.add(iconLabel, BorderLayout.CENTER);
        return panel;
    }
    
    @Override
    protected JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 5));
        
        JLabel nameLabel = new JLabel(data.getPlace().getName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BROWN);
        
        JLabel locationLabel = new JLabel(data.getPlace().getDistrict() + ", " + data.getPlace().getDivision());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        locationLabel.setForeground(Color.GRAY);
        
        JLabel suggestedByLabel = new JLabel("by " + data.getSuggester().getDisplayName());
        suggestedByLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        suggestedByLabel.setForeground(new Color(150, 150, 150));
        
        String reason = data.getReason();
        if (reason != null && reason.length() > 30) {
            reason = reason.substring(0, 27) + "...";
        }
        if (reason != null && !reason.isEmpty()) {
            JLabel reasonLabel = new JLabel("📝 " + reason);
            reasonLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            reasonLabel.setForeground(new Color(100, 100, 100));
            panel.add(reasonLabel);
        }
        
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(locationLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(suggestedByLabel);
        
        return panel;
    }
    
    @Override
    protected JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(150, 80));
        
        JPanel votePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        votePanel.setOpaque(false);
        
        JLabel approveLabel = new JLabel("✓ " + data.getApproveCount());
        approveLabel.setForeground(VOTE_GREEN);
        approveLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel rejectLabel = new JLabel("✗ " + data.getRejectCount());
        rejectLabel.setForeground(VOTE_RED);
        rejectLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        votePanel.add(approveLabel);
        votePanel.add(new JLabel("|"));
        votePanel.add(rejectLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        buttonPanel.setOpaque(false);
        
        if (isLeader && data.getStatus() == InvitationStatus.PENDING) {
            JButton approveBtn = createSmallButton("✓", VOTE_GREEN, 25);
            JButton rejectBtn = createSmallButton("✗", VOTE_RED, 25);
            
            approveBtn.setToolTipText("Approve for voting");
            rejectBtn.setToolTipText("Reject suggestion");
            
            approveBtn.addActionListener(e -> listener.onApproveByLeader(data));
            rejectBtn.addActionListener(e -> listener.onRejectByLeader(data));
            
            buttonPanel.add(approveBtn);
            buttonPanel.add(rejectBtn);
        }
        else if (data.getStatus() == InvitationStatus.ACCEPTED && !data.hasVoted(currentUser)) {
            JButton voteYesBtn = createSmallButton("✓", VOTE_GREEN, 25);
            JButton voteNoBtn = createSmallButton("✗", VOTE_RED, 25);
            
            voteYesBtn.setToolTipText("Vote Yes");
            voteNoBtn.setToolTipText("Vote No");
            
            voteYesBtn.addActionListener(e -> listener.onVote(data, true));
            voteNoBtn.addActionListener(e -> listener.onVote(data, false));
            
            buttonPanel.add(voteYesBtn);
            buttonPanel.add(voteNoBtn);
        }
        
        JButton viewBtn = createSmallButton("👁", DARK_BROWN, 25);
        viewBtn.setToolTipText("View Details");
        viewBtn.addActionListener(e -> listener.onViewDetails(data));
        buttonPanel.add(viewBtn);
        
        panel.add(votePanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}