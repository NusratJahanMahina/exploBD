package com.ExploBD.domain.entities.GroupSystem;

import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.object.ExploBDObject;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.domain.valueobjects.BudgetRange;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class Group extends ExploBDObject {

    private GroupInfo info;
    private MemberManager memberManager;
    private SuggestionManager suggestionManager;

   
    public Group(String name, GroupMember creator) {
        super(generateGroupId(), name);
        this.info = new GroupInfo(getId(), name);
        this.memberManager = new MemberManager(10); 
        this.suggestionManager = new SuggestionManager();
        this.memberManager.addLeader(creator);
        this.info.setInvitationCode(generateInvitationCode());
    }

    
    public Group(String id, String name, GroupMember creator) {
        super(id, name);
        this.info = new GroupInfo(id, name);
        this.memberManager = new MemberManager(10);
        this.suggestionManager = new SuggestionManager();
        this.memberManager.addLeader(creator);
    }

    public boolean isPublic() {
        return info.isPublic();
    }

    public void setPublic(boolean isPublic) {
        info.setPublic(isPublic);
    }
    

    private static String generateGroupId() {
        return "GRP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateInvitationCode() {
        return "GROUP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

   
    public void setDescription(String description) {
        info.setDescription(description);
    }

    public String getDescription() {
        return info.getDescription();
    }

    public void setDestinationName(String destinationName) {
        info.setDestinationName(destinationName);
    }

    public String getDestinationName() {
        return info.getDestinationName();
    }

    public void setDestinationDivision(String destinationDivision) {
        info.setDestinationDivision(destinationDivision);
    }

    public String getDestinationDivision() {
        return info.getDestinationDivision();
    }

    public void setDestinationImagePath(String path) {
        info.setDestinationImagePath(path);
    }

    public String getDestinationImagePath() {
        return info.getDestinationImagePath();
    }

    public void setStartDate(LocalDate date) {
        info.setStartDate(date);
    }

    public LocalDate getStartDate() {
        return info.getStartDate();
    }

    public void setEndDate(LocalDate date) {
        info.setEndDate(date);
    }

    public LocalDate getEndDate() {
        return info.getEndDate();
    }

    public void setBudgetRange(BudgetRange range) {
        info.setBudgetRange(range);
    }

    public BudgetRange getBudgetRange() {
        return info.getBudgetRange();
    }

    public void setMaxMembers(int max) {
        info.setMaxMembers(max);
        memberManager.setMaxMembers(max);
    }

    public int getMaxMembers() {
        return info.getMaxMembers();
    }

    public void setStatus(GroupStatus status) {
        info.setStatus(status);
    }

    public GroupStatus getStatus() {
        return info.getStatus();
    }

    public void setInvitationCode(String code) {
        info.setInvitationCode(code);
    }

    public String getInvitationCode() {
        return info.getInvitationCode();
    }

    public void setVotingStartTime(LocalDateTime time) {
        info.setVotingStartTime(time);
    }

    public LocalDateTime getVotingStartTime() {
        return info.getVotingStartTime();
    }

    public void setVotingEndTime(LocalDateTime time) {
        info.setVotingEndTime(time);
    }

    public LocalDateTime getVotingEndTime() {
        return info.getVotingEndTime();
    }

    public Place getSelectedDestination() {
        return suggestionManager.getSelectedDestination();
    }

    
    public void addMember(GroupMember member) {
        memberManager.addMember(member);
    }

    public void removeMember(String memberId) {
        memberManager.removeMember(memberId);
    }

    public GroupMember getMember(String memberId) {
        return memberManager.getMember(memberId);
    }

    public GroupMember getMember(User user) {
        return memberManager.getMember(user);
    }

    public boolean hasMember(String memberId) {
        return memberManager.hasMember(memberId);
    }

    public boolean hasMember(User user) {
        return memberManager.hasMember(user);
    }

    public boolean isLeader(String memberId) {
        return memberManager.isLeader(memberId);
    }

    public boolean isLeader(User user) {
        return memberManager.isLeader(user);
    }

    public boolean isLeader(GroupMember member) {
        return member != null && member.getRole() == MemberRole.LEADER;
    }

    public List<GroupMember> getAllMembers() {
        return memberManager.getAllMembers();
    }

    public int getMemberCount() {
        return memberManager.getMemberCount();
    }

    public GroupMember getLeader() {
        return memberManager.getLeader();
    }

    public User getLeaderAsUser() {
        return memberManager.getLeaderAsUser();
    }

    public Map<String, GroupMember> getMembers() {
        Map<String, GroupMember> map = new HashMap<>();
        for (GroupMember m : memberManager.getAllMembers()) {
            map.put(m.getId(), m);
        }
        return map;
    }

   
    public void suggestPlace(Place place, User suggester, String reason) {
        suggestionManager.suggestPlace(place, suggester, reason, memberManager);
    }

    public void approveSuggestion(String suggestionId, User leader) {
        suggestionManager.approveSuggestion(suggestionId, leader);
    }

    public void rejectSuggestion(String suggestionId, User leader) {
        suggestionManager.rejectSuggestion(suggestionId, leader);
    }

    public void castVote(String suggestionId, User voter) {
        suggestionManager.castVote(suggestionId, voter, memberManager);
    }

    public void removeVote(String suggestionId, User voter) {
        suggestionManager.removeVote(suggestionId, voter);
    }

    public PlaceSuggestion getWinningSuggestion() {
        return suggestionManager.getWinningSuggestion();
    }

    public void confirmDestination(String suggestionId, User leader) {
        suggestionManager.confirmDestination(suggestionId, leader);
        Place winner = suggestionManager.getSelectedDestination();
        if (winner != null) {
            info.setDestinationName(winner.getName());
            info.setDestinationDivision(winner.getDivision());
            info.setDestinationImagePath(winner.getImagePath());
        }
        info.setStatus(GroupStatus.CONFIRMED);
    }

    public List<PlaceSuggestion> getAllSuggestions() {
        return suggestionManager.getAllSuggestions();
    }

    public List<PlaceSuggestion> getPendingSuggestions() {
        return suggestionManager.getPendingSuggestions();
    }

    public List<PlaceSuggestion> getApprovedSuggestions() {
        return suggestionManager.getApprovedSuggestions();
    }

    public void addSuggestion(PlaceSuggestion suggestion) {
        suggestionManager.addSuggestion(suggestion);
    }

    public Map<String, Set<String>> getVoters() {
        return suggestionManager.getVoters();
    }

    public void setVoters(Map<String, Set<String>> voters) {
        suggestionManager.setVoters(voters);
    }

    public int getVoteCount(String suggestionId) {
        return suggestionManager.getVoteCount(suggestionId);
    }

    public boolean hasVoted(String suggestionId, User user) {
        return suggestionManager.hasVoted(suggestionId, user);
    }

   
    public boolean isVotingActive() {
        if (info.getStatus() != GroupStatus.VOTING) {
            return false;
        }
        if (info.getVotingEndTime() == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(info.getVotingEndTime());
    }

    public boolean isVotingExpired() {
        return info.getVotingEndTime() != null
                && LocalDateTime.now().isAfter(info.getVotingEndTime());
    }

    public void startVoting(User leader) {
        if (!isLeader(leader)) {
            throw new IllegalStateException("Only leader can start voting");
        }
        if (info.getStatus() != GroupStatus.PLANNING) {
            throw new IllegalStateException("Can only start voting from PLANNING state");
        }

        long approvedCount = getApprovedSuggestions().size();
        if (approvedCount == 0) {
            throw new IllegalStateException("Need at least one approved suggestion");
        }

        info.setStatus(GroupStatus.VOTING);
        info.setVotingStartTime(LocalDateTime.now());
        info.setVotingEndTime(info.getVotingStartTime().plusHours(48));
    }

    public void endVoting(User leader) {
        if (!isLeader(leader)) {
            throw new IllegalStateException("Only leader can end voting");
        }
        if (info.getStatus() != GroupStatus.VOTING) {
            throw new IllegalStateException("Not in voting state");
        }
        info.setVotingEndTime(LocalDateTime.now());
    }

    
    public boolean canEdit() {
        return info.getStatus() == GroupStatus.PLANNING
                || info.getStatus() == GroupStatus.VOTING
                || (info.getStatus() == GroupStatus.CONFIRMED
                && info.getStartDate() != null
                && LocalDate.now().isBefore(info.getStartDate()));
    }

    public boolean canSuggest(User user) {
        GroupMember member = getMember(user);
        if (member == null || !member.canVote()) {
            return false;
        }
        return info.getStatus() == GroupStatus.PLANNING;
    }

    public boolean canUserVote(User user) {
        GroupMember member = getMember(user);
        return member != null && member.canVote() && isVotingActive();
    }

    public boolean conflictsWith(Group otherGroup) {
        if (this.getStartDate() == null || this.getEndDate() == null
                || otherGroup.getStartDate() == null || otherGroup.getEndDate() == null) {
            return false;
        }
        return !(this.getEndDate().isBefore(otherGroup.getStartDate())
                || otherGroup.getEndDate().isBefore(this.getStartDate()));
    }

    public List<User> getAllMembersAsUsers() {
        List<User> users = new ArrayList<>();
        for (GroupMember member : getAllMembers()) {
            if (member instanceof RegisteredMember) {
                users.add(((RegisteredMember) member).getUser());
            }
        }
        return users;
    }

    @Override
    public String getObjectType() {
        return "GROUP";
    }
}
