package com.ExploBD.presentation.frames.module2.leftpanel;

import com.ExploBD.Frame.RoundedPanel;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;

public class LeftPanel extends RoundedPanel {

    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private JTabbedPane tabbedPane;
    private InvitationsPanel invitationsPanel;
    private MyGroupsPanel myGroupsPanel;

    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;

    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color DARK_BROWN = new Color(70, 58, 47);
    private int baseNormalSize = 12;

    public LeftPanel(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;

        setBackground(SOFT_WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Comic Sans MS", Font.BOLD, baseNormalSize));
        tabbedPane.setBackground(SOFT_WHITE);
        tabbedPane.setForeground(DARK_BROWN);

        invitationsPanel = new InvitationsPanel(dataManager, photoUtils);
        myGroupsPanel = new MyGroupsPanel(dataManager, photoUtils);

        tabbedPane.addTab("Invitations", invitationsPanel);
        tabbedPane.addTab("My Groups", myGroupsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adjustLeftPanelHeight();
                    }
                });
            }
        });
    }

    public void setSuggestFields(JTextField placeField, JTextField reasonField, JButton submitBtn) {
        this.suggestPlaceField = placeField;
        this.suggestReasonField = reasonField;
        this.suggestSubmitBtn = submitBtn;
        invitationsPanel.setSuggestFields(placeField, reasonField, submitBtn);
        myGroupsPanel.setSuggestFields(placeField, reasonField, submitBtn);
    }

    private void adjustLeftPanelHeight() {
        Component selectedComp = tabbedPane.getSelectedComponent();
        if (selectedComp instanceof JPanel) {
            JPanel tabPanel = (JPanel) selectedComp;

            for (Component comp : tabPanel.getComponents()) {
                if (comp instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) comp;
                    JViewport viewport = scrollPane.getViewport();
                    Component view = viewport.getView();

                    if (view instanceof JPanel) {
                        JPanel container = (JPanel) view;
                        int totalHeight = calculateTotalHeight(container);

                        container.setPreferredSize(new Dimension(container.getWidth(), totalHeight));
                        container.revalidate();

                        int availableHeight = tabbedPane.getHeight() - 50;

                        if (totalHeight < availableHeight) {
                            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
                        } else {
                            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                            scrollPane.setPreferredSize(null);
                        }

                        scrollPane.revalidate();
                    }
                    break;
                }
            }
        }
    }

    private int calculateTotalHeight(JPanel container) {
        int totalHeight = 0;
        int componentCount = container.getComponentCount();

        for (int i = 0; i < componentCount; i++) {
            Component comp = container.getComponent(i);
            if (comp.isVisible()) {
                if (comp instanceof JPanel) {
                    totalHeight += comp.getPreferredSize().height;
                } else if (comp instanceof Box.Filler) {
                    totalHeight += 3;
                }
            }
        }

        totalHeight += 20;
        return totalHeight;
    }

    public void refresh() {
        invitationsPanel.refresh();
        myGroupsPanel.refresh();
    }
}
