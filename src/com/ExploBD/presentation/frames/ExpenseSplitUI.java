package com.ExploBD.presentation.frames;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.Expense;
import com.ExploBD.domain.entities.ExpenseSettlement;
import com.ExploBD.domain.entities.Tour;
import com.ExploBD.service.ExpenseService;
import com.ExploBD.session.UserSession;
import com.ExploBD.service.GroupService;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.service.ExpenseService.ExpenseServiceException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ExpenseSplitUI extends JFrame {
    
    private User currentUser;
    private ExpenseService expenseService;
    private GroupService groupService;
    
    // Colors matching your Friends.java
    Color ABS_BG = new Color(150, 138, 127);
    Color TOP_BG = new Color(88, 74, 60);
    Color FRIENDS_COLOR = new Color(245, 180, 60);
    Color X_BG = new Color(255, 192, 0);
    Color SUCCESS_GREEN = new Color(76, 175, 80);
    Color DANGER_RED = new Color(244, 67, 54);
    
    JPanel contentPanel;
    CardLayout cardLayout;
    
    // UI Components
    private JTable expensesTable;
    private DefaultTableModel expensesTableModel;
    private JLabel tourInfoLabel;
    private JLabel totalLabel;
    private JComboBox<User> payerComboBox;
    private JTextField amountField;
    private JTextField descriptionField;
    private JPanel participantsPanel;
    private JTextArea settlementArea;
    private JComboBox<String> groupSelector;
    private Map<String, Group> groupMap = new HashMap<>();
    
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
    
    public ExpenseSplitUI(User user) {
        this.currentUser = user;
        this.expenseService = new ExpenseService();
        this.groupService = new GroupService();
        
        setTitle("Expense Splitter - " + user.getDisplayName());
        setSize(900, 700);
        setPreferredSize(new Dimension(900, 700));
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(ABS_BG);
        
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(createTopBar(), BorderLayout.NORTH);
        northContainer.add(createTabBar(), BorderLayout.SOUTH);
        mainWrapper.add(northContainer, BorderLayout.NORTH);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ABS_BG);
        
        // Create panels
        contentPanel.add(createActiveExpensePanel(), "active");
contentPanel.add(createSettlementPanel(), "settlement");
        
        mainWrapper.add(contentPanel, BorderLayout.CENTER);
        add(mainWrapper);
        
        // Load initial data
        refreshData();
        
        setVisible(true);
    }

    ExpenseSplitUI() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(TOP_BG);
        top.setBorder(new LineBorder(Color.BLACK, 2));
        
        JLabel title = new JLabel("EXPENSE SPLITTER", SwingConstants.CENTER);
        title.setForeground(FRIENDS_COLOR);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        top.add(title, BorderLayout.NORTH);
        
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(TOP_BG);
        row.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        
        tourInfoLabel = new JLabel("No active expense tour");
        tourInfoLabel.setForeground(Color.WHITE);
        tourInfoLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        
        JButton refreshBtn = new JButton("🔄");
        refreshBtn.setBackground(X_BG);
        refreshBtn.setForeground(Color.BLACK);
        refreshBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshData());
        
        JButton closeBtn = new JButton("X");
        closeBtn.setBackground(X_BG);
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        
        rightPanel.add(refreshBtn);
        rightPanel.add(closeBtn);
        
        row.add(tourInfoLabel, BorderLayout.WEST);
        row.add(rightPanel, BorderLayout.EAST);
        top.add(row, BorderLayout.SOUTH);
        
        return top;
    }
   
private JPanel createTabBar() {
    JPanel tabBar = new JPanel(new GridLayout(1, 2, 0, 0));
    tabBar.setPreferredSize(new Dimension(900, 50));
    
    JButton b1 = createTabButton("💰 Active Expense");
    JButton b2 = createTabButton("⚖️ Settlements");
    
    b1.addActionListener(e -> cardLayout.show(contentPanel, "active"));
    b2.addActionListener(e -> {
        cardLayout.show(contentPanel, "settlement");
        loadSettlements();
    });
    
    tabBar.add(b1);
    tabBar.add(b2);
    return tabBar;
}
    
    private JButton createTabButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ABS_BG);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return btn;
    }
    
    private JPanel createActiveExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ABS_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top: Create Tour Section
        JPanel createTourPanel = createCreateTourPanel();
        panel.add(createTourPanel, BorderLayout.NORTH);
        
        // Center: Split into two parts
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        
        // Left: Expense Form and List
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right: Participants
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCreateTourPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TOP_BG);
        panel.setBorder(new LineBorder(FRIENDS_COLOR, 2));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel label = new JLabel("  Create Expense Tour from Group:");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        
        groupSelector = new JComboBox<>();
        groupSelector.setBackground(Color.WHITE);
        groupSelector.setPreferredSize(new Dimension(200, 30));
        
        // Load user's groups
        loadGroupsIntoSelector();
        
        JButton createBtn = new JButton("Create Tour");
        createBtn.setBackground(FRIENDS_COLOR);
        createBtn.setForeground(Color.BLACK);
        createBtn.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        createBtn.addActionListener(e -> createExpenseTour());
        
        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        innerPanel.setBackground(TOP_BG);
        innerPanel.add(label);
        innerPanel.add(groupSelector);
        innerPanel.add(createBtn);
        
        panel.add(innerPanel, BorderLayout.WEST);
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setForeground(FRIENDS_COLOR);
        totalLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(totalLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadGroupsIntoSelector() {
        groupSelector.removeAllItems();
        groupMap.clear();
        
        List<Group> userGroups = groupService.getMyGroups();
        for (Group group : userGroups) {
            String groupName = group.getName();
            String destName = group.getDestinationName();
            String displayName = destName != null ? groupName + " (" + destName + ")" : groupName;
            groupSelector.addItem(displayName);
            groupMap.put(displayName, group);
        }
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ABS_BG);
        
        // Add Expense Form
        JPanel formPanel = createExpenseForm();
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Expenses Table
        JPanel tablePanel = createExpensesTable();
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createExpenseForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(TOP_BG);
        panel.setBorder(new LineBorder(FRIENDS_COLOR, 2));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JLabel title = new JLabel("  ➕ ADD NEW EXPENSE");
        title.setForeground(FRIENDS_COLOR);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.add(title);
        
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Amount field
        fieldsPanel.add(createLabel("Amount ($):"));
        amountField = new JTextField();
        amountField.setBackground(Color.WHITE);
        fieldsPanel.add(amountField);
        
        // Description field
        fieldsPanel.add(createLabel("Description:"));
        descriptionField = new JTextField();
        fieldsPanel.add(descriptionField);
        
        // Paid by combo
        fieldsPanel.add(createLabel("Paid by:"));
        payerComboBox = new JComboBox<>();
        payerComboBox.setBackground(Color.WHITE);
        fieldsPanel.add(payerComboBox);
        
        // Add button
        JButton addButton = new JButton("ADD EXPENSE");
        addButton.setBackground(FRIENDS_COLOR);
        addButton.setForeground(Color.BLACK);
        addButton.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addExpense());
        fieldsPanel.add(new JLabel());
        fieldsPanel.add(addButton);
        
        panel.add(fieldsPanel);
        
        // Participants section
        JLabel participantsLabel = new JLabel("  Split among:");
        participantsLabel.setForeground(Color.WHITE);
        participantsLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        panel.add(participantsLabel);
        
        participantsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        participantsPanel.setOpaque(false);
        participantsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        panel.add(participantsPanel);
        
        return panel;
    }
    
    private JPanel createExpensesTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ABS_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(FRIENDS_COLOR, 1), 
            "📋 EXPENSES",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Comic Sans MS", Font.BOLD, 12),
            FRIENDS_COLOR
        ));
        
        String[] columns = {"Date", "Description", "Amount", "Paid By"};
        expensesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        expensesTable = new JTable(expensesTableModel);
        expensesTable.setBackground(TOP_BG);
        expensesTable.setForeground(Color.WHITE);
        expensesTable.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
        expensesTable.getTableHeader().setBackground(ABS_BG);
        expensesTable.getTableHeader().setForeground(FRIENDS_COLOR);
        expensesTable.getTableHeader().setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        expensesTable.setRowHeight(25);
        expensesTable.setShowGrid(true);
        expensesTable.setGridColor(FRIENDS_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        scrollPane.setBackground(ABS_BG);
        scrollPane.getViewport().setBackground(TOP_BG);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

   
   
    private JPanel createSettlementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ABS_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        settlementArea = new JTextArea();
        settlementArea.setBackground(TOP_BG);
        settlementArea.setForeground(Color.WHITE);
        settlementArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        settlementArea.setEditable(false);
        settlementArea.setBorder(new LineBorder(FRIENDS_COLOR, 2));
        
        JScrollPane scrollPane = new JScrollPane(settlementArea);
        scrollPane.setBackground(ABS_BG);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        return label;
    }
    
private void refreshData() {
    Tour activeTour = null;
    try {
        activeTour = expenseService.getActiveTour();
    } catch (ExpenseService.ExpenseServiceException ex) {
        System.out.println("Error getting active tour: " + ex.getMessage());
    }
    
    if (activeTour != null) {
        tourInfoLabel.setText("  🏕️ Active Tour: " + activeTour.getName());
        
        // Load participants
        List<User> participants = activeTour.getParticipants();
        System.out.println("DEBUG: Found " + participants.size() + " participants for tour");
        
        // Print each participant
        for (User u : participants) {
            System.out.println("  - " + u.getDisplayName());
        }
        
        updateParticipantsList(participants);
        
        // Load expenses
        List<Expense> expenses = expenseService.getActiveTourExpenses();
        updateExpensesTable(expenses);
        
        // Update total
        double total = expenseService.getActiveTourTotal();
        totalLabel.setText(String.format("Total: $%.2f", total));
    } else {
        tourInfoLabel.setText("  🏕️ No active tour - Create one from your groups");
        totalLabel.setText("Total: $0.00");
        updateParticipantsList(new ArrayList<>());
        expensesTableModel.setRowCount(0);
    }
}
    
 
    
    //nusrat
    
private void updateParticipantsList(List<User> participants) {
    System.out.println("Updating participants list. Count: " + (participants != null ? participants.size() : 0));
    
    // Clear and update the split among checkboxes (left panel)
    participantsPanel.removeAll();
    
    if (participants == null || participants.isEmpty()) {
        JLabel emptyLabel = new JLabel("  No participants found");
        emptyLabel.setForeground(Color.WHITE);
        emptyLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        participantsPanel.add(emptyLabel);
    } else {
        // Add checkboxes for each participant
        for (User user : participants) {
            JCheckBox cb = new JCheckBox(user.getDisplayName());
            cb.setBackground(TOP_BG);
            cb.setForeground(Color.WHITE);
            cb.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
            cb.setSelected(true);  // Select all by default
            cb.setActionCommand(user.getUserId());
            participantsPanel.add(cb);
            System.out.println("Added checkbox for: " + user.getDisplayName());
        }
    }
    
    // Refresh the panel
    participantsPanel.revalidate();
    participantsPanel.repaint();
    
    // Also update the payer combo box
    updatePayerComboBox(participants);
}  
    private JPanel createRightPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(ABS_BG);
    
    // Participants list
    JPanel participantsList = createParticipantsList();
    panel.add(participantsList, BorderLayout.CENTER);
    
    // End Tour button
    JButton endTourButton = new JButton("🏁 END TOUR & CALCULATE");
    endTourButton.setBackground(DANGER_RED);
    endTourButton.setForeground(Color.WHITE);
    endTourButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
    endTourButton.setFocusPainted(false);
    endTourButton.setBorder(new LineBorder(Color.BLACK, 1));
    endTourButton.addActionListener(e -> {
        endTour();
    });
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setOpaque(false);
    buttonPanel.add(endTourButton);
    panel.add(buttonPanel, BorderLayout.SOUTH);
    
    return panel;
}
    private JPanel createParticipantsList() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(TOP_BG);
    panel.setBorder(new LineBorder(FRIENDS_COLOR, 2));
    
    JLabel title = new JLabel("  👥 TOUR PARTICIPANTS");
    title.setForeground(FRIENDS_COLOR);
    title.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
    title.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
    panel.add(title, BorderLayout.NORTH);
    
    JPanel listPanel = new JPanel();
    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
    listPanel.setBackground(TOP_BG);
    
    JScrollPane scrollPane = new JScrollPane(listPanel);
    scrollPane.setBackground(TOP_BG);
    scrollPane.getViewport().setBackground(TOP_BG);
    scrollPane.setBorder(null);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    panel.add(scrollPane, BorderLayout.CENTER);
    
    // Store reference to the list panel
    panel.putClientProperty("participantsListPanel", listPanel);
    
    return panel;
}
    
    //till this
    
    
    
    
    
    
    
    
//   
    private void updatePayerComboBox(List<User> participants) {
        payerComboBox.removeAllItems();
        if (participants != null) {
            for (User user : participants) {
                payerComboBox.addItem(user);
            }
        }
    }
    
    private void updateExpensesTable(List<Expense> expenses) {
        expensesTableModel.setRowCount(0);
        
        if (expenses != null) {
            for (Expense expense : expenses) {
                String dateStr = expense.getExpenseDate().format(timeFormatter);
                expensesTableModel.addRow(new Object[]{
                    dateStr,
                    expense.getDescription(),
                    String.format("$%.2f", expense.getAmount()),
                    expense.getPaidBy().getDisplayName()
                });
            }
        }
    }
    
    private void createExpenseTour() {
        int selectedIndex = groupSelector.getSelectedIndex();
        if (selectedIndex == -1 || groupSelector.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No groups available! Create a group first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedDisplayName = (String) groupSelector.getSelectedItem();
        Group selectedGroup = groupMap.get(selectedDisplayName);
        
        if (selectedGroup == null) {
            JOptionPane.showMessageDialog(this, "Group not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String tourName = JOptionPane.showInputDialog(this, "Enter tour name:", selectedGroup.getName() + " Expenses");
        if (tourName == null || tourName.trim().isEmpty()) return;
        
        boolean success = false;
        try {
            success = expenseService.createTourFromGroup(selectedGroup.getId(), tourName);
        } catch (ExpenseService.ExpenseServiceException ex) {
            System.getLogger(ExpenseSplitUI.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Expense tour created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create expense tour!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
   
    


private void addExpense() {
    try {
        Tour activeTour = expenseService.getActiveTour();
        if (activeTour == null) {
            JOptionPane.showMessageDialog(this, "No active expense tour! Create one first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get amount
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get description
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a description!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get who paid
        User paidBy = (User) payerComboBox.getSelectedItem();
        if (paidBy == null) {
            JOptionPane.showMessageDialog(this, "Please select who paid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get selected participants from checkboxes
        List<User> selectedParticipants = new ArrayList<>();
        for (Component comp : participantsPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox cb = (JCheckBox) comp;
                if (cb.isSelected()) {
                    String userId = cb.getActionCommand();
                    for (User participant : activeTour.getParticipants()) {
                        if (participant.getUserId().equals(userId)) {
                            selectedParticipants.add(participant);
                            break;
                        }
                    }
                }
            }
        }
        
        if (selectedParticipants.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one participant to split with!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add the expense
        boolean success = expenseService.addExpense(amount, description, paidBy, selectedParticipants);
        
        if (success) {
            // Clear form
            amountField.setText("");
            descriptionField.setText("");
            
            // Refresh the table immediately
            refreshExpenseTableOnly();
            
            // Update total
            double total = expenseService.getActiveTourTotal();
            totalLabel.setText(String.format("Total: $%.2f", total));
            
            JOptionPane.showMessageDialog(this, "Expense added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add expense!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (ExpenseService.ExpenseServiceException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
private void endTour() {
    try {
        Tour activeTour = expenseService.getActiveTour();
        if (activeTour == null) {
            JOptionPane.showMessageDialog(this, "No active expense tour to end!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if there are any expenses
        List<Expense> expenses = expenseService.getActiveTourExpenses();
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expenses added yet! Add some expenses before ending the tour.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to end this tour?\n\n" +
            "Tour: " + activeTour.getName() + "\n" +
            "Total Expenses: $" + expenseService.getActiveTourTotal() + "\n\n" +
            "This will calculate final settlements.",
            "End Tour", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Calculate settlements BEFORE ending the tour
            List<ExpenseSettlement> settlements = expenseService.getActiveTourSettlements();
            
            // Display settlements in the text area
            displaySettlements(settlements);
            
            // End the tour in database
            expenseService.endActiveTour();
            
            // Switch to settlement tab
            cardLayout.show(contentPanel, "settlement");
            
            // Refresh HomeFrame if open
            try {
                for (Frame frame : Frame.getFrames()) {
                    if (frame instanceof HomeFrame && frame.isVisible()) {
                        java.lang.reflect.Method method = frame.getClass().getMethod("refreshCompletedTours");
                        method.invoke(frame);
                        break;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Could not refresh HomeFrame: " + ex.getMessage());
            }
            
            JOptionPane.showMessageDialog(this, 
                "Tour ended successfully!\nCheck the Settlements tab to see who pays whom.", 
                "Tour Ended", 
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshData();
        }
    } catch (ExpenseService.ExpenseServiceException e) {
        JOptionPane.showMessageDialog(this, "Error ending tour: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
} 
    private void refreshExpenseTableOnly() {
    try {
        List<Expense> expenses = expenseService.getActiveTourExpenses();
        expensesTableModel.setRowCount(0);
        
        if (expenses != null) {
            for (Expense expense : expenses) {
                String dateStr = expense.getExpenseDate().format(timeFormatter);
                expensesTableModel.addRow(new Object[]{
                    dateStr,
                    expense.getDescription(),
                    String.format("$%.2f", expense.getAmount()),
                    expense.getPaidBy().getDisplayName()
                });
            }
        }
    } catch (Exception e) {
        System.err.println("Error refreshing expense table: " + e.getMessage());
    }
}
private void loadSettlements() {
    // First try to get active tour settlements
    List<ExpenseSettlement> settlements = expenseService.getActiveTourSettlements();
    
    // If no active tour settlements, try to get last saved settlements
    if (settlements == null || settlements.isEmpty()) {
        settlements = expenseService.getLastSettlements();
        System.out.println("Loading last saved settlements: " + (settlements != null ? settlements.size() : 0));
    }
    
    displaySettlements(settlements);
}
  private void displaySettlements(List<ExpenseSettlement> settlements) {
    StringBuilder sb = new StringBuilder();
    sb.append("══════════════════════════════════════════════════════════\n");
    sb.append("                    FINAL SETTLEMENTS\n");
    sb.append("══════════════════════════════════════════════════════════\n\n");
    
    if (settlements == null || settlements.isEmpty()) {
        sb.append("   ✓ All settled! No payments needed.\n");
        sb.append("   Everyone is square! 🎉\n");
    } else {
        sb.append("   The following people need to pay:\n\n");
        for (ExpenseSettlement s : settlements) {
            if (s != null && s.getFromUser() != null && s.getToUser() != null) {
                sb.append(String.format("   💰 %-15s → %-15s : $%8.2f\n",
                    s.getFromUser().getDisplayName(),
                    s.getToUser().getDisplayName(),
                    s.getAmount()));
            } else {
                sb.append("   💰 Invalid settlement entry\n");
            }
        }
        sb.append("\n──────────────────────────────────────────────────────\n");
        
        double total = 0;
        for (ExpenseSettlement s : settlements) {
            if (s != null) {
                total += s.getAmount();
            }
        }
        sb.append(String.format("   Total amount to settle: $%.2f\n", total));
        sb.append("──────────────────────────────────────────────────────\n");
        
        sb.append("\n   💡 Instructions:\n");
        sb.append("   - Pay the amount shown above to the respective person\n");
        sb.append("   - Mark as paid once settled\n");
    }
    
    settlementArea.setText(sb.toString());
    System.out.println("Displayed " + (settlements != null ? settlements.size() : 0) + " settlements");
}
    
}