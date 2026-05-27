package com.ExploBD.domain.enums;

public enum MemberRole {
    LEADER("Leader", true, true, true),
    MEMBER("Member", true, false, true),
    PENDING("Pending", false, false, false);

    private final String displayName;
    private final boolean canVote;
    private final boolean canInvite;
    private final boolean canSuggest;

    MemberRole(String displayName, boolean canVote, boolean canInvite, boolean canSuggest) {
        this.displayName = displayName;
        this.canVote = canVote;
        this.canInvite = canInvite;
        this.canSuggest = canSuggest;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canVote() {
        return canVote;
    }

    public boolean canInvite() {
        return canInvite;
    }

    public boolean canSuggest() {
        return canSuggest;
    }
}
