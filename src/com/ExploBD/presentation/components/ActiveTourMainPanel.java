package com.ExploBD.presentation.components;

import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.presentation.frames.module2.MyGroupsFrame;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ActiveTourMainPanel extends JPanel {
    
    private User currentUser;
    private Group currentGroup;
    private PlaceDatabaseObject placeDAO;
    
    // Colors - WARM BROWNISH/BEIGE THEME
    private Color BROWN_DARK = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color GOLD = new Color(205, 133, 63);  // Darker gold for better contrast
    private Color BG_MAIN = new Color(245, 235, 225);        // Warm beige background (similar to 255,245,238 but warmer)
    private Color BG_LIGHTER = new Color(252, 245, 240);     // Slightly lighter for panels
    private Color BORDER_COLOR = new Color(210, 190, 170);   // Soft brown border
    private Color TEXT_LIGHT = new Color(70, 58, 47);        // Dark brown text (for light bg)
    private Color TEXT_DARK = new Color(50, 40, 30);         // Darker text color
    private Color ACCENT_BG = new Color(240, 225, 210);      // Accent background for panels
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
    
    public ActiveTourMainPanel(User user) {
        this.currentUser = user;
        this.placeDAO = new PlaceDatabaseObject();
        
        setPreferredSize(new Dimension(650, 220));
        setMinimumSize(new Dimension(650, 220));
        setMaximumSize(new Dimension(650, 220));
        
        setLayout(new GridBagLayout());
        setBackground(BG_MAIN);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        loadData();
    }
    
    private void loadData() {
        removeAll();
        currentGroup = findActiveGroup();
        
        if (currentGroup == null) {
            showNoTour();
            return;
        }
        
        // LEFT: Image Panel (300x220 - FIXED SIZE)
        JPanel imagePanel = createImagePanel();
        
        // RIGHT: Info Panel (same height)
        JPanel infoPanel = createInfoPanel();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        // Image panel - FIXED WIDTH, DON'T STRETCH
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(imagePanel, gbc);
        
        // Info panel - TAKES REMAINING SPACE
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        add(infoPanel, gbc);
        
        revalidate();
        repaint();
    }
    
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ACCENT_BG);
        panel.setPreferredSize(new Dimension(300, 220));
        panel.setMinimumSize(new Dimension(300, 220));
        panel.setMaximumSize(new Dimension(300, 220));
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Try to load image
        boolean imageLoaded = loadImage(imageLabel);
        
        if (!imageLoaded) {
            imageLoaded = loadPlaceImage(imageLabel);
        }
        
        if (!imageLoaded) {
            // Show placeholder with destination initial
            String dest = currentGroup.getDestinationName();
            String letter = (dest != null && !dest.isEmpty()) ? dest.substring(0, 1) : "📍";
            imageLabel.setText(letter);
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
            imageLabel.setForeground(ORANGE);
            imageLabel.setOpaque(true);
            imageLabel.setBackground(ACCENT_BG);
        }
        
        panel.add(imageLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private boolean loadImage(JLabel label) {
        String imagePath = currentGroup.getDestinationImagePath();
        if (imagePath == null || imagePath.isEmpty()) return false;
        return tryLoadImage(label, imagePath);
    }
    
    private boolean loadPlaceImage(JLabel label) {
        String destName = currentGroup.getDestinationName();
        String division = currentGroup.getDestinationDivision();
        
        if (destName == null || destName.isEmpty() || division == null || division.isEmpty()) {
            return false;
        }
        
        List<Place> places = placeDAO.findAllByDivision(division);
        for (Place place : places) {
            if (place.getName().equalsIgnoreCase(destName)) {
                String imagePath = place.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    return tryLoadImage(label, imagePath);
                }
            }
        }
        return false;
    }
    
    private boolean tryLoadImage(JLabel label, String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) url = getClass().getResource("/" + path);
            if (url == null) url = getClass().getResource("/images/" + path);
            
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                if (icon.getIconWidth() > 0) {
                    // Scale to FILL 300x220 exactly (may crop)
                    Image scaled = icon.getImage().getScaledInstance(300, 220, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaled));
                    return true;
                }
            }
        } catch (Exception e) {}
        return false;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // ===== GROUP NAME =====
        String groupName = currentGroup.getName();
        if (groupName.length() > 35) groupName = groupName.substring(0, 32) + "...";
        JLabel nameLabel = new JLabel(groupName);
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 22));
        nameLabel.setForeground(BROWN_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Subtle underline
        JSeparator separator = new JSeparator();
        separator.setForeground(GOLD);
        separator.setPreferredSize(new Dimension(50, 2));
        separator.setMaximumSize(new Dimension(50, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // ===== DESTINATION =====
        String destText = currentGroup.getDestinationName();
        if (destText == null) destText = "Destination not set";
        if (destText.length() > 40) destText = destText.substring(0, 37) + "...";
        
        JPanel destPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        destPanel.setBackground(BG_MAIN);
        destPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel locationIcon = new JLabel("📍");
        locationIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        locationIcon.setForeground(ORANGE);
        
        JLabel destLabel = new JLabel(destText);
        destLabel.setFont(new Font("Arial", Font.BOLD, 18));
        destLabel.setForeground(TEXT_LIGHT);
        
        destPanel.add(locationIcon);
        destPanel.add(destLabel);
        
        // ===== DATES =====
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setBackground(BG_MAIN);
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String dateText = getDateText();
        JLabel calendarIcon = new JLabel("📅");
        calendarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        calendarIcon.setForeground(TEXT_LIGHT);
        
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        dateLabel.setForeground(TEXT_LIGHT);
        
        datePanel.add(calendarIcon);
        datePanel.add(dateLabel);
        
        // ===== DAYS/STATUS =====
        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        daysPanel.setBackground(BG_MAIN);
        daysPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String dayText = getDayText();
        Color dayColor = getDayColor();
        
        JLabel daysIcon = new JLabel(getDayIcon());
        daysIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        daysIcon.setForeground(dayColor);
        
        JLabel daysLabel = new JLabel(dayText);
        daysLabel.setFont(new Font("Arial", Font.BOLD, 15));
        daysLabel.setForeground(dayColor);
        
        daysPanel.add(daysIcon);
        daysPanel.add(daysLabel);
        
        // ===== MEMBERS =====
        JPanel membersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        membersPanel.setBackground(BG_MAIN);
        membersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel membersIcon = new JLabel("👥");
        membersIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        membersIcon.setForeground(TEXT_LIGHT);
        
        JLabel membersLabel = new JLabel(currentGroup.getMemberCount() + " members");
        membersLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        membersLabel.setForeground(TEXT_LIGHT);
        
        membersPanel.add(membersIcon);
        membersPanel.add(membersLabel);
        
        // Add all components
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(10));
        panel.add(destPanel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(datePanel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(daysPanel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(membersPanel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private String getDayText() {
        LocalDate today = LocalDate.now();
        
        if (currentGroup.getStartDate() != null && currentGroup.getEndDate() != null) {
            if (!today.isBefore(currentGroup.getStartDate()) && !today.isAfter(currentGroup.getEndDate())) {
                long currentDay = ChronoUnit.DAYS.between(currentGroup.getStartDate(), today) + 1;
                long totalDays = ChronoUnit.DAYS.between(currentGroup.getStartDate(), currentGroup.getEndDate()) + 1;
                return "Day " + currentDay + " of " + totalDays;
            }
            if (today.isBefore(currentGroup.getStartDate())) {
                long daysLeft = ChronoUnit.DAYS.between(today, currentGroup.getStartDate());
                if (daysLeft == 0) return "Starts today!";
                if (daysLeft == 1) return "Starts tomorrow!";
                return daysLeft + " days to go";
            }
        }
        return "Planning stage";
    }
    
    private String getDayIcon() {
        LocalDate today = LocalDate.now();
        
        if (currentGroup.getStartDate() != null && currentGroup.getEndDate() != null) {
            if (!today.isBefore(currentGroup.getStartDate()) && !today.isAfter(currentGroup.getEndDate())) {
                return "🗓️";
            }
            if (today.isBefore(currentGroup.getStartDate())) {
                return "⏳";
            }
        }
        return "📌";
    }
    
    private Color getDayColor() {
        LocalDate today = LocalDate.now();
        
        if (currentGroup.getStartDate() != null && currentGroup.getEndDate() != null) {
            if (!today.isBefore(currentGroup.getStartDate()) && !today.isAfter(currentGroup.getEndDate())) {
                return new Color(60, 120, 60); // Darker green for better contrast
            }
            if (today.isBefore(currentGroup.getStartDate())) {
                return new Color(30, 100, 180); // Darker blue for better contrast
            }
        }
        return new Color(180, 120, 30); // Darker gold/orange for planning
    }
    
    private void showNoTour() {
        setLayout(new BorderLayout());
        
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(BG_MAIN);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel iconLabel = new JLabel("🗺️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("No Active Tour");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 22));
        titleLabel.setForeground(BROWN_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subLabel = new JLabel("Create or join a group to get started");
        subLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subLabel.setForeground(TEXT_LIGHT);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        messagePanel.add(iconLabel);
        messagePanel.add(Box.createVerticalStrut(15));
        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(8));
        messagePanel.add(subLabel);
        
        add(messagePanel, BorderLayout.CENTER);
    }
    
    private Group findActiveGroup() {
        if (currentUser == null) return null;
        
        GroupDatabaseObject groupDAO = new GroupDatabaseObject();
        List<Group> myGroups = groupDAO.findByUser(currentUser.getUserId());
        
        if (myGroups == null || myGroups.isEmpty()) return null;
        
        LocalDate today = LocalDate.now();
        
        // Priority 1: Ongoing confirmed trip (happening right now)
        for (Group g : myGroups) {
            if (g.getStatus() == GroupStatus.CONFIRMED && g.getStartDate() != null && g.getEndDate() != null) {
                if (!today.isBefore(g.getStartDate()) && !today.isAfter(g.getEndDate())) {
                    return g;
                }
            }
        }
        
        // Priority 2: Most recent confirmed trip (by start date)
        Group mostRecentConfirmed = null;
        LocalDate latestDate = null;
        
        for (Group g : myGroups) {
            if (g.getStatus() == GroupStatus.CONFIRMED && g.getStartDate() != null) {
                if (latestDate == null || g.getStartDate().isAfter(latestDate)) {
                    latestDate = g.getStartDate();
                    mostRecentConfirmed = g;
                }
            }
        }
        
        return mostRecentConfirmed;
    }
    
    private String getDateText() {
        if (currentGroup.getStartDate() == null) return "Dates not set";
        return currentGroup.getStartDate().format(dateFormatter) + " — " + currentGroup.getEndDate().format(dateFormatter);
    }
    
    public void refresh() {
        loadData();
    }
}