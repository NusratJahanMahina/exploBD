package com.ExploBD.domain.entities;

public class CompletedTour {

    private String completedId;
    private String tourId;
    private String groupId;
    private String tourName;
    private String destinationName;
    private String destinationDivision;
    private String startDate;
    private String endDate;
    private double totalExpenses;
    private int memberCount;
    private int budgetMin;
    private int budgetMax;
    private String completedDate;
    private String expenseSummary;

    // Getters and Setters
    public String getCompletedId() {
        return completedId;
    }

    public void setCompletedId(String completedId) {
        this.completedId = completedId;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    @Override
    public String toString() {
        return tourName + (destinationName != null && !destinationName.isEmpty() ? " - " + destinationName : "");
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationDivision() {
        return destinationDivision;
    }

    public void setDestinationDivision(String destinationDivision) {
        this.destinationDivision = destinationDivision;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(int budgetMin) {
        this.budgetMin = budgetMin;
    }

    public int getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(int budgetMax) {
        this.budgetMax = budgetMax;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public String getExpenseSummary() {
        return expenseSummary;
    }

    public void setExpenseSummary(String expenseSummary) {
        this.expenseSummary = expenseSummary;
    }
}
