package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.domain.entities.CompletedTour;
import com.ExploBD.domain.entities.CompletedTourParticipant;
import com.ExploBD.domain.entities.Tour;
import com.ExploBD.object.User;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class CompletedTourDatabaseObject {

    private User user;
    private ExpenseDatabaseObject expenseDAO;
    public CompletedTourDatabaseObject() {
    this.expenseDAO = new ExpenseDatabaseObject();
}
    public List<CompletedTour> getUserCompletedTours(String userId) {
        
        List<CompletedTour> tours = new ArrayList<>();
        
        String sql = """
            SELECT ct.* FROM completed_tours ct
            INNER JOIN completed_tour_participants ctp ON ct.completed_id = ctp.completed_id
            WHERE ctp.user_id = ?
            ORDER BY ct.completed_date DESC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CompletedTour tour = new CompletedTour();
                tour.setCompletedId(rs.getString("completed_id"));
                tour.setTourId(rs.getString("tour_id"));
                tour.setGroupId(rs.getString("group_id"));
                tour.setTourName(rs.getString("tour_name"));
                tour.setDestinationName(rs.getString("destination_name"));
                tour.setDestinationDivision(rs.getString("destination_division"));
                tour.setStartDate(rs.getString("start_date"));
                tour.setEndDate(rs.getString("end_date"));
                tour.setTotalExpenses(rs.getDouble("total_expenses"));
                tour.setMemberCount(rs.getInt("member_count"));
                tour.setBudgetMin(rs.getInt("budget_min"));
                tour.setBudgetMax(rs.getInt("budget_max"));
                tour.setCompletedDate(rs.getString("completed_date"));
                tours.add(tour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tours;
    }
   // private ExpenseDatabaseObject expenseDAO;


   public void saveCompletedTour(Tour tour, List<ExpenseSettlement> settlements) {
    String sql = "INSERT INTO completed_tours (completed_id, tour_id, tour_name, destination_name, start_date, end_date, total_expenses, member_count, completed_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        String completedId = "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        pstmt.setString(1, completedId);
        pstmt.setString(2, tour.getId());
        pstmt.setString(3, tour.getName());
//        pstmt.setString(4, (String) tour.getDestinationName());
        pstmt.setString(5, tour.getStartDate() != null ? tour.getStartDate().toString() : null);
        pstmt.setString(6, tour.getEndDate() != null ? tour.getEndDate().toString() : null);
        //pstmt.setDouble(7, getTourTotalExpenses(tour));
        pstmt.setInt(8, tour.getParticipants().size());
        pstmt.setString(9, LocalDate.now().toString());
        pstmt.executeUpdate();
        
        // Save participants
        for (User User : tour.getParticipants()) {
            saveTourParticipant(conn, completedId, user);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void saveTourParticipant(Connection conn, String completedId, User user) {
    String sql = "INSERT INTO completed_tour_participants (completed_id, user_id, user_name, amount_paid, amount_owed) VALUES (?, ?, ?, ?, ?)";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, completedId);
        pstmt.setString(2, user.getUserId());
        pstmt.setString(3, user.getDisplayName());
        pstmt.setDouble(4, 0); // Will be calculated from expenses
        pstmt.setDouble(5, 0); // Will be calculated from expenses
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
/*private double getTourTotalExpenses(Tour tour) {
    double total = 0;
    List<Expense> expenses = expenseDAO.getExpensesByTour(tour.getId());
    for (Expense e : expenses) {
        total += e.getAmount();
    }
    return total;
}*/
/*public List<CompletedTourParticipant> getTourParticipants(String completedId) {
    List<CompletedTourParticipant> participants = new ArrayList<>();
    String sql = "SELECT * FROM completed_tour_participants WHERE completed_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, completedId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            CompletedTourParticipant p = new CompletedTourParticipant();
            p.setUserName(rs.getString("user_name"));
            p.setAmountPaid(rs.getDouble("amount_paid"));
            p.setAmountOwed(rs.getDouble("amount_owed"));
            participants.add(p);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return participants;
}*/
public List<CompletedTourParticipant> getTourParticipants(String completedId)  {
    List<CompletedTourParticipant> participants = new ArrayList<>();
    String sql = "SELECT * FROM completed_tour_participants WHERE completed_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, completedId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            CompletedTourParticipant p = new CompletedTourParticipant();
            p.setUserName(rs.getString("user_name"));
            p.setAmountPaid(rs.getDouble("amount_paid"));
            p.setAmountOwed(rs.getDouble("amount_owed"));
            participants.add(p);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return participants;
}
  /*  public List<CompletedTourParticipant> getTourParticipants(String completedId) {
        List<CompletedTourParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM completed_tour_participants WHERE completed_id = ? ORDER BY amount_owed DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, completedId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CompletedTourParticipant p = new CompletedTourParticipant();
                p.setId(rs.getInt("id"));
                p.setCompletedId(rs.getString("completed_id"));
                p.setUserId(rs.getString("user_id"));
                p.setUserName(rs.getString("user_name"));
                p.setUserEmail(rs.getString("user_email"));
                p.setAmountOwed(rs.getDouble("amount_owed"));
                p.setAmountPaid(rs.getDouble("amount_paid"));
                participants.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }*/

    private static class ExpenseSettlement {

        public ExpenseSettlement() {
        }
    }
}