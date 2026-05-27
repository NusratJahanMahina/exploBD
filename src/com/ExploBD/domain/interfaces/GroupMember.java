package com.ExploBD.domain.interfaces;

import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.enums.MemberType;

public interface GroupMember {

    String getId();

    String getDisplayName();

    String getContactMethod();

    MemberRole getRole();

    void setRole(MemberRole role);

    MemberType getType();

    boolean canVote();

    boolean canInvite();

    String getInvitationCode();
}
