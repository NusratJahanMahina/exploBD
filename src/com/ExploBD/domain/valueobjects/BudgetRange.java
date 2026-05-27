package com.ExploBD.domain.valueobjects;

public class BudgetRange {

    private int minPerPerson;
    private int maxPerPerson;

    public BudgetRange(int minPerPerson, int maxPerPerson) {
        if (minPerPerson < 0) {
            throw new IllegalArgumentException("Budget cannot be negative");
        }
        if (minPerPerson > maxPerPerson) {
            throw new IllegalArgumentException("Min budget cannot exceed max budget");
        }
        this.minPerPerson = minPerPerson;
        this.maxPerPerson = maxPerPerson;
    }

    public int getMinPerPerson() {
        return minPerPerson;
    }

    public int getMaxPerPerson() {
        return maxPerPerson;
    }

    public boolean isWithinRange(double cost) {
        return cost >= minPerPerson && cost <= maxPerPerson;
    }

    public int getTotalBudget(int memberCount) {
        return maxPerPerson * memberCount;
    }

    public String getFormattedRange() {
        return minPerPerson + " - " + maxPerPerson + " tk per person";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BudgetRange that = (BudgetRange) obj;
        return minPerPerson == that.minPerPerson && maxPerPerson == that.maxPerPerson;
    }

    public int hashCode() {
        return 31 * minPerPerson + maxPerPerson;
    }
}
