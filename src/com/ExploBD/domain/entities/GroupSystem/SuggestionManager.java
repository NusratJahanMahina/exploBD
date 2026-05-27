package com.ExploBD.domain.entities.GroupSystem;

import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.object.Place;
import com.ExploBD.object.User;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.interfaces.GroupMember;
import java.time.LocalDateTime;
import java.util.*;


public class SuggestionManager {

    private List<PlaceSuggestion> suggestions;
    private Map<String, Integer> voteCounts;
    private Map<String, Set<String>> voters;
    private Place selectedDestination;

    public SuggestionManager() {
        this.suggestions = new ArrayList<>();
        this.voteCounts = new HashMap<>();
        this.voters = new HashMap<>();
    }

    public void suggestPlace(Place place, User suggester, String reason, MemberManager memberManager) {
        if (!canSuggest(memberManager, suggester)) {
            throw new IllegalStateException("You cannot suggest places at this time");
        }

        boolean alreadySuggested = suggestions.stream()
                .anyMatch(s -> s.getPlace().getId().equals(place.getId()));

        if (alreadySuggested) {
            throw new IllegalArgumentException("This place has already been suggested");
        }

        PlaceSuggestion suggestion = new PlaceSuggestion(place, suggester, reason);
        suggestions.add(suggestion);
        voteCounts.put(suggestion.getSuggestionId(), 0);
        voters.put(suggestion.getSuggestionId(), new HashSet<>());
    }

    private boolean canSuggest(MemberManager memberManager, User user) {
        GroupMember member = memberManager.getMember(user);
        return member != null && member.canVote();
    }

    public void approveSuggestion(String suggestionId, User leader) {
        PlaceSuggestion suggestion = findSuggestionById(suggestionId);
        if (suggestion == null) {
            throw new IllegalArgumentException("Suggestion not found");
        }
        suggestion.approveByLeader(leader);
    }

    public void rejectSuggestion(String suggestionId, User leader) {
        PlaceSuggestion suggestion = findSuggestionById(suggestionId);
        if (suggestion == null) {
            throw new IllegalArgumentException("Suggestion not found");
        }
        suggestion.rejectByLeader(leader);
    }

    public void castVote(String suggestionId, User voter, MemberManager memberManager) {
        GroupMember member = memberManager.getMember(voter);
        if (member == null || !member.canVote()) {
            throw new IllegalStateException("Member cannot vote");
        }

        PlaceSuggestion suggestion = findSuggestionById(suggestionId);
        if (suggestion == null) {
            throw new IllegalArgumentException("Suggestion not found");
        }

        if (suggestion.getStatus() != InvitationStatus.APPROVED) {
            throw new IllegalStateException("This suggestion is not approved for voting");
        }

        Set<String> suggestionVoters = voters.get(suggestionId);
        if (suggestionVoters == null) {
            suggestionVoters = new HashSet<>();
            voters.put(suggestionId, suggestionVoters);
        }

        if (suggestionVoters.contains(voter.getUserId())) {
            throw new IllegalStateException("You have already voted for this suggestion");
        }

        suggestionVoters.add(voter.getUserId());
        suggestion.incrementApproveCount(); 
        voteCounts.put(suggestionId, voteCounts.getOrDefault(suggestionId, 0) + 1);
        suggestion.addVote(voter.getUserId(), true);
    }

    public void removeVote(String suggestionId, User voter) {
        PlaceSuggestion suggestion = findSuggestionById(suggestionId);
        if (suggestion == null) {
            throw new IllegalArgumentException("Suggestion not found");
        }

        Set<String> suggestionVoters = voters.get(suggestionId);
        if (suggestionVoters == null || !suggestionVoters.contains(voter.getUserId())) {
            throw new IllegalStateException("You haven't voted for this suggestion");
        }

        suggestionVoters.remove(voter.getUserId());
        int currentVotes = voteCounts.getOrDefault(suggestionId, 0);
        if (currentVotes > 0) {
            voteCounts.put(suggestionId, currentVotes - 1);
        }
        suggestion.removeVote(voter);
    }

    public PlaceSuggestion getWinningSuggestion() {
        if (suggestions.isEmpty()) {
            return null;
        }

        PlaceSuggestion winner = null;
        int maxVotes = -1;
        for (PlaceSuggestion s : suggestions) {
            if (s.getStatus() == InvitationStatus.APPROVED) {
                int votes = voteCounts.getOrDefault(s.getSuggestionId(), 0);
                if (votes > maxVotes) {
                    maxVotes = votes;
                    winner = s;
                }
            }
        }
        return winner;
    }

    public void confirmDestination(String suggestionId, User leader) {
        PlaceSuggestion suggestion = findSuggestionById(suggestionId);
        if (suggestion == null) {
            throw new IllegalArgumentException("Suggestion not found");
        }
        this.selectedDestination = suggestion.getPlace();
    }

public PlaceSuggestion findSuggestionById(String suggestionId) {
    for (PlaceSuggestion s : suggestions) {
        if (s.getSuggestionId().equals(suggestionId)) {
            return s;
        }
    }
    return null;
}

    public List<PlaceSuggestion> getAllSuggestions() {
        return new ArrayList<>(suggestions);
    }

    public List<PlaceSuggestion> getPendingSuggestions() {
        List<PlaceSuggestion> pending = new ArrayList<>();
        for (PlaceSuggestion s : suggestions) {
            if (s.getStatus() == InvitationStatus.PENDING) {
                pending.add(s);
            }
        }
        return pending;
    }

    public List<PlaceSuggestion> getApprovedSuggestions() {
        List<PlaceSuggestion> approved = new ArrayList<>();
        for (PlaceSuggestion s : suggestions) {
            if (s.getStatus() == InvitationStatus.APPROVED) {
                approved.add(s);
            }
        }
        return approved;
    }

    public void addSuggestion(PlaceSuggestion suggestion) {
        this.suggestions.add(suggestion);
        this.voteCounts.put(suggestion.getSuggestionId(), suggestion.getApproveCount());
    }

    public Map<String, Set<String>> getVoters() {
        return voters;
    }

    public void setVoters(Map<String, Set<String>> voters) {
        this.voters = voters;
    }

    public Place getSelectedDestination() {
        return selectedDestination;
    }

    public boolean hasVoted(String suggestionId, User user) {
        if (user == null) {
            return false;
        }
        Set<String> suggestionVoters = voters.get(suggestionId);
        return suggestionVoters != null && suggestionVoters.contains(user.getUserId());
    }

    public int getVoteCount(String suggestionId) {
        return voteCounts.getOrDefault(suggestionId, 0);
    }
}
