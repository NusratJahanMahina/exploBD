package com.ExploBD.domain.entities;

import com.ExploBD.domain.enums.MessageType;
import com.ExploBD.object.User;

public class BulletinMessage extends Message {
    
    private boolean isImportant;
    
    
    public BulletinMessage(User sender, String groupId, String title, String content) {
        super(sender, groupId, MessageType.BULLETIN, title, content);
        this.isImportant = false;
    }
    
    public BulletinMessage(User sender, String groupId, String title, String content, boolean isImportant) {
        this(sender, groupId, title, content);
        this.isImportant = isImportant;
    }
    
    
    public BulletinMessage(String messageId, User sender, String groupId, 
                          String title, String content, boolean isImportant) {
        super(messageId, sender, groupId, MessageType.BULLETIN, title, content);
        this.isImportant = isImportant;
    }
    
    @Override
    public boolean isPublic() { return true; }
    
    @Override
    public boolean needsApproval() { return false; }
    
    @Override
    public String getDisplayType() { return isImportant ? "Important Announcement" : "Bulletin"; }
    
    public boolean isImportant() { return isImportant; }

    @Override
    public void fillStatement(java.sql.PreparedStatement pstmt) throws java.sql.SQLException {
        pstmt.setNull(10, java.sql.Types.VARCHAR);   // place_id
        pstmt.setNull(11, java.sql.Types.VARCHAR);   // place_name
        pstmt.setNull(12, java.sql.Types.INTEGER);   // upvotes
        pstmt.setNull(13, java.sql.Types.INTEGER);   // downvotes
        pstmt.setNull(14, java.sql.Types.VARCHAR);   // reason
        pstmt.setNull(15, java.sql.Types.VARCHAR);   // invitee_name
        pstmt.setNull(16, java.sql.Types.VARCHAR);   // invitee_email
        pstmt.setNull(17, java.sql.Types.VARCHAR);   // request_type
        pstmt.setBoolean(18, isImportant);           // is_important
    }
}