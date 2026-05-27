package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class GroupHeaderPanel extends JPanel {
    
    private GroupDataManager dataManager;
    private Group currentGroup;
    
    private JLabel nameLabel;
    private JLabel roleLabel;
    private JLabel destinationLabel;
    private JLabel datesLabel;
    private JLabel membersLabel;
    private JLabel statusLabel;
    
    private Color CREAM = new Color(250, 245, 240);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    
    private DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM");
    
    public GroupHeaderPanel(GroupDataManager dataManager) {
        this.dataManager = dataManager;
        
        setLayout(new BorderLayout(10, 0));
        setBackground(CREAM);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(CREAM);
        
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 22));
        nameLabel.setForeground(DARK_BROWN);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(8));
        
        destinationLabel = new JLabel();
        destinationLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        destinationLabel.setForeground(DARK_BROWN);
        destinationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(destinationLabel);
        leftPanel.add(Box.createVerticalStrut(12));
        
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoRow.setBackground(CREAM);
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        datesLabel = new JLabel();
        datesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoRow.add(datesLabel);
        
        membersLabel = new JLabel();
        membersLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoRow.add(membersLabel);
        
        leftPanel.add(infoRow);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(CREAM);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        rightPanel.setPreferredSize(new Dimension(140, 70));
        
        roleLabel = new JLabel();
        roleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setMaximumSize(new Dimension(120, 25));
        roleLabel.setPreferredSize(new Dimension(120, 25));
        rightPanel.add(roleLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setMaximumSize(new Dimension(120, 25));
        statusLabel.setPreferredSize(new Dimension(120, 25));
        rightPanel.add(statusLabel);
        
        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    public void setGroup(Group group) {
        this.currentGroup = group;
        refresh();
    }
    
    public void refresh() {
        if (currentGroup == null) return;
        
        User currentUser = dataManager.getCurrentUser();
        
        nameLabel.setText(currentGroup.getName());
        
        String destStr = currentGroup.getDestinationName() != null
                ? currentGroup.getDestinationName() : "No destination set";
        destinationLabel.setText("Destination: " + destStr);
        
        String dateStr = (currentGroup.getStartDate() != null)
                ? currentGroup.getStartDate().format(shortDateFormatter) + " - "
                + currentGroup.getEndDate().format(shortDateFormatter)
                : "Dates: TBD";
        datesLabel.setText(dateStr);
        
        membersLabel.setText("Members: " + currentGroup.getMemberCount() + "/" + currentGroup.getMaxMembers());
        
        boolean isLeader = currentGroup.isLeader(currentUser);
        
        roleLabel.setText(isLeader ? "LEADER" : "MEMBER");
        roleLabel.setForeground(isLeader ? ORANGE : Color.GRAY);
        roleLabel.setBackground(isLeader ? new Color(255, 240, 220) : new Color(240, 240, 240));
        roleLabel.setOpaque(true);
        roleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isLeader ? ORANGE : Color.GRAY, 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        
        String statusText = currentGroup.getStatus().toString();
        if (currentGroup.getStatus() == GroupStatus.VOTING && !currentGroup.isVotingActive()) {
            statusText = "VOTING ENDED";
        }
        statusLabel.setText(statusText);
        
        Color statusColor = getStatusColor(currentGroup.getStatus());
        statusLabel.setForeground(statusColor);
        statusLabel.setBackground(new Color(245, 245, 245));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(statusColor, 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
    }
    
    private Color getStatusColor(GroupStatus status) {
        switch (status) {
            case PLANNING: return DARK_BROWN;
            case VOTING: return ORANGE;
            case CONFIRMED: return SUCCESS_GREEN;
            default: return Color.GRAY;
        }
    }
}