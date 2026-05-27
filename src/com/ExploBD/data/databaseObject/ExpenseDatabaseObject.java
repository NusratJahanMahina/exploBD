package com.ExploBD.data.databaseObject;
import java.sql.SQLIntegrityConstraintViolationException;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.domain.entities.Expense;
import com.ExploBD.domain.entities.ExpenseSettlement;
import com.ExploBD.domain.entities.Tour;
import com.ExploBD.object.User;


import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ExpenseDatabaseObject {
    
    private UserDatabaseObject userDAO;
    
    public ExpenseDatabaseObject() {
        this.userDAO = new UserDatabaseObject();
    }
    
    /**
     * Create a new active tour
     */
    public boolean createTour(Tour tour) throws SQLException {
        String sql = """
            INSERT INTO tours (tour_id, name, description, start_date, end_date, is_active, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tour.getId());
            pstmt.setString(2, tour.getName());
            pstmt.setString(3, tour.getDescription());
            pstmt.setString(4, tour.getStartDate() != null ? tour.getStartDate().toString() : null);
            pstmt.setString(5, tour.getEndDate() != null ? tour.getEndDate().toString() : null);
            pstmt.setInt(6, tour.isActive() ? 1 : 0);
            pstmt.setString(7, tour.getCreatedBy().getUserId());
            pstmt.executeUpdate();
            /*
            // Add creator as participant
           addTourParticipant(conn, tour.getId(), tour.getCreatedBy().getUserId(), "LEADER");
            
            // Add other participants
            for (User participant : tour.getParticipants()) {
                addTourParticipant(conn, tour.getId(), participant.getUserId(), "MEMBER");
            }*/
            // Add creator as participant (with LEADER role)
addTourParticipant(conn, tour.getId(), tour.getCreatedBy().getUserId(), "LEADER");

// Add other participants (excluding the creator to avoid duplicate)
for (User participant : tour.getParticipants()) {
    // Skip if this participant is the creator (already added)
    if (!participant.getUserId().equals(tour.getCreatedBy().getUserId())) {
        addTourParticipant(conn, tour.getId(), participant.getUserId(), "MEMBER");
    }
}
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    

private void addTourParticipant(Connection conn, String tourId, String userId, String role) throws SQLException {
    String sql = "INSERT INTO tour_participants (tour_id, user_id, role) VALUES (?, ?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, tourId);
        pstmt.setString(2, userId);
        pstmt.setString(3, role);
        pstmt.executeUpdate();
    } catch (SQLIntegrityConstraintViolationException e) {
        // Ignore duplicate entries
        System.out.println("User already in tour, skipping: " + userId);
    }
}
    
    /**
     * Get active tour for a user
     */
    public Tour getActiveTour(String userId) throws SQLException {
        String sql = """
            SELECT t.* FROM tours t
            INNER JOIN tour_participants tp ON t.tour_id = tp.tour_id
            WHERE tp.user_id = ? AND t.is_active = 1
            ORDER BY t.created_at DESC LIMIT 1
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return buildTourFromResultSet(rs, conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
    
    /**
     * Get all participants in a tour
     */
    public List<User> getTourParticipants(String tourId) throws SQLException {
        List<User> participants = new ArrayList<>();
        String sql = """
            SELECT u.user_id FROM users u
            INNER JOIN tour_participants tp ON u.user_id = tp.user_id
            WHERE tp.tour_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tourId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = userDAO.findById(rs.getString("user_id"));
                if (user != null) {
                    participants.add(user);
                }
            }
        }
        return participants;
    }
    
 
public List<ExpenseSettlement> calculateSettlements(Map<User, Double> balances) {
    List<ExpenseSettlement> settlements = new ArrayList<>();
    
    if (balances == null || balances.isEmpty()) {
        System.out.println("Balances is null or empty");
        return settlements;
    }
    
    // Calculate total and equal share
    double total = 0;
    List<User> users = new ArrayList<>();
    
    for (Map.Entry<User, Double> entry : balances.entrySet()) {
        if (entry.getKey() != null && entry.getValue() != null) {
            users.add(entry.getKey());
            total += entry.getValue();
            System.out.println(entry.getKey().getDisplayName() + " paid: $" + entry.getValue());
        }
    }
    
    if (users.isEmpty()) {
        return settlements;
    }
    
    double equalShare = total / users.size();
    System.out.println("Total: $" + total + ", Each should pay: $" + equalShare);
    
    // Calculate difference
    Map<User, Double> diff = new HashMap<>();
    for (Map.Entry<User, Double> entry : balances.entrySet()) {
        double difference = entry.getValue() - equalShare;
        diff.put(entry.getKey(), difference);
        System.out.println(entry.getKey().getDisplayName() + " difference: $" + difference);
    }
    
    // Create settlements: people with negative difference pay people with positive difference
    List<User> debtors = new ArrayList<>();
    List<User> creditors = new ArrayList<>();
    
    for (Map.Entry<User, Double> entry : diff.entrySet()) {
        if (entry.getValue() < -0.01) {
            debtors.add(entry.getKey());
        } else if (entry.getValue() > 0.01) {
            creditors.add(entry.getKey());
        }
    }
    
    // For each debtor, pay each creditor proportionally
    for (User debtor : debtors) {
        double debtAmount = -diff.get(debtor);
        for (User creditor : creditors) {
            double creditAmount = diff.get(creditor);
            if (debtAmount > 0.01 && creditAmount > 0.01) {
                double payAmount = Math.min(debtAmount, creditAmount);
                String settlementId = "STL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                ExpenseSettlement settlement = new ExpenseSettlement(
                    settlementId,
                    debtor,
                    creditor,
                    Math.round(payAmount * 100.0) / 100.0
                );
                settlements.add(settlement);
                System.out.println(debtor.getDisplayName() + " pays " + creditor.getDisplayName() + ": $" + payAmount);
                
                // Update amounts
                diff.put(debtor, diff.get(debtor) + payAmount);
                diff.put(creditor, diff.get(creditor) - payAmount);
                debtAmount -= payAmount;
            }
        }
    }
    
    System.out.println("Total settlements created: " + settlements.size());
    return settlements;
}

// Get tour by ID (including completed tours)
public Tour getTourById(String tourId) throws SQLException {
    String sql = "SELECT * FROM tours WHERE tour_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, tourId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return buildTourFromResultSet(rs, conn);
        }
    }
    return null;
}
    public boolean addExpense(Expense expense) throws SQLException {
    System.out.println("=== ADDING EXPENSE ===");
    System.out.println("Amount: " + expense.getAmount());
    System.out.println("Description: " + expense.getDescription());
    System.out.println("Paid by: " + expense.getPaidBy().getDisplayName());
    System.out.println("Tour ID: " + expense.getTour().getId());
    
    Connection conn = null;
    PreparedStatement expenseStmt = null;
    PreparedStatement splitStmt = null;
    
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        
        // Get participants if not set
        List<User> participants = expense.getParticipants();
        if (participants.isEmpty()) {
            participants = getTourParticipants(expense.getTour().getId());
            expense.setParticipants(participants);
        }
        
        System.out.println("Participants count: " + participants.size());
        
        // Calculate share
        double shareAmount = expense.getAmount() / participants.size();
        System.out.println("Share amount per person: " + shareAmount);
        
        // Insert expense
        String expenseSql = """
            INSERT INTO expenses (expense_id, tour_id, paid_by, amount, description, expense_date)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        expenseStmt = conn.prepareStatement(expenseSql);
        expenseStmt.setString(1, expense.getId());
        expenseStmt.setString(2, expense.getTour().getId());
        expenseStmt.setString(3, expense.getPaidBy().getUserId());
        expenseStmt.setDouble(4, expense.getAmount());
        expenseStmt.setString(5, expense.getDescription());
        expenseStmt.setString(6, expense.getExpenseDate().toString());
        
        int expenseResult = expenseStmt.executeUpdate();
        System.out.println("Expense inserted: " + expenseResult);
        
        // Insert splits
        String splitSql = """
            INSERT INTO expense_splits (split_id, expense_id, user_id, share_amount)
            VALUES (?, ?, ?, ?)
            """;
        splitStmt = conn.prepareStatement(splitSql);
        
        int splitCount = 0;
        for (User participant : participants) {
            if (participant.equals(expense.getPaidBy())) continue;
            
            String splitId = "SPLIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            splitStmt.setString(1, splitId);
            splitStmt.setString(2, expense.getId());
            splitStmt.setString(3, participant.getUserId());
            splitStmt.setDouble(4, shareAmount);
            splitStmt.addBatch();
            splitCount++;
        }
        
        int[] splitResults = splitStmt.executeBatch();
        System.out.println("Splits inserted: " + splitResults.length);
        
        conn.commit();
        System.out.println("Expense added successfully!");
        return true;
        
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back due to error");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        System.err.println("SQL Error adding expense: " + e.getMessage());
        e.printStackTrace();
        throw e;
    } finally {
        if (expenseStmt != null) {
            try { expenseStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (splitStmt != null) {
            try { splitStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (conn != null) {
            try { 
                conn.setAutoCommit(true);
                conn.close(); 
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
   
     // Calculate settlements (who pays whom)
     
    public List<Expense> getTourExpenses(Tour tour) throws SQLException {
    List<Expense> expenses = new ArrayList<>();
    String sql = "SELECT * FROM expenses WHERE tour_id = ? ORDER BY expense_date DESC";
    
    System.out.println("Getting expenses for tour: " + tour.getId());
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, tour.getId());
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            String paidById = rs.getString("paid_by");
            User paidBy = userDAO.findById(paidById);
            
            if (paidBy == null) {
                System.err.println("User not found for paid_by: " + paidById);
                // Try to find the user from tour participants
                for (User p : tour.getParticipants()) {
                    if (p.getUserId().equals(paidById)) {
                        paidBy = p;
                        break;
                    }
                }
                if (paidBy == null) {
                    System.err.println("Could not find user: " + paidById + " - skipping expense");
                    continue;
                }
            }
            
            Expense expense = new Expense(
                rs.getString("expense_id"),
                tour,
                paidBy,
                rs.getDouble("amount"),
                rs.getString("description")
            );
            expense.setExpenseDate(LocalDateTime.parse(rs.getString("expense_date")));
            expenses.add(expense);
            System.out.println("Found expense: " + expense.getDescription() + " - $" + expense.getAmount() + " paid by " + paidBy.getDisplayName());
        }
    }
    
    System.out.println("Total expenses found: " + expenses.size());
    return expenses;
}
   public Map<User, Double> calculateBalances(Tour tour) throws SQLException {
    Map<User, Double> balances = new HashMap<>();
    
    // Initialize balances for ALL participants
    List<User> allParticipants = tour.getParticipants();
    System.out.println("Initializing balances for " + allParticipants.size() + " participants");
    
    for (User user : allParticipants) {
        balances.put(user, 0.0);
        System.out.println("  - " + user.getDisplayName() + ": $0.00");
    }
    
    // Get all expenses
    List<Expense> expenses = getTourExpenses(tour);
    System.out.println("Processing " + expenses.size() + " expenses");
    
    for (Expense expense : expenses) {
        System.out.println("Expense: " + expense.getDescription() + " - $" + expense.getAmount() + " paid by " + expense.getPaidBy().getDisplayName());
        
        // Add full amount to payer (using userId to find the correct map entry)
        User paidBy = expense.getPaidBy();
        boolean paidByFound = false;
        for (Map.Entry<User, Double> entry : balances.entrySet()) {
            if (entry.getKey().getUserId().equals(paidBy.getUserId())) {
                double newBalance = entry.getValue() + expense.getAmount();
                balances.put(entry.getKey(), newBalance);
                System.out.println("  Added to " + paidBy.getDisplayName() + ": +$" + expense.getAmount() + " = $" + newBalance);
                paidByFound = true;
                break;
            }
        }
        if (!paidByFound) {
            System.err.println("WARNING: Payer " + paidBy.getDisplayName() + " not found in balances!");
            balances.put(paidBy, expense.getAmount());
        }
        
        // Get splits for this expense
        String splitSql = "SELECT user_id, share_amount FROM expense_splits WHERE expense_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement splitStmt = conn.prepareStatement(splitSql)) {
            splitStmt.setString(1, expense.getId());
            ResultSet splitRs = splitStmt.executeQuery();
            
            while (splitRs.next()) {
                String userId = splitRs.getString("user_id");
                double share = splitRs.getDouble("share_amount");
                
                // Find the user in balances by userId
                boolean userFound = false;
                for (Map.Entry<User, Double> entry : balances.entrySet()) {
                    if (entry.getKey().getUserId().equals(userId)) {
                        double newBalance = entry.getValue() - share;
                        balances.put(entry.getKey(), newBalance);
                        System.out.println("  Subtracted from " + entry.getKey().getDisplayName() + ": -$" + share + " = $" + newBalance);
                        userFound = true;
                        break;
                    }
                }
                if (!userFound) {
                    // User not in balances, add them
                    User user = userDAO.findById(userId);
                    if (user != null) {
                        balances.put(user, -share);
                        System.out.println("  Added new user " + user.getDisplayName() + " with -$" + share);
                    }
                }
            }
            splitRs.close();
        }
    }
    
    // Final balances
    System.out.println("=== FINAL BALANCES ===");
    for (Map.Entry<User, Double> entry : balances.entrySet()) {
        System.out.println(entry.getKey().getDisplayName() + ": $" + String.format("%.2f", entry.getValue()));
    }
    
    return balances;
}
   
    
  
    private Tour buildTourFromResultSet(ResultSet rs, Connection conn) throws SQLException {
    String tourId = rs.getString("tour_id");
    User createdBy = userDAO.findById(rs.getString("created_by"));
    
    Tour tour = new Tour(
        tourId,
        rs.getString("name"),
        createdBy
    );
    tour.setDescription(rs.getString("description"));
    if (rs.getString("start_date") != null) {
        tour.setStartDate(LocalDate.parse(rs.getString("start_date")));
    }
    if (rs.getString("end_date") != null) {
        tour.setEndDate(LocalDate.parse(rs.getString("end_date")));
    }
    tour.setActive(rs.getInt("is_active") == 1);
    
    // Get participants and also make sure the creator is in the list
    List<User> participants = getTourParticipants(tourId);
    
    // If creator is not in participants, add them
    boolean creatorInList = false;
    for (User p : participants) {
        if (p.getUserId().equals(createdBy.getUserId())) {
            creatorInList = true;
            break;
        }
    }
    if (!creatorInList && createdBy != null) {
        participants.add(createdBy);
        System.out.println("Added creator to participants list: " + createdBy.getDisplayName());
    }
    
    tour.setParticipants(participants);
    
    // Debug print
    System.out.println("Tour loaded with " + tour.getParticipants().size() + " participants:");
    for (User p : tour.getParticipants()) {
        System.out.println("  - " + p.getDisplayName() + " (ID: " + p.getUserId() + ")");
    }
    
    return tour;
}
/**
 * Get completed tours for a user (is_active = 0)
 */
    
public List<Tour> getCompletedTours(String userId) throws SQLException {
    List<Tour> tours = new ArrayList<>();
    String sql = """
        SELECT t.* FROM tours t
        INNER JOIN tour_participants tp ON t.tour_id = tp.tour_id
        WHERE tp.user_id = ? AND t.is_active = 0
        ORDER BY t.end_date DESC
        """;
    
    System.out.println("Getting completed tours for user: " + userId);
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, userId);
        ResultSet rs = pstmt.executeQuery();
        
        int count = 0;
        while (rs.next()) {
            Tour tour = buildTourFromResultSet(rs, conn);
            tours.add(tour);
            count++;
            System.out.println("Found completed tour: " + tour.getName() + " (ID: " + tour.getId() + ")");
        }
        System.out.println("Total completed tours found: " + count);
    } catch (SQLException e) {
        System.err.println("SQL Error in getCompletedTours: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
    return tours;
}
public void endTour(Tour tour) throws SQLException {
    String sql = "UPDATE tours SET is_active = 0, end_date = ? WHERE tour_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        String endDate = LocalDate.now().toString();
        pstmt.setString(1, endDate);
        pstmt.setString(2, tour.getId());
        int rowsUpdated = pstmt.executeUpdate();
        System.out.println("Ending tour " + tour.getId() + ", rows updated: " + rowsUpdated);
        System.out.println("End date set to: " + endDate);
    }
}
    
}