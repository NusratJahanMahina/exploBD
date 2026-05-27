package com.ExploBD.session;

import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.data.databaseObject.NotificationDatabaseObject;
import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.util.RefreshManager;
import java.util.*;

public class UserSession {

    private static UserSession instance;
    private User currentUser;
    private Map<String, Object> tempData;
    private List<Group> myGroups;
    private List<Invitation> pendingInvitations;
    private List<Notification> notifications;

    private UserSession() {
        tempData = new HashMap<>();
        myGroups = new ArrayList<>();
        pendingInvitations = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        tempData.clear();
        loadUserData();
    }

    public void loadUserData() {
        if (currentUser == null) {
            return;
        }

        GroupDatabaseObject groupDAO = new GroupDatabaseObject();
        InvitationDatabaseObject invDAO = new InvitationDatabaseObject();
        NotificationDatabaseObject notifDAO = new NotificationDatabaseObject();

        this.myGroups = groupDAO.findByUser(currentUser.getUserId());
        this.pendingInvitations = invDAO.findPendingByEmail(currentUser.getEmail());
        this.notifications = notifDAO.findByUser(currentUser.getUserId());

        RefreshManager.getInstance().refreshAll();
    }

    public void logout() {
        this.currentUser = null;
        tempData.clear();
        myGroups.clear();
        pendingInvitations.clear();
        notifications.clear();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public List<Group> getMyGroups() {
        return myGroups;
    }

    public List<Invitation> getPendingInvitations() {
        return pendingInvitations;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public int getUnreadNotificationCount() {
        int count = 0;
        for (Notification n : notifications) {
            if (!n.isRead()) {
                count++;
            }
        }
        return count;
    }

    public void setAttribute(String key, Object value) {
        tempData.put(key, value);
    }

    public Object getAttribute(String key) {
        return tempData.get(key);
    }

    public void removeAttribute(String key) {
        tempData.remove(key);
    }

    public boolean hasAttribute(String key) {
        return tempData.containsKey(key);
    }
}