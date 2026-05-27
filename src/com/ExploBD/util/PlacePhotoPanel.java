package com.ExploBD.util;

import com.ExploBD.object.Place;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PlacePhotoPanel extends JPanel {
    
    private Place place;
    private int width;
    private int height;
    private JLabel photoLabel;
    
    public PlacePhotoPanel(Place place, int width, int height) {
        this.place = place;
        this.width = width;
        this.height = height;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140)));
        
        photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        loadImage();
        
        add(photoLabel, BorderLayout.CENTER);
    }
    
    private void loadImage() {
        String imagePath = place.getImagePath();
        boolean imageLoaded = false;
        
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
            try {
                URL url = getClass().getResource(imagePath);
                if (url == null) {
                    url = getClass().getResource("/" + imagePath);
                }
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    
                    Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaled));
                    imageLoaded = true;
                }
            } catch (Exception e) {
                
            }
        }
        
        if (!imageLoaded) {
            String placeName = place.getName();
            String firstLetter = placeName != null && placeName.length() > 0
                    ? placeName.substring(0, 1).toUpperCase() : "P";
            photoLabel.setText(firstLetter);
            photoLabel.setFont(new Font("Bookman Old Style", Font.BOLD, height / 2));
            photoLabel.setForeground(new Color(70, 58, 47));
            photoLabel.setBackground(new Color(250, 245, 240));
            photoLabel.setOpaque(true);
        }
    }
    
    public void updatePlace(Place newPlace) {
        this.place = newPlace;
        loadImage();
        revalidate();
        repaint();
    }
}