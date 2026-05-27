package com.ExploBD.domain.entities.GroupSystem;

import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.valueobjects.BudgetRange;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class GroupInfo {

    private String id;
    private String name;
    private String description;
    private String destinationName;
    private String destinationDivision;
    private String destinationImagePath;
    private LocalDate startDate;
    private LocalDate endDate;
    private BudgetRange budgetRange;
    private int maxMembers;
    private GroupStatus status;
    private String invitationCode;
    private LocalDateTime votingStartTime;
    private LocalDateTime votingEndTime;
    private boolean isPublic;

    
    public GroupInfo(String id, String name) {
        this.id = id;
        this.name = name;
        this.status = GroupStatus.DRAFT;
        this.maxMembers = 10;
        this.isPublic = true;
    }

    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDestinationImagePath() {
        return destinationImagePath;
    }

    public void setDestinationImagePath(String destinationImagePath) {
        this.destinationImagePath = destinationImagePath;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BudgetRange getBudgetRange() {
        return budgetRange;
    }

    public void setBudgetRange(BudgetRange budgetRange) {
        this.budgetRange = budgetRange;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public LocalDateTime getVotingStartTime() {
        return votingStartTime;
    }

    public void setVotingStartTime(LocalDateTime votingStartTime) {
        this.votingStartTime = votingStartTime;
    }

    public LocalDateTime getVotingEndTime() {
        return votingEndTime;
    }

    public void setVotingEndTime(LocalDateTime votingEndTime) {
        this.votingEndTime = votingEndTime;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
