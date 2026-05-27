package com.ExploBD.domain.entities;

import com.ExploBD.object.User;
import com.ExploBD.domain.enums.FriendRequestStatus;
import java.util.Date;
import java.util.UUID;

public class FriendRequest {

    private String requestId;
    private User from;
    private User to;
    private FriendRequestStatus status;
    private Date sentAt;

    public FriendRequest(User from, User to) {
        this.requestId = "FR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.from = from;
        this.to = to;
        this.status = FriendRequestStatus.PENDING;
        this.sentAt = new Date();
    }
   

    public FriendRequest(String requestId, User from, User to, FriendRequestStatus status, Date sentAt) {
        this.requestId = requestId;
        this.from = from;
        this.to = to;
        this.status = status;
        this.sentAt = sentAt;
    }

    public void accept() {
        this.status = FriendRequestStatus.ACCEPTED;
    }

    public void decline() {
        this.status = FriendRequestStatus.DECLINED;
    }

    public String getRequestId() {
        return requestId;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public Date getSentAt() {
        return sentAt;
    }
}
