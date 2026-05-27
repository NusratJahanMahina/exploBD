package com.ExploBD.presentation.frames.module2.rightpanel;

import com.ExploBD.object.User;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.PlaceSuggestion;
import com.ExploBD.domain.enums.GroupStatus;
import com.ExploBD.presentation.frames.module2.data.GroupDataManager;
import com.ExploBD.util.ProfilePhotoUtils;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class VoteTab extends JPanel {

    private GroupDataManager dataManager;
    private ProfilePhotoUtils photoUtils;
    private Group currentGroup;

    private JTextField suggestPlaceField;
    private JTextField suggestReasonField;
    private JButton suggestSubmitBtn;

    private Color DARK_BROWN = new Color(70, 58, 47);
    private Color ORANGE = new Color(255, 153, 51);
    private Color SOFT_WHITE = new Color(255, 250, 245);
    private Color SUCCESS_GREEN = new Color(46, 125, 50);
    private Color GOLD = new Color(255, 215, 0);
    private Color ERROR_RED = new Color(198, 40, 40);

    public VoteTab(GroupDataManager dataManager, ProfilePhotoUtils photoUtils) {
        this.dataManager = dataManager;
        this.photoUtils = photoUtils;

        setLayout(new BorderLayout());
        setBackground(SOFT_WHITE);
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

        if (currentGroup == null) {
            return;
        }

        User currentUser = dataManager.getCurrentUser();
        boolean isLeader = currentGroup.isLeader(currentUser);
        boolean isVotingActive = currentGroup.getStatus() == GroupStatus.VOTING && currentGroup.isVotingActive();
        boolean isVotingEnded = (currentGroup.getStatus() == GroupStatus.VOTING && !currentGroup.isVotingActive());
        boolean isConfirmed = currentGroup.getStatus() == GroupStatus.CONFIRMED;
        List<PlaceSuggestion> approvedSuggestions = currentGroup.getApprovedSuggestions();

        boolean hasLeaderDestination = currentGroup.getDestinationName() != null
                && !currentGroup.getDestinationName().isEmpty();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(SOFT_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (isConfirmed || isVotingActive || currentGroup.getStatus() == GroupStatus.PLANNING) {
            JPanel statusPanel = new JPanel();
            statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
            statusPanel.setBackground(SOFT_WHITE);
            statusPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            String statusMessage;
            if (isConfirmed) {
                statusMessage = "TRIP CONFIRMED";
            } else if (isVotingActive && currentGroup.isVotingActive()) {
                statusMessage = "VOTING IN PROGRESS";
            } else {
                statusMessage = "PLANNING STAGE";
            }

            JLabel statusText = new JLabel(statusMessage);
            statusText.setFont(new Font("Arial", Font.BOLD, 16));
            statusText.setForeground(isConfirmed ? SUCCESS_GREEN : (isVotingActive ? ORANGE : DARK_BROWN));
            statusText.setAlignmentX(Component.LEFT_ALIGNMENT);
            statusPanel.add(statusText);

            if (isVotingActive && currentGroup.isVotingActive() && currentGroup.getVotingEndTime() != null) {
                statusPanel.add(Box.createVerticalStrut(5));

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime endTime = currentGroup.getVotingEndTime();

                if (now.isBefore(endTime)) {
                    long totalMinutes = ChronoUnit.MINUTES.between(now, endTime);
                    long days = totalMinutes / (24 * 60);
                    long hours = (totalMinutes % (24 * 60)) / 60;
                    long minutes = totalMinutes % 60;

                    String timeRemainingText;
                    if (days > 0) {
                        timeRemainingText = days + "d " + hours + "h " + minutes + "m remaining";
                    } else if (hours > 0) {
                        timeRemainingText = hours + "h " + minutes + "m remaining";
                    } else {
                        timeRemainingText = minutes + "m remaining";
                    }

                    JLabel timerLabel = new JLabel(timeRemainingText);
                    timerLabel.setFont(new Font("Arial", Font.BOLD, 12));
                    timerLabel.setForeground(SUCCESS_GREEN);
                    timerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    statusPanel.add(timerLabel);
                }
            }

//            if (isVotingActive && currentGroup.isVotingActive() && currentGroup.getVotingEndTime() != null) {
//                statusPanel.add(Box.createVerticalStrut(5));
//                
//                LocalDateTime now = LocalDateTime.now();
//                LocalDateTime endTime = currentGroup.getVotingEndTime();
//                
//                if (now.isBefore(endTime)) {
//                    long hoursLeft = ChronoUnit.HOURS.between(now, endTime);
//                    long minutesLeft = ChronoUnit.MINUTES.between(now, endTime) % 60;
//                    String timeRemainingText = hoursLeft + "h " + minutesLeft + "m remaining";
//                    
//                    JLabel timerLabel = new JLabel(timeRemainingText);
//                    timerLabel.setFont(new Font("Arial", Font.BOLD, 12));
//                    timerLabel.setForeground(SUCCESS_GREEN);
//                    timerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//                    statusPanel.add(timerLabel);
//                }
//            }
            contentPanel.add(statusPanel);
            contentPanel.add(Box.createVerticalStrut(15));
        }

        if (isVotingEnded && !isConfirmed) {
            JPanel endedPanel = new JPanel();
            endedPanel.setLayout(new BoxLayout(endedPanel, BoxLayout.Y_AXIS));
            endedPanel.setBackground(SOFT_WHITE);
            endedPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ORANGE, 2),
                    BorderFactory.createEmptyBorder(20, 15, 20, 15)
            ));
            endedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            endedPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            JLabel endedMessage = new JLabel("VOTING HAS ENDED", SwingConstants.CENTER);
            endedMessage.setFont(new Font("Arial", Font.BOLD, 16));
            endedMessage.setForeground(ORANGE);
            endedMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
            endedPanel.add(endedMessage);

            endedPanel.add(Box.createVerticalStrut(5));

            JLabel readyMessage = new JLabel("We are now ready to go!", SwingConstants.CENTER);
            readyMessage.setFont(new Font("Arial", Font.PLAIN, 14));
            readyMessage.setForeground(DARK_BROWN);
            readyMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
            endedPanel.add(readyMessage);

            contentPanel.add(endedPanel);
            contentPanel.add(Box.createVerticalStrut(15));

            PlaceSuggestion winner = currentGroup.getWinningSuggestion();
            if (winner != null) {
                JPanel winnerPanel = new JPanel();
                winnerPanel.setLayout(new BoxLayout(winnerPanel, BoxLayout.Y_AXIS));
                winnerPanel.setBackground(SOFT_WHITE);
                winnerPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_GREEN, 2),
                        BorderFactory.createEmptyBorder(20, 15, 20, 15)
                ));
                winnerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                winnerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                JLabel winnerTitle = new JLabel("WINNING DESTINATION", SwingConstants.CENTER);
                winnerTitle.setFont(new Font("Arial", Font.BOLD, 14));
                winnerTitle.setForeground(SUCCESS_GREEN);
                winnerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(winnerTitle);

                winnerPanel.add(Box.createVerticalStrut(8));

                JLabel winnerPlace = new JLabel(winner.getPlace().getName(), SwingConstants.CENTER);
                winnerPlace.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
                winnerPlace.setForeground(DARK_BROWN);
                winnerPlace.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(winnerPlace);

                contentPanel.add(winnerPanel);
            }

            if (isLeader) {
                contentPanel.add(Box.createVerticalStrut(10));

                JPanel buttonWrapper = new JPanel();
                buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.X_AXIS));
                buttonWrapper.setBackground(SOFT_WHITE);
                buttonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

                buttonWrapper.add(Box.createHorizontalGlue());

                JButton confirmBtn = new JButton("CONFIRM WINNER");
                confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
                confirmBtn.setBackground(SUCCESS_GREEN);
                confirmBtn.setForeground(Color.WHITE);
                confirmBtn.setFocusPainted(false);
                confirmBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                if (winner != null) {
                    confirmBtn.addActionListener(e -> {
                        dataManager.confirmWinner(winner.getSuggestionId());
                    });
                }

                buttonWrapper.add(confirmBtn);
                buttonWrapper.add(Box.createHorizontalGlue());

                contentPanel.add(buttonWrapper);
            }
        } else if (isConfirmed && currentGroup.getSelectedDestination() != null) {
            JPanel confirmedPanel = new JPanel();
            confirmedPanel.setLayout(new BoxLayout(confirmedPanel, BoxLayout.Y_AXIS));
            confirmedPanel.setBackground(SOFT_WHITE);
            confirmedPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SUCCESS_GREEN, 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            confirmedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            confirmedPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JLabel confirmedTitle = new JLabel("TRIP CONFIRMED", SwingConstants.CENTER);
            confirmedTitle.setFont(new Font("Arial", Font.BOLD, 14));
            confirmedTitle.setForeground(SUCCESS_GREEN);
            confirmedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            confirmedPanel.add(confirmedTitle);

            confirmedPanel.add(Box.createVerticalStrut(10));

            JLabel confirmedPlace = new JLabel(currentGroup.getDestinationName(), SwingConstants.CENTER);
            confirmedPlace.setFont(new Font("Bookman Old Style", Font.BOLD, 18));
            confirmedPlace.setForeground(DARK_BROWN);
            confirmedPlace.setAlignmentX(Component.CENTER_ALIGNMENT);
            confirmedPanel.add(confirmedPlace);

            contentPanel.add(confirmedPanel);
            contentPanel.add(Box.createVerticalStrut(15));
        } else if (!hasLeaderDestination && approvedSuggestions.isEmpty() && !isVotingEnded) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setBackground(SOFT_WHITE);
            emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                    BorderFactory.createEmptyBorder(30, 15, 30, 15)
            ));
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JLabel emptyText = new JLabel("No destinations available for voting", SwingConstants.CENTER);
            emptyText.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyText.setForeground(Color.GRAY);
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyText);
            contentPanel.add(emptyPanel);
        } else if (isVotingActive && currentGroup.isVotingActive() && !isConfirmed) {
            JLabel votingTitle = new JLabel("CAST YOUR VOTE");
            votingTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
            votingTitle.setForeground(DARK_BROWN);
            votingTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(votingTitle);
            contentPanel.add(Box.createVerticalStrut(10));

            boolean userVoted = false;
            String votedSuggestionId = null;

            if (!isLeader) {
                for (PlaceSuggestion s : approvedSuggestions) {
                    if (s.hasVoted(currentUser)) {
                        userVoted = true;
                        votedSuggestionId = s.getSuggestionId();
                        break;
                    }
                }
            }

            if (hasLeaderDestination) {
                JPanel leaderCard = createLeaderVoteCard(
                        currentGroup.getDestinationName(),
                        currentGroup.getDestinationDivision(),
                        isLeader,
                        userVoted,
                        votedSuggestionId
                );
                leaderCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
                leaderCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(leaderCard);
                contentPanel.add(Box.createVerticalStrut(10));
            }

            for (PlaceSuggestion suggestion : approvedSuggestions) {
                VoteCard card = new VoteCard(suggestion, isLeader, userVoted, votedSuggestionId,
                        currentGroup, currentUser, dataManager, photoUtils);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(card);
                contentPanel.add(Box.createVerticalStrut(10));
            }

            if (isLeader) {
                contentPanel.add(Box.createVerticalStrut(15));

                JLabel leaderTitle = new JLabel("LEADER CONTROLS");
                leaderTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
                leaderTitle.setForeground(DARK_BROWN);
                leaderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(leaderTitle);
                contentPanel.add(Box.createVerticalStrut(10));

                JPanel leaderPanel = new JPanel();
                leaderPanel.setLayout(new BoxLayout(leaderPanel, BoxLayout.Y_AXIS));
                leaderPanel.setBackground(SOFT_WHITE);
                leaderPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                leaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                leaderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

                JLabel votesHeader = new JLabel("Current Vote Status:");
                votesHeader.setFont(new Font("Arial", Font.BOLD, 12));
                votesHeader.setForeground(DARK_BROWN);
                votesHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
                leaderPanel.add(votesHeader);
                leaderPanel.add(Box.createVerticalStrut(8));

                JPanel votesContainer = new JPanel(new GridLayout(0, 2, 10, 5));
                votesContainer.setBackground(SOFT_WHITE);
                votesContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

                if (hasLeaderDestination) {
                    JLabel placeVote = new JLabel("  " + currentGroup.getDestinationName() + ":");
                    placeVote.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
                    placeVote.setForeground(ORANGE);

                    JLabel countLabel = new JLabel("0 votes");
                    countLabel.setFont(new Font("Arial", Font.BOLD, 11));
                    countLabel.setForeground(ORANGE);

                    votesContainer.add(placeVote);
                    votesContainer.add(countLabel);
                }

                for (PlaceSuggestion s : approvedSuggestions) {
                    JLabel placeVote = new JLabel("  " + s.getPlace().getName() + ":");
                    placeVote.setFont(new Font("Bookman Old Style", Font.PLAIN, 11));
                    placeVote.setForeground(DARK_BROWN);

                    JLabel yesCount = new JLabel(s.getApproveCount() + " votes");
                    yesCount.setFont(new Font("Arial", Font.BOLD, 11));
                    yesCount.setForeground(SUCCESS_GREEN);

                    votesContainer.add(placeVote);
                    votesContainer.add(yesCount);
                }

                leaderPanel.add(votesContainer);
                leaderPanel.add(Box.createVerticalStrut(15));

                JPanel buttonWrapper = new JPanel();
                buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.X_AXIS));
                buttonWrapper.setBackground(SOFT_WHITE);
                buttonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

                buttonWrapper.add(Box.createHorizontalGlue());

                JButton endVotingBtn = new JButton("END VOTING");
                endVotingBtn.setFont(new Font("Arial", Font.BOLD, 12));
                endVotingBtn.setBackground(ORANGE);
                endVotingBtn.setForeground(Color.WHITE);
                endVotingBtn.setFocusPainted(false);
                endVotingBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                endVotingBtn.addActionListener(e -> dataManager.endVoting());

                buttonWrapper.add(endVotingBtn);
                buttonWrapper.add(Box.createHorizontalGlue());

                leaderPanel.add(buttonWrapper);

                contentPanel.add(leaderPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setBackground(SOFT_WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createLeaderVoteCard(String destName, String division, boolean isLeader,
            boolean userVoted, String votedSuggestionId) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 160), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        namePanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(destName);
        nameLabel.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        nameLabel.setForeground(DARK_BROWN);
        namePanel.add(nameLabel);

        JLabel leaderBadge = new JLabel("Leader's Choice");
        leaderBadge.setFont(new Font("Arial", Font.BOLD, 10));
        leaderBadge.setForeground(ORANGE);
        leaderBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ORANGE, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        namePanel.add(leaderBadge);

        topRow.add(namePanel, BorderLayout.WEST);

        if (division != null) {
            JLabel locationLabel = new JLabel(division);
            locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            locationLabel.setForeground(Color.GRAY);
            topRow.add(locationLabel, BorderLayout.EAST);
        }

        card.add(topRow);
        card.add(Box.createVerticalStrut(8));

        JPanel middleRow = new JPanel(new BorderLayout(10, 0));
        middleRow.setBackground(Color.WHITE);
        middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        int totalMembers = currentGroup.getMemberCount();
        int votesForLeader = 0;
        int percentage = totalMembers > 0 ? (votesForLeader * 100) / totalMembers : 0;

        JLabel percentageLabel = new JLabel(votesForLeader + "/" + totalMembers + " votes (" + percentage + "%)");
        percentageLabel.setFont(new Font("Arial", Font.BOLD, 12));
        percentageLabel.setForeground(DARK_BROWN);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(percentage);
        progressBar.setString(percentage + "%");
        progressBar.setStringPainted(true);
        progressBar.setForeground(ORANGE);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setFont(new Font("Arial", Font.BOLD, 9));
        progressBar.setPreferredSize(new Dimension(150, 18));

        middleRow.add(percentageLabel, BorderLayout.WEST);
        middleRow.add(progressBar, BorderLayout.CENTER);

        card.add(middleRow);
        card.add(Box.createVerticalStrut(8));

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        String leaderVoteId = "LEADER_" + currentGroup.getId();
        boolean votedForLeader = userVoted && votedSuggestionId != null && votedSuggestionId.equals(leaderVoteId);

        if (!isLeader) {
            if (!votedForLeader && !userVoted) {
                JButton voteBtn = new JButton("VOTE");
                voteBtn.setFont(new Font("Arial", Font.BOLD, 11));
                voteBtn.setBackground(SUCCESS_GREEN);
                voteBtn.setForeground(Color.WHITE);
                voteBtn.setFocusPainted(false);
                voteBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                voteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                voteBtn.addActionListener(e -> castVoteForLeader(destName));
                bottomRow.add(voteBtn);

            } else if (votedForLeader) {
                JPanel votedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                votedPanel.setBackground(Color.WHITE);

                JLabel votedLabel = new JLabel("YOU VOTED");
                votedLabel.setFont(new Font("Arial", Font.BOLD, 11));
                votedLabel.setForeground(SUCCESS_GREEN);
                votedLabel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_GREEN, 1),
                        BorderFactory.createEmptyBorder(3, 8, 3, 8)
                ));
                votedPanel.add(votedLabel);

                JButton removeBtn = new JButton("REMOVE");
                removeBtn.setFont(new Font("Arial", Font.BOLD, 9));
                removeBtn.setBackground(ERROR_RED);
                removeBtn.setForeground(Color.WHITE);
                removeBtn.setFocusPainted(false);
                removeBtn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                removeBtn.addActionListener(e -> removeVoteForLeader());
                votedPanel.add(removeBtn);

                bottomRow.add(votedPanel);
            }
        } else {
            JLabel leaderMsg = new JLabel("Leader observes voting");
            leaderMsg.setFont(new Font("Arial", Font.ITALIC, 10));
            leaderMsg.setForeground(Color.GRAY);
            leaderMsg.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            bottomRow.add(leaderMsg);
        }

        JButton viewBtn = new JButton("Details");
        viewBtn.setFont(new Font("Arial", Font.BOLD, 10));
        viewBtn.setBackground(DARK_BROWN);
        viewBtn.setForeground(GOLD);
        viewBtn.setFocusPainted(false);
        viewBtn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewBtn.addActionListener(e -> showLeaderDestinationDetails(destName, division));
        bottomRow.add(viewBtn);

        card.add(bottomRow);

        return card;
    }

    private void castVoteForLeader(String destName) {
        JOptionPane.showMessageDialog(VoteTab.this,
                "You voted for Leader's choice: " + destName,
                "Vote Recorded",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeVoteForLeader() {
        JOptionPane.showMessageDialog(VoteTab.this,
                "Your vote has been removed from Leader's choice",
                "Vote Removed",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLeaderDestinationDetails(String destName, String division) {
        JOptionPane.showMessageDialog(VoteTab.this,
                "Leader's Destination: " + destName + "\n"
                + "Division: " + division,
                "Leader's Choice",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
