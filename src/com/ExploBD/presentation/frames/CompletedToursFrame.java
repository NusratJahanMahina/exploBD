package com.ExploBD.presentation.frames;

import com.ExploBD.data.databaseObject.CompletedTourDatabaseObject;
import com.ExploBD.domain.entities.CompletedTour;
import com.ExploBD.domain.entities.CompletedTourParticipant;
import com.ExploBD.object.User;
import com.ExploBD.session.UserSession;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CompletedToursFrame extends JFrame {
    
    private User currentUser;
    private CompletedTourDatabaseObject completedTourDAO;
    
    // Colors matching your theme
    Color ABS_BG = new Color(150, 138, 127);
    Color TOP_BG = new Color(88, 74, 60);
    Color FRIENDS_COLOR = new Color(245, 180, 60);
    Color X_BG = new Color(255, 192, 0);
    
    private JList<CompletedTour> tourList;
    private DefaultListModel<CompletedTour> listModel;
    private JTextArea detailArea;
    private JLabel countLabel;
    
    public CompletedToursFrame(User user) {
        this.currentUser = user;
        this.completedTourDAO = new CompletedTourDatabaseObject();
        
        setTitle("Completed Tours - " + user.getDisplayName());
        setSize(950, 650);
        setPreferredSize(new Dimension(950, 650));
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(ABS_BG);
        
        // Top Bar
        JPanel topBar = createTopBar();
        mainWrapper.add(topBar, BorderLayout.NORTH);
        
        // Main split pane - Tour List on left, Details on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);
        
        // Left Panel - Tour List
        JPanel leftPanel = createTourListPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right Panel - Tour Details
        JPanel rightPanel = createDetailPanel();
        splitPane.setRightComponent(rightPanel);
        
        mainWrapper.add(splitPane, BorderLayout.CENTER);
        
        // Bottom Panel with refresh button
        JPanel bottomPanel = createBottomPanel();
        mainWrapper.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainWrapper);
        
        // Load tours
        loadTours();
        
        setVisible(true);
    }
    
    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(TOP_BG);
        top.setBorder(new LineBorder(Color.BLACK, 2));
        
        JLabel title = new JLabel("COMPLETED TOURS", SwingConstants.CENTER);
        title.setForeground(FRIENDS_COLOR);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        top.add(title, BorderLayout.NORTH);
        
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(TOP_BG);
        row.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        
        JLabel infoLabel = new JLabel("Your travel memories");
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        
        JButton refreshBtn = new JButton("🔄");
        refreshBtn.setBackground(X_BG);
        refreshBtn.setForeground(Color.BLACK);
        refreshBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadTours());
        
        JButton closeBtn = new JButton("X");
        closeBtn.setBackground(X_BG);
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        
        rightPanel.add(refreshBtn);
        rightPanel.add(closeBtn);
        
        row.add(infoLabel, BorderLayout.WEST);
        row.add(rightPanel, BorderLayout.EAST);
        top.add(row, BorderLayout.SOUTH);
        
        return top;
    }
    
    private JPanel createTourListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TOP_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FRIENDS_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header with count
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(TOP_BG);
        
        JLabel title = new JLabel("  📜 YOUR TOURS");
        title.setForeground(FRIENDS_COLOR);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        
        countLabel = new JLabel("0 tours");
        countLabel.setForeground(FRIENDS_COLOR);
        countLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tour list
        listModel = new DefaultListModel<>();
        tourList = new JList<>(listModel);
        tourList.setBackground(TOP_BG);
        tourList.setForeground(Color.WHITE);
        tourList.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        tourList.setSelectionBackground(FRIENDS_COLOR);
        tourList.setSelectionForeground(Color.BLACK);
        tourList.setCellRenderer(new TourListCellRenderer());
        
        tourList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                CompletedTour selected = tourList.getSelectedValue();
                if (selected != null) {
                    showTourDetails(selected);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tourList);
        scrollPane.setBackground(TOP_BG);
        scrollPane.getViewport().setBackground(TOP_BG);
        scrollPane.setBorder(null);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TOP_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FRIENDS_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel title = new JLabel("  📋 TOUR DETAILS");
        title.setForeground(FRIENDS_COLOR);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setBackground(new Color(70, 58, 47));
        detailArea.setForeground(Color.WHITE);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(detailArea);
        scrollPane.setBackground(TOP_BG);
        scrollPane.getViewport().setBackground(TOP_BG);
        scrollPane.setBorder(null);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Initial message
        detailArea.setText("Select a tour from the list to view details");
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(TOP_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        JButton refreshButton = new JButton("🔄 Refresh List");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 11));
        refreshButton.setBackground(FRIENDS_COLOR);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        refreshButton.addActionListener(e -> loadTours());
        
        panel.add(refreshButton);
        
        return panel;
    }
    
    private void loadTours() {
        listModel.clear();
        List<CompletedTour> tours = completedTourDAO.getUserCompletedTours(currentUser.getUserId());
        
        if (tours == null || tours.isEmpty()) {
            countLabel.setText("0 tours");
            detailArea.setText("No completed tours yet.\n\nComplete a tour with Expense Split to see it here.");
        } else {
            countLabel.setText(tours.size() + " tours");
            for (CompletedTour tour : tours) {
                listModel.addElement(tour);
            }
        }
        
        System.out.println("Loaded " + (tours != null ? tours.size() : 0) + " completed tours");
    }
    
    private void showTourDetails(CompletedTour tour) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("╔═══════════════════════════════════════════════════╗\n");
        sb.append("║                 TOUR INFORMATION                  ║\n");
        sb.append("╚═══════════════════════════════════════════════════╝\n\n");
        
        sb.append("📌 TOUR NAME: ").append(tour.getTourName()).append("\n");
        sb.append("📍 DESTINATION: ").append(tour.getDestinationName()).append("\n\n");
        
        if (tour.getStartDate() != null && !tour.getStartDate().isEmpty()) {
            sb.append("📅 DATES: ").append(tour.getStartDate());
            if (tour.getEndDate() != null && !tour.getEndDate().isEmpty()) {
                sb.append(" → ").append(tour.getEndDate());
            }
            sb.append("\n\n");
        }
        
        sb.append("💰 TOTAL EXPENSES: $").append(String.format("%,.2f", tour.getTotalExpenses())).append("\n");
        sb.append("👥 TOTAL MEMBERS: ").append(tour.getMemberCount()).append(" people\n\n");
        
        if (tour.getBudgetMin() > 0 || tour.getBudgetMax() > 0) {
            sb.append("💵 BUDGET RANGE: ").append(tour.getBudgetMin()).append(" - ").append(tour.getBudgetMax()).append(" tk per person\n\n");
        }
        
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("                  PARTICIPANTS\n");
        sb.append("───────────────────────────────────────────────────\n\n");
        
        List<CompletedTourParticipant> participants = completedTourDAO.getTourParticipants(tour.getCompletedId());
        
        if (participants == null || participants.isEmpty()) {
            sb.append("  No participant data available\n");
        } else {
            for (CompletedTourParticipant p : participants) {
                sb.append("  👤 ").append(p.getUserName());
                if (p.getAmountOwed() > 0.01) {
                    sb.append(String.format("  → Received: $%.2f\n", p.getAmountOwed()));
                } else if (p.getAmountPaid() > 0.01) {
                    sb.append(String.format("  → Paid: $%.2f\n", p.getAmountPaid()));
                } else {
                    sb.append("  → Settled\n");
                }
            }
        }
        
        sb.append("\n───────────────────────────────────────────────────\n");
        sb.append("  ✅ Completed on: ").append(tour.getCompletedDate()).append("\n");
        sb.append("═══════════════════════════════════════════════════\n");
        
        detailArea.setText(sb.toString());
    }
    
    // Custom cell renderer for the tour list
    private class TourListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof CompletedTour) {
                CompletedTour tour = (CompletedTour) value;
                String displayText = tour.getTourName() + " - " + tour.getDestinationName();
                setText(displayText);
            }
            
            if (isSelected) {
                setBackground(FRIENDS_COLOR);
                setForeground(Color.BLACK);
            } else {
                setBackground(TOP_BG);
                setForeground(Color.WHITE);
            }
            
            setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            
            return this;
        }
    }
}