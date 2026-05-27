package com.ExploBD.object;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.domain.entities.FriendRequest;
import com.ExploBD.domain.entities.Friendship;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.NotificationType;
import java.util.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import javax.swing.JComboBox;

public class User extends ExploBDObject {

 //   nazifa
       private int groupId;

    private int budget;

    public int getBudget() {
        return budget;
    }
    private String preference;

    public String getPreference() {
        return preference;
    }

    private String division;

    public String getDivision() {
        return division;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public void setDivision(String division) {
        this.division = division;
    }

//nazifa end 
    
    
    
    
    
    
    
    
    
    
    
    
    private String username;
    private String email;
    private int securityQuestionIndex;
    private String fullName;
    private String nationality;
    private String phone;
    private String emergencyContact;
    private String nidPassport;
    private String dateOfBirth;
    private String gender;
    private int minBudget;
    private int maxBudget;
    private String profileImagePath;
    private List<String> travelStyles;
    private List<String> favoritePlaces;
    private List<String> wishlistPlaces;
    private List<String> visitedPlaces;
    private List<Friendship> friendships;
    private List<FriendRequest> sentFriendRequests;
    private List<FriendRequest> receivedFriendRequests;
    private boolean isLeader;
    private boolean isProfileComplete;
    private Date lastUpdated;

    public User(String userId, String username, String email, int securityQuestionIndex) {
        super(userId, username);
        this.username = username;
        this.email = email;
        this.securityQuestionIndex = securityQuestionIndex;
        this.travelStyles = new ArrayList<>();
        this.favoritePlaces = new ArrayList<>();
        this.wishlistPlaces = new ArrayList<>();
        this.visitedPlaces = new ArrayList<>();
        this.friendships = new ArrayList<>();
        this.sentFriendRequests = new ArrayList<>();
        this.receivedFriendRequests = new ArrayList<>();
        this.nationality = "Bangladeshi";
        this.minBudget = 500;
        this.maxBudget = 10000;
        this.isLeader = false;
        this.isProfileComplete = false;
        this.lastUpdated = new Date();
    }
    
    
    
    //nazifa 
    
        //Nazifa
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
        public String getDateFromComponents(JComboBox<String> day, JComboBox<String> month, JComboBox<String> year) {
        if (day.getSelectedItem() == null || month.getSelectedItem() == null || year.getSelectedItem() == null) {
            return "";
        }
        String dayStr = String.format("%02d", Integer.parseInt((String) day.getSelectedItem()));
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int monthNum = Arrays.asList(months).indexOf(month.getSelectedItem()) + 1;
        String monthStr = String.format("%02d", monthNum);
        return dayStr + "-" + monthStr + "-" + year.getSelectedItem();
    }

    //etuk
    //nazifa 
// Budget Methods
    // public boolean matchesBudget(double cost) {
    //  return cost >= minBudget && cost <= maxBudget;
    // }
    // public double getBudgetMidpoint() {
    //  return (minBudget + maxBudget) / 2.0;
    // }
    //etuk nazifar code bt 2 ber ase...
    
    //till this
    

    @Override
    public String getObjectType() {
        return "USER";
    }

    public int getAge() {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return 0;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isForeigner() {
        return nationality != null && !nationality.equalsIgnoreCase("Bangladeshi");
    }

    public boolean isProfileComplete() {
        return isProfileComplete && fullName != null && !fullName.isEmpty()
                && phone != null && !phone.isEmpty();
    }

    public void addTravelStyle(String style) {
        if (style != null && !style.trim().isEmpty()) {
            String upperStyle = style.toUpperCase().trim();
            if (!travelStyles.contains(upperStyle)) {
                travelStyles.add(upperStyle);
            }
        }
    }

    public void removeTravelStyle(String style) {
        travelStyles.remove(style.toUpperCase());
    }

    public boolean hasTravelStyle(String style) {
        return travelStyles.contains(style.toUpperCase());
    }

    public void addFavoritePlace(String placeId) {
        if (placeId != null && !placeId.trim().isEmpty() && !favoritePlaces.contains(placeId)) {
            favoritePlaces.add(placeId);
        }
    }

    public void removeFavoritePlace(String placeId) {
        favoritePlaces.remove(placeId);
    }

    public boolean hasFavoritePlace(String placeId) {
        return favoritePlaces.contains(placeId);
    }

    public void addWishlistPlace(String placeId) {
        if (placeId != null && !placeId.trim().isEmpty() && !wishlistPlaces.contains(placeId)) {
            wishlistPlaces.add(placeId);
        }
    }

    public void removeWishlistPlace(String placeId) {
        wishlistPlaces.remove(placeId);
    }

    public boolean hasWishlistPlace(String placeId) {
        return wishlistPlaces.contains(placeId);
    }

    public void addVisitedPlace(String placeId) {
        if (placeId != null && !placeId.trim().isEmpty() && !visitedPlaces.contains(placeId)) {
            visitedPlaces.add(placeId);
        }
    }

    public void removeVisitedPlace(String placeId) {
        visitedPlaces.remove(placeId);
    }

    public boolean hasVisitedPlace(String placeId) {
        return visitedPlaces.contains(placeId);
    }

    public boolean matchesBudget(double cost) {
        return cost >= minBudget && cost <= maxBudget;
    }

    public double getBudgetMidpoint() {
        return (minBudget + maxBudget) / 2.0;
    }

    public int getPreferenceMatchScore(List<String> placeStyles) {
        int score = 0;
        for (String style : travelStyles) {
            if (placeStyles.contains(style)) {
                score += 10;
            }
        }
        return score;
    }

    public List<String> getCommonStylesWith(User otherUser) {
        List<String> common = new ArrayList<>();
        for (String style : travelStyles) {
            if (otherUser.hasTravelStyle(style)) {
                common.add(style);
            }
        }
        return common;
    }
    
    public String getUserId() {
        return getId();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        setName(username);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSecurityQuestionIndex() {
        return securityQuestionIndex;
    }

    public void setSecurityQuestionIndex(int securityQuestionIndex) {
        this.securityQuestionIndex = securityQuestionIndex;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getNidPassport() {
        return nidPassport;
    }

    public void setNidPassport(String nidPassport) {
        this.nidPassport = nidPassport;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
      //  System.out.println("[DEBUG] Setting dateOfBirth to: " + dateOfBirth);
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(int minBudget) {
        this.minBudget = minBudget;
    }

    public int getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(int maxBudget) {
        this.maxBudget = maxBudget;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public List<String> getTravelStyles() {
        return new ArrayList<>(travelStyles);
    }

    public void setTravelStyles(List<String> travelStyles) {
        this.travelStyles = new ArrayList<>(travelStyles);
    }

    public String getTravelStylesAsString() {
        return String.join(",", travelStyles);
    }

//    public void setTravelStylesFromString(String stylesString) {
//        travelStyles.clear();
//        if (stylesString != null && !stylesString.isEmpty()) {
//            String[] styles = stylesString.split(",");
//            for (String style : styles) {
//                addTravelStyle(style);
//            }
//        }
//    }

    public List<String> getFavoritePlaces() {
        return new ArrayList<>(favoritePlaces);
    }

    public void setFavoritePlaces(List<String> favoritePlaces) {
        this.favoritePlaces = new ArrayList<>(favoritePlaces);
    }

    public String getFavoritePlacesAsString() {
        return String.join(",", favoritePlaces);
    }

    public void setFavoritePlacesFromString(String placesString) {
        favoritePlaces.clear(); //nai
        if (placesString != null && !placesString.isEmpty()) {
            String[] places = placesString.split(",");
            for (String place : places) {
                addFavoritePlace(place);
            }
        }
    }

    public List<String> getWishlistPlaces() {
        return new ArrayList<>(wishlistPlaces);
    }

    public void setWishlistPlaces(List<String> wishlistPlaces) {
        this.wishlistPlaces = new ArrayList<>(wishlistPlaces);
    }

    public String getWishlistPlacesAsString() {
        return String.join(",", wishlistPlaces);
    }

    public void setWishlistPlacesFromString(String placesString) {
        wishlistPlaces.clear(); //nai
        if (placesString != null && !placesString.isEmpty()) {
            String[] places = placesString.split(",");
            for (String place : places) {
                addWishlistPlace(place);
            }
        }
    }

    public List<String> getVisitedPlaces() {
        return new ArrayList<>(visitedPlaces);
    }

    public void setVisitedPlaces(List<String> visitedPlaces) {
        this.visitedPlaces = new ArrayList<>(visitedPlaces);
    }

    public String getVisitedPlacesAsString() {
        return String.join(",", visitedPlaces);
    }

    public void setVisitedPlacesFromString(String placesString) {
        visitedPlaces.clear();
        if (placesString != null && !placesString.isEmpty()) {
            String[] places = placesString.split(",");
            for (String place : places) {
                addVisitedPlace(place);
            }
        }
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public void setProfileComplete(boolean profileComplete) {
        isProfileComplete = profileComplete;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSecurityQuestionText() {
        if (securityQuestionIndex >= 0 && securityQuestionIndex < DatabaseConnection.SECURITY_QUESTIONS.length) {
            return DatabaseConnection.SECURITY_QUESTIONS[securityQuestionIndex];
        }
        return "Unknown question";
    }

    @Override
    public String toString() {
        String status = isProfileComplete() ? "Complete" : "Incomplete";
        return String.format("%s (%s) - %s - Budget: %d-%d tk",
                fullName != null ? fullName : username,
                email,
                status,
                minBudget,
                maxBudget);
    }

    public boolean canCreateGroup() {
        return isProfileComplete();
    }

    public boolean canJoinGroup() {
        return true;
    }

    public String getDisplayName() {
        return fullName != null ? fullName : username;
    }

    public void sendFriendRequest(User toUser) {
        FriendRequest request = new FriendRequest(this, toUser);
        com.ExploBD.data.databaseObject.FriendDatabaseObject friendDAO = new com.ExploBD.data.databaseObject.FriendDatabaseObject();
        boolean saved = friendDAO.saveFriendRequest(request);

        if (saved) {
            sentFriendRequests.add(request);
            toUser.getReceivedFriendRequests().add(request);

            Notification notif = new Notification(
                    toUser,
                    NotificationType.FRIEND_REQUEST,
                    "Friend Request",
                    this.getDisplayName() + " sent you a friend request",
                    request.getRequestId()
            );
            new com.ExploBD.data.databaseObject.NotificationDatabaseObject().save(notif);
        }
    }

    public void acceptFriendRequest(FriendRequest request) {
        if (receivedFriendRequests.contains(request)) {
            request.accept();

            com.ExploBD.data.databaseObject.FriendDatabaseObject friendDAO = new com.ExploBD.data.databaseObject.FriendDatabaseObject();
            friendDAO.updateFriendRequestStatus(request.getRequestId(), com.ExploBD.domain.enums.FriendRequestStatus.ACCEPTED);

            Friendship friendship = new Friendship(this, request.getFrom());
            friendDAO.createFriendship(this.getUserId(), request.getFrom().getUserId());

            friendships.add(friendship);
            request.getFrom().getFriendships().add(friendship);
            receivedFriendRequests.remove(request);

            friendDAO.deleteFriendRequest(request.getRequestId());
        }
    }

    public boolean isFriendWith(User otherUser) {
        for (Friendship f : friendships) {
            if (f.getUser1().equals(otherUser) || f.getUser2().equals(otherUser)) {
                return true;
            }
        }
        return false;
    }

    public List<User> getFriends() {
        List<User> friends = new ArrayList<>();
        for (Friendship f : friendships) {
            User other = f.getOtherUser(this);
            if (other != null) {
                friends.add(other);
            }
        }
        return friends;
    }

    public List<Friendship> getFriendships() {
        return friendships;
    }

    public List<FriendRequest> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public List<FriendRequest> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public void loadFriendsFromDatabase() {
        com.ExploBD.data.databaseObject.FriendDatabaseObject friendDAO = new com.ExploBD.data.databaseObject.FriendDatabaseObject();

        this.friendships = friendDAO.getUserFriendships(this.getUserId());
        this.receivedFriendRequests = friendDAO.getPendingRequestsForUser(this.getUserId());
        this.sentFriendRequests = friendDAO.getSentRequestsFromUser(this.getUserId());

        System.out.println("Loaded " + friendships.size() + " friendships, "
                + receivedFriendRequests.size() + " received requests, "
                + sentFriendRequests.size() + " sent requests");
    }

    
    
    
    
    
    
    
    
    
    
    
    
    //naxifa
       public void applyTravelStylesToCheckboxes(javax.swing.JCheckBox beach, javax.swing.JCheckBox mountain,
            javax.swing.JCheckBox urban, javax.swing.JCheckBox historical, javax.swing.JCheckBox culture,
            javax.swing.JCheckBox ecotourism, javax.swing.JCheckBox adventure, javax.swing.JCheckBox relaxation,
            javax.swing.JCheckBox photographic, javax.swing.JCheckBox nature, javax.swing.JCheckBox food) {
        beach.setSelected(hasTravelStyle("BEACH"));
        mountain.setSelected(hasTravelStyle("MOUNTAIN"));
        urban.setSelected(hasTravelStyle("URBAN"));
        historical.setSelected(hasTravelStyle("HISTORICAL"));
        culture.setSelected(hasTravelStyle("CULTURE"));
        ecotourism.setSelected(hasTravelStyle("ECOTOURISM"));
        adventure.setSelected(hasTravelStyle("ADVENTURE"));
        relaxation.setSelected(hasTravelStyle("RELAXATION"));
        photographic.setSelected(hasTravelStyle("PHOTOGRAPHIC"));
        nature.setSelected(hasTravelStyle("NATURE"));
        food.setSelected(hasTravelStyle("FOOD"));
    }
    
    public void updateTravelStylesFromCheckboxes(javax.swing.JCheckBox beach, javax.swing.JCheckBox mountain,
            javax.swing.JCheckBox urban, javax.swing.JCheckBox historical, javax.swing.JCheckBox culture,
            javax.swing.JCheckBox ecotourism, javax.swing.JCheckBox adventure, javax.swing.JCheckBox relaxation,
            javax.swing.JCheckBox photographic, javax.swing.JCheckBox nature, javax.swing.JCheckBox food) {
        travelStyles.clear();
        if (beach.isSelected()) {
            addTravelStyle("BEACH");
        }
        if (mountain.isSelected()) {
            addTravelStyle("MOUNTAIN");
        }
        if (urban.isSelected()) {
            addTravelStyle("URBAN");
        }
        if (historical.isSelected()) {
            addTravelStyle("HISTORICAL");
        }
        if (culture.isSelected()) {
            addTravelStyle("CULTURE");
        }
        if (ecotourism.isSelected()) {
            addTravelStyle("ECOTOURISM");
        }
        if (adventure.isSelected()) {
            addTravelStyle("ADVENTURE");
        }
        if (relaxation.isSelected()) {
            addTravelStyle("RELAXATION");
        }
        if (photographic.isSelected()) {
            addTravelStyle("PHOTOGRAPHIC");
        }
        if (nature.isSelected()) {
            addTravelStyle("NATURE");
        }
        if (food.isSelected()) {
            addTravelStyle("FOOD");
        }
    }

    
        public void setDateToComponents(JComboBox<String> day, JComboBox<String> month, JComboBox<String> year) {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return;
        }
        String[] parts = dateOfBirth.split("-");
        if (parts.length == 3) {
            day.setSelectedItem(String.valueOf(Integer.parseInt(parts[0])));
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            month.setSelectedItem(months[Integer.parseInt(parts[1]) - 1]);
            year.setSelectedItem(parts[2]);
        }
    }

    public void setTravelStylesFromString(String travelStylesStr) {
        if (travelStylesStr != null && !travelStylesStr.isEmpty()) {
            // travelStylesStr কমা দিয়ে আলাদা করলে List<String> তে রূপান্তর
            travelStyles = new ArrayList<>();
            String[] arr = travelStylesStr.split(",");
            for (String style : arr) {
                travelStyles.add(style.trim());
            }
        }
    }

    
    //till this
    
    
    
}
