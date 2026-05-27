package com.ExploBD.presentation.components;

import com.ExploBD.domain.enums.MemberRole;
import com.ExploBD.domain.enums.MemberType;
import com.ExploBD.domain.interfaces.GroupMember;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MemberCardPanel extends BaseCard<GroupMember> {
    
    private boolean showRemoveButton;
    private ActionListener removeListener;
    
    public MemberCardPanel(GroupMember member, boolean showRemoveButton) {
        super(member);
        this.showRemoveButton = showRemoveButton;
        
    }
    
    @Override
    protected JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(40, 40));
        
        String icon = data.getType() == MemberType.REGISTERED ? "👤" : "👥";
        JLabel iconLabel = createIconLabel(icon, 24);
        iconLabel.setForeground(data.getType() == MemberType.REGISTERED ? DARK_BROWN : Color.GRAY);
        
        panel.add(iconLabel, BorderLayout.CENTER);
        return panel;
    }
    
    @Override
    protected JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 5));
        
        JLabel nameLabel = new JLabel(data.getDisplayName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 13));
        nameLabel.setForeground(DARK_BROWN);
        
        String typeText = data.getType() == MemberType.REGISTERED ? "Registered" : "Guest";
        JLabel typeLabel = new JLabel(typeText);
        typeLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        typeLabel.setForeground(Color.GRAY);
        
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(typeLabel);
        
        return panel;
    }
    
    @Override
    protected JPanel createRightPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panel.setOpaque(false);
        
        
        JLabel roleLabel = new JLabel(getRoleText());
        roleLabel.setFont(new Font("Arial", Font.BOLD, 10));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setOpaque(true);
        roleLabel.setBackground(getRoleColor());
        roleLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        panel.add(roleLabel);
        
        
        if (showRemoveButton && data.getRole() != MemberRole.LEADER) {
            JButton removeBtn = createActionButton("✕", Color.RED);
            removeBtn.setPreferredSize(new Dimension(22, 22));
            if (removeListener != null) {
                removeBtn.addActionListener(removeListener);
            }
            panel.add(removeBtn);
        }
        
        return panel;
    }
    
    private String getRoleText() {
        if (data.getRole() == MemberRole.LEADER) return "LEADER";
        if (data.getType() == MemberType.GUEST) return "PENDING";
        return "MEMBER";
    }
    
    private Color getRoleColor() {
        if (data.getRole() == MemberRole.LEADER) return ORANGE;
        if (data.getType() == MemberType.GUEST) return Color.GRAY;
        return DARK_BROWN;
    }
    
    public void addRemoveListener(ActionListener listener) {
        this.removeListener = listener;
    }
    
    
    public static MemberCardPanel createLeaderCard(GroupMember leader) {
        return new MemberCardPanel(leader, false);
    }
    
    public static MemberCardPanel createMemberCard(GroupMember member, boolean canRemove) {
        return new MemberCardPanel(member, canRemove);
    }
    
    public static MemberCardPanel createGuestCard(GroupMember guest) {
        return new MemberCardPanel(guest, false);
    }
}