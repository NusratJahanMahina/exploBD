package com.ExploBD.presentation.frames.module2.leftpanel;

import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyGroupsPanel extends JPanel {

    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private JPanel container;

    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;

    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private int baseSubtitleSize = 14;

    public MyGroupsPanel(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;

        setLayout(new BorderLayout());
        setBackground(SOFT_WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel headerLabel = new JLabel("MY GROUPS");
        headerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, baseSubtitleSize));
        headerLabel.setForeground(DARK_BROWN);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(SOFT_WHITE);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(headerLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    public void setSuggestFields(JTextField placeField, JTextField reasonField, JButton submitBtn) {
        this.suggestPlaceField = placeField;
        this.suggestReasonField = reasonField;
        this.suggestSubmitBtn = submitBtn;
    }

    public void refresh() {
        container.removeAll();

        List<Group> otherGroups = dataManager.getOtherGroups();
        Group selectedGroup = dataManager.getSelectedGroup();

        if (otherGroups.isEmpty()) {
            JLabel emptyLabel = new JLabel("No other groups");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
            container.add(emptyLabel);
        } else {
            for (Group group : otherGroups) {
                GroupCard card = new GroupCard(group, dataManager, photoUtils, group.equals(selectedGroup));
                container.add(card);
                container.add(Box.createVerticalStrut(3));
            }
        }

        container.revalidate();
        container.repaint();
    }
}
