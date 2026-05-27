package com.ExploBD.domain.entities;

public class CompletedTourParticipant {
    private int id;
    private String completedId;
    private String userId;
    private String userName;
    private String userEmail;
    private double amountOwed;
    private double amountPaid;
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCompletedId() { return completedId; }
    public void setCompletedId(String completedId) { this.completedId = completedId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public double getAmountOwed() { return amountOwed; }
    public void setAmountOwed(double amountOwed) { this.amountOwed = amountOwed; }
    
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
}