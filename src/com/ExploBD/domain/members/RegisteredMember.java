package com.ExploBD.domain.members;

import com.ExploBD.object.User;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.enums.MemberType;

public class RegisteredMember implements GroupMember {

    private User user;
    private MemberRole role;

    public RegisteredMember(User user, MemberRole role) {
        this.user = user;
        this.role = role;
    }

    public static RegisteredMember asLeader(User user) {
        return new RegisteredMember(user, MemberRole.LEADER);
    }

    public static RegisteredMember asMember(User user) {
        return new RegisteredMember(user, MemberRole.MEMBER);
    }

    public static RegisteredMember asPending(User user) {
        return new RegisteredMember(user, MemberRole.PENDING);
    }

    public String getId() {
        return user.getUserId();
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }

    public String getContactMethod() {
        return "In-app notification";
    }

    public MemberRole getRole() {
        return role;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public MemberType getType() {
        return MemberType.REGISTERED;
    }

    public boolean canVote() {
        return role != MemberRole.PENDING;
    }

    public boolean canInvite() {
        return role == MemberRole.LEADER;
    }

    public String getInvitationCode() {
        return null;
    }

    public User getUser() {
        return user;
    }
}
