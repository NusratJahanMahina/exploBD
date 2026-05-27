package com.ExploBD.presentation.frames;

import com.ExploBD.object.User;
import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.service.UserService;
import com.ExploBD.session.UserSession;
import javax.swing.JOptionPane;

public class LoginFrame extends javax.swing.JFrame {

    private UserService userService = new UserService();

    public LoginFrame() {
        initComponents();

        setLocationRelativeTo(null);
        initializePasswordToggle();

        AppConfig.applyAndTrack(this);
    }

    private void initializePasswordToggle() {
        jButton2.addActionListener(e -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (jPasswordField1.getEchoChar() == '*') {
            jPasswordField1.setEchoChar((char) 0);
            jButton2.setToolTipText("Hide Password");
        } else {
            jPasswordField1.setEchoChar('*');
            jButton2.setToolTipText("Show Password");
        }
    }

    private ValidationResult validateLoginForm() {
        String email = jTextField1.getText().trim();
        String password = new String(jPasswordField1.getPassword());

        String errors = "";
        int errorCode = -1;

        if (email.isEmpty()) {
            errors = "• Email cannot be empty\n";
            errorCode = 0;
        } else if (!email.contains("@") || !email.contains(".com") || email.length() <= 6) {
            errors = "• Please enter a valid email address\n";
            errorCode = 0;
        }

        if (password.isEmpty()) {
            if (errors.isEmpty()) {
                errors = "• Password cannot be empty\n";
                errorCode = 1;
            } else {
                errors += "• Password cannot be empty\n";
            }
        }

        return new ValidationResult(errorCode, errors, email, password);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".com") && email.length() > 6;
    }

    private void showValidationErrors(ValidationResult result) {
        if (result.errorCode != -1) {
            JOptionPane.showMessageDialog(this,
                    "Please fix the following errors:\n\n" + result.errorMessages,
                    "Login Errors",
                    JOptionPane.ERROR_MESSAGE);

            switch (result.errorCode) {
                case 0:
                    jTextField1.requestFocus();
                    jTextField1.selectAll();
                    break;
                case 1:
                    jPasswordField1.requestFocus();
                    jPasswordField1.selectAll();
                    break;
            }
        }
    }

    private void openHomeFrame(User user) {
        this.dispose();

        new HomeFrame(user).setVisible(true);
    }

    private class ValidationResult {

        int errorCode;
        String errorMessages;
        String email;
        String password;

        ValidationResult(int errorCode, String errorMessages, String email, String password) {
            this.errorCode = errorCode;
            this.errorMessages = errorMessages;
            this.email = email;
            this.password = password;
        }

        boolean isValid() {
            return errorCode == -1;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        Rightpanel = new javax.swing.JPanel();
        fromBoxRi = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        Leftpanel = new javax.swing.JPanel();
        fromboxle = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        signup = new javax.swing.JButton();
        forgetpass = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        login1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jPanel1.setMinimumSize(new java.awt.Dimension(800, 600));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        Rightpanel.setBackground(new java.awt.Color(88, 74, 60));
        Rightpanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        Rightpanel.setMinimumSize(new java.awt.Dimension(360, 600));
        Rightpanel.setLayout(new java.awt.GridBagLayout());

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
        Rightpanel.add(fromBoxRi, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(Rightpanel, gridBagConstraints);

        Leftpanel.setBackground(new java.awt.Color(255, 255, 255));
        Leftpanel.setMinimumSize(new java.awt.Dimension(440, 600));
        Leftpanel.setPreferredSize(new java.awt.Dimension(440, 600));
        Leftpanel.setLayout(new java.awt.GridBagLayout());

        fromboxle.setBackground(new java.awt.Color(255, 255, 255));
        fromboxle.setMinimumSize(new java.awt.Dimension(440, 600));
        fromboxle.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(88, 74, 60));
        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(70, 58, 47));
        jLabel1.setText("LOGIN");
        jLabel1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        fromboxle.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(152, 150, -1, -1));

        jTextField1.setFont(new java.awt.Font("Footlight MT Light", 1, 18)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(70, 58, 47));
        jTextField1.setBorder(null);
        jTextField1.setMinimumSize(new java.awt.Dimension(65, 20));
        jTextField1.setPreferredSize(new java.awt.Dimension(65, 20));
        jTextField1.addActionListener(this::jTextField1ActionPerformed);
        fromboxle.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 240, 250, 30));

        jPasswordField1.setFont(new java.awt.Font("Footlight MT Light", 1, 18)); // NOI18N
        jPasswordField1.setForeground(new java.awt.Color(70, 58, 47));
        jPasswordField1.setBorder(null);
        jPasswordField1.setMinimumSize(new java.awt.Dimension(65, 20));
        jPasswordField1.setPreferredSize(new java.awt.Dimension(65, 20));
        jPasswordField1.addActionListener(this::jPasswordField1ActionPerformed);
        fromboxle.add(jPasswordField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 290, 250, 30));

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jSeparator1.setMinimumSize(new java.awt.Dimension(250, 20));
        jSeparator1.setPreferredSize(new java.awt.Dimension(250, 20));
        fromboxle.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 270, -1, 10));

        jSeparator2.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator2.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jSeparator2.setMinimumSize(new java.awt.Dimension(250, 20));
        jSeparator2.setPreferredSize(new java.awt.Dimension(250, 20));
        fromboxle.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 320, -1, 10));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(70, 58, 47));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mail.png"))); // NOI18N
        jLabel2.setText("Email");
        jLabel2.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jLabel2.setMinimumSize(new java.awt.Dimension(90, 40));
        jLabel2.setPreferredSize(new java.awt.Dimension(90, 40));
        fromboxle.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, -1, -1));

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(70, 58, 47));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pass.png"))); // NOI18N
        jLabel3.setText("Password");
        jLabel3.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jLabel3.setPreferredSize(new java.awt.Dimension(104, 26));
        fromboxle.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 110, -1));

        signup.setBackground(new java.awt.Color(70, 58, 47));
        signup.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        signup.setForeground(new java.awt.Color(255, 215, 0));
        signup.setText("SignUP");
        signup.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(70, 58, 47), 5, true));
        signup.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        signup.setMinimumSize(new java.awt.Dimension(150, 40));
        signup.setPreferredSize(new java.awt.Dimension(150, 40));
        signup.addActionListener(this::signupActionPerformed);
        fromboxle.add(signup, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 500, 70, -1));
        signup.getAccessibleContext().setAccessibleName("login");

        forgetpass.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        forgetpass.setForeground(new java.awt.Color(70, 58, 47));
        forgetpass.setText("  Forgot Password ? ");
        forgetpass.setBorder(null);
        forgetpass.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        forgetpass.addActionListener(this::forgetpassActionPerformed);
        fromboxle.add(forgetpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 400, 160, 20));

        jSeparator3.setForeground(new java.awt.Color(70, 58, 47));
        jSeparator3.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jSeparator3.setMinimumSize(new java.awt.Dimension(150, 10));
        jSeparator3.setPreferredSize(new java.awt.Dimension(150, 10));
        fromboxle.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 420, -1, -1));

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(70, 58, 47));
        jLabel4.setText("Don't have an acocunt ?");
        fromboxle.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 510, 180, 20));

        login1.setBackground(new java.awt.Color(70, 58, 47));
        login1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        login1.setForeground(new java.awt.Color(255, 215, 0));
        login1.setText("LOGIN");
        login1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(70, 58, 47), 5, true));
        login1.setMaximumSize(new java.awt.Dimension(100, 40));
        login1.setMinimumSize(new java.awt.Dimension(100, 40));
        login1.setPreferredSize(new java.awt.Dimension(100, 40));
        login1.addActionListener(this::login1ActionPerformed);
        fromboxle.add(login1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 360, 70, -1));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/toggle_pass.png"))); // NOI18N
        fromboxle.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 300, 20, 20));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        Leftpanel.add(fromboxle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(Leftpanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void login1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_login1ActionPerformed
         ValidationResult validation = validateLoginForm();
    if (!validation.isValid()) {
        showValidationErrors(validation);
        return;
    }

    try {
        User user = userService.login(validation.email, validation.password);
        
        if (user != null) {
            
            UserSession.getInstance().loadUserData();
            openHomeFrame(user);
        } else {
            
            JOptionPane.showMessageDialog(this,
                    "Invalid email or password!\n\n" +
                    "Please check:\n" +
                    "• Email address is correct\n" +
                    "• Password is correct\n" +
                    "• Caps Lock is not on",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            
            
            jPasswordField1.setText("");
            jPasswordField1.requestFocus();
        }
    } catch (Exception e) {
        
        JOptionPane.showMessageDialog(this,
                "An error occurred during login: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_login1ActionPerformed

    private void forgetpassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forgetpassActionPerformed
        String email = JOptionPane.showInputDialog(this,
                "Enter your registered email:",
                "Forgot Password",
                JOptionPane.QUESTION_MESSAGE);

        if (email != null && !email.trim().isEmpty()) {
String question = userService.getSecurityQuestion(email);
            if (question != null) {
                String answer = JOptionPane.showInputDialog(this,
                        "Security Question:\n" + question + "\n\nEnter your answer:",
                        "Security Verification",
                        JOptionPane.QUESTION_MESSAGE);

                if (answer != null && !answer.trim().isEmpty()) {
                    String newPassword = JOptionPane.showInputDialog(this,
                            "Enter new password (min 6 characters):",
                            "New Password",
                            JOptionPane.QUESTION_MESSAGE);

                    if (newPassword != null && newPassword.length() >= 6) {
                        int questionIndex = -1;
                        for (int i = 0; i < UserDatabaseObject.SECURITY_QUESTIONS.length; i++) {
                            if (UserDatabaseObject.SECURITY_QUESTIONS[i].equals(question)) {
                                questionIndex = i;
                                break;
                            }
                        }

                        if (questionIndex != -1) {
                           boolean success = userService.resetPassword(email, questionIndex, answer, newPassword);
                            if (success) {
                                JOptionPane.showMessageDialog(this,
                                        "Password reset successful!\nYou can now login with your new password.",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } else if (newPassword != null) {
                        JOptionPane.showMessageDialog(this,
                                "Password must be at least 6 characters!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Email not found in our system!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_forgetpassActionPerformed

    private void signupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupActionPerformed
        this.dispose();
        new SignUPFrame().setVisible(true);
    }//GEN-LAST:event_signupActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        login1ActionPerformed(evt);
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        jPasswordField1.requestFocus();
    }//GEN-LAST:event_jTextField1ActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Leftpanel;
    private javax.swing.JPanel Rightpanel;
    private javax.swing.JButton forgetpass;
    private javax.swing.JPanel fromBoxRi;
    private javax.swing.JPanel fromboxle;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton login1;
    private javax.swing.JButton signup;
    // End of variables declaration//GEN-END:variables
}
