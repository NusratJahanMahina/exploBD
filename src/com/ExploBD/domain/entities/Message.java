package com.ExploBD.domain.entities;

import com.ExploBD.object.User;
import com.ExploBD.domain.enums.MessageType;
import com.ExploBD.domain.enums.MessageStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Message {
    
    protected String messageId;  
    protected User sender;
    protected String groupId;
    protected MessageType type;
    protected MessageStatus status;
    protected String title;
    protected String content;
    protected LocalDateTime createdAt;
    protected int likeCount;
    
    
    public Message(User sender, String groupId, MessageType type, String title, String content) {
        this.messageId = "MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.sender = sender;
        this.groupId = groupId;
        this.type = type;
        this.status = MessageStatus.ACTIVE;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.likeCount = 0;
    }
    
    
    public Message(String messageId, User sender, String groupId, MessageType type, String title, String content) {
        this.messageId = messageId;
        this.sender = sender;
        this.groupId = groupId;
        this.type = type;
        this.status = MessageStatus.ACTIVE;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.likeCount = 0;
    }
    
    public abstract boolean isPublic();
    public abstract boolean needsApproval();
    public abstract String getDisplayType();

    /**
     * Each subclass fills the type-specific columns (params 10-18) in the
     * INSERT prepared statement.  The common columns 1-9 are already set by
     * MessageDatabaseObject before this is called.
     *
     * Columns by position:
     *  10 place_id | 11 place_name | 12 upvotes | 13 downvotes | 14 reason
     *  15 invitee_name | 16 invitee_email | 17 request_type | 18 is_important
     */
    public abstract void fillStatement(java.sql.PreparedStatement pstmt) throws java.sql.SQLException;
    
    public void addLike() { this.likeCount++; }
    
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
   
    public String getMessageId() { return messageId; }
    public User getSender() { return sender; }
    public String getGroupId() { return groupId; }
    public MessageType getType() { return type; }
    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getLikeCount() { return likeCount; }
}