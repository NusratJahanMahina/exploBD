package com.ExploBD.domain.entities;

import com.ExploBD.object.ExploBDObject;
import com.ExploBD.object.User;
import java.time.LocalDate;
import java.util.*;

public class Tour extends ExploBDObject {
    
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private User createdBy;
    private List<User> participants;
    private List<Expense> expenses;
    private List<ExpenseSettlement> settlements;
   
    public Tour(String tourId, String name, User createdBy) {
        super(tourId, name);
        this.createdBy = createdBy;
        this.isActive = true;
        this.participants = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.settlements = new ArrayList<>();
    }
    
    @Override
    public String getObjectType() {
        return "TOUR";
    }
    
    public void addParticipant(User user) {
        if (!participants.contains(user)) {
            participants.add(user);
        }
    }
    
    public double getTotalExpenses() {
    double total = 0.0;
    for (Expense expense : expenses) {
        total += expense.getAmount();
    }
    return total;
}
    
    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public User getCreatedBy() { return createdBy; }
    
    public List<User> getParticipants() { return participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }
    
    public List<Expense> getExpenses() { return expenses; }
    public List<ExpenseSettlement> getSettlements() { return settlements; }
    public void setSettlements(List<ExpenseSettlement> settlements) { this.settlements = settlements; }

   
public void setExpenses(List<Expense> expenses) {
    this.expenses = expenses;
}


}


