package com.ExploBD.presentation.components;

import com.ExploBD.object.*;
import com.ExploBD.Frame.RoundedPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PlaceInfoDialog extends JDialog {
    
    private Place place;
    private List<String> imagePaths;
    private int currentImageIndex = 0;
    
    
    
    private Color ORANGE = new Color(255, 153, 51);
    
    public PlaceInfoDialog(Frame parent, Place place) {
        super(parent, place.getName(), true);
        this.place = place;
        this.imagePaths = collectImagePaths(place);
        
        setUndecorated(true);
        setSize(650, 550);
        setLocationRelativeTo(parent);
        
        initComponents();
    }
    
  private List<String> collectImagePaths(Place place) {
    List<String> paths = new ArrayList<>();
    
    if (place instanceof HistoricalPlace) {
        HistoricalPlace hp = (HistoricalPlace) place;
        addIfValid(paths, hp.getImage1());
        addIfValid(paths, hp.getImage2());
        addIfValid(paths, hp.getImage3());
        addIfValid(paths, hp.getImage4());
        addIfValid(paths, hp.getImage5()); 
    } else if (place instanceof NaturePlace) {
        NaturePlace np = (NaturePlace) place;
        addIfValid(paths, np.getImage1());
        addIfValid(paths, np.getImage2());
        addIfValid(paths, np.getImage3());
        addIfValid(paths, np.getImage4());
        addIfValid(paths, np.getImage5());  
    } else if (place instanceof ManmadePlace) {
        ManmadePlace mp = (ManmadePlace) place;
        addIfValid(paths, mp.getImage1());
        addIfValid(paths, mp.getImage2());
        addIfValid(paths, mp.getImage3());
        addIfValid(paths, mp.getImage4());
        addIfValid(paths, mp.getImage5());  
    }
    
    return paths;
}
    
    private void addIfValid(List<String> paths, String path) {
        if (path != null && !path.isEmpty() && !path.equals("null")) {
            paths.add(path);
        }
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 250, 245));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        header.setBackground(new Color(255, 250, 245));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel iconLabel = new JLabel(getCategoryIcon(place.getPlaceCategory()));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(getCategoryColor(place.getPlaceCategory()));
        
        JLabel titleLabel = new JLabel(place.getName());
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 24));
        titleLabel.setForeground(new Color(100, 50, 30));
        
        JPanel badge = new JPanel();
        badge.setBackground(getCategoryColor(place.getPlaceCategory()));
        badge.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel categoryText = new JLabel(place.getPlaceCategory());
        categoryText.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        categoryText.setForeground(Color.WHITE);
        badge.add(categoryText);
        
        header.add(iconLabel);
        header.add(titleLabel);
        header.add(badge);
        
        return header;
    }
    
    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(new Color(255, 250, 245));
        
        content.add(createImagePanel(), BorderLayout.WEST);
        
        content.add(createDetailsPanel(), BorderLayout.CENTER);
        
        return content;
    }
    
    private JPanel createImagePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(250, 280));
        wrapper.setBackground(new Color(255, 250, 245));
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(255, 250, 245));
        imagePanel.setPreferredSize(new Dimension(240, 240));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 1));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JButton prevBtn = new JButton("<");
        prevBtn.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        prevBtn.setBackground(new Color(139, 69, 19));
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setFocusPainted(false);
        prevBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton nextBtn = new JButton(">");
        nextBtn.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        nextBtn.setBackground(new Color(139, 69, 19));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        loadImage(imageLabel);
        
        
        prevBtn.addActionListener(e -> {
            if (currentImageIndex > 0) {
                currentImageIndex--;
                loadImage(imageLabel);
            }
        });
        
        nextBtn.addActionListener(e -> {
            if (currentImageIndex < imagePaths.size() - 1) {
                currentImageIndex++;
                loadImage(imageLabel);
            }
        });
        
        
        prevBtn.setVisible(imagePaths.size() > 1);
        nextBtn.setVisible(imagePaths.size() > 1);
        
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        navPanel.setBackground(new Color(255, 250, 245));
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        
        wrapper.add(imagePanel, BorderLayout.CENTER);
        wrapper.add(navPanel, BorderLayout.SOUTH);
        
        return wrapper;
    }
    
    private void loadImage(JLabel imageLabel) {
        if (imagePaths.isEmpty()) {
            imageLabel.setText("No Images Available");
            imageLabel.setFont(new Font("Bookman Old Style", Font.ITALIC, 14));
            imageLabel.setForeground(new Color(150, 150, 150));
            return;
        }
        
        String path = imagePaths.get(currentImageIndex);
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) url = getClass().getResource("/" + path);
            
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaled = icon.getImage().getScaledInstance(240, 240, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
                imageLabel.setText("");
            } else {
                imageLabel.setText("Image " + (currentImageIndex + 1));
            }
        } catch (Exception e) {
            imageLabel.setText("Image " + (currentImageIndex + 1));
        }
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 253, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 180, 140), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setBackground(new Color(255, 253, 250));
        details.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
        details.setText(buildDetailsText());
        details.setCaretPosition(0);
        
        JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    
    private String buildDetailsText() {
        StringBuilder text = new StringBuilder();
        
        text.append("Location:\n").append(place.getAddress() != null ? place.getAddress() : "N/A").append("\n\n");
        
        if (place instanceof HistoricalPlace) {
            HistoricalPlace hp = (HistoricalPlace) place;
            if (hp.getHistoricalFacts() != null && !hp.getHistoricalFacts().isEmpty()) {
                text.append("Historical Facts:\n").append(hp.getHistoricalFacts()).append("\n\n");
            }
            if (hp.getArchitectureStyle() != null) {
                text.append("Architecture: ").append(hp.getArchitectureStyle()).append("\n");
            }
            if (hp.getYearBuilt() != null) {
                text.append("Built: ").append(hp.getYearBuilt()).append("\n");
            }
            if (hp.isUNESCO()) {
                text.append("UNESCO World Heritage Site\n");
            }
        } else if (place instanceof NaturePlace) {
            NaturePlace np = (NaturePlace) place;
            if (np.getConservationStatus() != null) {
                text.append("Conservation Status:\n").append(np.getConservationStatus()).append("\n\n");
            }
            if (np.getActivities() != null) {
                text.append("Activities: ").append(np.getActivities()).append("\n");
            }
            if (np.getAreaSize() != null) {
                text.append("Area: ").append(np.getAreaSize()).append("\n");
            }
        } else if (place instanceof ManmadePlace) {
            ManmadePlace mp = (ManmadePlace) place;
            if (mp.getSpecialEvents() != null) {
                text.append("Special Events:\n").append(mp.getSpecialEvents()).append("\n\n");
            }
            if (mp.getOpeningHours() != null) {
                text.append("Hours: ").append(mp.getOpeningHours()).append("\n");
            }
            if (mp.getContactInfo() != null) {
                text.append("Contact: ").append(mp.getContactInfo()).append("\n");
            }
        }
        
        if (place.getDescription() != null && !place.getDescription().isEmpty()) {
            text.append("\nDescription:\n").append(place.getDescription());
        }
        
        return text.toString();
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(255, 250, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        closeBtn.setBackground(new Color(139, 69, 19));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setBackground(ORANGE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setBackground(new Color(139, 69, 19));
            }
        });
        
        panel.add(closeBtn);
        return panel;
    }
    
    private String getCategoryIcon(String category) {
        if (category.equalsIgnoreCase("HISTORICAL") || category.equalsIgnoreCase("Historical")) {
            return "🏛️";
        } else if (category.equalsIgnoreCase("NATURE") || category.equalsIgnoreCase("Nature")) {
            return "🌿";
        } else if (category.equalsIgnoreCase("ENTERTAINMENT") || category.equalsIgnoreCase("Entertainment")) {
            return "🎪";
        }
        return "📍";
    }
    
    private Color getCategoryColor(String category) {
        if (category.equalsIgnoreCase("HISTORICAL") || category.equalsIgnoreCase("Historical")) {
            return new Color(139, 69, 19);
        } else if (category.equalsIgnoreCase("NATURE") || category.equalsIgnoreCase("Nature")) {
            return new Color(34, 139, 34);
        } else if (category.equalsIgnoreCase("ENTERTAINMENT") || category.equalsIgnoreCase("Entertainment")) {
            return new Color(255, 140, 0);
        }
        return new Color(100, 100, 100);
    }
}