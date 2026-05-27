package com.ExploBD.presentation.frames.module2.leftpanel;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GroupCard extends JPanel {
    
    private Group group;
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private boolean isSelected;
    
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color CREAM = new Color(250, 245, 240);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color ACTIVE_GREEN = new Color(46, 125, 50);
    private Color ACTIVE_LIGHT_GREEN = new Color(235, 255, 235);
    
    private DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM");
    
    public GroupCard(Group group, GroupDataManager dataManager, ProfilePhotoUtils photoUtils, boolean isSelected) {
        this.group = group;
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        this.isSelected = isSelected;
        
        User currentUser = dataManager.getCurrentUser();
        boolean isPastTrip = group.getEndDate() != null && group.getEndDate().isBefore(LocalDate.now());
        boolean isActive = group.getStatus() == GroupStatus.PLANNING || group.getStatus() == GroupStatus.VOTING;
        boolean isCurrentActive = group.equals(dataManager.getCurrentActiveGroup());
        
        Color bgColor = SOFT_WHITE;
        Color hoverColor = SOFT_WHITE;
        
        if (isPastTrip) {
            bgColor = new Color(245, 245, 245);
            hoverColor = new Color(235, 235, 235);
        } else if (isCurrentActive) {
            bgColor = ACTIVE_LIGHT_GREEN;
            hoverColor = new Color(250, 245, 240);
        } else if (isSelected) {
            bgColor = CREAM;
            hoverColor = new Color(250, 245, 240);
        } else {
            bgColor = SOFT_WHITE;
            hoverColor = new Color(250, 245, 240);
        }
        
        final Color finalBgColor = bgColor;
        final Color finalHoverColor = hoverColor;
        final boolean finalIsPastTrip = isPastTrip;
        final boolean finalIsSelected = isSelected;
        
        setLayout(new BorderLayout());
        setBackground(finalBgColor);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isSelected ? ORANGE : new Color(200, 180, 160),
                        isSelected ? 2 : 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setPreferredSize(new Dimension(240, 100));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topRow.setBackground(finalBgColor);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 5, 3, 5));
        
        if (isPastTrip) {
            JLabel pastLabel = new JLabel("COMPLETED");
            pastLabel.setFont(new Font("Arial", Font.BOLD, 9));
            pastLabel.setForeground(Color.WHITE);
            pastLabel.setBackground(Color.GRAY);
            pastLabel.setOpaque(true);
            pastLabel.setHorizontalAlignment(SwingConstants.CENTER);
            pastLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            topRow.add(pastLabel);
        } else if (isCurrentActive) {
            JLabel currentLabel = new JLabel("CURRENT");
            currentLabel.setFont(new Font("Arial", Font.BOLD, 9));
            currentLabel.setForeground(Color.WHITE);
            currentLabel.setBackground(ACTIVE_GREEN);
            currentLabel.setOpaque(true);
            currentLabel.setHorizontalAlignment(SwingConstants.CENTER);
            currentLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            topRow.add(currentLabel);
        } else if (isActive) {
            JLabel futureLabel = new JLabel("In FUTURE");
            futureLabel.setFont(new Font("Arial", Font.BOLD, 9));
            futureLabel.setForeground(Color.WHITE);
            futureLabel.setBackground(new Color(100, 100, 100));
            futureLabel.setOpaque(true);
            futureLabel.setHorizontalAlignment(SwingConstants.CENTER);
            futureLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            topRow.add(futureLabel);
        }
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(finalBgColor);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        String role = group.isLeader(currentUser) ? " (Leader)" : "";
        JLabel nameLabel = new JLabel(group.getName() + role);
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 13));
        nameLabel.setForeground(isPastTrip ? Color.GRAY : DARK_BROWN);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        
        String destText = group.getDestinationName() != null ? group.getDestinationName() : "No destination";
        JLabel destLabel = new JLabel(destText);
        destLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        destLabel.setForeground(isPastTrip ? Color.LIGHT_GRAY : new Color(100, 100, 100));
        destLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(destLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        
        JPanel detailsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        detailsRow.setBackground(finalBgColor);
        detailsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String dateText = group.getStartDate() != null
                ? group.getStartDate().format(shortDateFormatter) + " - " + group.getEndDate().format(shortDateFormatter)
                : "Dates TBD";
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        dateLabel.setForeground(isPastTrip ? Color.LIGHT_GRAY : ORANGE);
        detailsRow.add(dateLabel);
        
        JLabel memberCountLabel = new JLabel(group.getMemberCount() + "/" + group.getMaxMembers());
        memberCountLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        memberCountLabel.setForeground(isPastTrip ? Color.LIGHT_GRAY : new Color(120, 120, 120));
        detailsRow.add(memberCountLabel);
        
        infoPanel.add(detailsRow);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(finalBgColor);
        contentPanel.add(topRow, BorderLayout.NORTH);
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dataManager.setSelectedGroup(group);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!finalIsSelected) {
                    setBackground(finalHoverColor);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!finalIsSelected) {
                    setBackground(finalBgColor);
                }
            }
        });
    }
}