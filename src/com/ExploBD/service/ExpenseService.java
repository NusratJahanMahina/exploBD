package com.ExploBD.service;

import com.ExploBD.data.databaseObject.ExpenseDatabaseObject;
import com.ExploBD.domain.entities.Expense;
import com.ExploBD.domain.entities.ExpenseSettlement;
import com.ExploBD.domain.entities.Tour;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.object.User;
import com.ExploBD.session.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import javax.swing.JOptionPane;
public class ExpenseService {
    
    private ExpenseDatabaseObject expenseDAO;
    private GroupService groupService;
    private UserSession userSession;
 private List<ExpenseSettlement> lastSettlements; 
   
    
    // Custom exception class for viva demonstration
    public static class ExpenseServiceException extends Exception {
        public ExpenseServiceException(String message) {
            super(message);
        }
        public ExpenseServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public ExpenseService() {
        this.expenseDAO = new ExpenseDatabaseObject();
        this.groupService = new GroupService();
        this.userSession = UserSession.getInstance();
    }
    
    /**
     * Create a new expense tour from a group
     * @throws ExpenseServiceException if tour creation fails
     */
    public boolean createTourFromGroup(String groupId, String tourName) throws ExpenseServiceException {
        // Validate inputs
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new ExpenseServiceException("Group ID cannot be null or empty");
        }
        if (tourName == null || tourName.trim().isEmpty()) {
            throw new ExpenseServiceException("Tour name cannot be null or empty");
        }
        
        try {
            User currentUser = userSession.getCurrentUser();
            if (currentUser == null) {
                throw new ExpenseServiceException("No user is currently logged in");
            }
            
            // Get the group
            Group group = groupService.getGroup(groupId);
            if (group == null) {
                throw new ExpenseServiceException("Group not found with ID: " + groupId);
            }
            
            // Check if there's already an active tour
            Tour existingTour = getActiveTour();
            if (existingTour != null) {
                throw new ExpenseServiceException("You already have an active tour: " + existingTour.getName() + 
                    ". Please end it before creating a new one.");
            }
            
            // Create tour
            String tourId = "TOUR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Tour tour = new Tour(tourId, tourName, currentUser);
            tour.setStartDate(group.getStartDate());
            tour.setEndDate(group.getEndDate());
            tour.setDescription("Expense tracking for group: " + group.getName());
            
            // Add all group members as participants
            // Add all group members as participants
int participantCount = 0;

// FIRST, add the creator (current user)
tour.addParticipant(currentUser);
participantCount++;
System.out.println("Added creator: " + currentUser.getDisplayName());

// Then add other group members
for (GroupMember member : group.getAllMembers()) {
    if (member instanceof RegisteredMember) {
        User user = ((RegisteredMember) member).getUser();
        // Don't add the creator again
        if (!user.getUserId().equals(currentUser.getUserId())) {
            tour.addParticipant(user);
            participantCount++;
            System.out.println("Added participant: " + user.getDisplayName());
        }
    }
}
            
            if (participantCount == 0) {
                throw new ExpenseServiceException("No registered members found in the group to add as participants");
            }
            
            System.out.println("Creating tour with " + participantCount + " participants");
            boolean success = expenseDAO.createTour(tour);
            
            if (!success) {
                throw new ExpenseServiceException("Database operation failed while creating tour");
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("SQL Error in createTourFromGroup: " + e.getMessage());
            e.printStackTrace();
            throw new ExpenseServiceException("Database error while creating tour: " + e.getMessage(), e);
        } catch (ExpenseServiceException e) {
            throw e; // Re-throw custom exceptions
        } catch (Exception e) {
            System.err.println("Unexpected error in createTourFromGroup: " + e.getMessage());
            e.printStackTrace();
            throw new ExpenseServiceException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get the current active expense tour
     * @return Tour object if active tour exists, null otherwise
     * @throws ExpenseServiceException if database error occurs
     */
    public Tour getActiveTour() throws ExpenseServiceException {
        try {
            User currentUser = userSession.getCurrentUser();
            if (currentUser == null) {
                System.out.println("DEBUG: No user logged in");
                return null;
            }
            
            System.out.println("DEBUG: Getting active tour for user: " + currentUser.getUserId());
            Tour tour = expenseDAO.getActiveTour(currentUser.getUserId());
            System.out.println("DEBUG: Active tour result: " + (tour != null ? tour.getName() : "null"));
            
            return tour;
            
        } catch (SQLException e) {
            System.err.println("SQL Error in getActiveTour: " + e.getMessage());
            e.printStackTrace();
            throw new ExpenseServiceException("Failed to retrieve active tour: " + e.getMessage(), e);
        }
    }
    
    /**
     * Add an expense to the active tour
     * @throws ExpenseServiceException if expense addition fails
     */
   public boolean addExpense(double amount, String description, User paidBy, List<User> participants) throws ExpenseServiceException {
    // Validate inputs
    if (amount <= 0) {
        throw new ExpenseServiceException("Amount must be greater than zero");
    }
    if (description == null || description.trim().isEmpty()) {
        throw new ExpenseServiceException("Description cannot be empty");
    }
    if (paidBy == null) {
        throw new ExpenseServiceException("Please select who paid for this expense");
    }
    if (participants == null || participants.isEmpty()) {
        throw new ExpenseServiceException("Please select at least one participant to split with");
    }
    
    try {
        Tour activeTour = getActiveTour();
        if (activeTour == null) {
            throw new ExpenseServiceException("No active expense tour found. Please create a tour first.");
        }
        
        // Find the actual paidBy user from tour participants (match by ID)
        User actualPaidBy = null;
        for (User tourParticipant : activeTour.getParticipants()) {
            if (tourParticipant.getUserId().equals(paidBy.getUserId())) {
                actualPaidBy = tourParticipant;
                break;
            }
        }
        
        // If payer not found, add them
        if (actualPaidBy == null) {
            actualPaidBy = paidBy;
        }
        
        // Find actual participants from tour participants (match by ID)
        List<User> actualParticipants = new ArrayList<>();
        for (User selectedParticipant : participants) {
            for (User tourParticipant : activeTour.getParticipants()) {
                if (tourParticipant.getUserId().equals(selectedParticipant.getUserId())) {
                    actualParticipants.add(tourParticipant);
                    break;
                }
            }
        }
        
        // Make sure payer is in participants
        boolean payerIncluded = false;
        for (User p : actualParticipants) {
            if (p.getUserId().equals(actualPaidBy.getUserId())) {
                payerIncluded = true;
                break;
            }
        }
        if (!payerIncluded) {
            actualParticipants.add(actualPaidBy);
        }
        
        if (actualParticipants.isEmpty()) {
            throw new ExpenseServiceException("No valid participants found for this expense");
        }
        
        String expenseId = "EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Expense expense = new Expense(expenseId, activeTour, actualPaidBy, amount, description);
        expense.setParticipants(actualParticipants);
        expense.splitEqually();
        
        boolean success = expenseDAO.addExpense(expense);
        
        if (!success) {
            throw new ExpenseServiceException("Database operation failed while adding expense");
        }
        
        return true;
        
    } catch (SQLException e) {
        System.err.println("SQL Error in addExpense: " + e.getMessage());
        e.printStackTrace();
        throw new ExpenseServiceException("Database error while adding expense: " + e.getMessage(), e);
    } catch (ExpenseServiceException e) {
        throw e;
    } catch (Exception e) {
        System.err.println("Unexpected error in addExpense: " + e.getMessage());
        e.printStackTrace();
        throw new ExpenseServiceException("Unexpected error: " + e.getMessage(), e);
    }
}
    /**
     * Get all expenses for active tour
     */
    public List<Expense> getActiveTourExpenses() {
        try {
            Tour activeTour = getActiveTour();
            if (activeTour == null) {
                return new ArrayList<>();
            }
            return expenseDAO.getTourExpenses(activeTour);
        } catch (SQLException e) {
            System.err.println("SQL Error in getActiveTourExpenses: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } catch (ExpenseServiceException e) {
            System.err.println("Service Error in getActiveTourExpenses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Calculate balances for active tour
     */
    public Map<User, Double> getActiveTourBalances() {
        try {
            Tour activeTour = getActiveTour();
            if (activeTour == null) {
                return new HashMap<>();
            }
            return expenseDAO.calculateBalances(activeTour);
        } catch (SQLException e) {
            System.err.println("SQL Error in getActiveTourBalances: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        } catch (ExpenseServiceException e) {
            System.err.println("Service Error in getActiveTourBalances: " + e.getMessage());
            return new HashMap<>();
        }
    }
    

  public List<ExpenseSettlement> getActiveTourSettlements() {
    try {
        Tour activeTour = getActiveTour();
        if (activeTour == null) {
            System.out.println("No active tour found for settlements");
            return new ArrayList<>();
        }
        
        System.out.println("=== CALCULATING SETTLEMENTS ===");
        Map<User, Double> balances = expenseDAO.calculateBalances(activeTour);
        
        System.out.println("Balances:");
        for (Map.Entry<User, Double> entry : balances.entrySet()) {
            System.out.println("  " + entry.getKey().getDisplayName() + ": $" + entry.getValue());
        }
        
        List<ExpenseSettlement> settlements = expenseDAO.calculateSettlements(balances);
        
        System.out.println("Settlements created: " + settlements.size());
        for (ExpenseSettlement s : settlements) {
            System.out.println("  " + s.getFromUser().getDisplayName() + " → " + 
                             s.getToUser().getDisplayName() + ": $" + s.getAmount());
        }
        
        return settlements;
        
    } catch (SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>();
    } catch (ExpenseServiceException e) {
        System.err.println("Service Error: " + e.getMessage());
        return new ArrayList<>();
    }
}
    public List<Tour> getCompletedTours() throws ExpenseServiceException {
    try {
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return expenseDAO.getCompletedTours(currentUser.getUserId());
    } catch (SQLException e) {
        System.err.println("SQL Error in getCompletedTours: " + e.getMessage());
        e.printStackTrace();
        throw new ExpenseServiceException("Failed to get completed tours: " + e.getMessage(), e);
    }
}
    /**
     * End the active tour
     * @throws ExpenseServiceException if tour ending fails
     *//*public boolean endActiveTour() throws ExpenseServiceException {
    try {
        Tour activeTour = getActiveTour();
        if (activeTour == null) {
            throw new ExpenseServiceException("No active tour found to end");
        }
        
        // Calculate final settlements before ending
        lastSettlements = getActiveTourSettlements();  // Save to variable
        System.out.println("Final settlements calculated: " + lastSettlements.size() + " settlements");
        
        // Save to completed tours history
        saveCompletedTourToHistory(activeTour, lastSettlements);
        
        // End the tour in database
        expenseDAO.endTour(activeTour);
        
        return true;
        
    } catch (SQLException e) {
        System.err.println("SQL Error in endActiveTour: " + e.getMessage());
        e.printStackTrace();
        throw new ExpenseServiceException("Database error while ending tour: " + e.getMessage(), e);
    } catch (ExpenseServiceException e) {
        throw e;
    }
}*/
     public List<ExpenseSettlement> getLastSettlements() {
    return lastSettlements;
}
     public void saveCompletedTourToHistory(Tour tour, List<ExpenseSettlement> settlements) {
    try {
        System.out.println("=== SAVING TOUR TO HISTORY ===");
        System.out.println("Tour: " + tour.getName());
        System.out.println("Tour ID: " + tour.getId());
        
        // Save to completed_tours table
     //   completedTourDAO.saveCompletedTour(tour, settlements);
        
    } catch (Exception e) {
        System.err.println("Error saving tour history: " + e.getMessage());
        e.printStackTrace();
    }
}
     // Get expenses for a specific tour by ID
public List<Expense> getTourExpenses(String tourId) {
    try {
        Tour tour = expenseDAO.getTourById(tourId);
        if (tour == null) return new ArrayList<>();
        return expenseDAO.getTourExpenses(tour);
    } catch (SQLException e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}

// Get balances for a specific tour by ID
public Map<User, Double> getTourBalances(String tourId) throws ExpenseServiceException {
    try {
        Tour tour = expenseDAO.getTourById(tourId);
        if (tour == null) return new HashMap<>();
        return expenseDAO.calculateBalances(tour);
    } catch (SQLException e) {
        throw new ExpenseServiceException("Failed to get balances: " + e.getMessage(), e);
    }
}

// Calculate settlements from balances
public List<ExpenseSettlement> calculateSettlementsFromBalances(Map<User, Double> balances) {
    return expenseDAO.calculateSettlements(balances);
}
 public boolean endActiveTour() throws ExpenseServiceException {
    try {
        Tour activeTour = getActiveTour();
        if (activeTour == null) {
            throw new ExpenseServiceException("No active tour found to end");
        }
        
        // Calculate final settlements before ending
        List<ExpenseSettlement> settlements = getActiveTourSettlements();
        
        // Save to completed tours database
     //   completedTourDAO.saveCompletedTour(activeTour, settlements);
        
        // End the tour in database
        expenseDAO.endTour(activeTour);
        
        return true;
        
    } catch (SQLException e) {
        throw new ExpenseServiceException("Database error while ending tour: " + e.getMessage(), e);
    }
}
    public double getActiveTourTotal() {
        try {
            List<Expense> expenses = getActiveTourExpenses();
            double total = 0.0;
            for (Expense expense : expenses) {
                total += expense.getAmount();
            }
            return total;
        } catch (Exception e) {
            System.err.println("Error calculating total: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Refresh tour data (force reload from database)
     */
    public void refreshTourData() {
        try {
            Tour tour = getActiveTour();
            if (tour != null) {
                System.out.println("Tour refreshed: " + tour.getName());
                System.out.println("Participants: " + tour.getParticipants().size());
            }
        } catch (ExpenseServiceException e) {
            System.err.println("Failed to refresh tour: " + e.getMessage());
        }
    }
    /**
 * Clean up any existing duplicate participants before creating a new tour
 */
private void cleanupDuplicateParticipants(String tourId, Connection conn) throws SQLException {
    String sql = """
        DELETE FROM tour_participants 
        WHERE tour_id = ? AND rowid NOT IN (
            SELECT MIN(rowid) FROM tour_participants 
            WHERE tour_id = ? 
            GROUP BY user_id
        )
    """;
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, tourId);
        pstmt.setString(2, tourId);
        int deleted = pstmt.executeUpdate();
        if (deleted > 0) {
            System.out.println("Cleaned up " + deleted + " duplicate participants");
        }
    }
}
}