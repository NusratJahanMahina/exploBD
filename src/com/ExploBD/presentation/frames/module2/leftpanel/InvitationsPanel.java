package com.ExploBD.presentation.frames.module2.leftpanel;

import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InvitationsPanel extends JPanel {
    
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private JPanel container;
    private JLabel countLabel;
    
    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;
    
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private int baseSubtitleSize = 14;
    private int baseNormalSize = 12;
    
    public InvitationsPanel(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        
        setLayout(new BorderLayout());
        setBackground(SOFT_WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SOFT_WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JLabel headerLabel = new JLabel("PENDING INVITATIONS");
        headerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseSubtitleSize));
        headerLabel.setForeground(DARK_BROWN);
        
        countLabel = new JLabel("0");
        countLabel.setFont(new Font("Arial", Font.BOLD, baseNormalSize));
        countLabel.setForeground(Color.WHITE);
        countLabel.setBackground(ORANGE);
        countLabel.setOpaque(true);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel.setPreferredSize(new Dimension(30, 25));
        countLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(SOFT_WHITE);
        
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        refresh();
    }
    
    public void setSuggestFields(JTextField placeField, JTextField reasonField, JButton submitBtn) {
        this.suggestPlaceField = placeField;
        this.suggestReasonField = reasonField;
        this.suggestSubmitBtn = submitBtn;
    }
    
    public void refresh() {
        container.removeAll();
        
        List<Invitation> invitations = dataManager.getPendingInvitations();
        countLabel.setText(String.valueOf(invitations.size()));
        
        if (invitations.isEmpty()) {
            JLabel emptyLabel = new JLabel("No pending invitations");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
            container.add(emptyLabel);
        } else {
            for (Invitation inv : invitations) {
                InvitationCard card = new InvitationCard(inv, dataManager, photoUtils);
                container.add(card);
                container.add(Box.createVerticalStrut(3));
            }
        }
        
        container.revalidate();
        container.repaint();
    }
}