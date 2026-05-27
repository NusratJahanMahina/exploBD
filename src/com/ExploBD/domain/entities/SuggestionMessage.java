package com.ExploBD.domain.entities;

import com.ExploBD.domain.enums.MessageType;
import com.ExploBD.object.User;

public class SuggestionMessage extends Message {
    
    private String placeId;
    private String placeName;
    private String reason;
    private int upvotes;
    private int downvotes;
    
    
    public SuggestionMessage(User sender, String groupId, 
                            String placeId, String placeName, String reason) {
        super(sender, groupId, MessageType.SUGGESTION, "Suggestion: " + placeName, reason);
        this.placeId = placeId;
        this.placeName = placeName;
        this.reason = reason;
        this.upvotes = 0;
        this.downvotes = 0;
    }
    
    
    public SuggestionMessage(String messageId, User sender, String groupId,
                            String placeId, String placeName, String reason) {
        super(messageId, sender, groupId, MessageType.SUGGESTION, "Suggestion: " + placeName, reason);
        this.placeId = placeId;
        this.placeName = placeName;
        this.reason = reason;
        this.upvotes = 0;
        this.downvotes = 0;
    }
    
    @Override
    public boolean isPublic() { return true; }
    
    @Override
    public boolean needsApproval() { return false; }
    
    @Override
    public String getDisplayType() { return "Place Suggestion"; }

    @Override
    public void fillStatement(java.sql.PreparedStatement pstmt) throws java.sql.SQLException {
        pstmt.setString(10, placeId);
        pstmt.setString(11, placeName);
        pstmt.setInt(12, upvotes);
        pstmt.setInt(13, downvotes);
        pstmt.setString(14, reason);
        pstmt.setNull(15, java.sql.Types.VARCHAR);
        pstmt.setNull(16, java.sql.Types.VARCHAR);
        pstmt.setNull(17, java.sql.Types.VARCHAR);
        pstmt.setNull(18, java.sql.Types.BOOLEAN);
    }
    
    public void upvote() { this.upvotes++; }
    public void downvote() { this.downvotes++; }
    
    
    public String getPlaceId() { return placeId; }
    public String getPlaceName() { return placeName; }
    public String getReason() { return reason; }
    public int getUpvotes() { return upvotes; }
    public int getDownvotes() { return downvotes; }
    public int getNetVotes() { return upvotes - downvotes; }
}