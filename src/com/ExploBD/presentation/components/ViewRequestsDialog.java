package com.ExploBD.presentation.components;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.entities.InviteRequestMessage;
import com.ExploBD.domain.entities.Message;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.enums.MessageStatus;
import com.ExploBD.domain.enums.MessageType;
import com.ExploBD.service.GroupService;
import com.ExploBD.service.InvitationService;
import com.ExploBD.data.databaseObject.MessageDatabaseObject;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ViewRequestsDialog extends JDialog {

    private Group group;
    private User currentUser;
    private GroupService groupService;
    private InvitationService invitationService;
    private MessageDatabaseObject messageRepo;
    private JPanel requestsContainer;
    private Runnable onRequestsProcessed;
    private ProfilePhotoUtils photoUtils;

    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color ERROR_RED = new Color(198, 40, 40);

    public ViewRequestsDialog(Frame parent, Group group, User currentUser, Runnable onProcessed) {
        super(parent, "Member Requests", true);
        this.group = group;
        this.currentUser = currentUser;
        this.groupService = new GroupService();
        this.invitationService = new InvitationService();
        this.messageRepo = new MessageDatabaseObject();
        this.onRequestsProcessed = onProcessed;
        this.photoUtils = new ProfilePhotoUtils();

        initComponents();
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SOFT_WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Pending Invitation Requests");
        titleLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
        titleLabel.setForeground(DARK_BROWN);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        requestsContainer = new JPanel();
        requestsContainer.setLayout(new BoxLayout(requestsContainer, BoxLayout.Y_AXIS));
        requestsContainer.setBackground(SOFT_WHITE);

        JScrollPane scrollPane = new JScrollPane(requestsContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 180, 160)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(DARK_BROWN);
        closeBtn.setForeground(ORANGE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(SOFT_WHITE);
        bottomPanel.add(closeBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadRequests();
    }

    private void loadRequests() {
        requestsContainer.removeAll();

        List<Message> messages = messageRepo.getGroupMessages(group.getId());
        List<InviteRequestMessage> requests = new ArrayList<>();

        System.out.println("=== Loading Requests ===");
        System.out.println("Total messages in group: " + messages.size());

        for (Message msg : messages) {
            System.out.println("Message in DB: " + msg.getMessageId() + " Type: " + msg.getType() + " Class: " + msg.getClass().getSimpleName());
            if (msg instanceof InviteRequestMessage) {
                InviteRequestMessage req = (InviteRequestMessage) msg;
                System.out.println("  → InviteRequest: " + req.getInviteeName() + " Status: " + req.getStatus() + " ID: " + req.getMessageId());

                if (req.getStatus() == MessageStatus.PENDING) {
                    requests.add(req);
                }
            }
        }

        System.out.println("Pending requests found: " + requests.size());

        if (requests.isEmpty()) {
            JLabel emptyLabel = new JLabel("No pending requests");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
            requestsContainer.add(emptyLabel);
        } else {
            for (InviteRequestMessage request : requests) {
                System.out.println("Adding card for request ID: " + request.getMessageId() + " Name: " + request.getInviteeName());
                requestsContainer.add(createRequestCard(request));
                requestsContainer.add(Box.createVerticalStrut(10));
            }
        }

        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private JPanel createRequestCard(InviteRequestMessage request) {
        System.out.println("Creating card for request - ID: " + request.getMessageId()
                + " Name: " + request.getInviteeName());

        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel photoLabel = photoUtils.createEmailPhotoLabel(
                request.getInviteeEmail(),
                request.getInviteeName(),
                50
        );

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(request.getInviteeName());
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BROWN);

        JLabel emailLabel = new JLabel(request.getInviteeEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailLabel.setForeground(Color.GRAY);

        JLabel requesterLabel = new JLabel("Requested by: " + request.getSender().getDisplayName());
        requesterLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        requesterLabel.setForeground(new Color(120, 120, 120));

        infoPanel.add(nameLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(requesterLabel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton approveBtn = new JButton("Approve");
        approveBtn.setFont(new Font("Arial", Font.BOLD, 10));
        approveBtn.setBackground(SUCCESS_GREEN);
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFocusPainted(false);
        approveBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        approveBtn.addActionListener(e -> approveRequest(request));

        JButton declineBtn = new JButton("Decline");
        declineBtn.setFont(new Font("Arial", Font.BOLD, 10));
        declineBtn.setBackground(ERROR_RED);
        declineBtn.setForeground(Color.WHITE);
        declineBtn.setFocusPainted(false);
        declineBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        declineBtn.addActionListener(e -> declineRequest(request));

        btnPanel.add(approveBtn);
        btnPanel.add(declineBtn);

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }

    private void approveRequest(InviteRequestMessage request) {
        System.out.println("approveRequest called with message ID: " + request.getMessageId()); 

        int confirm = JOptionPane.showConfirmDialog(this,
                "Approve request for " + request.getInviteeName() + "?\n\n"
                + "An invitation will be sent to: " + request.getInviteeEmail(),
                "Approve Request",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                groupService.approveInvitationRequest(
                        group.getId(),
                        request.getMessageId(),
                        currentUser
                );

                JOptionPane.showMessageDialog(this,
                        "✓ Request approved!\nInvitation sent to " + request.getInviteeEmail(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                
                group = groupService.getGroup(group.getId());
                loadRequests();
                if (onRequestsProcessed != null) {
                    onRequestsProcessed.run();
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void declineRequest(InviteRequestMessage request) {
        System.out.println("declineRequest called with message ID: " + request.getMessageId()); 

        int confirm = JOptionPane.showConfirmDialog(this,
                "Decline request from " + request.getSender().getDisplayName() + "?",
                "Decline Request",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                groupService.rejectInvitationRequest(
                        group.getId(),
                        request.getMessageId(),
                        currentUser
                );

                JOptionPane.showMessageDialog(this,
                        "✗ Request declined.",
                        "Declined",
                        JOptionPane.INFORMATION_MESSAGE);

                
                group = groupService.getGroup(group.getId());
                loadRequests();
                if (onRequestsProcessed != null) {
                    onRequestsProcessed.run();
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
