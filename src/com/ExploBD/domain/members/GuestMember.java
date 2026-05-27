package com.ExploBD.domain.members;

import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.enums.MemberType;
import java.util.UUID;

public class GuestMember implements GroupMember {
    private String email;
    private String invitedBy;
    private String invitationCode;
    private MemberRole role;

    public GuestMember(String email, String invitedBy) {
        this.email = email;
        this.invitedBy = invitedBy;
        this.invitationCode = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.role = MemberRole.PENDING;
    }

    public String getId() {
        return email;
    }

    public String getDisplayName() {
        return email;
    }

    public String getContactMethod() {
        return "Email invitation";
    }

    public MemberRole getRole() {
        return role;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public MemberType getType() {
        return MemberType.GUEST;
    }

    public boolean canVote() {
        return false;
    }

    public boolean canInvite() {
        return false;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public String getEmail() {
        return email;
    }

    public String getInvitedBy() {
        return invitedBy;
    }
}