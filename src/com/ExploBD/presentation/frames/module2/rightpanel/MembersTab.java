package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.interfaces.GroupMember;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MembersTab extends JPanel {
    
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private Group currentGroup;
    
    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;
    
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);
    private Color GOLD = new Color(255, 215, 0);
    
    private int baseNormalSize = 12;
    private int baseSmallSize = 11;
    private int baseButtonSize = 11;
    
    public MembersTab(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        
        setLayout(new BorderLayout());
        setBackground(SOFT_WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
    
    public void setSuggestFields(JTextField placeField, JTextField reasonField, JButton submitBtn) {
        this.suggestPlaceField = placeField;
        this.suggestReasonField = reasonField;
        this.suggestSubmitBtn = submitBtn;
    }
    
    public void setGroup(Group group) {
        this.currentGroup = group;
        refresh();
    }
    
    public void refresh() {
        removeAll();
        
        if (currentGroup == null) return;
        
        User currentUser = dataManager.getCurrentUser();
        boolean isLeader = currentGroup.isLeader(currentUser);
        
        JPanel membersListPanel = new JPanel();
        membersListPanel.setLayout(new BoxLayout(membersListPanel, BoxLayout.Y_AXIS));
        membersListPanel.setBackground(SOFT_WHITE);
        membersListPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        
        for (GroupMember member : currentGroup.getAllMembers()) {
            MemberCard card = new MemberCard(member, isLeader, currentGroup, dataManager, photoUtils);
            
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dataManager.showMemberProfile(member, (JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            membersListPanel.add(card);
            membersListPanel.add(Box.createVerticalStrut(5));
        }
        
        JScrollPane membersScroll = new JScrollPane(membersListPanel);
        membersScroll.setBorder(null);
        membersScroll.getVerticalScrollBar().setUnitIncrement(16);
        membersScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        membersScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        membersScroll.getVerticalScrollBar().setBackground(SOFT_WHITE);
        
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBackground(SOFT_WHITE);
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        bottomBar.setBackground(SOFT_WHITE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 180, 160)),
                BorderFactory.createEmptyBorder(15, 0, 10, 0)
        ));
        bottomBar.setPreferredSize(new Dimension(350, 70));
        
        if (isLeader) {
            JButton inviteUserBtn = createCardStyleButton("Invite User", 105, 38);
            JButton inviteGuestBtn = createCardStyleButton("Invite Guest", 105, 38);
            JButton inviteFriendBtn = createCardStyleButton("Invite Friend", 105, 38);
            JButton approveBtn = createCardStyleButton("Request", 95, 38);
            approveBtn.setBackground(SUCCESS_GREEN);
            approveBtn.setForeground(Color.WHITE);
            
            inviteUserBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showInviteUserDialog((JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            inviteGuestBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showInviteGuestDialog((JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            inviteFriendBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showFriendDialog((JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            approveBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showRequestsDialog((JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            bottomBar.add(inviteUserBtn);
            bottomBar.add(inviteGuestBtn);
            bottomBar.add(inviteFriendBtn);
            bottomBar.add(approveBtn);
            
        } else {
            JButton requestUserBtn = createCardStyleButton("Request User", 110, 38);
            JButton requestGuestBtn = createCardStyleButton("Request Guest", 115, 38);
            JButton requestFriendBtn = createCardStyleButton("Request Friend", 115, 38);
            JButton leaveBtn = createCardStyleButton("Leave", 85, 38);
            leaveBtn.setBackground(ERROR_RED);
            leaveBtn.setForeground(Color.WHITE);
            
            requestUserBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showRequestUserDialog((JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            requestGuestBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showRequestGuestDialog((JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            requestFriendBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.showFriendRequestDialog((JFrame) SwingUtilities.getWindowAncestor(MembersTab.this));
                }
            });
            
            leaveBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dataManager.leaveGroup();
                }
            });
            
            bottomBar.add(requestUserBtn);
            bottomBar.add(requestGuestBtn);
            bottomBar.add(requestFriendBtn);
            bottomBar.add(leaveBtn);
        }
        
        bottomWrapper.add(bottomBar, BorderLayout.SOUTH);
        
        add(membersScroll, BorderLayout.CENTER);
        add(bottomWrapper, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    private JButton createCardStyleButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(Color.WHITE);
        button.setForeground(DARK_BROWN);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}