package com.ExploBD.service;

import com.ExploBD.object.ExploBDObject;
import com.ExploBD.object.User;
import java.time.LocalDateTime;
import com.ExploBD.domain.entities.Tour;
import java.util.*;

public class Expense extends ExploBDObject {
    
    private Tour tour;
    private User paidBy;
    private double amount;
    private String description;
    private LocalDateTime expenseDate;
    private List<User> participants;
    private Map<User, Double> splits;
    
    public Expense(String expenseId, Tour tour, User paidBy, double amount, String description) {
        super(expenseId, description);
        this.tour = tour;
        this.paidBy = paidBy;
        this.amount = amount;
        this.description = description;
        this.expenseDate = LocalDateTime.now();
        this.participants = new ArrayList<>();
        this.splits = new HashMap<>();
    }
    
    @Override
    public String getObjectType() {
        return "EXPENSE";
    }
    
    public void splitEqually() {
        if (participants.isEmpty()) {
            participants = tour.getParticipants();
        }
        double sharePerPerson = amount / participants.size();
        splits.clear();
        for (User participant : participants) {
            if (participant.equals(paidBy)) {
                splits.put(participant, 0.0);
            } else {
                splits.put(participant, sharePerPerson);
            }
        }
    }
    
    // Getters and Setters
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public User getPaidBy() { return paidBy; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; }
    public List<User> getParticipants() { return participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }
    public Map<User, Double> getSplits() { return splits; }
}

//srithy did not mentioned but it was in her project 
