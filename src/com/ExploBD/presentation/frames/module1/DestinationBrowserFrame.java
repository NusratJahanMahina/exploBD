package com.ExploBD.presentation.frames.module1;

import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.NaturePlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class DestinationBrowserFrame extends JFrame {

    private User currentUser;
    private Place selectedPlace;
    private boolean placeSelected = false;
    private String currentView = "MY_LIST";
    private PlaceDatabaseObject placeDAO;

    private JPanel mainPanel;
    private JPanel titlePanel;
    private JPanel buttonPanel;
    private JPanel placesContainer;
    private JScrollPane scrollPane;
    private JLabel titleLabel;

    private JButton myListBtn;
    private JButton dhakaBtn, chittagongBtn, sylhetBtn, khulnaBtn;
    private JButton rajshahiBtn, barishalBtn, rangpurBtn, mymensinghBtn;

    private Color BROWN_BG = new Color(88, 74, 60);
    private Color ORANGE = new Color(255, 153, 51);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color GOLD = new Color(255, 215, 0);
    private Color LIGHT_CREAM = new Color(250, 245, 240);

    private int baseWidth = 800;
    private int baseHeight = 600;
    private int currentWidth = 800;
    private int currentHeight = 600;

    public DestinationBrowserFrame(User user) {
        this.currentUser = user;
        this.placeDAO = new PlaceDatabaseObject();
        initComponents();
        setSize(baseWidth, baseHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Select Destination");
        showMyList();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                currentWidth = getWidth();
                currentHeight = getHeight();
                scaleComponents();
            }
        });
    }

    private void scaleComponents() {
        double scale = Math.min(currentWidth / (double) baseWidth, currentHeight / (double) baseHeight);
        scale = Math.min(scale, 1.5);
        scale = Math.max(scale, 0.8);

        int titleFontSize = (int) (28 * scale);
        int buttonFontSize = (int) (13 * scale);

        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, titleFontSize));

        JButton[] buttons = {myListBtn, dhakaBtn, chittagongBtn, sylhetBtn,
            khulnaBtn, rajshahiBtn, barishalBtn, rangpurBtn, mymensinghBtn};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Bookman Old Style", Font.BOLD, buttonFontSize));
        }

        if (currentView.equals("MY_LIST")) {
            showMyList();
        } else {
            showDivision(currentView);
        }
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BROWN_BG);

        titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BROWN_BG);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        titlePanel.setPreferredSize(new Dimension(baseWidth, 70));
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ORANGE),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        titleLabel = new JLabel("CHOOSE DESTINATION");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 28));
        titleLabel.setForeground(ORANGE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(BROWN_BG);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        buttonPanel.setPreferredSize(new Dimension(baseWidth, 60));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        myListBtn = createNavButton("My List", new Color(255, 215, 0), DARK_BROWN);
        dhakaBtn = createNavButton("Dhaka", DARK_BROWN, GOLD);
        chittagongBtn = createNavButton("Chittagong", DARK_BROWN, GOLD);
        sylhetBtn = createNavButton("Sylhet", DARK_BROWN, GOLD);
        khulnaBtn = createNavButton("Khulna", DARK_BROWN, GOLD);
        rajshahiBtn = createNavButton("Rajshahi", DARK_BROWN, GOLD);
        barishalBtn = createNavButton("Barishal", DARK_BROWN, GOLD);
        rangpurBtn = createNavButton("Rangpur", DARK_BROWN, GOLD);
        mymensinghBtn = createNavButton("Mymensingh", DARK_BROWN, GOLD);

        myListBtn.addActionListener(e -> showMyList());
        dhakaBtn.addActionListener(e -> openDivision("Dhaka"));
        chittagongBtn.addActionListener(e -> openDivision("Chittagong"));
        sylhetBtn.addActionListener(e -> openDivision("Sylhet"));
        khulnaBtn.addActionListener(e -> openDivision("Khulna"));
        rajshahiBtn.addActionListener(e -> openDivision("Rajshahi"));
        barishalBtn.addActionListener(e -> openDivision("Barishal"));
        rangpurBtn.addActionListener(e -> openDivision("Rangpur"));
        mymensinghBtn.addActionListener(e -> openDivision("Mymensingh"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 2, 0, 2);

        gbc.gridx = 0;
        buttonPanel.add(myListBtn, gbc);
        gbc.gridx = 1;
        buttonPanel.add(dhakaBtn, gbc);
        gbc.gridx = 2;
        buttonPanel.add(chittagongBtn, gbc);
        gbc.gridx = 3;
        buttonPanel.add(sylhetBtn, gbc);
        gbc.gridx = 4;
        buttonPanel.add(khulnaBtn, gbc);
        gbc.gridx = 5;
        buttonPanel.add(rajshahiBtn, gbc);
        gbc.gridx = 6;
        buttonPanel.add(barishalBtn, gbc);
        gbc.gridx = 7;
        buttonPanel.add(rangpurBtn, gbc);
        gbc.gridx = 8;
        buttonPanel.add(mymensinghBtn, gbc);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(BROWN_BG);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        placesContainer = new JPanel();
        placesContainer.setLayout(new BoxLayout(placesContainer, BoxLayout.Y_AXIS));
        placesContainer.setBackground(BROWN_BG);

        contentWrapper.add(placesContainer, BorderLayout.NORTH);

        scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(BROWN_BG);

        mainPanel.add(titlePanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(scrollPane);

        add(mainPanel);
    }

    private JButton createNavButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Bookman Old Style", Font.BOLD, 10));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ORANGE, 1),
                BorderFactory.createEmptyBorder(6, 2, 6, 2)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 35));
        btn.setMinimumSize(new Dimension(60, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ORANGE);
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
                btn.setForeground(fgColor);
            }
        });

        return btn;
    }

    private void setActiveButton(JButton activeBtn) {
        JButton[] buttons = {myListBtn, dhakaBtn, chittagongBtn, sylhetBtn,
            khulnaBtn, rajshahiBtn, barishalBtn, rangpurBtn, mymensinghBtn};
        Color[] bgColors = {new Color(255, 215, 0), DARK_BROWN, DARK_BROWN, DARK_BROWN,
            DARK_BROWN, DARK_BROWN, DARK_BROWN, DARK_BROWN, DARK_BROWN};
        Color[] fgColors = {DARK_BROWN, GOLD, GOLD, GOLD, GOLD, GOLD, GOLD, GOLD, GOLD};

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackground(bgColors[i]);
            buttons[i].setForeground(fgColors[i]);
        }

        activeBtn.setBackground(ORANGE);
        activeBtn.setForeground(Color.WHITE);
    }

    private void showMyList() {
        currentView = "MY_LIST";
        setActiveButton(myListBtn);

        placesContainer.removeAll();

        List<Place> wishlist = placeDAO.getUserWishlist(currentUser.getUserId());
        List<Place> favorites = placeDAO.getUserFavorites(currentUser.getUserId());

        java.util.Set<Place> placeSet = new java.util.HashSet<>();
        placeSet.addAll(wishlist);
        placeSet.addAll(favorites);
        List<Place> allMyPlaces = new java.util.ArrayList<>(placeSet);

        if (allMyPlaces.isEmpty()) {
            showNoPlacesMessage("No places in your list");
        } else {
            for (Place place : allMyPlaces) {
                placesContainer.add(createPlaceBox(place));
                placesContainer.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        placesContainer.revalidate();
        placesContainer.repaint();
    }

    private void showDivision(String division) {
        currentView = division;

        JButton[] buttons = {dhakaBtn, chittagongBtn, sylhetBtn, khulnaBtn,
            rajshahiBtn, barishalBtn, rangpurBtn, mymensinghBtn};
        String[] divisions = {"Dhaka", "Chittagong", "Sylhet", "Khulna",
            "Rajshahi", "Barishal", "Rangpur", "Mymensingh"};

        for (int i = 0; i < divisions.length; i++) {
            if (divisions[i].equals(division)) {
                setActiveButton(buttons[i]);
                break;
            }
        }

        placesContainer.removeAll();

        List<Place> places = placeDAO.findAllByDivision(division);

        if (places.isEmpty()) {
            showNoPlacesMessage("No places found in " + division);
        } else {
            for (Place place : places) {
                placesContainer.add(createPlaceBox(place));
                placesContainer.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        placesContainer.revalidate();
        placesContainer.repaint();
    }

    private void openDivision(String division) {
        DivisionExploreFrame divFrame = new DivisionExploreFrame(currentUser, division, true);

        divFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Place selected = divFrame.getSelectedPlace();
                if (selected != null) {
                    selectedPlace = selected;
                    placeSelected = true;
                    dispose();
                }
            }
        });

        divFrame.setVisible(true);
    }

    private void showNoPlacesMessage(String message) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setForeground(Color.GRAY);

        messagePanel.add(messageLabel, BorderLayout.CENTER);
        placesContainer.add(messagePanel);
    }

    private JPanel createPlaceBox(Place place) {
        double scale = Math.min(currentWidth / (double) baseWidth, currentHeight / (double) baseHeight);
        scale = Math.min(scale, 1.5);
        scale = Math.max(scale, 0.8);

        int boxHeight = (int) (100 * scale);
        int imageSize = (int) (80 * scale);
        int nameFontSize = (int) (15 * scale);
        int textFontSize = (int) (11 * scale);

        JPanel placeBox = new JPanel(new BorderLayout(8, 0));
        placeBox.setBackground(Color.WHITE);
        placeBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        placeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, boxHeight));
        placeBox.setPreferredSize(new Dimension((int) (750 * scale), boxHeight));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(LIGHT_CREAM);
        imagePanel.setPreferredSize(new Dimension(imageSize, imageSize));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140)));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String imagePath = place.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                java.net.URL url = getClass().getResource(imagePath);
                if (url == null) {
                    url = getClass().getResource("/" + imagePath);
                }
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    Image scaled = icon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                } else {
                    imageLabel.setText("No Image");
                    imageLabel.setFont(new Font("Arial", Font.ITALIC, textFontSize));
                }
            } catch (Exception e) {
                imageLabel.setText("No Image");
                imageLabel.setFont(new Font("Arial", Font.ITALIC, textFontSize));
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setFont(new Font("Arial", Font.ITALIC, textFontSize));
        }

        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 5));

        JLabel nameLabel = new JLabel(place.getName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, nameFontSize));
        nameLabel.setForeground(DARK_BROWN);

        JLabel locationLabel = new JLabel(place.getDistrict() + ", " + place.getDivision());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, textFontSize));
        locationLabel.setForeground(new Color(100, 100, 100));

        String cost = place.getEntryCost() == 0 ? "Free Entry" : "Tk. " + place.getEntryCost();
        JLabel costLabel = new JLabel(cost);
        costLabel.setFont(new Font("Arial", Font.PLAIN, textFontSize));
        costLabel.setForeground(place.getEntryCost() == 0 ? new Color(34, 139, 34) : new Color(139, 69, 19));

        String bestTime = place.getBestTime() != null ? place.getBestTime() : "Anytime";
        JLabel timeLabel = new JLabel("Best: " + bestTime);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, textFontSize));
        timeLabel.setForeground(new Color(100, 100, 100));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(locationLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(costLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(timeLabel);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));

        JButton detailsBtn = createActionButton("Details", DARK_BROWN, GOLD, scale);
        JButton selectBtn = createActionButton("Create Group", new Color(65, 105, 225), Color.WHITE, scale);

        detailsBtn.addActionListener(e -> showPlaceDetailsInfoBox(place));
        selectBtn.addActionListener(e -> {
            selectedPlace = place;
            placeSelected = true;
            dispose();
        });

        JPanel buttonWrapper = new JPanel(new GridLayout(2, 1, 0, 3));
        buttonWrapper.setBackground(Color.WHITE);
        buttonWrapper.add(detailsBtn);
        buttonWrapper.add(selectBtn);

        buttonPanel.add(buttonWrapper, BorderLayout.CENTER);

        placeBox.add(imagePanel, BorderLayout.WEST);
        placeBox.add(infoPanel, BorderLayout.CENTER);
        placeBox.add(buttonPanel, BorderLayout.EAST);

        return placeBox;
    }

    private JButton createActionButton(String text, Color bgColor, Color fgColor, double scale) {
        JButton button = new JButton(text);
        int fontSize = (int) (11 * scale);
        int padding = (int) (5 * scale);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(
                padding, (int) (12 * scale), padding, (int) (12 * scale)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(null);
        return button;
    }

    private void showPlaceDetailsInfoBox(Place place) {
        JDialog dialog = new JDialog(this, place.getName(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));

        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(place.getName()).append("\n\n");
        details.append("Location: ").append(place.getAddress() != null ? place.getAddress() : "N/A").append("\n");
        details.append("District: ").append(place.getDistrict()).append("\n");
        details.append("Division: ").append(place.getDivision()).append("\n\n");
        details.append("Category: ").append(place.getPlaceCategory()).append("\n");

        if (place instanceof HistoricalPlace) {
            HistoricalPlace hp = (HistoricalPlace) place;
            if (hp.getTimePeriod() != null && !hp.getTimePeriod().isEmpty()) {
                details.append("Period: ").append(hp.getTimePeriod()).append("\n");
            }
            if (hp.getArchitectureStyle() != null && !hp.getArchitectureStyle().isEmpty()) {
                details.append("Architecture: ").append(hp.getArchitectureStyle()).append("\n");
            }
            if (hp.getYearBuilt() != null && !hp.getYearBuilt().isEmpty()) {
                details.append("Built: ").append(hp.getYearBuilt()).append("\n");
            }
            if (hp.getHistoricalFacts() != null && !hp.getHistoricalFacts().isEmpty()) {
                details.append("\nFacts: ").append(hp.getHistoricalFacts()).append("\n");
            }
        } else if (place instanceof NaturePlace) {
            NaturePlace np = (NaturePlace) place;
            if (np.getNatureType() != null && !np.getNatureType().isEmpty()) {
                details.append("Type: ").append(np.getNatureType()).append("\n");
            }
            if (np.getAreaSize() != null && !np.getAreaSize().isEmpty()) {
                details.append("Area: ").append(np.getAreaSize()).append("\n");
            }
            if (np.getActivities() != null && !np.getActivities().isEmpty()) {
                details.append("Activities: ").append(np.getActivities()).append("\n");
            }
        } else if (place instanceof ManmadePlace) {
            ManmadePlace mp = (ManmadePlace) place;
            if (mp.getEntertainmentType() != null && !mp.getEntertainmentType().isEmpty()) {
                details.append("Type: ").append(mp.getEntertainmentType()).append("\n");
            }
            if (mp.getOpeningHours() != null && !mp.getOpeningHours().isEmpty()) {
                details.append("Hours: ").append(mp.getOpeningHours()).append("\n");
            }
            if (mp.getContactInfo() != null && !mp.getContactInfo().isEmpty()) {
                details.append("Contact: ").append(mp.getContactInfo()).append("\n");
            }
        }

        details.append("\nEntry Cost: ").append(place.getEntryCost() == 0 ? "Free" : "Tk. " + place.getEntryCost()).append("\n");
        details.append("Best Time: ").append(place.getBestTime() != null ? place.getBestTime() : "N/A").append("\n\n");

        if (place.getDescription() != null && !place.getDescription().isEmpty()) {
            details.append("Description:\n").append(place.getDescription());
        }

        textArea.setText(details.toString());
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(DARK_BROWN);
        closeBtn.setForeground(GOLD);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public Place getSelectedPlace() {
        return selectedPlace;
    }

    public boolean isPlaceSelected() {
        return placeSelected;
    }
}
