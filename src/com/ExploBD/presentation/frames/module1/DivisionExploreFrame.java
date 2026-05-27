package com.ExploBD.presentation.frames.module1;

import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.*;
import com.ExploBD.presentation.components.PlaceInfoDialog;
import com.ExploBD.presentation.frames.AppConfig;
import com.ExploBD.Frame.RoundedPanel;
import com.ExploBD.presentation.frames.module2.CreateGroupFrame;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class DivisionExploreFrame extends javax.swing.JFrame {

    private User currentUser;
    private String divisionName;
    private boolean isSelectionMode = false;
    private Place selectedPlace = null;
    private PlaceDatabaseObject placeDAO;

    private JPanel mainPanel;
    private JPanel placesContainer;
    private JScrollPane scrollPane;
    private JButton backButton;
    private JLabel titleLabel;
    private JLabel countLabel;

    private int baseFontSizeTitle = 22;
    private int baseFontSizePlaceName = 20;
    private int baseFontSizeNormal = 12;
    private int baseFontSizeSmall = 11;

    private int baseImageWidth = 180;
    private int baseImageHeight = 130;
    private int baseBoxHeight = 210;

    private int currentWindowWidth = 800;
    private int currentWindowHeight = 600;

    private boolean isResizing = false;
    private Timer resizeTimer;

    public DivisionExploreFrame(User user, String divisionName, boolean isSelectionMode) {
        this.currentUser = user;
        this.divisionName = divisionName;
        this.isSelectionMode = isSelectionMode;
        this.placeDAO = new PlaceDatabaseObject();
        initComponents();
        loadPlaces();
        setLocationRelativeTo(null);
        AppConfig.applyAndTrack(this);
        resizeTimer = new Timer(150, e -> {
            performDelayedResize();
        });
        resizeTimer.setRepeats(false);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                handleResize();
            }
        });
    }

    private void handleResize() {
        if (!isResizing) {
            isResizing = true;
            currentWindowWidth = getWidth();
            currentWindowHeight = getHeight();

            updateFontSizes();
            resizeTimer.restart();
        }
    }

    private void performDelayedResize() {
        refreshAllPlaceBoxes();
        isResizing = false;
    }

    public Place getSelectedPlace() {
        return selectedPlace;
    }

    private void updateFontSizes() {
        double scaleFactor = calculateScaleFactor();

        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD,
                (int) (baseFontSizeTitle * scaleFactor)));

        countLabel.setFont(new Font("Arial", Font.BOLD,
                (int) (baseFontSizeSmall * scaleFactor)));
    }

    private double calculateScaleFactor() {
        if (currentWindowWidth <= 800 && currentWindowHeight <= 600) {
            return 1.0;
        }

        double widthFactor = currentWindowWidth / 800.0;
        double heightFactor = currentWindowHeight / 600.0;

        double scaleFactor = Math.min(widthFactor, heightFactor);

        return Math.min(scaleFactor, 1.8);
    }

    private int calculateScaledFontSize(int baseSize) {
        return (int) (baseSize * calculateScaleFactor());
    }

    private int calculateScaledImageWidth() {
        double scaleFactor = calculateScaleFactor();
        if (currentWindowWidth <= 800 && currentWindowHeight <= 600) {
            return baseImageWidth;
        }
        return (int) (baseImageWidth * scaleFactor);
    }

    private int calculateScaledImageHeight() {
        double scaleFactor = calculateScaleFactor();
        if (currentWindowWidth <= 800 && currentWindowHeight <= 600) {
            return baseImageHeight;
        }
        return (int) (baseImageHeight * scaleFactor);
    }

    private int calculateScaledBoxHeight() {
        double scaleFactor = calculateScaleFactor();
        if (currentWindowWidth <= 800 && currentWindowHeight <= 600) {
            return baseBoxHeight;
        }
        return (int) (baseBoxHeight * scaleFactor);
    }

    private void refreshAllPlaceBoxes() {
        List<Place> places = placeDAO.findAllByDivision(divisionName);

        placesContainer.removeAll();

        if (places.isEmpty()) {
            showNoPlacesMessage();
        } else {
            for (int i = 0; i < places.size(); i++) {
                Place place = places.get(i);
                placesContainer.add(createPlaceBox(place));
                if (i < places.size() - 1) {
                    placesContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }

        SwingUtilities.invokeLater(() -> {
            placesContainer.revalidate();
            placesContainer.repaint();
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Explore " + divisionName + " - ExploBD");

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(88, 74, 60));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(88, 74, 60));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(255, 153, 51)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.ITALIC, baseFontSizeNormal));
        backButton.setBackground(new Color(255, 153, 51));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 120, 30), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> backButtonActionPerformed());

        titleLabel = new JLabel(divisionName.toUpperCase() + " DIVISION");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, baseFontSizeTitle));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        countLabel = new JLabel("Loading places...");
        countLabel.setFont(new Font("Arial", Font.BOLD, baseFontSizeSmall));
        countLabel.setForeground(new Color(255, 204, 102));
        countLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(countLabel, BorderLayout.EAST);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(88, 74, 60));

        placesContainer = new JPanel();
        placesContainer.setLayout(new BoxLayout(placesContainer, BoxLayout.Y_AXIS));
        placesContainer.setBackground(new Color(88, 74, 60));
        placesContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        wrapperPanel.add(placesContainer, BorderLayout.NORTH);

        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(new Color(88, 74, 60));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
    }

    private void loadPlaces() {
        placesContainer.removeAll();

        List<Place> places = placeDAO.findAllByDivision(divisionName);
        countLabel.setText(places.size() + " places found");

        if (places.isEmpty()) {
            showNoPlacesMessage();
        } else {
            for (int i = 0; i < places.size(); i++) {
                Place place = places.get(i);
                placesContainer.add(createPlaceBox(place));
                if (i < places.size() - 1) {
                    placesContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }

        placesContainer.revalidate();
        placesContainer.repaint();
    }

    private void showNoPlacesMessage() {
        RoundedPanel messagePanel = new RoundedPanel();
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        messagePanel.setLayout(new BorderLayout());

        JLabel messageLabel = new JLabel("No places found in " + divisionName + " division");
        messageLabel.setFont(new Font("Arial", Font.BOLD, calculateScaledFontSize(baseFontSizeTitle)));
        messageLabel.setForeground(new Color(100, 100, 100));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        messagePanel.add(messageLabel, BorderLayout.CENTER);
        placesContainer.add(messagePanel);
    }

    private RoundedPanel createPlaceBox(Place place) {
        RoundedPanel placeBox = new RoundedPanel();
        placeBox.setBackground(Color.WHITE);
        placeBox.setLayout(new BorderLayout(0, 0));

        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 0));
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(240, 248, 255));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JPanel imagePanel = createImagePanel(place);
        int imageWidth = calculateScaledImageWidth();
        int imageHeight = calculateScaledImageHeight();
        imagePanel.setPreferredSize(new Dimension(imageWidth, imageHeight));

        leftPanel.add(imagePanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setBackground(new Color(255, 245, 238));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        JPanel topPanel = createTopPanel(place);
        JPanel generalInfoPanel = createGeneralInfoPanel(place);
        JPanel categorySpecificPanel = createCategorySpecificPanel(place);
        JPanel buttonsPanel = createButtonPanel(place);

        JPanel centerInfoPanel = new JPanel(new BorderLayout(0, 10));
        centerInfoPanel.setBackground(new Color(255, 245, 238));
        centerInfoPanel.add(generalInfoPanel, BorderLayout.NORTH);
        centerInfoPanel.add(categorySpecificPanel, BorderLayout.CENTER);

        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(centerInfoPanel, BorderLayout.CENTER);
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        mainContentPanel.add(leftPanel, BorderLayout.WEST);
        mainContentPanel.add(rightPanel, BorderLayout.CENTER);

        placeBox.add(mainContentPanel, BorderLayout.CENTER);

        int boxHeight = calculateScaledBoxHeight();
        placeBox.setPreferredSize(new Dimension(1200, boxHeight));
        placeBox.setMaximumSize(new Dimension(1500, boxHeight));

        return placeBox;
    }

    private JPanel createTopPanel(Place place) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(new Color(255, 245, 238));
        panel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));

        int currentPlaceNameSize = calculateScaledFontSize(baseFontSizePlaceName);

        JLabel iconLabel = new JLabel(getCategoryIcon(place.getPlaceCategory()));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, currentPlaceNameSize));
        iconLabel.setForeground(getCategoryColor(place.getPlaceCategory()));

        JLabel nameLabel = new JLabel(place.getName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, currentPlaceNameSize));
        nameLabel.setForeground(new Color(70, 58, 47));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 1));
        badgePanel.setBackground(getCategoryColor(place.getPlaceCategory()));
        int badgePadding = Math.max(3, currentPlaceNameSize / 7);
        badgePanel.setBorder(BorderFactory.createEmptyBorder(badgePadding, badgePadding * 2, badgePadding, badgePadding * 2));

        JLabel categoryLabel = new JLabel(place.getPlaceCategory());
        categoryLabel.setFont(new Font("Arial", Font.BOLD, calculateScaledFontSize(baseFontSizeSmall)));
        categoryLabel.setForeground(Color.WHITE);
        badgePanel.add(categoryLabel);

        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(8, 0)));
        panel.add(badgePanel);

        return panel;
    }

    private JPanel createImagePanel(Place place) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));

        String imagePath = place.getImagePath();

        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                java.net.URL imageURL = getClass().getResource(imagePath);
                if (imageURL == null) {
                    imageURL = getClass().getResource("/" + imagePath);
                }
                if (imageURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imageURL);
                    if (originalIcon.getIconWidth() > 0) {
                        int imageWidth = calculateScaledImageWidth();
                        int imageHeight = calculateScaledImageHeight();
                        Image scaledImage = originalIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        panel.add(imageLabel, BorderLayout.CENTER);
                        return panel;
                    }
                }
            } catch (Exception e) {
                System.err.println("Image load error: " + e.getMessage());
            }
        }

        JLabel placeholderLabel = new JLabel("No Image");
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, calculateScaledFontSize(baseFontSizeSmall)));
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderLabel.setForeground(new Color(150, 150, 150));
        panel.add(placeholderLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGeneralInfoPanel(Place place) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 245, 238));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        int currentFontSize = calculateScaledFontSize(baseFontSizeNormal);
        int currentSmallFontSize = calculateScaledFontSize(baseFontSizeSmall);

        JPanel tagsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagsRow.setBackground(new Color(255, 245, 238));
        tagsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String tags = place.getTags();
        if (tags != null && !tags.isEmpty()) {
            String[] tagArray = tags.split(",");
            for (String tag : tagArray) {
                tag = tag.trim();
                if (!tag.isEmpty()) {
                    JLabel tagLabel = new JLabel(tag);
                    tagLabel.setFont(new Font("Bookman Old Style", Font.BOLD, currentSmallFontSize));
                    tagLabel.setForeground(new Color(139, 69, 19));
                    int tagPadding = Math.max(2, currentSmallFontSize / 6);
                    tagLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(210, 180, 140), 1),
                            BorderFactory.createEmptyBorder(tagPadding, tagPadding * 3, tagPadding, tagPadding * 3)
                    ));
                    tagsRow.add(tagLabel);
                }
            }
        }

        panel.add(tagsRow);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoRow.setBackground(new Color(255, 245, 238));
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String address = place.getAddress();
        JLabel locationLabel = new JLabel(createBoldLabelText("Location", address != null ? address : "N/A"));
        locationLabel.setFont(new Font("Bookman Old Style", Font.PLAIN, currentFontSize));
        locationLabel.setForeground(new Color(70, 70, 70));

        String cost = place.getEntryCost() == 0 ? "FREE" : "tk." + place.getEntryCost();
        JLabel costLabel = new JLabel(createBoldLabelText("Cost", cost));
        costLabel.setFont(new Font("Bookman Old Style", Font.PLAIN, currentFontSize));
        costLabel.setForeground(place.getEntryCost() == 0
                ? new Color(34, 139, 34) : new Color(139, 69, 19));

        String bestTime = place.getBestTime();
        JLabel timeLabel = new JLabel(createBoldLabelText("Best Time", bestTime != null ? bestTime : "N/A"));
        timeLabel.setFont(new Font("Bookman Old Style", Font.PLAIN, currentFontSize));
        timeLabel.setForeground(new Color(70, 70, 70));

        infoRow.add(locationLabel);
        infoRow.add(costLabel);
        infoRow.add(timeLabel);

        panel.add(infoRow);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        String whyVisit = place.getWhyVisit();
        if (whyVisit == null || whyVisit.isEmpty()) {
            whyVisit = place.getDescription();
        }
        int maxTextLength = 120 + (currentWindowWidth / 15);
        if (whyVisit != null && whyVisit.length() > maxTextLength) {
            whyVisit = whyVisit.substring(0, maxTextLength) + "...";
        }

        JPanel whyVisitRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        whyVisitRow.setBackground(new Color(255, 245, 238));
        whyVisitRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel whyVisitLabel = new JLabel(whyVisit != null ? whyVisit : "");
        whyVisitLabel.setFont(new Font("Bookman Old Style", Font.PLAIN, currentFontSize));
        whyVisitLabel.setForeground(new Color(80, 80, 80));
        whyVisitRow.add(whyVisitLabel);

        panel.add(whyVisitRow);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        return panel;
    }

    private String createBoldLabelText(String boldPart, String normalPart) {
        return boldPart + ": " + normalPart;
    }

    private JPanel createCategorySpecificPanel(Place place) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setBackground(new Color(255, 245, 238));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(3, 0, 3, 0)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int currentFontSize = calculateScaledFontSize(baseFontSizeNormal - 1);

        if (place instanceof HistoricalPlace) {
            HistoricalPlace hp = (HistoricalPlace) place;
            panel.add(createMixedFontLabel("Period", hp.getTimePeriod(), currentFontSize));
            panel.add(createMixedFontLabel("Architecture", hp.getArchitectureStyle(), currentFontSize));
            panel.add(createMixedFontLabel("Built", hp.getYearBuilt(), currentFontSize));

        } else if (place instanceof NaturePlace) {
            NaturePlace np = (NaturePlace) place;
            panel.add(createMixedFontLabel("Type", np.getNatureType(), currentFontSize));
            panel.add(createMixedFontLabel("Activities", np.getActivities(), currentFontSize));
            panel.add(createMixedFontLabel("Area", np.getAreaSize(), currentFontSize));

        } else if (place instanceof ManmadePlace) {
            ManmadePlace mp = (ManmadePlace) place;
            panel.add(createMixedFontLabel("Type", mp.getEntertainmentType(), currentFontSize));
            panel.add(createMixedFontLabel("Hours", mp.getOpeningHours(), currentFontSize));
            panel.add(createMixedFontLabel("Spend", mp.getAverageSpending() > 0 ? "tk." + mp.getAverageSpending() : "Varies", currentFontSize));
        }

        return panel;
    }

    private JLabel createMixedFontLabel(String boldWord, String normalText, int fontSize) {
        JLabel label = new JLabel(boldWord + ": " + (normalText != null ? normalText : "N/A"));
        label.setFont(new Font("Bookman Old Style", Font.PLAIN, fontSize));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JPanel createButtonPanel(Place place) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(new Color(255, 245, 238));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int currentButtonFontSize = calculateScaledFontSize(baseFontSizeSmall);

        JButton detailsBtn = createSmallButton("See Details", new Color(70, 58, 47), currentButtonFontSize);
        JButton favBtn = createSmallButton("Add to Favorite", new Color(255, 182, 193), currentButtonFontSize);
        JButton wishBtn = createSmallButton("Add to Wishlist", new Color(255, 215, 0), currentButtonFontSize);
        JButton groupBtn = createSmallButton("Create Group", new Color(65, 105, 225), currentButtonFontSize);

        if (currentUser != null) {
            if (placeDAO.isInWishlist(currentUser.getUserId(), place.getId())) {
                wishBtn.setText("In Wishlist");
                wishBtn.setBackground(new Color(218, 165, 32));
            }

            if (placeDAO.isFavorite(currentUser.getUserId(), place.getId())) {
                favBtn.setText("Favorited");
                favBtn.setBackground(new Color(220, 50, 50));
            }
        }

        detailsBtn.addActionListener(e -> showPlaceDetailsInfoBox(place));

        favBtn.addActionListener(e -> {
            if (currentUser == null) {
                return;
            }

            boolean currentlyFavorite = placeDAO.isFavorite(currentUser.getUserId(), place.getId());
            if (currentlyFavorite) {
                if (placeDAO.removeFromFavorite(currentUser.getUserId(), place.getId())) {
                    favBtn.setText("Add to Favorite");
                    favBtn.setBackground(new Color(255, 182, 193));
                }
            } else {
                if (placeDAO.addToFavorite(currentUser.getUserId(), place.getId())) {
                    favBtn.setText("Favorited");
                    favBtn.setBackground(new Color(220, 50, 50));
                }
            }
        });

        wishBtn.addActionListener(e -> {
            if (currentUser == null) {
                return;
            }

            boolean currentlyInWishlist = placeDAO.isInWishlist(currentUser.getUserId(), place.getId());
            if (currentlyInWishlist) {
                if (placeDAO.removeFromWishlist(currentUser.getUserId(), place.getId())) {
                    wishBtn.setText("Add to Wishlist");
                    wishBtn.setBackground(new Color(255, 215, 0));
                }
            } else {
                if (placeDAO.addToWishlist(currentUser.getUserId(), place.getId())) {
                    wishBtn.setText("In Wishlist");
                    wishBtn.setBackground(new Color(218, 165, 32));
                }
            }
        });

        groupBtn.addActionListener(e -> {
            if (isSelectionMode) {
                selectedPlace = place;
                dispose();
            } else {
                new CreateGroupFrame(currentUser, place).setVisible(true);
                dispose();
            }
        });

        panel.add(detailsBtn);
        panel.add(favBtn);
        panel.add(wishBtn);
        panel.add(groupBtn);

        return panel;
    }

    private void showPlaceDetailsInfoBox(Place place) {
        new PlaceInfoDialog(this, place).setVisible(true);
    }

    private void loadImageInDisplay(JLabel displayLabel, String imagePath, int maxWidth, int maxHeight) {
        try {
            if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
                ImageIcon originalIcon = null;

                try {
                    java.net.URL imageURL = getClass().getResource(imagePath);
                    if (imageURL == null && !imagePath.startsWith("/")) {
                        imageURL = getClass().getResource("/" + imagePath);
                    }

                    if (imageURL != null) {
                        originalIcon = new ImageIcon(imageURL);
                    } else {
                        java.io.File file = new java.io.File(imagePath);
                        if (file.exists()) {
                            originalIcon = new ImageIcon(imagePath);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (originalIcon != null && originalIcon.getIconWidth() > 0) {

                    int originalWidth = originalIcon.getIconWidth();
                    int originalHeight = originalIcon.getIconHeight();

                    double widthRatio = (double) maxWidth / originalWidth;
                    double heightRatio = (double) maxHeight / originalHeight;
                    double scale = Math.min(widthRatio, heightRatio);

                    int scaledWidth = (int) (originalWidth * scale);
                    int scaledHeight = (int) (originalHeight * scale);

                    Image scaledImage = originalIcon.getImage().getScaledInstance(
                            scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    displayLabel.setIcon(new ImageIcon(scaledImage));
                    displayLabel.setText("");
                } else {
                    displayLabel.setIcon(null);
                    displayLabel.setText("Image Not Available");
                    displayLabel.setFont(new Font("Bookman Old Style", Font.ITALIC, 12));
                    displayLabel.setForeground(Color.WHITE);
                }
            } else {
                displayLabel.setIcon(null);
                displayLabel.setText("Image Not Available");
                displayLabel.setFont(new Font("Bookman Old Style", Font.ITALIC, 12));
                displayLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            displayLabel.setIcon(null);
            displayLabel.setText("Error Loading");
            displayLabel.setFont(new Font("Bookman Old Style", Font.ITALIC, 12));
            displayLabel.setForeground(Color.WHITE);
        }
    }

    private JPanel createImagePanel(String imagePath, String label) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 120, 90), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                java.net.URL imageURL = getClass().getResource(imagePath);
                if (imageURL == null) {
                    imageURL = getClass().getResource("/" + imagePath);
                }
                if (imageURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imageURL);
                    if (originalIcon.getIconWidth() > 0) {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        panel.add(imageLabel, BorderLayout.CENTER);
                        return panel;
                    }
                }
            } catch (Exception e) {

            }
        }
        JLabel placeholderLabel = new JLabel(label + " (No Image)");
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderLabel.setForeground(new Color(150, 150, 150));
        panel.add(placeholderLabel, BorderLayout.CENTER);

        return panel;
    }

    private String getPlaceDetailsText(Place place) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(place.getName()).append("\n");
        sb.append("Category: ").append(place.getPlaceCategory()).append("\n");
        sb.append("Location: ").append(place.getLocation()).append("\n");
        sb.append("Address: ").append(place.getAddress() != null ? place.getAddress() : "N/A").append("\n");
        sb.append("Entry Cost: ").append(place.getEntryCost() == 0 ? "FREE" : "৳" + place.getEntryCost()).append("\n");
        sb.append("Best Time: ").append(place.getBestTime() != null ? place.getBestTime() : "N/A").append("\n");
        sb.append("Tags: ").append(place.getTags() != null ? place.getTags() : "N/A").append("\n\n");

        if (place.getDescription() != null) {
            sb.append("Description:\n").append(place.getDescription()).append("\n\n");
        }

        if (place.getWhyVisit() != null) {
            sb.append("Why Visit:\n").append(place.getWhyVisit());
        }

        return sb.toString();
    }

    private JButton createSmallButton(String text, Color bgColor, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        int r = Math.max(0, bgColor.getRed() - 20);
        int g = Math.max(0, bgColor.getGreen() - 20);
        int b = Math.max(0, bgColor.getBlue() - 20);

        int padding = Math.max(5, fontSize / 2);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(r, g, b), 1),
                BorderFactory.createEmptyBorder(padding, padding * 2, padding, padding * 2)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private String getCategoryIcon(String category) {
        if (category.equals("HISTORICAL") || category.equals("Historical")) {
            return "🏛️";
        } else if (category.equals("NATURE") || category.equals("Nature")) {
            return "🌿";
        } else if (category.equals("ENTERTAINMENT") || category.equals("Entertainment")) {
            return "🎪";
        } else {
            return "📍";
        }
    }

    private Color getCategoryColor(String category) {
        if (category.equals("HISTORICAL") || category.equals("Historical")) {
            return new Color(139, 69, 19);
        } else if (category.equals("NATURE") || category.equals("Nature")) {
            return new Color(34, 139, 34);
        } else if (category.equals("ENTERTAINMENT") || category.equals("Entertainment")) {
            return new Color(255, 140, 0);
        } else {
            return new Color(100, 100, 100);
        }
    }

    private void planGroupTrip(Place place) {
        JOptionPane.showMessageDialog(this,
                "Group trip planning feature coming soon!\n"
                + "Selected: " + place.getName() + "\n"
                + "Location: " + place.getLocation(),
                "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }

    private void backButtonActionPerformed() {
        this.dispose();
    }

    public void refreshPlaces() {
        loadPlaces();
    }
}
