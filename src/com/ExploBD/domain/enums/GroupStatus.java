package com.ExploBD.domain.enums;

import java.awt.Color;

public enum GroupStatus {
    DRAFT("Draft", new Color(169, 169, 169), false, false),
    PLANNING("Planning", new Color(255, 165, 0), true, true),
    VOTING("Voting", new Color(30, 144, 255), true, false),
    CONFIRMED("Confirmed", new Color(50, 205, 50), false, false);

    private final String displayName;
    private final Color color;
    private final boolean canSuggest;
    private final boolean canInvite;

    GroupStatus(String displayName, Color color, boolean canSuggest, boolean canInvite) {
        this.displayName = displayName;
        this.color = color;
        this.canSuggest = canSuggest;
        this.canInvite = canInvite;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public boolean canSuggest() {
        return canSuggest;
    }

    public boolean canInvite() {
        return canInvite;
    }

    public boolean isActive() {
        return this == PLANNING || this == VOTING;
    }
}
