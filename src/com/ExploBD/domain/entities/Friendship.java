package com.ExploBD.domain.entities;

import com.ExploBD.object.User;
import java.util.Date;
import java.util.UUID;

public class Friendship {

    private String friendshipId;
    private User user1;
    private User user2;
    private Date since;

    public Friendship(User user1, User user2) {
        this.friendshipId = "FS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (user1.getUserId().compareTo(user2.getUserId()) < 0) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
        this.since = new Date();
    }

    public boolean involves(User user) {
        return user1.equals(user) || user2.equals(user);
    }

    /*public User getOtherUser(User me) {
        if (me.equals(user1)) {
            return user2;
        }
        if (me.equals(user2)) {
            return user1;
        }
        return null;
    }*/

    public String getFriendshipId() {
        return friendshipId;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public Date getSince() {
        return since;
    }
       public User getOtherUser(User me) {
    System.out.println("getOtherUser called with user: " + me.getUserId());
    System.out.println("  user1 ID: " + user1.getUserId());
    System.out.println("  user2 ID: " + user2.getUserId());
    
    if (me.getUserId().equals(user1.getUserId())) {
        System.out.println("  Returning user2: " + user2.getDisplayName());
        return user2;
    }
    if (me.getUserId().equals(user2.getUserId())) {
        System.out.println("  Returning user1: " + user1.getDisplayName());
        return user1;
    }
    System.out.println("  No match found!");
    return null;
}
}
