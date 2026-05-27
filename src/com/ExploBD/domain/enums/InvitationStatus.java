package com.ExploBD.domain.enums;

import java.awt.Color;

public enum InvitationStatus {
    PENDING("Pending", new Color(255, 165, 0), true, true),
    APPROVED("Approved", new Color(100, 149, 237), false, true),
    SENT("Sent", new Color(30, 144, 255), false, false),
    ACCEPTED("Accepted", new Color(50, 205, 50), false, false),
    DECLINED("Declined", new Color(220, 20, 60), false, false),
    LEADER_APPROVAL("Awaiting Leader", new Color(147, 112, 219), false, true);
    private final String displayName;
    private final Color color;
    private final boolean canRespond;
    private final boolean isActive;

    InvitationStatus(String displayName, Color color, boolean canRespond, boolean isActive) {
        this.displayName = displayName;
        this.color = color;
        this.canRespond = canRespond;
        this.isActive = isActive;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public boolean canRespond() {
        return canRespond;
    }

    public boolean isActive() {
        return isActive;
    }
}
