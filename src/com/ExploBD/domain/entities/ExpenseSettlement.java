package com.ExploBD.domain.entities;

import com.ExploBD.object.ExploBDObject;
import com.ExploBD.object.User;

public class ExpenseSettlement extends ExploBDObject {
    
    private User fromUser;
    private User toUser;
    private double amount;
    private SettlementStatus status;
    
    public enum SettlementStatus {
        PENDING, PAID
    }
    
    public ExpenseSettlement(String settlementId, User fromUser, User toUser, double amount) {
        super(settlementId, fromUser.getDisplayName() + " → " + toUser.getDisplayName());
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.status = SettlementStatus.PENDING;
    }
    
    @Override
    public String getObjectType() {
        return "SETTLEMENT";
    }
    
    public void markAsPaid() {
        this.status = SettlementStatus.PAID;
    }
    
    // Getters
    public User getFromUser() { return fromUser; }
    public User getToUser() { return toUser; }
    public double getAmount() { return amount; }
    public SettlementStatus getStatus() { return status; }
}