package com.ExploBD.presentation.frames;

import com.ExploBD.data.databaseObject.GroupDatabaseObject;
import com.ExploBD.data.databaseObject.InvitationDatabaseObject;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.domain.entities.GroupSystem.Group;
import com.ExploBD.domain.entities.Invitation;
import com.ExploBD.domain.entities.Notification;
import com.ExploBD.domain.enums.InvitationStatus;
import com.ExploBD.domain.enums.NotificationType;
import com.ExploBD.domain.members.RegisteredMember;
import com.ExploBD.object.User;
import com.ExploBD.service.InvitationService;
import com.ExploBD.service.UserService;
import com.ExploBD.session.UserSession;
import java.awt.Color;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JOptionPane;

public class SignUPFrame extends javax.swing.JFrame {
private UserService userService;              
private InvitationService invitationService;  
    public SignUPFrame() {
        initComponents();
         this.userService = new UserService();           
    this.invitationService = new InvitationService(); 
        setLocationRelativeTo(null);
        AppConfig.applyAndTrack(this);
        initializeSecurityQuestions();

    }

    private void initializeSecurityQuestions() {

        sequrity_qn_set.removeAllItems();
        for (String question : UserDatabaseObject.SECURITY_QUESTIONS) {
            sequrity_qn_set.addItem(question);
        }
        sequrity_qn_set.setSelectedIndex(0);
    }

    private ValidationResult validateForm() {

        String username = txt_username.getText().trim();
        String email = txt_email.getText().trim();
        String password = new String(pass_to_save.getPassword());
        String confirmPassword = new String(pass_again.getPassword());
        String securityAnswer = sequrity_qn_answer.getText().trim();
        int securityQuestionIndex = sequrity_qn_set.getSelectedIndex();

        int errorCode = -1;
        StringBuilder errorMessages = new StringBuilder();

        if (username.isEmpty()) {
            errorMessages.append("• Username cannot be empty\n");
            errorCode = (errorCode == -1) ? 0 : errorCode;
        }

        if (email.isEmpty()) {
            errorMessages.append("• Email cannot be empty\n");
            errorCode = (errorCode == -1) ? 1 : errorCode;
        } else if (!email.contains("@") || !email.contains(".")) {
            errorMessages.append("• Please enter a valid email address\n");
            errorCode = (errorCode == -1) ? 1 : errorCode;
        }

        if (password.isEmpty()) {
            errorMessages.append("• Password cannot be empty\n");
            errorCode = (errorCode == -1) ? 2 : errorCode;
        } else if (password.length() < 6) {
            errorMessages.append("• Password must be at least 6 characters\n");
            errorCode = (errorCode == -1) ? 2 : errorCode;
        }

        if (confirmPassword.isEmpty()) {
            errorMessages.append("• Please confirm your password\n");
            errorCode = (errorCode == -1) ? 3 : errorCode;
        } else if (!password.equals(confirmPassword)) {
            errorMessages.append("• Passwords do not match\n");
            errorCode = (errorCode == -1) ? 3 : errorCode;
        }

        if (securityAnswer.isEmpty()) {
            errorMessages.append("• Please answer the security question\n");
            errorCode = (errorCode == -1) ? 4 : errorCode;
        }

        return new ValidationResult(errorCode, errorMessages.toString(),
                username, email, password, securityQuestionIndex, securityAnswer);

    }

    private void showValidationErrors(ValidationResult result) {
        if (result.errorCode != -1) {
            JOptionPane.showMessageDialog(this,
                    "Please fix the following errors:\n\n" + result.errorMessages,
                    "Registration Errors",
                    JOptionPane.ERROR_MESSAGE);

            switch (result.errorCode) {
                case 0:
                    txt_username.requestFocus();
                    break;
                case 1:
                    txt_email.requestFocus();
                    break;
                case 2:
                    pass_to_save.requestFocus();
                    break;
                case 3:
                    pass_again.requestFocus();
                    break;
                case 4:
                    sequrity_qn_answer.requestFocus();
                    break;
            }
        }
    }

    private void clearForm() {
        txt_username.setText("");
        txt_email.setText("");
        pass_to_save.setText("");
        pass_again.setText("");
        sequrity_qn_answer.setText("");
        sequrity_qn_set.setSelectedIndex(0);
        txt_username.requestFocus();
    }

    private class ValidationResult {

        int errorCode;
        String errorMessages;
        String username;
        String email;
        String password;
        int securityQuestionIndex;
        String securityAnswer;

        ValidationResult(int errorCode, String errorMessages, String username, String email,
                String password, int securityQuestionIndex, String securityAnswer) {
            this.errorCode = errorCode;
            this.errorMessages = errorMessages;
            this.username = username;
            this.email = email;
            this.password = password;
            this.securityQuestionIndex = securityQuestionIndex;
            this.securityAnswer = securityAnswer;
        }

        boolean isValid() {
            return errorCode == -1;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        right = new javax.swing.JPanel();
        fromBoxRi = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        left = new javax.swing.JPanel();
        FromContainer = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_username = new javax.swing.JTextField();
        txt_email = new javax.swing.JTextField();
        pass_to_save = new javax.swing.JPasswordField();
        pass_again = new javax.swing.JPasswordField();
        sequrity_qn_set = new javax.swing.JComboBox<>();
        sequrity_qn_answer = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        signup = new javax.swing.JButton();
        say6digit = new javax.swing.JLabel();
        login = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        right.setBackground(new java.awt.Color(88, 74, 60));
        right.setMinimumSize(new java.awt.Dimension(360, 600));
        right.setLayout(new java.awt.GridBagLayout());

        fromBoxRi.setBackground(new java.awt.Color(88, 74, 60));
        fromBoxRi.setMinimumSize(new java.awt.Dimension(360, 600));
        fromBoxRi.setPreferredSize(new java.awt.Dimension(360, 600));
        fromBoxRi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo200.png"))); // NOI18N
        jLabel6.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        fromBoxRi.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 199, 200, 200));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        right.add(fromBoxRi, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(right, gridBagConstraints);

        left.setBackground(new java.awt.Color(255, 255, 255));
        left.setMinimumSize(new java.awt.Dimension(440, 600));
        left.setPreferredSize(new java.awt.Dimension(440, 600));
        left.setLayout(new java.awt.GridBagLayout());

        FromContainer.setBackground(new java.awt.Color(255, 255, 255));
        FromContainer.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(70, 58, 47));
        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(70, 58, 47));
        jLabel1.setText("SignUP");
        jLabel1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        FromContainer.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, -1, -1));

        txt_username.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        txt_username.setForeground(new java.awt.Color(70, 58, 47));
        txt_username.setBorder(null);
        txt_username.addActionListener(this::txt_usernameActionPerformed);
        FromContainer.add(txt_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 150, 250, 30));

        txt_email.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        txt_email.setForeground(new java.awt.Color(70, 58, 47));
        txt_email.setBorder(null);
        txt_email.addActionListener(this::txt_emailActionPerformed);
        FromContainer.add(txt_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 210, 250, 30));

        pass_to_save.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        pass_to_save.setForeground(new java.awt.Color(70, 58, 47));
        pass_to_save.setBorder(null);
        pass_to_save.addActionListener(this::pass_to_saveActionPerformed);
        FromContainer.add(pass_to_save, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 265, 250, 30));

        pass_again.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        pass_again.setForeground(new java.awt.Color(70, 58, 47));
        pass_again.setBorder(null);
        pass_again.addActionListener(this::pass_againActionPerformed);
        FromContainer.add(pass_again, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 330, 250, 35));

        sequrity_qn_set.setBackground(new java.awt.Color(88, 74, 60));
        sequrity_qn_set.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        sequrity_qn_set.setForeground(new java.awt.Color(255, 215, 0));
        sequrity_qn_set.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", " " }));
        sequrity_qn_set.setBorder(null);
        sequrity_qn_set.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        sequrity_qn_set.addActionListener(this::sequrity_qn_setActionPerformed);
        FromContainer.add(sequrity_qn_set, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 395, 270, 30));

        sequrity_qn_answer.setFont(new java.awt.Font("Footlight MT Light", 1, 14)); // NOI18N
        sequrity_qn_answer.setBorder(null);
        FromContainer.add(sequrity_qn_answer, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 430, 250, 30));

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator1.setMinimumSize(new java.awt.Dimension(250, 20));
        jSeparator1.setPreferredSize(new java.awt.Dimension(250, 20));
        FromContainer.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 460, 250, 20));

        jSeparator2.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator2.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jSeparator2.setMinimumSize(new java.awt.Dimension(250, 20));
        FromContainer.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 180, 250, 20));

        jSeparator3.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator3.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator3.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jSeparator3.setMinimumSize(new java.awt.Dimension(250, 20));
        jSeparator3.setPreferredSize(new java.awt.Dimension(250, 20));
        FromContainer.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 240, 250, 20));

        jSeparator4.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator4.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator4.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jSeparator4.setMinimumSize(new java.awt.Dimension(250, 20));
        jSeparator4.setPreferredSize(new java.awt.Dimension(250, 20));
        FromContainer.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 300, 250, 20));

        jSeparator5.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator5.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator5.setMinimumSize(new java.awt.Dimension(250, 20));
        FromContainer.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 370, 250, 10));

        jLabel3.setBackground(new java.awt.Color(70, 58, 47));
        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(70, 58, 47));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        jLabel3.setText("Username");
        jLabel3.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 30));
        FromContainer.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 100, 30));

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(70, 58, 47));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mail.png"))); // NOI18N
        jLabel4.setText("Email");
        jLabel4.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jLabel4.setMinimumSize(new java.awt.Dimension(100, 30));
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 30));
        FromContainer.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, 80, 30));

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(70, 58, 47));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pass.png"))); // NOI18N
        jLabel5.setText("Password");
        jLabel5.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        FromContainer.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 100, 30));

        jLabel7.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(70, 58, 47));
        jLabel7.setText("Question");
        jLabel7.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        FromContainer.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, 60, 30));

        jLabel8.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(70, 58, 47));
        jLabel8.setText("Answer");
        jLabel8.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        FromContainer.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 430, 100, 40));

        jLabel9.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(70, 58, 47));
        jLabel9.setText("Password");
        jLabel9.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        FromContainer.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 70, 30));

        jLabel11.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(70, 58, 47));
        jLabel11.setText("Confirm");
        jLabel11.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        FromContainer.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 327, 60, 30));

        jLabel10.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(70, 58, 47));
        jLabel10.setText("Sequrity");
        jLabel10.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        FromContainer.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 386, 60, 30));

        signup.setBackground(new java.awt.Color(70, 58, 47));
        signup.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        signup.setForeground(new java.awt.Color(255, 215, 0));
        signup.setText("SignUP");
        signup.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(70, 58, 47), 5, true));
        signup.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        signup.setMinimumSize(new java.awt.Dimension(100, 40));
        signup.setPreferredSize(new java.awt.Dimension(150, 40));
        signup.addActionListener(this::signupActionPerformed);
        FromContainer.add(signup, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 470, 70, 40));

        say6digit.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        say6digit.setForeground(new java.awt.Color(255, 51, 51));
        say6digit.setText("( at least 6 digit long )");
        FromContainer.add(say6digit, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 310, 190, -1));

        login.setBackground(new java.awt.Color(70, 58, 47));
        login.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        login.setForeground(new java.awt.Color(255, 215, 0));
        login.setText("Login");
        login.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(70, 58, 47), 5, true));
        login.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        login.setMinimumSize(new java.awt.Dimension(100, 40));
        login.setPreferredSize(new java.awt.Dimension(150, 40));
        login.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginMouseExited(evt);
            }
        });
        login.addActionListener(this::loginActionPerformed);
        FromContainer.add(login, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 520, 70, 40));

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(88, 74, 60));
        jLabel2.setText("Already have an account?");
        FromContainer.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 540, -1, -1));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 67;
        gridBagConstraints.ipady = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        left.add(FromContainer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(left, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_usernameActionPerformed
        txt_email.requestFocus();
    }//GEN-LAST:event_txt_usernameActionPerformed

    private void txt_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_emailActionPerformed
        pass_to_save.requestFocus();
    }//GEN-LAST:event_txt_emailActionPerformed

    private void pass_to_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pass_to_saveActionPerformed
        pass_again.requestFocus();
    }//GEN-LAST:event_pass_to_saveActionPerformed

    private void pass_againActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pass_againActionPerformed
        sequrity_qn_answer.requestFocus();
    }//GEN-LAST:event_pass_againActionPerformed

    private void signupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupActionPerformed
        ValidationResult validation = validateForm();

        if (!validation.isValid()) {
            showValidationErrors(validation);
            return;
        }

        try {
           User newUser = userService.register(
        validation.username,
        validation.email,
        validation.password,
        validation.securityQuestionIndex,
        validation.securityAnswer
);

if (newUser != null) {
    List<Invitation> byEmail = invitationService.getPendingInvitations(validation.email);
    List<Invitation> byName = invitationService.getPendingInvitationsForUser(validation.username, validation.username);

                java.util.Set<Invitation> allInvitations = new java.util.HashSet<>();
                allInvitations.addAll(byEmail);
                allInvitations.addAll(byName);

                System.out.println("Found " + allInvitations.size() + " pending invitations for " + validation.email);

                for (Invitation invitation : allInvitations) {
                    try {
                        if (invitation.getExpiryDate() != null
                                && LocalDate.now().isAfter(invitation.getExpiryDate())) {
                            System.out.println("Skipping expired invitation: " + invitation.getInvitationId());
                            continue;
                        }

                        Group group = invitation.getGroup();

                        if ("PLACEHOLDER".equals(invitation.getType())) {
                            invitation.convertToEmailInvitation(validation.email);
                        }

                        if (!group.hasMember(newUser.getUserId())) {
                            group.addMember(RegisteredMember.asMember(newUser));
                            new GroupDatabaseObject().saveGroup(group);
                        }

                        invitation.accept();
                      invitationService.updateStatus(invitation.getInvitationId(), InvitationStatus.ACCEPTED);
                        System.out.println("Processed invitation: " + invitation.getInvitationId());

                    } catch (Exception e) {
                        System.out.println("Error processing invitation: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                UserSession.getInstance().login(newUser);
                UserSession.getInstance().loadUserData();
                clearForm();
             
                this.dispose();
                new HomeFrame(newUser).setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Registration Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_signupActionPerformed

    private void sequrity_qn_setActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sequrity_qn_setActionPerformed

    }//GEN-LAST:event_sequrity_qn_setActionPerformed

    private void loginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginMouseClicked
        LoginFrame login = new LoginFrame();
        AppConfig.applyAndTrack(this);
        login.setVisible(true);

        login.pack();

        login.setLocationRelativeTo(null);
        this.dispose();

    }//GEN-LAST:event_loginMouseClicked

    private void loginMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginMouseEntered
        login.setBackground(new Color(140, 125, 105));
    }//GEN-LAST:event_loginMouseEntered

    private void loginMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loginMouseExited
        login.setBackground(new Color(70, 58, 47));
    }//GEN-LAST:event_loginMouseExited

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loginActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> new SignUPFrame().setVisible(true));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FromContainer;
    private javax.swing.JPanel fromBoxRi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JPanel left;
    private javax.swing.JButton login;
    private javax.swing.JPasswordField pass_again;
    private javax.swing.JPasswordField pass_to_save;
    private javax.swing.JPanel right;
    private javax.swing.JLabel say6digit;
    private javax.swing.JTextField sequrity_qn_answer;
    private javax.swing.JComboBox<String> sequrity_qn_set;
    private javax.swing.JButton signup;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables

}
