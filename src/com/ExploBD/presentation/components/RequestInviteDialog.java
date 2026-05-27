package com.ExploBD.presentation.components;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.InviteRequestMessage;
import com.ExploBD.service.GroupService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RequestInviteDialog extends JDialog {
    
    private User currentUser;
    private Group group;
    private GroupService groupService;
    private boolean requestSubmitted = false;
    
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> typeCombo;
    
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    
    public RequestInviteDialog(Frame parent, User user, Group group) {
        super(parent, "Request to Invite", true);
        this.currentUser = user;
        this.group = group;
        this.groupService = new GroupService();
        
        initComponents();
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Request to Invite Someone");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        titleLabel.setForeground(DARK_BROWN);
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        panel.add(new JSeparator(), gbc);
        
        
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        nameLabel.setForeground(DARK_BROWN);
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(15);
        nameField.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        panel.add(nameField, gbc);
        
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        emailLabel.setForeground(DARK_BROWN);
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(15);
        emailField.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        panel.add(emailField, gbc);
        
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        typeLabel.setForeground(DARK_BROWN);
        panel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"User", "Guest", "Friend"});
        typeCombo.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
        panel.add(typeCombo, gbc);
        
        
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton submitBtn = new JButton("Submit Request");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 12));
        submitBtn.setBackground(ORANGE);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> submitRequest());
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        
        panel.add(buttonPanel, gbc);
        
        add(panel);
    }
    
    private void submitRequest() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            
            InviteRequestMessage created = groupService.createInvitationRequest(
                group.getId(),
                currentUser,
                name,        
                email,
                type
            );
            
            JOptionPane.showMessageDialog(this,
                "✓ Request sent to group leader for approval!\n\n" +
                "You can check the status in the Messages tab.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            requestSubmitted = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isRequestSubmitted() {
        return requestSubmitted;
    }
}