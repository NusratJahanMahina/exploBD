package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SuggestionCard extends JPanel {

    private PlaceSuggestion suggestion;
    private Group group;
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private boolean isLeader;
    private User currentUser;

    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color GOLD = new Color(255, 215, 0);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color SOFT_WHITE = new Color(255, 250, 245);

    public SuggestionCard(PlaceSuggestion suggestion, boolean isLeader, User currentUser,
            Group group, GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.suggestion = suggestion;
        this.isLeader = isLeader;
        this.currentUser = currentUser;
        this.group = group;
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;

        setLayout(new BorderLayout(10, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        setPreferredSize(new Dimension(360, 160));

        JPanel photoPanel = new JPanel(new BorderLayout());

        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String imagePath = suggestion.getPlace().getImagePath();
        boolean imageLoaded = false;

        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                java.net.URL url = getClass().getResource(imagePath);
                if (url == null) {
                    url = getClass().getResource("/" + imagePath);
                }
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaled));
                    imageLoaded = true;
                }
            } catch (Exception e) {
            }
        }

        if (!imageLoaded) {
            String placeName = suggestion.getPlace().getName();
            String firstLetter = placeName != null && placeName.length() > 0
                    ? placeName.substring(0, 1).toUpperCase() : "P";
            photoLabel.setText(firstLetter);
            photoLabel.setForeground(DARK_BROWN);
            photoLabel.setBackground(SOFT_WHITE);  
            photoLabel.setOpaque(true);
        }

        photoPanel.add(photoLabel, BorderLayout.CENTER);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        JLabel nameLabel = new JLabel(suggestion.getPlace().getName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        nameLabel.setForeground(DARK_BROWN);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));

        JLabel locationLabel = new JLabel(suggestion.getPlace().getDistrict() + ", "
                + suggestion.getPlace().getDivision());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        locationLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(locationLabel);
        infoPanel.add(Box.createVerticalStrut(3));

        JLabel suggesterLabel = new JLabel(suggestion.getSuggester().getDisplayName() + " says:");
        suggesterLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 12));
        suggesterLabel.setForeground(DARK_BROWN);
        infoPanel.add(suggesterLabel);
        infoPanel.add(Box.createVerticalStrut(2));

        String reasonText = suggestion.getReason();
        if (reasonText != null && !reasonText.isEmpty()) {
            if (reasonText.length() > 60) {
                reasonText = reasonText.substring(0, 57) + "...";
            }
            JLabel reasonLabel = new JLabel("\"" + reasonText + "\"");
            reasonLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            reasonLabel.setForeground(DARK_BROWN);
            infoPanel.add(reasonLabel);
            infoPanel.add(Box.createVerticalStrut(3));
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        rightPanel.setPreferredSize(new Dimension(100, 100));

        JLabel statusBadge = new JLabel();
        statusBadge.setFont(new Font("Arial", Font.BOLD, 10));
        statusBadge.setOpaque(true);
        statusBadge.setHorizontalAlignment(SwingConstants.CENTER);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        statusBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (suggestion.getStatus() == InvitationStatus.PENDING) {
            statusBadge.setText("PENDING");
            statusBadge.setForeground(Color.WHITE);
            statusBadge.setBackground(ORANGE);
        } else if (suggestion.getStatus() == InvitationStatus.APPROVED) {
            statusBadge.setText("APPROVED");
            statusBadge.setForeground(Color.WHITE);
            statusBadge.setBackground(SUCCESS_GREEN);
        } else if (suggestion.getStatus() == InvitationStatus.DECLINED) {
            statusBadge.setText("REJECTED");
            statusBadge.setForeground(Color.WHITE);
            statusBadge.setBackground(ERROR_RED);
        }

        rightPanel.add(statusBadge);
        rightPanel.add(Box.createVerticalStrut(8));

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setFont(new Font("Arial", Font.BOLD, 10));
        viewDetailsBtn.setBackground(DARK_BROWN);
        viewDetailsBtn.setForeground(GOLD);
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewDetailsBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        viewDetailsBtn.setPreferredSize(new Dimension(90, 25));
        viewDetailsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataManager.showPlaceDetails(suggestion.getPlace(), (JFrame) SwingUtilities.getWindowAncestor(SuggestionCard.this));
            }
        });
        rightPanel.add(viewDetailsBtn);

        if (!isLeader && suggestion.getStatus() == InvitationStatus.PENDING
                && suggestion.getSuggester().getUserId().equals(currentUser.getUserId())) {
            rightPanel.add(Box.createVerticalStrut(5));
            JButton deleteBtn = new JButton("Cancel");
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 9));
            deleteBtn.setBackground(ERROR_RED);
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            deleteBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(SuggestionCard.this,
                            "Cancel your suggestion?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dataManager.cancelSuggestion(suggestion.getSuggestionId());
                    }
                }
            });
            rightPanel.add(deleteBtn);
        }

        add(photoPanel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        if (isLeader && suggestion.getStatus() == InvitationStatus.PENDING) {
            JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            bottomBtnPanel.setBackground(Color.WHITE);
            bottomBtnPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

            JButton approveBtn = new JButton("Approve");
            approveBtn.setFont(new Font("Arial", Font.BOLD, 11));
            approveBtn.setBackground(SUCCESS_GREEN);
            approveBtn.setForeground(Color.WHITE);
            approveBtn.setFocusPainted(false);
            approveBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
            approveBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.approveSuggestion(suggestion.getSuggestionId());
                }
            });

            JButton rejectBtn = new JButton("Reject");
            rejectBtn.setFont(new Font("Arial", Font.BOLD, 11));
            rejectBtn.setBackground(ERROR_RED);
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.setFocusPainted(false);
            rejectBtn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
            rejectBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.rejectSuggestion(suggestion.getSuggestionId());
                }
            });

            bottomBtnPanel.add(approveBtn);
            bottomBtnPanel.add(rejectBtn);

            add(bottomBtnPanel, BorderLayout.SOUTH);
        }
    }
}
