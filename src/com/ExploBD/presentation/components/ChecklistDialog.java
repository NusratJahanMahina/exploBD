package com.ExploBD.presentation.components;

import com.ExploBD.domain.entities.ChecklistItem;
import com.ExploBD.object.User;
import com.ExploBD.service.ChecklistManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChecklistDialog<T> extends JDialog {
     
    private ChecklistManager<T> manager;
    private User currentUser;
    private boolean isLeader;
    private JPanel itemsPanel;
    private JLabel progressLabel;
    
    // Colors
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color GOLD = new Color(255, 215, 0);
    private Color BG_WHITE = new Color(255, 255, 255);
    private Color BORDER_LIGHT = new Color(200, 180, 160);
    private Color GREEN_SUCCESS = new Color(46, 125, 50);
    
    public ChecklistDialog(JFrame parent, String title, ChecklistManager<T> manager, 
                           User currentUser, boolean isLeader) {
        super(parent, title, true);
        this.manager = manager;
        this.currentUser = currentUser;
        this.isLeader = isLeader;
        
        // Larger frame
        setSize(450, 600);
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setUndecorated(true);
        initComponents();
        refreshItems();
    }
    
    private void initComponents() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Items Panel
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BG_WHITE);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(DARK_BROWN);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        if (isLeader) {
            JButton addBtn = new JButton("+ ADD  TASK");
            addBtn.setFont(new Font("Arial", Font.BOLD, 14));
            addBtn.setBackground(ORANGE);
            addBtn.setForeground(Color.WHITE);
            addBtn.setFocusPainted(false);
            addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addBtn.setPreferredSize(new Dimension(150, 42));
            addBtn.addActionListener(e -> showAddItemDialog());
            bottomPanel.add(addBtn);
        }
        
        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setBackground(DARK_BROWN);
        closeBtn.setForeground(GOLD);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(120, 42));
        closeBtn.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(DARK_BROWN);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("TO-DO LIST");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 22));
        titleLabel.setForeground(GOLD);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(titleLabel);
        
        header.add(Box.createVerticalStrut(12));
        
        progressLabel = new JLabel("0% Complete");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 13));
        progressLabel.setForeground(Color.WHITE);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(progressLabel);
        
        // Progress Bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(GREEN_SUCCESS);
        progressBar.setBackground(new Color(100, 86, 72));
        progressBar.setFont(new Font("Arial", Font.BOLD, 11));
        progressBar.setPreferredSize(new Dimension(350, 20));
        progressBar.setMaximumSize(new Dimension(400, 20));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(Box.createVerticalStrut(8));
        header.add(progressBar);
        header.putClientProperty("progressBar", progressBar);
        
        return header;
    }
    
    private void refreshItems() {
        itemsPanel.removeAll();
        
        List<ChecklistItem<T>> items = manager.getAllItems();
        Map<String, Object> stats = manager.getStatistics();
        
        int totalItems = (int) stats.get("total");
        int userCompleted = (int) stats.get("userCompleted");
        
        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No tasks yet. Click + ADD NEW TASK to get started!");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 13));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
            itemsPanel.add(emptyLabel);
        } else {
            for (ChecklistItem<T> item : items) {
                itemsPanel.add(createTaskRow(item));
                itemsPanel.add(Box.createVerticalStrut(8));
            }
        }
        
        itemsPanel.revalidate();
        itemsPanel.repaint();
        
        // Update header stats
        updateHeaderStats(totalItems, userCompleted);
    }
    
    private JPanel createTaskRow(ChecklistItem<T> item) {
        String itemId = item.getId();
        String itemText = item.getTitle();
        boolean completed = item.isCompletedByUser(currentUser.getUserId());
        int completionCount = item.getCompletionCount();
        
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(12, 0));
        row.setBackground(BG_WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
            BorderFactory.createEmptyBorder(14, 12, 14, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Left: Task text
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_WHITE);
        
        JLabel taskLabel = new JLabel(itemText);
        taskLabel.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
        taskLabel.setForeground(completed ? new Color(150, 150, 150) : DARK_BROWN);
        leftPanel.add(taskLabel, BorderLayout.NORTH);
        
        // Stats (how many completed)
        if (completionCount > 0) {
            JLabel statsLabel = new JLabel("Done by " + completionCount + " member(s)");
            statsLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            statsLabel.setForeground(GREEN_SUCCESS);
            leftPanel.add(statsLabel, BorderLayout.SOUTH);
        }
        
        row.add(leftPanel, BorderLayout.CENTER);
        
        // Right: Checkbox and actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(BG_WHITE);
        
        // Checkbox
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(completed);
        checkBox.setPreferredSize(new Dimension(24, 24));
        checkBox.setBackground(BG_WHITE);
        checkBox.addActionListener(e -> {
            manager.toggleCompletion(itemId);
            refreshItems();
        });
        rightPanel.add(checkBox);
        
        // Leader actions (EDIT and DELETE buttons)
        if (isLeader) {
            JButton editBtn = new JButton("EDIT");
            editBtn.setFont(new Font("Arial", Font.BOLD, 8));
            editBtn.setBackground(new Color(100, 86, 72));
            editBtn.setForeground(GOLD);
            editBtn.setFocusPainted(false);
            editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editBtn.setPreferredSize(new Dimension(65, 32));
            editBtn.setToolTipText("Edit task");
            editBtn.addActionListener(e -> showEditItemDialog(itemId, itemText));
            
            JButton deleteBtn = new JButton("Remove");
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 8));
            deleteBtn.setBackground(new Color(198, 40, 40));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.setPreferredSize(new Dimension(75, 32));
            deleteBtn.setToolTipText("Delete task");
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this task?\n\n\"" + itemText + "\"",
                    "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.removeItem(itemId);
                    refreshItems();
                }
            });
            
            rightPanel.add(editBtn);
            rightPanel.add(deleteBtn);
        }
        
        row.add(rightPanel, BorderLayout.EAST);
        
        return row;
    }
    
    private void showAddItemDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        JLabel label = new JLabel("Enter new task:");
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(DARK_BROWN);
        
        JTextField textField = new JTextField(25);
        textField.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(300, 35));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        
        gbc.gridy = 1;
        panel.add(textField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add New Task", JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String itemText = textField.getText().trim();
            if (!itemText.isEmpty()) {
                String id = "TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                manager.addItem(id, itemText, null);
                refreshItems();
            } else {
                JOptionPane.showMessageDialog(this, "Task cannot be empty!");
            }
        }
    }
    
    private void showEditItemDialog(String itemId, String currentText) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        JLabel label = new JLabel("Edit task:");
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(DARK_BROWN);
        
        JTextField textField = new JTextField(currentText, 25);
        textField.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(300, 35));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        
        gbc.gridy = 1;
        panel.add(textField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Edit Task", JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newText = textField.getText().trim();
            if (!newText.isEmpty()) {
                manager.updateItemTitle(itemId, newText);
                refreshItems();
            } else {
                JOptionPane.showMessageDialog(this, "Task cannot be empty!");
            }
        }
    }
    
    private void updateHeaderStats(int totalItems, int userCompleted) {
        int percentage = totalItems > 0 ? (userCompleted * 100 / totalItems) : 0;
        progressLabel.setText(userCompleted + " / " + totalItems + " tasks completed (" + percentage + "%)");
        
        JProgressBar progressBar = (JProgressBar) ((JPanel) getContentPane().getComponent(0)).getClientProperty("progressBar");
        if (progressBar != null) {
            progressBar.setValue(percentage);
            progressBar.setString(percentage + "%");
        }
    }
}