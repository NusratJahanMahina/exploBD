package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.Frame.RoundedPanel;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;

public class RightPanel extends RoundedPanel {
    
    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private GroupHeaderPanel headerPanel;
    public JTabbedPane tabbedPane;
    private PlanTab planTab;
    private MembersTab membersTab;
    private SuggestTab suggestTab;
    private VoteTab voteTab;
    private int currentTabIndex = 0;
    
    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;
    
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private int baseNormalSize = 12;
    
    public RightPanel(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;
        
        setBackground(SOFT_WHITE);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        headerPanel = new GroupHeaderPanel(dataManager);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Comic Sans MS", Font.BOLD, baseNormalSize));
        tabbedPane.setBackground(SOFT_WHITE);
        tabbedPane.setForeground(DARK_BROWN);
        
        planTab = new PlanTab(dataManager, photoUtils);
        membersTab = new MembersTab(dataManager, photoUtils);
        suggestTab = new SuggestTab(dataManager, photoUtils);
        voteTab = new VoteTab(dataManager, photoUtils);
        
        tabbedPane.addTab("Plan", planTab);
        tabbedPane.addTab("Members", membersTab);
        tabbedPane.addTab("Suggest", suggestTab);
        tabbedPane.addTab("Vote", voteTab);
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        setGroup(dataManager.getSelectedGroup());
    }
    
    public void setSuggestFields(JTextField placeField, JTextField reasonField, JButton submitBtn) {
        this.suggestPlaceField = placeField;
        this.suggestReasonField = reasonField;
        this.suggestSubmitBtn = submitBtn;
        planTab.setSuggestFields(placeField, reasonField, submitBtn);
        membersTab.setSuggestFields(placeField, reasonField, submitBtn);
        suggestTab.setSuggestFields(placeField, reasonField, submitBtn);
        voteTab.setSuggestFields(placeField, reasonField, submitBtn);
    }
    
    public void setGroup(Group group) {
        headerPanel.setGroup(group);
        planTab.setGroup(group);
        membersTab.setGroup(group);
        suggestTab.setGroup(group);
        voteTab.setGroup(group);
    }
    
    public void refresh() {
        if (tabbedPane != null && tabbedPane.getSelectedIndex() != -1) {
            currentTabIndex = tabbedPane.getSelectedIndex();
        }
        
        headerPanel.refresh();
        planTab.refresh();
        membersTab.refresh();
        suggestTab.refresh();
        voteTab.refresh();
        
        if (tabbedPane != null && currentTabIndex >= 0 && currentTabIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(currentTabIndex);
        }
    }
}