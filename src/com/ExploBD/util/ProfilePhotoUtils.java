package com.ExploBD.util;

import com.ExploBD.object.User;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.domain.members.GuestMember;
import com.ExploBD.data.databaseObject.UserDatabaseObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class ProfilePhotoUtils {

    private UserDatabaseObject userDAO;

    public ProfilePhotoUtils() {
        this.userDAO = new UserDatabaseObject();
    }
    private static final Color DARK_BROWN = new Color(70, 58, 47);
    private static final Color ORANGE = new Color(255, 153, 51);
    private static final Color LIGHT_CREAM = new Color(250, 245, 240);
    private static final Color GUEST_COLOR = new Color(100, 86, 72);
    private static final Color PENDING_COLOR = new Color(240, 240, 240);

    /**
     * Create a profile photo label for a User
     */
    public  JLabel createUserPhotoLabel(User user, int size) {
        JLabel photoLabel = createBaseLabel(size);

        if (user == null) {
            photoLabel.setText("?");
            photoLabel.setFont(new Font("Arial", Font.BOLD, size / 2));
            photoLabel.setForeground(DARK_BROWN);
            return photoLabel;
        }

        
        String imagePath = user.getProfileImagePath();
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("images/nophoto.jpg")) {
            ImageIcon icon = loadImage(imagePath, size);
            if (icon != null) {
                photoLabel.setIcon(icon);
                photoLabel.setText("");
                photoLabel.setBackground(Color.WHITE);
                return photoLabel;
            }
        }

        
        String displayName = user.getDisplayName();
        if (displayName != null && displayName.length() > 0) {
            photoLabel.setText(displayName.substring(0, 1).toUpperCase());
        } else {
            photoLabel.setText("?");
        }
        photoLabel.setFont(new Font("Arial", Font.BOLD, size / 2));
        photoLabel.setForeground(DARK_BROWN);
        photoLabel.setBackground(LIGHT_CREAM);

        return photoLabel;
    }

    
    public  JLabel createMemberPhotoLabel(GroupMember member, int size) {
        if (member instanceof RegisteredMember) {
            User user = ((RegisteredMember) member).getUser();
            return createUserPhotoLabel(user, size);
        } else {
            
            JLabel photoLabel = createBaseLabel(size);
            GuestMember guest = (GuestMember) member;

            String displayName = guest.getDisplayName();
            if (displayName != null && displayName.length() > 0) {
                photoLabel.setText(displayName.substring(0, 1).toUpperCase());
            } else {
                photoLabel.setText("G");
            }
            photoLabel.setFont(new Font("Arial", Font.BOLD, size / 2));
            photoLabel.setForeground(Color.WHITE);
            photoLabel.setBackground(GUEST_COLOR);
            return photoLabel;
        }
    }

    
    public  JLabel createEmailPhotoLabel(String email, String name, int size) {
        
        User user = userDAO.findByEmail(email);
        if (user != null) {
            return createUserPhotoLabel(user, size);
        }

       
        JLabel photoLabel = createBaseLabel(size);
        if (name != null && name.length() > 0) {
            photoLabel.setText(name.substring(0, 1).toUpperCase());
        } else if (email != null && email.length() > 0) {
            photoLabel.setText(email.substring(0, 1).toUpperCase());
        } else {
            photoLabel.setText("?");
        }
        photoLabel.setFont(new Font("Arial", Font.BOLD, size / 2));
        photoLabel.setForeground(DARK_BROWN);
        photoLabel.setBackground(LIGHT_CREAM);

        return photoLabel;
    }

    
    private  JLabel createBaseLabel(int size) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(size, size));
        label.setMinimumSize(new Dimension(size, size));
        label.setMaximumSize(new Dimension(size, size));
        label.setBackground(new Color(240, 240, 240));
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        return label;
    }

    
    private ImageIcon loadImage(String imagePath, int size) {
        try {
            URL url = null;
            if (imagePath.startsWith("/")) {
                url = ProfilePhotoUtils.class.getResource(imagePath);
            } else {
                url = ProfilePhotoUtils.class.getResource("/" + imagePath);
            }

            if (url == null) {
                File file = new File(imagePath);
                if (file.exists()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    if (icon.getIconWidth() > 0) {
                        return scaleIcon(icon, size);
                    }
                }
            } else {
                ImageIcon icon = new ImageIcon(url);
                if (icon.getIconWidth() > 0) {
                    return scaleIcon(icon, size);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
        return null;
    }

    
    private  ImageIcon scaleIcon(ImageIcon icon, int size) {
        Image img = icon.getImage();
        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();
        double scale = Math.min((double) (size - 4) / imgWidth, (double) (size - 4) / imgHeight);
        int scaledWidth = (int) (imgWidth * scale);
        int scaledHeight = (int) (imgHeight * scale);
        Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
