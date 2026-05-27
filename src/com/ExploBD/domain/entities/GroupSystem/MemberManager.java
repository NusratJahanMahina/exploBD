package com.ExploBD.domain.entities.GroupSystem;

import com.ExploBD.object.User;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.members.RegisteredMember;
import java.util.*;


public class MemberManager {

    private Map<String, GroupMember> members;
    private int maxMembers;

    public MemberManager(int maxMembers) {
        this.members = new HashMap<>();
        this.maxMembers = maxMembers;
    }

    
    public void addLeader(GroupMember leader) {
        leader.setRole(MemberRole.LEADER);
        members.put(leader.getId(), leader);
    }

    
    public void addMember(GroupMember member) {
        if (members.size() >= maxMembers) {
            throw new IllegalStateException("Group has reached maximum members");
        }
        if (members.containsKey(member.getId())) {
            throw new IllegalArgumentException("Member already in group");
        }
        members.put(member.getId(), member);
    }

    public void removeMember(String memberId) {
        GroupMember member = members.get(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }
        if (member.getRole() == MemberRole.LEADER) {
            throw new IllegalStateException("Cannot remove the leader");
        }
        members.remove(memberId);
    }

    public GroupMember getMember(String memberId) {
        return members.get(memberId);
    }

    public GroupMember getMember(User user) {
        return user != null ? members.get(user.getUserId()) : null;
    }

    public boolean hasMember(String memberId) {
        return members.containsKey(memberId);
    }

    public boolean hasMember(User user) {
        return user != null && members.containsKey(user.getUserId());
    }

    public boolean isLeader(String memberId) {
        GroupMember member = members.get(memberId);
        return member != null && member.getRole() == MemberRole.LEADER;
    }

    public boolean isLeader(User user) {
        if (user == null) {
            return false;
        }
        return isLeader(user.getUserId());
    }

    public List<GroupMember> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    public int getMemberCount() {
        return members.size();
    }

    public GroupMember getLeader() {
        for (GroupMember m : members.values()) {
            if (m.getRole() == MemberRole.LEADER) {
                return m;
            }
        }
        return null;
    }

    public User getLeaderAsUser() {
        GroupMember leader = getLeader();
        if (leader instanceof RegisteredMember) {
            return ((RegisteredMember) leader).getUser();
        }
        return null;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getMaxMembers() {
        return maxMembers;
    }
}
