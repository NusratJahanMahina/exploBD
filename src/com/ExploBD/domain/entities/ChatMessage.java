package com.ExploBD.domain.entities;

import com.ExploBD.domain.enums.MessageType;
import com.ExploBD.object.User;

public class ChatMessage extends Message {
    
    
    public ChatMessage(User sender, String groupId, String content) {
        super(sender, groupId, MessageType.CHAT, sender.getDisplayName(), content);
    }
    
    
    public ChatMessage(String messageId, User sender, String groupId, String content) {
        super(messageId, sender, groupId, MessageType.CHAT, sender.getDisplayName(), content);
    }
    
    @Override
    public boolean isPublic() { return true; }
    
    @Override
    public boolean needsApproval() { return false; }
    
    @Override
    public String getDisplayType() { return "Chat"; }

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
        pstmt.setNull(18, java.sql.Types.BOOLEAN);   // is_important
    }
}