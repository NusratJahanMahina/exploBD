package com.ExploBD.presentation.frames.module2.leftpanel;

import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvitationCard extends JPanel {
    
    private Invitation invitation;
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color EXPIRED_GRAY = new Color(200, 200, 200);
    private Color LIGHT_RED = new Color(255, 235, 230);
    
    private DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM");
    
    public InvitationCard(Invitation invitation, GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.invitation = invitation;
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        
        boolean isExpired = dataManager.isInvitationExpired(invitation);
        Color bgColor = isExpired ? EXPIRED_GRAY : LIGHT_RED;
        
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isExpired ? Color.GRAY : new Color(255, 180, 160), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        setPreferredSize(new Dimension(240, 140));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(bgColor);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        JLabel groupLabel = new JLabel(invitation.getGroup().getName());
        groupLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
        groupLabel.setForeground(isExpired ? Color.GRAY : DARK_BROWN);
        groupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(groupLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        
        String destText = "";
        if (invitation.getGroup().getDestinationName() != null) {
            destText += invitation.getGroup().getDestinationName();
            if (invitation.getGroup().getDestinationDivision() != null) {
                destText += " - " + invitation.getGroup().getDestinationDivision();
            }
        } else {
            destText += "Destination not set";
        }
        JLabel destLabel = new JLabel(destText);
        destLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        destLabel.setForeground(isExpired ? Color.LIGHT_GRAY : new Color(100, 100, 100));
        destLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(destLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        
        JLabel fromLabel = new JLabel("From: " + invitation.getInviter().getDisplayName());
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        fromLabel.setForeground(isExpired ? Color.LIGHT_GRAY : Color.GRAY);
        fromLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(fromLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        
        String dateText = "Starts: ";
        if (invitation.getGroup().getStartDate() != null) {
            dateText += invitation.getGroup().getStartDate().format(shortDateFormatter);
        } else {
            dateText += "TBD";
        }
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(isExpired ? Color.LIGHT_GRAY : ORANGE);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(dateLabel);
        
        add(infoPanel, BorderLayout.CENTER);
        
        if (!isExpired) {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            btnPanel.setBackground(bgColor);
            btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            
            JButton acceptBtn = new JButton("Accept");
            acceptBtn.setFont(new Font("Arial", Font.BOLD, 11));
            acceptBtn.setBackground(SUCCESS_GREEN);
            acceptBtn.setForeground(Color.WHITE);
            acceptBtn.setFocusPainted(false);
            acceptBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            acceptBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.acceptInvitation(invitation);
                }
            });
            
            JButton declineBtn = new JButton("Decline");
            declineBtn.setFont(new Font("Arial", Font.BOLD, 11));
            declineBtn.setBackground(ERROR_RED);
            declineBtn.setForeground(Color.WHITE);
            declineBtn.setFocusPainted(false);
            declineBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            declineBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.declineInvitation(invitation);
                }
            });
            
            btnPanel.add(acceptBtn);
            btnPanel.add(declineBtn);
            add(btnPanel, BorderLayout.SOUTH);
        } else {
            JPanel expiredPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            expiredPanel.setBackground(bgColor);
            expiredPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            
            JLabel expiredLabel = new JLabel("Expired");
            expiredLabel.setFont(new Font("Arial", Font.BOLD, 12));
            expiredLabel.setForeground(Color.GRAY);
            expiredPanel.add(expiredLabel);
            add(expiredPanel, BorderLayout.SOUTH);
        }
    }
}