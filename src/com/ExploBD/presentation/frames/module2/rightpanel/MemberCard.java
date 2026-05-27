package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.domain.members.GuestMember;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MemberCard extends JPanel {
    
    private GroupMember member;
    private Group group;
    private GroupDataManager dataManager;
    private boolean isLeader;
    private ProfilePhotoUtils photoUtils;
    
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color ERROR_RED = new Color(198, 40, 40);
    
    private int baseNormalSize = 12;
    private int baseSmallSize = 11;
    private int baseButtonSize = 11;
    
    public MemberCard(GroupMember member, boolean isLeader, Group group, 
                      GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.member = member;
        this.isLeader = isLeader;
        this.group = group;
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        setPreferredSize(new Dimension(200, 75));
        
        JLabel photoLabel = photoUtils.createMemberPhotoLabel(member, 50);
        photoLabel.setPreferredSize(new Dimension(50, 50));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));
        
        String displayName = member.getDisplayName();
        String email = "";
        boolean isPending = false;
        
        if (member instanceof RegisteredMember) {
            User user = ((RegisteredMember) member).getUser();
            email = user.getEmail();
        } else {
            GuestMember guest = (GuestMember) member;
            email = guest.getEmail() != null ? guest.getEmail() : "No email";
            isPending = true;
        }
        
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, baseNormalSize));
        nameLabel.setForeground(DARK_BROWN);
        
        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, baseSmallSize - 1));
        emailLabel.setForeground(Color.GRAY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(emailLabel);
        
        add(photoLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 8));
        rightPanel.setBackground(Color.WHITE);
        
        if (member.getRole().toString().equals("LEADER")) {
            JLabel leaderLabel = new JLabel("LEADER");
            leaderLabel.setFont(new Font("Arial", Font.BOLD, baseSmallSize - 1));
            leaderLabel.setForeground(ORANGE);
            leaderLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            leaderLabel.setOpaque(true);
            leaderLabel.setBackground(new Color(255, 240, 220));
            rightPanel.add(leaderLabel);
        } else if (isPending) {
            JLabel pendingLabel = new JLabel("PENDING");
            pendingLabel.setFont(new Font("Arial", Font.BOLD, baseSmallSize - 1));
            pendingLabel.setForeground(Color.GRAY);
            pendingLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            pendingLabel.setOpaque(true);
            pendingLabel.setBackground(new Color(240, 240, 240));
            rightPanel.add(pendingLabel);
        }
        
        if (isLeader && !member.getRole().toString().equals("LEADER")) {
            JButton removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font("Arial", Font.BOLD, baseButtonSize - 2));
            removeBtn.setForeground(ERROR_RED);
            removeBtn.setBackground(Color.WHITE);
            removeBtn.setBorder(BorderFactory.createLineBorder(ERROR_RED));
            removeBtn.setFocusPainted(false);
            removeBtn.setPreferredSize(new Dimension(65, 25));
            removeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(MemberCard.this,
                            "Remove " + member.getDisplayName() + " from group?",
                            "Remove Member", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            group.removeMember(member.getId());
                            new GroupDatabaseObject().saveGroup(group);
                            dataManager.refreshData();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(MemberCard.this, 
                                "Error: " + ex.getMessage());
                        }
                    }
                }
            });
            rightPanel.add(removeBtn);
        }
        
        add(rightPanel, BorderLayout.EAST);
    }
}