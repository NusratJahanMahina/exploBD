package com.ExploBD.presentation.frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Intro extends JFrame {

    private int currentImageIndex = 0;
    private JLabel imageLabel;
    private JLabel line1Label;
    private JLabel line2Label;
    private final JProgressBar progressBar;
    private List<ImageIcon> images;
    private Timer imageTimer;
    private Timer progressTimer;
    private Timer navigationTimer;

    private final Color brown_bg = new Color(88, 74, 60);
    private final Color gold = new Color(212, 175, 55);
    private final Color progress_bar_colour = new Color(232, 195, 75);
    private final Color progress_bg_colour = new Color(100, 86, 72);

    public Intro() {
        setTitle("ExploBD");
        setSize(800, 600);
        com.ExploBD.presentation.frames.AppConfig.applyAndTrack(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

     
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(brown_bg);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        
        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.65; 
        gbc.insets = new Insets(20, 50, 10, 50);
        mainPanel.add(imagePanel, gbc);

       
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        line1Label = new JLabel("", SwingConstants.CENTER);
        line1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        line1Label.setForeground(gold);

        line2Label = new JLabel("", SwingConstants.CENTER);
        line2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
        line2Label.setForeground(gold);

        textPanel.add(line1Label);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(line2Label);
        
        gbc.gridy = 1;
        gbc.weighty = 0.25; 
        gbc.insets = new Insets(10, 50, 10, 50);
        mainPanel.add(textPanel, gbc);

        
        progressBar = new JProgressBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                
                g2.setColor(progress_bg_colour);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                
                int progressWidth = (int) ((getWidth() - 2) * ((double) getValue() / getMaximum()));
                g2.setColor(progress_bar_colour);
                g2.fillRect(1, 1, progressWidth, getHeight() - 2);
                
                
                g2.setColor(new Color(120, 106, 92));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                
                if (isStringPainted()) {
                    String text = getString();
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.setColor(Color.WHITE);
                    g2.setFont(getFont());
                    g2.drawString(text, x, y);
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                
                return new Dimension(super.getPreferredSize().width, 6);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(100, 4);
            }
            
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, 8);
            }
        };

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(progress_bar_colour);
        progressBar.setBackground(progress_bg_colour);
        progressBar.setFont(new Font("Comic Sans MS", Font.ITALIC, 10));
        
        
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(0, 50, 40, 50)); 
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        gbc.gridy = 2;
        gbc.weighty = 0.1; 
        gbc.insets = new Insets(5, 50, 20, 50); 
        mainPanel.add(progressPanel, gbc);
        
        images = new ArrayList<>();
        String[] imageNames = {
            "intro1.jpeg", 
            "intro2.jpg", 
            "intro3.jpeg", 
            "intro4.jpg", 
            "intro5.jpg"
        };

        for (String name : imageNames) {
            java.net.URL url = getClass().getResource("/images/" + name);
            if (url != null) {
                images.add(new ImageIcon(url));
            }
        }

        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeComponents();
            }
        });

        updateContent(0);
        resizeComponents(); 
        add(mainPanel);

        startIntroSequence();
        setVisible(true);
    }

    private void resizeComponents() {
        
        int windowWidth = getWidth();
        int windowHeight = getHeight();
        
        
        int imageWidth = Math.max(300, (int)(windowWidth * 0.7));
        int imageHeight = Math.max(150, (int)(windowHeight * 0.4)); 
        
        
        if (currentImageIndex >= 0 && currentImageIndex < images.size()) {
            ImageIcon originalIcon = images.get(currentImageIndex);
            if (originalIcon != null) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                    imageWidth, imageHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            }
        }
        
        
        int baseFontSize = Math.max(12, windowHeight / 30);
        Font line1Font = line1Label.getFont();
        Font line2Font = line2Label.getFont();
        
        
        switch (currentImageIndex) {
            case 0:
                line1Label.setFont(new Font("Comic Sans MS", Font.BOLD, Math.max(24, baseFontSize + 8)));
                line2Label.setFont(new Font("Comic Sans MS", Font.ITALIC, Math.max(14, baseFontSize - 4)));
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                line1Label.setFont(new Font("Comic Sans MS", Font.ITALIC, Math.max(16, baseFontSize)));
                line2Label.setFont(new Font("Comic Sans MS", Font.ITALIC, Math.max(16, baseFontSize)));
                break;
        }
        
        
        int progressFontSize = Math.max(8, windowHeight / 60);
        progressBar.setFont(new Font("Comic Sans MS", Font.ITALIC, progressFontSize));
        
        int progressBarHeight = Math.max(4, Math.min(8, windowHeight / 100));
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, progressBarHeight));
        
        
        revalidate();
        repaint();
    }

    private void updateContent(int index) {
        if (index >= 0 && index < images.size()) {
            currentImageIndex = index;
            
           
            if (images.get(index) != null) {
               
                imageLabel.setIcon(images.get(index));
            }
            
          
            switch (index) {
                case 0:
                    line1Label.setText("WELCOME TO ExploBD");
                    line2Label.setText("Unveiling the Soul of Bengal. Journey through the land of stories.");
                    break;
                case 1:
                    line1Label.setText("Find your next favorite spot.");
                    line2Label.setText("Search by food, history or nature");
                    break;
                case 2:
                    line1Label.setText("Create profiles, form groups, share destinations");
                    line2Label.setText("and plan trips as a team");
                    break;
                case 3:
                    line1Label.setText("Get rule-based travel recommendations");
                    line2Label.setText("based on Group preferences and budget");
                    break;
                case 4:
                    line1Label.setText("Track who paid what and settle");
                    line2Label.setText("expenses transparently");
                    break;
            }
            
          
            resizeComponents();
        }
    }

    private void startIntroSequence() {
        int slideTime = 2000;
        int totalTime = 10000;

        imageTimer = new Timer(slideTime, e -> {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            updateContent(currentImageIndex);
        });

        int progressSteps = 100;
        int progressInterval = totalTime / progressSteps;

        progressTimer = new Timer(progressInterval, e -> {
            int currentValue = progressBar.getValue();
            if (currentValue < 100) {
                progressBar.setValue(currentValue + 1);
                progressBar.setString((currentValue + 1) + "%");
                progressBar.repaint();
            } else {
                progressTimer.stop();
            }
        });

        navigationTimer = new Timer(totalTime, e -> {
            imageTimer.stop();
            progressTimer.stop();
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        });

        imageTimer.start();
        progressTimer.start();
        navigationTimer.setRepeats(false);
        navigationTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Intro();
        });
    }
}