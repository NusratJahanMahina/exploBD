package com.ExploBD.service;

import com.ExploBD.data.databaseObject.FriendDatabaseObject;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.data.databaseObject.NotificationDatabaseObject;
import com.ExploBD.domain.entities.FriendRequest;
import com.ExploBD.domain.entities.Friendship;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.FriendRequestStatus;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.object.User;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class FriendService {

    private FriendDatabaseObject friendDAO;
    private UserDatabaseObject userDAO;
    private NotificationDatabaseObject notificationDAO;
    private User currentUser;

    public FriendService(User currentUser) {
        this.friendDAO = new FriendDatabaseObject();
        this.userDAO = new UserDatabaseObject();
        this.notificationDAO = new NotificationDatabaseObject();
        this.currentUser = currentUser;
    }

    // Get all friends
    /* public List<User> getFriends() {
        List<Friendship> friendships = friendDAO.getUserFriendships(currentUser.getUserId());
        List<User> friends = new ArrayList<>();
        
        for (Friendship friendship : friendships) {
            User friend = friendship.getOtherUser(currentUser);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }*/
    public boolean acceptFriendRequest(String fromUserId, String requestId) {
        System.out.println("=== ACCEPTING FRIEND REQUEST ===");
        System.out.println("From User ID: " + fromUserId);
        System.out.println("Request ID: " + requestId);
        System.out.println("Current User ID: " + currentUser.getUserId());

        // Update request status
        boolean updated = friendDAO.updateFriendRequestStatus(requestId, FriendRequestStatus.ACCEPTED);
        System.out.println("Request status updated: " + updated);

        if (updated) {
            // Create friendship
            boolean friendshipCreated = friendDAO.createFriendship(fromUserId, currentUser.getUserId());
            System.out.println("Friendship created: " + friendshipCreated);

            if (friendshipCreated) {
                friendDAO.deleteFriendRequest(requestId);
                System.out.println("Request deleted");
                // ===== ADD NOTIFICATION FOR REQUESTER =====
//                try {
//                    User requester = userDAO.findById(fromUserId);
//                    if (requester != null && !requester.getUserId().equals(currentUser.getUserId())) {
//                        Notification notif = new Notification(
//                                requester,
//                                NotificationType.FRIEND_REQUEST,
//                                "Friend Request Accepted",
//                                currentUser.getDisplayName() + " accepted your friend request",
//                                requestId
//                        );
//                        new NotificationDatabaseObject().save(notif);
//                    }
//                } catch (Exception notifEx) {
//                    System.out.println("Failed to send notification: " + notifEx.getMessage());
//                }

                try {
                    User requester = userDAO.findById(fromUserId);
                    if (requester != null && !requester.getUserId().equals(currentUser.getUserId())) {
                        Notification notif = new Notification(
                                requester,
                                NotificationType.FRIEND_REQUEST_ACCEPTED,
                                "🤝 Friend Request Accepted",
                                currentUser.getDisplayName() + " accepted your friend request",
                                requestId
                        );
                        new NotificationDatabaseObject().save(notif);
                    }
                } catch (Exception e) {
                    System.out.println("Failed to send FRIEND_REQUEST_ACCEPTED notification: " + e.getMessage());
                }

                //till this
                return true;
            }
        }
        return false;
    }

    // Get friend names (for display)
    /*  public List<String> getFriendNames() {
        return friendDAO.getFriendNames(currentUser.getUserId());
    }*/
    // Get friend count
    public int getFriendCount() {
        return friendDAO.getFriendCount(currentUser.getUserId());
    }

    // Get pending friend requests (sent to current user)
    /*  public List<FriendRequest> getPendingRequests() {
        return friendDAO.getPendingRequestsForUser(currentUser.getUserId());
    }
     */
    // Get pending request names
    public List<String> getPendingRequestNames() {
        return friendDAO.getPendingRequestNames(currentUser.getUserId());
    }

    // Get suggestions
    // Decline friend request
    public boolean declineFriendRequest(String requestId) {
        boolean updated = friendDAO.updateFriendRequestStatus(requestId, FriendRequestStatus.DECLINED);
        if (updated) {
            friendDAO.deleteFriendRequest(requestId);
            return true;
        }
        return false;
    }

    public List<FriendRequest> getPendingRequests() {
        friendDAO.debugPrintAllRequests(); // ADD THIS LINE

        List<FriendRequest> requests = friendDAO.getPendingRequestsForUser(currentUser.getUserId());
        System.out.println("FriendService.getPendingRequests() returned: " + requests.size());
        return requests;
    }
    // Send friend request

    public void checkRequestInDatabase(String toUserId) {
        FriendRequest request = friendDAO.findFriendRequest(currentUser.getUserId(), toUserId);
        if (request != null) {
            System.out.println("Request found in DB: " + request.getRequestId());
            System.out.println("Status: " + request.getStatus());
            System.out.println("From: " + request.getFrom().getEmail());
            System.out.println("To: " + request.getTo().getEmail());
        } else {
            System.out.println("No request found in DB");
        }
    }
    // Add this method to FriendService.java

    public List<User> searchUsers(String searchTerm) {
        UserDatabaseObject userDAO = new UserDatabaseObject();
        return userDAO.searchUsers(searchTerm, currentUser.getUserId());
    }

// Add this to get search results as display names with profile pics
    public List<SearchResult> searchUsersWithDetails(String searchTerm) {
        List<User> users = searchUsers(searchTerm);
        List<SearchResult> results = new ArrayList<>();

        for (User user : users) {
            boolean isFriend = friendDAO.areFriends(currentUser.getUserId(), user.getUserId());
            boolean hasPendingRequest = friendDAO.findFriendRequest(currentUser.getUserId(), user.getUserId()) != null;

            results.add(new SearchResult(user, isFriend, hasPendingRequest));
        }
        return results;
    }

// Inner class to hold search result data
    public static class SearchResult {

        private User user;
        private boolean isFriend;
        private boolean hasPendingRequest;

        public SearchResult(User user, boolean isFriend, boolean hasPendingRequest) {
            this.user = user;
            this.isFriend = isFriend;
            this.hasPendingRequest = hasPendingRequest;
        }

        public User getUser() {
            return user;
        }

        public boolean isFriend() {
            return isFriend;
        }

        public boolean hasPendingRequest() {
            return hasPendingRequest;
        }
    }
// Update this method in FriendService.java

    public List<User> getSuggestions() {
        return friendDAO.getSuggestionsForUser(currentUser.getUserId());
    }

    public List<String> getSuggestionNames() {
        List<User> suggestions = getSuggestions();
        List<String> names = new ArrayList<>();
        for (User user : suggestions) {
            names.add(user.getDisplayName());
        }
        return names;
    }

// NEW: Get suggestions with full user objects (including profile pics)
    public List<User> getSuggestionUsers() {
        return friendDAO.getSuggestionsForUser(currentUser.getUserId());
    }
// Add this method to get suggestion users (returns User objects, not just names)

// Add this method to remove a friend
    public boolean removeFriend(String friendUserId) {
        return friendDAO.removeFriendship(currentUser.getUserId(), friendUserId);
    }

// Fix the acceptFriendRequest method (replace your existing one)
    public boolean sendFriendRequest(String toUserId) {
        System.out.println("=== SEND FRIEND REQUEST ===");
        System.out.println("Current User ID: " + currentUser.getUserId());
        System.out.println("Target User ID: " + toUserId);

        // Check if already friends
        if (friendDAO.areFriends(currentUser.getUserId(), toUserId)) {
            JOptionPane.showMessageDialog(null, "You are already friends.", "Already Friends", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Check if request already exists
        FriendRequest existingRequest = friendDAO.findFriendRequest(currentUser.getUserId(), toUserId);
        if (existingRequest != null) {
            JOptionPane.showMessageDialog(null, "Request already sent.", "Request Pending", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Get target user
        User toUser = userDAO.findById(toUserId);
        if (toUser == null) {
            System.out.println("Target user not found!");
            return false;
        }
        System.out.println("Target User: " + toUser.getDisplayName() + " (" + toUser.getEmail() + ")");

        // Create and save request
        FriendRequest request = new FriendRequest(currentUser, toUser);
        System.out.println("Request ID: " + request.getRequestId());

        boolean saved = friendDAO.saveFriendRequest(request);
        System.out.println("Save result: " + saved);

        if (saved) {
            // Verify it was actually saved by trying to find it
            FriendRequest verify = friendDAO.findFriendRequest(currentUser.getUserId(), toUserId);
            System.out.println("Verification - Found request: " + (verify != null));

            // Create notification
            try {
                Notification notif = new Notification(
                        toUser,
                        NotificationType.FRIEND_REQUEST,
                        "New Friend Request",
                        currentUser.getDisplayName() + " sent you a friend request",
                        request.getRequestId()
                );
                notificationDAO.save(notif);
            } catch (Exception e) {
                System.out.println("Notification error: " + e.getMessage());
            }

            JOptionPane.showMessageDialog(null, "Friend request sent to " + toUser.getDisplayName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        return false;
    }
// Add this method to FriendService.java

    public void debugCheckRequests() {
        System.out.println("=== DEBUG: Checking requests for user: " + currentUser.getUserId());
        System.out.println("User email: " + currentUser.getEmail());
        friendDAO.debugPrintAllRequests();

        List<FriendRequest> requests = friendDAO.getPendingRequestsForUser(currentUser.getUserId());
        System.out.println("Pending requests for this user: " + requests.size());
        for (FriendRequest req : requests) {
            System.out.println("  - From: " + req.getFrom().getDisplayName() + " (ID: " + req.getFrom().getUserId() + ")");
            System.out.println("    Request ID: " + req.getRequestId());
            System.out.println("    Status: " + req.getStatus());
        }
    }

    /*public List<User> getFriends() {
    System.out.println("=== GETTING FRIENDS FOR USER: " + currentUser.getUserId());
     friendDAO.debugPrintAllFriendships(); 
    List<Friendship> friendships = friendDAO.getUserFriendships(currentUser.getUserId());
    System.out.println("Friendships found: " + friendships.size());
    
    List<User> friends = new ArrayList<>();
    for (Friendship friendship : friendships) {
        User friend = friendship.getOtherUser(currentUser);
        if (friend != null) {
            System.out.println("  Friend found: " + friend.getDisplayName() + " (ID: " + friend.getUserId() + ")");
            friends.add(friend);
        } else {
            System.out.println("  Friend was null for this friendship");
        }
    }
    System.out.println("Total friends returned: " + friends.size());
    return friends;
}*/
    public List<String> getFriendNames() {
        System.out.println("\n=== GET FRIEND NAMES ===");
        List<User> friends = getFriends();
        System.out.println("Friends count: " + friends.size());

        List<String> names = new ArrayList<>();
        for (User friend : friends) {
            String name = friend.getDisplayName();
            System.out.println("Adding friend: " + name);
            names.add(name);
        }
        return names;
    }

    public List<User> getFriends() {
        System.out.println("\n=== FRIEND SERVICE: getFriends() ===");
        System.out.println("Current User ID: " + currentUser.getUserId());

        List<Friendship> friendships = friendDAO.getUserFriendships(currentUser.getUserId());
        System.out.println("Friendships from DAO: " + friendships.size());

        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendships) {
            System.out.println("Checking friendship: " + friendship.getFriendshipId());
            System.out.println("  User1: " + friendship.getUser1().getUserId());
            System.out.println("  User2: " + friendship.getUser2().getUserId());

            User friend = friendship.getOtherUser(currentUser);
            if (friend != null) {
                System.out.println("  Friend found: " + friend.getDisplayName());
                friends.add(friend);
            } else {
                System.out.println("  getOtherUser returned null!");
            }
        }

        System.out.println("Returning " + friends.size() + " friends");
        return friends;
    }
    /*public List<FriendRequest> getPendingRequests() {
    List<FriendRequest> requests = friendDAO.getPendingRequestsForUser(currentUser.getUserId());
    System.out.println("FriendService.getPendingRequests() for user " + currentUser.getUserId() + " returned: " + requests.size());
    for (FriendRequest req : requests) {
        System.out.println("  Request from: " + req.getFrom().getDisplayName() + ", ID: " + req.getRequestId());
    }
    return requests;
}*/

}
