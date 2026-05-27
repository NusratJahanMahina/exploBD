package com.ExploBD.domain.entities;

import com.ExploBD.domain.enums.MessageStatus;
import com.ExploBD.domain.enums.MessageType;
import com.ExploBD.object.User;
import java.time.LocalDateTime;

public class InviteRequestMessage extends Message {
    
    private String inviteeName;
    private String inviteeEmail;
    private String requestType;
    private User reviewedBy;
    private LocalDateTime reviewedAt;
    
    
    public InviteRequestMessage(User sender, String groupId, 
                                String inviteeName, String inviteeEmail, 
                                String requestType) {
        super(sender, groupId, MessageType.INVITE_REQUEST,
              "Invite Request: " + inviteeName,
              "Would like to invite " + inviteeName + " (" + inviteeEmail + ")");
        this.inviteeName = inviteeName;
        this.inviteeEmail = inviteeEmail;
        this.requestType = requestType;
        this.status = MessageStatus.PENDING;
    }
    
    
    public InviteRequestMessage(String messageId, User sender, String groupId,
                                String inviteeName, String inviteeEmail, 
                                String requestType) {
        super(messageId, sender, groupId, MessageType.INVITE_REQUEST,
              "Invite Request: " + inviteeName,
              "Would like to invite " + inviteeName + " (" + inviteeEmail + ")");
        this.inviteeName = inviteeName;
        this.inviteeEmail = inviteeEmail;
        this.requestType = requestType;
        this.status = MessageStatus.PENDING;
    }
    
    @Override
    public boolean isPublic() { return false; }
    
    @Override
    public boolean needsApproval() { return true; }
    
    @Override
    public String getDisplayType() { return "Invite Request"; }

    @Override
    public void fillStatement(java.sql.PreparedStatement pstmt) throws java.sql.SQLException {
        pstmt.setNull(10, java.sql.Types.VARCHAR);
        pstmt.setNull(11, java.sql.Types.VARCHAR);
        pstmt.setNull(12, java.sql.Types.INTEGER);
        pstmt.setNull(13, java.sql.Types.INTEGER);
        pstmt.setNull(14, java.sql.Types.VARCHAR);
        pstmt.setString(15, inviteeName);
        pstmt.setString(16, inviteeEmail);
        pstmt.setString(17, requestType);
        pstmt.setNull(18, java.sql.Types.BOOLEAN);
    }
    
    public void approve(User leader) {
        this.status = MessageStatus.APPROVED;
        this.reviewedBy = leader;
        this.reviewedAt = LocalDateTime.now();
    }
    
    public void reject(User leader) {
        this.status = MessageStatus.REJECTED;
        this.reviewedBy = leader;
        this.reviewedAt = LocalDateTime.now();
    }
    
   
    public String getInviteeName() { return inviteeName; }
    public String getInviteeEmail() { return inviteeEmail; }
    public String getRequestType() { return requestType; }
    public User getReviewedBy() { return reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
}