package com.ExploBD.service;

import com.ExploBD.object.User;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.session.UserSession;
import java.util.List;

public class UserService {
    
    private UserDatabaseObject userDAO;  

    public UserService() {
        this.userDAO = new UserDatabaseObject();  
    }

    public User login(String email, String password) {
        
        User user = userDAO.loginUser(email, password);
        if (user != null) {
            UserSession.getInstance().login(user);
        }
        return user;
    }
    
    public User register(String username, String email, String password, 
                        int securityIndex, String securityAnswer) {
        
        boolean success = userDAO.registerUser(username, email, password, 
                                                    securityIndex, securityAnswer);
        if (success) {
            return userDAO.loginUser(email, password);
        }
        return null;
    }
    
//    public boolean updateProfile(User user) {
//        
//        boolean success = userDAO.saveUserProfile(user);
//        if (success) {
//            UserSession.getInstance().login(user);
//        }
//        return success;
//    }
    public boolean updateProfile(User user) {
    boolean success = userDAO.saveUserProfile(user);
    if (success) {
        // Reload from database to get fresh data
        User updatedUser = userDAO.findById(user.getUserId());
        if (updatedUser != null) {
            UserSession.getInstance().login(updatedUser);
        } else {
            UserSession.getInstance().login(user);
        }
    }
    return success;
}
    
    public User getCurrentUser() {
        return UserSession.getInstance().getCurrentUser();
    }
    
    public void logout() {
        UserSession.getInstance().logout();
    }
    
    public List<User> searchUsers(String searchTerm) {
        
        return userDAO.searchUsers(searchTerm, getCurrentUser().getUserId());
    }

    
    public String getSecurityQuestion(String email) {
        return userDAO.getSecurityQuestion(email);
    }

    public boolean resetPassword(String email, int securityIndex, String answer, String newPassword) {
        return userDAO.resetPassword(email, securityIndex, answer, newPassword);
    }
}