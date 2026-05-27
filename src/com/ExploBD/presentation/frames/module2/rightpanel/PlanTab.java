package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.Place;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.domain.entities.GroupSystem.Group;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlanTab extends JPanel {

    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private Group currentGroup;

    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;

    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color GOLD = new Color(255, 215, 0);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color ORANGE = new Color(255, 153, 51);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);

    private DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM");
    private int baseSmallSize = 11;
    private int baseNormalSize = 12;

    public PlanTab(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;

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

        if (currentGroup == null) {
            return;
        }

        boolean isLeader = currentGroup.isLeader(dataManager.getCurrentUser());

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(SOFT_WHITE);
        topSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                "DESTINATION",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, baseSmallSize),
                DARK_BROWN
        ));

        JPanel destCard = new JPanel(new BorderLayout(10, 5));
        destCard.setBackground(Color.WHITE);
        destCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        destCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBackground(Color.WHITE);

        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Place destinationPlace = dataManager.getDestinationPlace();

        boolean imageLoaded = false;
        String imagePath = destinationPlace != null ? destinationPlace.getImagePath() : null;

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

         String destName = currentGroup.getDestinationName() != null
                ? currentGroup.getDestinationName() : "No destination set";

        if (!imageLoaded) {
            String firstLetter = destName != null && destName.length() > 0
                    ? destName.substring(0, 1).toUpperCase() : "D";
            photoLabel.setText(firstLetter);
            photoLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 48));
            photoLabel.setForeground(DARK_BROWN);
            photoLabel.setOpaque(true);
        }

        photoPanel.add(photoLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        JLabel nameLabel = new JLabel(destName);
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        nameLabel.setForeground(DARK_BROWN);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));

        String division = currentGroup.getDestinationDivision() != null
                ? currentGroup.getDestinationDivision() : "";
        JLabel locationLabel = new JLabel(division);
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        locationLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(locationLabel);
        infoPanel.add(Box.createVerticalStrut(3));

        if (currentGroup.getBudgetRange() != null) {
            String budgetText = "Budget: " + currentGroup.getBudgetRange().getMinPerPerson()
                    + " - " + currentGroup.getBudgetRange().getMaxPerPerson() + " tk";
            JLabel budgetLabel = new JLabel(budgetText);
            budgetLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            budgetLabel.setForeground(DARK_BROWN);
            infoPanel.add(budgetLabel);
            infoPanel.add(Box.createVerticalStrut(3));
        }

        String datesText = currentGroup.getStartDate().format(shortDateFormatter) + " - "
                + currentGroup.getEndDate().format(shortDateFormatter);
        JLabel datesLabel = new JLabel(datesText);
        datesLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        datesLabel.setForeground(new Color(120, 120, 120));
        infoPanel.add(datesLabel);
        infoPanel.add(Box.createVerticalStrut(3));

        String membersText = "Members: " + currentGroup.getMemberCount() + "/" + currentGroup.getMaxMembers();
        JLabel membersLabel = new JLabel(membersText);
        membersLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        membersLabel.setForeground(new Color(120, 120, 120));
        infoPanel.add(membersLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        rightPanel.setPreferredSize(new Dimension(100, 100));

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setFont(new Font("Arial", Font.BOLD, 10));
        viewDetailsBtn.setBackground(DARK_BROWN);
        viewDetailsBtn.setForeground(GOLD);
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewDetailsBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        viewDetailsBtn.setPreferredSize(new Dimension(90, 25));

        if (destinationPlace != null) {
            viewDetailsBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showPlaceDetails(destinationPlace, (JFrame) SwingUtilities.getWindowAncestor(PlanTab.this));
                }
            });
        } else {
            viewDetailsBtn.setEnabled(false);
        }

        rightPanel.add(viewDetailsBtn);

        destCard.add(photoPanel, BorderLayout.WEST);
        destCard.add(infoPanel, BorderLayout.CENTER);
        destCard.add(rightPanel, BorderLayout.EAST);

        topSection.add(destCard);
        add(topSection);
        add(Box.createVerticalStrut(15));

        JPanel bottomSection = new JPanel();
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
        bottomSection.setBackground(SOFT_WHITE);
        bottomSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                "TRIP PLAN",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, baseSmallSize),
                DARK_BROWN
        ));

        String planText = "No plan yet";
        String fullDescription = currentGroup.getDescription();

        if (fullDescription != null) {
            int planIndex = fullDescription.indexOf("Plan: ");
            if (planIndex >= 0) {
                int privacyIndex = fullDescription.indexOf("Privacy: ");
                if (privacyIndex > planIndex) {
                    planText = fullDescription.substring(planIndex + 6, privacyIndex).trim();
                } else {
                    planText = fullDescription.substring(planIndex + 6).trim();
                }
            } else {
                planText = fullDescription;
            }
        }

        JTextArea planArea = new JTextArea(planText);
        planArea.setEditable(isLeader);
        planArea.setLineWrap(true);
        planArea.setWrapStyleWord(true);
        planArea.setFont(new Font("Bookman Old Style", Font.PLAIN, baseNormalSize));
        planArea.setBackground(isLeader ? Color.WHITE : new Color(250, 250, 250));
        planArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        planArea.setRows(5);

        JScrollPane planScroll = new JScrollPane(planArea);
        planScroll.setBorder(null);
        planScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        planScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        planScroll.setPreferredSize(new Dimension(400, 120));

        bottomSection.add(planScroll);

        if (isLeader) {
            bottomSection.add(Box.createVerticalStrut(8));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setBackground(SOFT_WHITE);

            JButton saveBtn = new JButton("SAVE PLAN");
            saveBtn.setFont(new Font("Arial", Font.BOLD, 11));
            saveBtn.setBackground(ORANGE);
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setFocusPainted(false);
            saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            saveBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String currentDesc = currentGroup.getDescription();
                    String newDesc;

                    if (currentDesc != null && currentDesc.contains("Plan: ")) {
                        int planStart = currentDesc.indexOf("Plan: ");
                        int privacyStart = currentDesc.indexOf("Privacy: ");
                        if (privacyStart > planStart) {
                            newDesc = "Plan: " + planArea.getText() + "\n"
                                    + currentDesc.substring(privacyStart);
                        } else {
                            newDesc = "Plan: " + planArea.getText();
                        }
                    } else {
                        newDesc = "Plan: " + planArea.getText() + "\n"
                                + (currentDesc != null ? currentDesc : "");
                    }

                    currentGroup.setDescription(newDesc);
                    new GroupDatabaseObject().saveGroup(currentGroup);
                    JOptionPane.showMessageDialog(PlanTab.this, "Plan updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dataManager.refreshData();
                }
            });
            buttonPanel.add(saveBtn);

            JButton editBtn = new JButton("EDIT GROUP");
            editBtn.setFont(new Font("Arial", Font.BOLD, 11));
            editBtn.setBackground(DARK_BROWN);
            editBtn.setForeground(GOLD);
            editBtn.setFocusPainted(false);
            editBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showEditGroupDialog((JFrame) SwingUtilities.getWindowAncestor(PlanTab.this));
                }
            });
            buttonPanel.add(editBtn);

            JButton disbandBtn = new JButton("DISBAND");
            disbandBtn.setFont(new Font("Arial", Font.BOLD, 11));
            disbandBtn.setBackground(ERROR_RED);
            disbandBtn.setForeground(Color.WHITE);
            disbandBtn.setFocusPainted(false);
            disbandBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            disbandBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            disbandBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.disbandGroup();
                }
            });
            buttonPanel.add(disbandBtn);

            bottomSection.add(buttonPanel);
        }

        add(bottomSection);

        revalidate();
        repaint();
    }
}
