package com.ExploBD.presentation.frames;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.presentation.frames.module2.rightpanel.MemberCard;
import com.ExploBD.util.ProfilePhotoUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ViewOnlyMembersTab extends JPanel {
    
    private Group group;
    private User currentUser;
    private ProfilePhotoUtils photoUtils;
    private GroupDataManager dataManager;
    private JDialog parentDialog;
    
    private Color SOFT_WHITE = new Color(255, 250, 245);
    
    public ViewOnlyMembersTab(Group group, User currentUser, ProfilePhotoUtils photoUtils, JDialog parentDialog) {
        this.group = group;
        this.currentUser = currentUser;
        this.photoUtils = photoUtils;
        this.parentDialog = parentDialog;
        
        this.dataManager = new GroupDataManager(currentUser);
        dataManager.setSelectedGroup(group);
        
        setLayout(new BorderLayout());
        setBackground(SOFT_WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel membersList = new JPanel();
        membersList.setLayout(new BoxLayout(membersList, BoxLayout.Y_AXIS));
        membersList.setBackground(SOFT_WHITE);
        
        List<GroupMember> allMembers = group.getAllMembers();
        
        for (GroupMember member : allMembers) {
            MemberCard card = new MemberCard(member, false, group, dataManager, photoUtils);
            
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Window window = SwingUtilities.getWindowAncestor(ViewOnlyMembersTab.this);
                    while (window != null && !(window instanceof JFrame)) {
                        window = SwingUtilities.getWindowAncestor(window);
                    }
                    
                    if (window instanceof JFrame) {
                        dataManager.showMemberProfile(member, (JFrame) window);
                    } else {
                        dataManager.showMemberProfile(member, (JFrame) parentDialog.getOwner());
                    }
                }
            });
            
            membersList.add(card);
            membersList.add(Box.createVerticalStrut(3));
        }
        
        JScrollPane scrollPane = new JScrollPane(membersList);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        
        add(scrollPane, BorderLayout.CENTER);
    }
}