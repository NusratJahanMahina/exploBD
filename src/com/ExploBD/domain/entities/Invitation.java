package com.ExploBD.domain.entities;

import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.object.User;
import com.ExploBD.domain.enums.InvitationStatus;
import java.time.LocalDate;
import java.util.UUID;

public class Invitation {

    private String invitationId;
    private Group group;
    private User inviter;
    private String inviteeEmail;
    private String inviteeName;
    private String type;
    private InvitationStatus status;
    private LocalDate sentDate;
    private LocalDate expiryDate;
    private boolean needsLeaderApproval;
    private String invitationCode;

    public Invitation(Group group, User inviter, String inviteeEmail) {
        this.invitationId = generateId();
        this.group = group;
        this.inviter = inviter;
        this.inviteeEmail = inviteeEmail;
        this.inviteeName = null;
        this.type = "EMAIL";
        this.status = InvitationStatus.PENDING;
        this.sentDate = LocalDate.now();
        this.expiryDate = LocalDate.now().plusDays(7);
        this.needsLeaderApproval = !group.isLeader(inviter.getUserId());
    }

    public static Invitation createPlaceholder(Group group, User inviter, String name, String email) {
        Invitation inv = new Invitation(group, inviter, email);
        inv.type = "PLACEHOLDER";
        inv.inviteeName = name;
        return inv;
    }

    public static Invitation createPlaceholder(Group group, User inviter, String name) {
        return createPlaceholder(group, inviter, name, null);
    }

    public static Invitation createPlaceholderWithEmail(Group group, User inviter, String name, String email) {
        Invitation inv = new Invitation(group, inviter, email);
        inv.type = "PLACEHOLDER";
        inv.inviteeName = name;
        inv.invitationCode = group.getInvitationCode();
        return inv;
    }

    private String generateId() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void approveByLeader() {
        if (needsLeaderApproval) {
            this.status = InvitationStatus.APPROVED;
        }
    }

    public void send() {
        if (!needsLeaderApproval || status == InvitationStatus.APPROVED) {
            this.status = InvitationStatus.SENT;
        }
    }

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void decline() {
        this.status = InvitationStatus.DECLINED;
    }

    public void convertToEmailInvitation(String email) {
        if ("PLACEHOLDER".equals(this.type)) {
            this.type = "EMAIL";
            this.inviteeEmail = email;
        }
    }

    
    public String getInvitationId() {
        return invitationId;
    }

    public Group getGroup() {
        return group;
    }

    public User getInviter() {
        return inviter;
    }

    public String getInviteeEmail() {
        return inviteeEmail;
    }

    public String getInviteeName() {
        return inviteeName;
    }

    public String getType() {
        return type;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public boolean needsLeaderApproval() {
        return needsLeaderApproval;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    
    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setNeedsLeaderApproval(boolean needsLeaderApproval) {
        this.needsLeaderApproval = needsLeaderApproval;
    }

    public void setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public void setInviteeEmail(String inviteeEmail) {
        this.inviteeEmail = inviteeEmail;
    }
}