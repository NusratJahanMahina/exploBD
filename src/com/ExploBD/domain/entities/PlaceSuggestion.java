package com.ExploBD.domain.entities;

import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.domain.enums.InvitationStatus;
import java.time.LocalDateTime;
import java.util.*;

public class PlaceSuggestion {

    private final String suggestionId;
    private final Place place;
    private final User suggester;
    private String reason;
    private InvitationStatus status;
    private LocalDateTime suggestedAt;
    private LocalDateTime reviewedAt;
    private User reviewedBy;
    private Map<String, Boolean> votes;
    private int approveCount;
    private int rejectCount;

    
    public PlaceSuggestion(Place place, User suggester, String reason) {
        this.suggestionId = "SUG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.place = place;
        this.suggester = suggester;
        this.reason = reason;
        this.status = InvitationStatus.PENDING;
        this.suggestedAt = LocalDateTime.now();
        this.votes = new HashMap<>();
        this.approveCount = 0;
        this.rejectCount = 0;
    }

    
    public PlaceSuggestion(String suggestionId, Place place, User suggester, 
                           String reason, InvitationStatus status, 
                           LocalDateTime suggestedAt, int approveCount, int rejectCount) {
        this.suggestionId = suggestionId;
        this.place = place;
        this.suggester = suggester;
        this.reason = reason;
        this.status = status;
        this.suggestedAt = suggestedAt;
        this.votes = new HashMap<>();
        this.approveCount = approveCount;
        this.rejectCount = rejectCount;
    }

    
    public void incrementApproveCount() {
        this.approveCount++;
    }

    public void incrementRejectCount() {
        this.rejectCount++;
    }

    
    public void setApproveCount(int approveCount) {
        this.approveCount = approveCount;
    }

    
    public void setRejectCount(int rejectCount) {
        this.rejectCount = rejectCount;
    }

   
    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    
    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    
    public void setVotes(Map<String, Boolean> votes) {
        this.votes = votes;
    }

    
    public void addVote(String userId, boolean approve) {
        this.votes.put(userId, approve);
       
    }

    public void castVote(User voter, boolean approve) {
        if (approve) {
            approveCount++;
        } else {
            rejectCount++;
        }
        votes.put(voter.getUserId(), approve);
    }

    public void removeVote(User voter) {
        Boolean voteType = votes.remove(voter.getUserId());
        if (voteType != null) {
            if (voteType) {
                approveCount--;
            } else {
                rejectCount--;
            }
        }
    }

    public void approveByLeader(User leader) {
        this.status = InvitationStatus.APPROVED;  
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = leader;
    }

    public void rejectByLeader(User leader) {
        this.status = InvitationStatus.DECLINED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = leader;
    }

    public boolean hasVoted(User user) {
        return votes.containsKey(user.getUserId());
    }

    public boolean isApproved() {
        return approveCount > rejectCount;
    }

    
    public String getSuggestionId() {
        return suggestionId;
    }

    public Place getPlace() {
        return place;
    }

    public User getSuggester() {
        return suggester;
    }

    public String getReason() {
        return reason;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public LocalDateTime getSuggestedAt() {
        return suggestedAt;
    }

    public int getApproveCount() {
        return approveCount;
    }

    public int getRejectCount() {
        return rejectCount;
    }

    public int getTotalVotes() {
        return votes.size();
    }
}