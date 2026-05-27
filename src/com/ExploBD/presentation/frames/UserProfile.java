/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.ExploBD.presentation.frames;

import com.ExploBD.data.databaseObject.UserDatabaseObject;
import com.ExploBD.object.User;
import com.ExploBD.session.UserSession;
import com.ExploBD.util.DateHelper;
import com.ExploBD.util.RefreshManager;
import com.ExploBD.util.TravelStyleHelper;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * .
 * @author Mahina
 */
public class UserProfile extends javax.swing.JFrame {

    private User currentUser;
    private boolean isReadOnly = false;

    public UserProfile(User user) {

        this.currentUser = user;
        this.isReadOnly = false;
        initComponents();
        styleComponents();
        setLocationRelativeTo(null);
        initializeDateComponents();
        initializeGenderComboBox();
        initializeBudgetSlider();
        makeFrameResponsive();
        AppConfig.applyAndTrack(this);

        if (currentUser != null) {
            loadUserData();
        }
        makeFieldsReadOnly();
    }
// Add this NEW constructor for viewing other people's profiles (no edit button)

    public UserProfile(User user, boolean readOnly) {
        this.currentUser = user;
        this.isReadOnly = readOnly;
        initComponents();
        styleComponents();
        setLocationRelativeTo(null);
        initializeDateComponents();
        initializeGenderComboBox();
        initializeBudgetSlider();
        makeFrameResponsive();

        AppConfig.applyAndTrack(this);

        if (currentUser != null) {
            loadUserData();
        }
        makeFieldsReadOnly();
    }

    public UserProfile() {
        initComponents();
        styleComponents();
        setLocationRelativeTo(null);
        initializeDateComponents();
        initializeGenderComboBox();
        initializeBudgetSlider();
        makeFrameResponsive();
        AppConfig.applyAndTrack(this);
        makeFieldsReadOnly();
    }

    private void styleComponents() {

        Color mainBrown = new Color(88, 74, 60);
        Color lightBg = new Color(240, 235, 230);
        Color darkBrown = new Color(70, 58, 47);
        Color accentOrange = new Color(255, 153, 51);
        Color goldColor = new Color(255, 215, 0);

        minpane.setBackground(mainBrown);

        myprofile.setBackground(mainBrown);
        myprofile.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accentOrange));

        jLabel2.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        jLabel2.setForeground(Color.ORANGE);
        jLabel2.setText("MY PROFILE");

        backbutton.setText("< Back");
        backbutton.setFont(new Font("Arial", Font.BOLD, 12));
        backbutton.setBackground(accentOrange);
        backbutton.setForeground(Color.WHITE);
        backbutton.setFocusPainted(false);
        backbutton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 120, 30), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        backbutton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Find this section where editprofilebutton is styled and actions are set
        editprofilebutton.setBackground(darkBrown);
        editprofilebutton.setForeground(goldColor);
        editprofilebutton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        editprofilebutton.setText("Edit Profile");
        editprofilebutton.setBorder(new javax.swing.border.LineBorder(darkBrown, 2, true));
        editprofilebutton.setFocusPainted(false);
        editprofilebutton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ADD THIS CONDITION - hide edit button for read-only mode
        if (isReadOnly) {
            editprofilebutton.setVisible(false);
        } else {
            editprofilebutton.setVisible(true);
            editprofilebutton.addActionListener(e -> {
                if (currentUser != null) {
                    EditUserProfile editFrame = new EditUserProfile(currentUser);
                    editFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            currentUser = UserSession.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                loadUserData();
                            }
                            RefreshManager.getInstance().refreshAll();
                        }
                    });
                    editFrame.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(UserProfile.this, "No user data available to edit!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        infobox.setBackground(lightBg);
        infobox.setBorder(BorderFactory.createLineBorder(new Color(200, 180, 160), 2));

        Font labelFont = new Font("Comic Sans MS", Font.BOLD, 14);
        Color labelColor = darkBrown;

        name.setFont(labelFont);
        name.setForeground(labelColor);
        email.setFont(labelFont);
        email.setForeground(labelColor);
        nationality.setFont(labelFont);
        nationality.setForeground(labelColor);
        contact.setFont(labelFont);
        contact.setForeground(labelColor);
        contactinfo.setFont(labelFont);
        contactinfo.setForeground(labelColor);
        nid.setFont(labelFont);
        nid.setForeground(labelColor);
        bday.setFont(labelFont);
        bday.setForeground(labelColor);
        jLabel1.setFont(labelFont);
        jLabel1.setForeground(labelColor);
        jLabel4.setFont(labelFont);
        jLabel4.setForeground(labelColor);
        jLabel5.setFont(labelFont);
        jLabel5.setForeground(labelColor);

        userid.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        userid.setForeground(accentOrange);

        idfromdatabase.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        idfromdatabase.setForeground(Color.WHITE);

        Font textFieldFont = new Font("Bookman Old Style", Font.PLAIN, 14);
        Color textFieldBg = Color.WHITE;

        Component[] textFields = {jTextField1, jTextField2, jTextField4, jTextField5,
            jTextField6, jTextField7};

        for (Component field : textFields) {
            if (field instanceof JTextField) {
                ((JTextField) field).setBackground(textFieldBg);
                ((JTextField) field).setFont(textFieldFont);
                ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 160, 140)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        }

        Font comboFont = new Font("Bookman Old Style", Font.PLAIN, 14);
        gender.setBackground(textFieldBg);
        gender.setFont(comboFont);
        day.setBackground(textFieldBg);
        day.setFont(comboFont);
        month.setBackground(textFieldBg);
        month.setFont(comboFont);
        year.setBackground(textFieldBg);
        year.setFont(comboFont);

        Border comboBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 160, 140)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        gender.setBorder(comboBorder);
        day.setBorder(comboBorder);
        month.setBorder(comboBorder);
        year.setBorder(comboBorder);

        preferencebox.setBackground(new Color(250, 245, 240));
        preferencebox.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 2));

        showstatisticsbox.setBackground(new Color(250, 245, 240));
        showstatisticsbox.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 2));

        Font sectionTitleFont = new Font("Comic Sans MS", Font.BOLD, 18);
        preferencetitle.setFont(sectionTitleFont);
        preferencetitle.setForeground(darkBrown);
        preferencetitle.setText("PREFERENCES");

        statistics.setFont(sectionTitleFont);
        statistics.setForeground(darkBrown);
        statistics.setText("STATISTICS");

        Font checkBoxFont = new Font("Comic Sans MS", Font.BOLD, 13);
        Component[] checkboxes = {beach, mountain, urban, historical, culture,
            ecotourism, adventure, relaxation, photographic, nature, food};

        for (Component cb : checkboxes) {
            if (cb instanceof JCheckBox) {
                ((JCheckBox) cb).setFont(checkBoxFont);
                ((JCheckBox) cb).setForeground(darkBrown);
                ((JCheckBox) cb).setBackground(new Color(250, 245, 240));
            }
        }

    }

    private void makeFieldsReadOnly() {
        // Make all text fields read-only
        jTextField1.setEditable(false);
        jTextField2.setEditable(false);
        jTextField4.setEditable(false);
        jTextField5.setEditable(false);
        jTextField6.setEditable(false);
        jTextField7.setEditable(false);

        // Make combo boxes read-only
        gender.setEnabled(false);
        day.setEnabled(false);
        month.setEnabled(false);
        year.setEnabled(false);

        // Make sliders read-only
        jSlider1.setEnabled(false);
        jSlider2.setEnabled(false);

        // ✅ FIX: Make ALL preference checkboxes read-only in UserProfile
        // This applies regardless of isReadOnly (own profile or viewing others)
        beach.setEnabled(false);
        mountain.setEnabled(false);
        urban.setEnabled(false);
        historical.setEnabled(false);
        culture.setEnabled(false);
        ecotourism.setEnabled(false);
        adventure.setEnabled(false);
        relaxation.setEnabled(false);
        photographic.setEnabled(false);
        nature.setEnabled(false);
        food.setEnabled(false);

        // If viewing another user's profile, hide edit button
        if (isReadOnly) {
            editprofilebutton.setVisible(false);
        } else {
            editprofilebutton.setVisible(true);
        }
    }

    private void makeFrameResponsive() {

        getContentPane().removeAll();

        minpane = new javax.swing.JPanel();
        minpane.setBackground(new Color(88, 74, 60));
        minpane.setLayout(new java.awt.BorderLayout(5, 5));

        myprofile.setPreferredSize(new java.awt.Dimension(100, 60));
        myprofile.setLayout(new java.awt.BorderLayout());

        javax.swing.JPanel backPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));
        backPanel.setBackground(new Color(88, 74, 60));
        backbutton.setMinimumSize(new Dimension(90, 35));
        backbutton.setPreferredSize(new java.awt.Dimension(110, 35));
        backPanel.add(backbutton);
        myprofile.add(backPanel, java.awt.BorderLayout.WEST);

        javax.swing.JPanel titlePanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        titlePanel.setBackground(new Color(88, 74, 60));

        java.awt.GridBagConstraints titleGbc = new java.awt.GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.anchor = java.awt.GridBagConstraints.CENTER;
        titlePanel.add(jLabel2, titleGbc);

        myprofile.add(titlePanel, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel rightBalancePanel = new javax.swing.JPanel();
        rightBalancePanel.setBackground(new Color(88, 74, 60));
        rightBalancePanel.setPreferredSize(new java.awt.Dimension(80, 35));
        myprofile.add(rightBalancePanel, java.awt.BorderLayout.EAST);

        minpane.add(myprofile, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel contentPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(3, 3, 3, 3);

        rebuildInfobox();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(3, 3, 3, 5);
        contentPanel.add(infobox, gbc);

        javax.swing.JPanel mainRightPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints rightGbc = new java.awt.GridBagConstraints();
        rightGbc.insets = new java.awt.Insets(2, 2, 2, 2);

        rebuildPreferenceBox();
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.weighty = 0.60;
        rightGbc.fill = java.awt.GridBagConstraints.BOTH;
        rightGbc.insets = new java.awt.Insets(0, 0, 3, 0);
        mainRightPanel.add(preferencebox, rightGbc);

        rebuildStatisticsBox();
        rightGbc.gridy = 1;
        rightGbc.weighty = 0.40;
        rightGbc.insets = new java.awt.Insets(3, 0, 0, 0);
        mainRightPanel.add(showstatisticsbox, rightGbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.insets = new java.awt.Insets(3, 5, 3, 3);
        contentPanel.add(mainRightPanel, gbc);

        minpane.add(contentPanel, java.awt.BorderLayout.CENTER);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(minpane, java.awt.BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustForResize();
            }
        });

        adjustForResize();

        revalidate();
        repaint();
    }

    private void rebuildInfobox() {

        infobox.removeAll();

        infobox.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 3));
        idPanel.setBackground(infobox.getBackground());

        String displayId = currentUser != null && currentUser.getUserId() != null
                ? currentUser.getUserId() : " Not Set";
        JLabel idValueLabel = new JLabel("ID: " + displayId);
        idValueLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        idValueLabel.setForeground(new Color(255, 153, 51));

        idPanel.add(idValueLabel);
        infobox.add(idPanel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.8;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel photoFormPanel = new JPanel(new BorderLayout(20, 0));
        photoFormPanel.setBackground(infobox.getBackground());

        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBackground(infobox.getBackground());

        profilephoto.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel photoWrapper = new JPanel(new BorderLayout());
        photoWrapper.setBackground(infobox.getBackground());
        photoWrapper.add(profilephoto, BorderLayout.CENTER);

        JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        editButtonPanel.setBackground(infobox.getBackground());
        editprofilebutton.setPreferredSize(new Dimension(120, 32));
        editButtonPanel.add(editprofilebutton);

        photoPanel.add(photoWrapper, BorderLayout.CENTER);
        photoPanel.add(editButtonPanel, BorderLayout.SOUTH);

        photoFormPanel.add(photoPanel, BorderLayout.WEST);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(infobox.getBackground());
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        addCompactFormRowWithSeparator(formPanel, name, jTextField1, "Name");
        addCompactFormRowWithSeparator(formPanel, email, jTextField2, "Email");
        addCompactFormRowWithSeparator(formPanel, nationality, jTextField6, "Nationality");
        addCompactFormRowWithSeparator(formPanel, contact, jTextField5, "Emergency Contact");
        addCompactFormRowWithSeparator(formPanel, contactinfo, jTextField7, "Contact");
        addCompactFormRowWithSeparator(formPanel, nid, jTextField4, "NID/Passport");

        JPanel dobPanel = new JPanel(new BorderLayout(8, 0));
        dobPanel.setBackground(infobox.getBackground());
        dobPanel.setMaximumSize(new Dimension(800, 35));

        bday.setText("Date of Birth:");
        bday.setPreferredSize(new Dimension(100, 25));
        dobPanel.add(bday, BorderLayout.WEST);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        datePanel.setBackground(infobox.getBackground());

        datePanel.add(day);
        datePanel.add(month);
        datePanel.add(year);
        dobPanel.add(datePanel, BorderLayout.CENTER);

        formPanel.add(dobPanel);
        formPanel.add(Box.createVerticalStrut(8));

        JPanel genderPanel = new JPanel(new BorderLayout(8, 0));
        genderPanel.setBackground(infobox.getBackground());
        genderPanel.setMaximumSize(new Dimension(800, 40));

        jLabel1.setText("Gender:");
        jLabel1.setPreferredSize(new Dimension(100, 25));
        genderPanel.add(jLabel1, BorderLayout.WEST);

        genderPanel.add(gender, BorderLayout.CENTER);

        formPanel.add(genderPanel);
        formPanel.add(Box.createVerticalGlue());

        photoFormPanel.add(formPanel, BorderLayout.CENTER);
        infobox.add(photoFormPanel, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 10, 10, 10);

        JPanel budgetPanel = createBudgetPanel();
        infobox.add(budgetPanel, gbc);
    }

    private void addCompactFormRowWithSeparator(JPanel parent, JLabel label, JTextField field, String labelText) {
        JPanel rowPanel = new JPanel(new BorderLayout(8, 0));
        rowPanel.setBackground(parent.getBackground());
        rowPanel.setMaximumSize(new Dimension(800, 35));

        label.setText(labelText + ":");
        label.setPreferredSize(new Dimension(120, 25));
        rowPanel.add(label, BorderLayout.WEST);

        rowPanel.add(field, BorderLayout.CENTER);

        parent.add(rowPanel);
        parent.add(Box.createVerticalStrut(4));

        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(180, 160, 140));
        separator.setForeground(new Color(180, 160, 140));
        parent.add(separator);
        parent.add(Box.createVerticalStrut(4));
    }

    private void addCompactFormRow(javax.swing.JPanel parent, javax.swing.JLabel label,
            javax.swing.JTextField field, String labelText, int fieldWidth) {
        javax.swing.JPanel rowPanel = new javax.swing.JPanel(new java.awt.BorderLayout(5, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new java.awt.Dimension(900, 35));

        label.setText(labelText + ":");
        label.setPreferredSize(new java.awt.Dimension(100, 25));
        label.setMinimumSize(new java.awt.Dimension(90, 25));
        rowPanel.add(label, java.awt.BorderLayout.WEST);

        field.setPreferredSize(new java.awt.Dimension(fieldWidth, 28));
        field.setMinimumSize(new java.awt.Dimension(fieldWidth - 20, 25));
        rowPanel.add(field, java.awt.BorderLayout.CENTER);

        parent.add(rowPanel);
        parent.add(javax.swing.Box.createVerticalStrut(5));
    }

    private void addFormRowWithSeparator(JPanel parent, JLabel label, JTextField field, String labelText, int fieldWidth) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(parent.getBackground());
        rowPanel.setMaximumSize(new Dimension(1000, 40));

        label.setText(labelText + ":");
        label.setPreferredSize(new Dimension(140, 25));
        rowPanel.add(label, BorderLayout.WEST);

        field.setPreferredSize(new Dimension(fieldWidth, 30));
        field.setMinimumSize(new Dimension(fieldWidth - 50, 30));
        rowPanel.add(field, BorderLayout.CENTER);

        parent.add(rowPanel);
        parent.add(Box.createVerticalStrut(5));

        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(180, 160, 140));
        separator.setForeground(new Color(180, 160, 140));
        parent.add(separator);
        parent.add(Box.createVerticalStrut(5));
    }

    private void addCompactFormRow(javax.swing.JPanel parent, javax.swing.JLabel label,
            javax.swing.JTextField field, String labelText) {
        javax.swing.JPanel rowPanel = new javax.swing.JPanel(new java.awt.BorderLayout(5, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new java.awt.Dimension(1000, 35));

        label.setText(labelText + ":");
        label.setPreferredSize(new java.awt.Dimension(110, 25));
        label.setMinimumSize(new java.awt.Dimension(100, 25));
        rowPanel.add(label, java.awt.BorderLayout.WEST);

        field.setPreferredSize(new java.awt.Dimension(180, 25));
        field.setMinimumSize(new java.awt.Dimension(160, 25));
        rowPanel.add(field, java.awt.BorderLayout.CENTER);

        parent.add(rowPanel);
        parent.add(javax.swing.Box.createVerticalStrut(5));
    }

    private void addFormRowToBoxLayout(javax.swing.JPanel parent, javax.swing.JLabel label, javax.swing.JTextField field, String labelText) {
        javax.swing.JPanel rowPanel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new java.awt.Dimension(400, 40));
        rowPanel.setPreferredSize(new java.awt.Dimension(350, 35));

        label.setText(labelText);
        label.setPreferredSize(new java.awt.Dimension(140, 25));
        label.setMinimumSize(new java.awt.Dimension(120, 25));
        rowPanel.add(label, java.awt.BorderLayout.WEST);

        field.setPreferredSize(new java.awt.Dimension(180, 25));
        field.setMinimumSize(new java.awt.Dimension(150, 25));
        rowPanel.add(field, java.awt.BorderLayout.CENTER);

        parent.add(rowPanel);
        parent.add(javax.swing.Box.createVerticalStrut(5));
    }

    private javax.swing.JPanel createDateOfBirthRow() {
        javax.swing.JPanel dobRow = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        dobRow.setBackground(Color.WHITE);
        dobRow.setMaximumSize(new java.awt.Dimension(400, 40));

        bday.setText("Date of Birth:");
        bday.setPreferredSize(new java.awt.Dimension(140, 25));
        dobRow.add(bday, java.awt.BorderLayout.WEST);

        javax.swing.JPanel dateFieldsPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        dateFieldsPanel.setBackground(Color.WHITE);

        day.setPreferredSize(new java.awt.Dimension(70, 25));
        day.setMinimumSize(new java.awt.Dimension(60, 25));
        month.setPreferredSize(new java.awt.Dimension(80, 25));
        month.setMinimumSize(new java.awt.Dimension(70, 25));
        year.setPreferredSize(new java.awt.Dimension(80, 25));
        year.setMinimumSize(new java.awt.Dimension(70, 25));

        dateFieldsPanel.add(day);
        dateFieldsPanel.add(month);
        dateFieldsPanel.add(year);

        dobRow.add(dateFieldsPanel, java.awt.BorderLayout.CENTER);

        return dobRow;
    }

    private javax.swing.JPanel createGenderRow() {
        javax.swing.JPanel genderRow = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        genderRow.setBackground(Color.WHITE);
        genderRow.setMaximumSize(new java.awt.Dimension(400, 40));

        jLabel1.setText("Gender:");
        jLabel1.setPreferredSize(new java.awt.Dimension(140, 25));
        genderRow.add(jLabel1, java.awt.BorderLayout.WEST);

        gender.setPreferredSize(new java.awt.Dimension(180, 25));
        gender.setMinimumSize(new java.awt.Dimension(150, 25));
        genderRow.add(gender, java.awt.BorderLayout.CENTER);

        return genderRow;
    }

    private JPanel createBudgetPanel() {
        JPanel budgetPanel = new JPanel();
        budgetPanel.setLayout(new BoxLayout(budgetPanel, BoxLayout.Y_AXIS));
        budgetPanel.setBackground(new Color(250, 245, 240));
        budgetPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 160, 140), 1),
                "BUDGET",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Comic Sans MS", Font.BOLD, 14),
                new Color(88, 74, 60)
        ));

        budgetPanel.setMinimumSize(new Dimension(300, 120));
        budgetPanel.setPreferredSize(new Dimension(350, 130));

        JPanel minRow = new JPanel(new BorderLayout(10, 0));
        minRow.setBackground(budgetPanel.getBackground());
        minRow.setMaximumSize(new Dimension(800, 40));

        jLabel4.setText("Min:");
        jLabel4.setPreferredSize(new Dimension(50, 25));
        minRow.add(jLabel4, BorderLayout.WEST);

        jSlider1.setPreferredSize(new Dimension(180, 30));
        jSlider1.setMinimumSize(new Dimension(150, 30));
        minRow.add(jSlider1, BorderLayout.CENTER);

        jLabel3.setPreferredSize(new Dimension(70, 25));
        minRow.add(jLabel3, BorderLayout.EAST);

        budgetPanel.add(minRow);
        budgetPanel.add(Box.createVerticalStrut(8));

        JPanel maxRow = new JPanel(new BorderLayout(10, 0));
        maxRow.setBackground(budgetPanel.getBackground());
        maxRow.setMaximumSize(new Dimension(800, 40));

        jLabel5.setText("Max:");
        jLabel5.setPreferredSize(new Dimension(50, 25));
        maxRow.add(jLabel5, BorderLayout.WEST);

        jSlider2.setPreferredSize(new Dimension(180, 30));
        jSlider2.setMinimumSize(new Dimension(150, 30));
        maxRow.add(jSlider2, BorderLayout.CENTER);

        jLabel6.setPreferredSize(new Dimension(70, 25));
        maxRow.add(jLabel6, BorderLayout.EAST);

        budgetPanel.add(maxRow);

        return budgetPanel;
    }

    private void styleActionButton(javax.swing.JButton button) {
        button.setFont(new java.awt.Font("Bookman Old Style", java.awt.Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(88, 74, 60));
        button.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 153, 51), 2));
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 153, 51));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(88, 74, 60));
            }
        });
    }

    private void rebuildPreferenceBox() {
        preferencebox.removeAll();
        preferencebox.setLayout(new BorderLayout(0, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        titlePanel.setBackground(preferencebox.getBackground());
        preferencetitle.setText("PREFERENCES");
        titlePanel.add(preferencetitle);

        JSeparator titleSeparator = new JSeparator();
        titleSeparator.setBackground(new Color(255, 153, 51));
        titleSeparator.setForeground(new Color(255, 153, 51));
        titleSeparator.setPreferredSize(new Dimension(200, 2));

        JPanel separatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        separatorPanel.setBackground(preferencebox.getBackground());
        separatorPanel.add(titleSeparator);

        JPanel checkboxesPanel = new JPanel(new GridLayout(6, 2, 15, 8));
        checkboxesPanel.setBackground(preferencebox.getBackground());
        checkboxesPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Font boldCheckboxFont = new Font("Comic Sans MS", Font.BOLD, 13);
        beach.setFont(boldCheckboxFont);
        mountain.setFont(boldCheckboxFont);
        urban.setFont(boldCheckboxFont);
        historical.setFont(boldCheckboxFont);
        culture.setFont(boldCheckboxFont);
        ecotourism.setFont(boldCheckboxFont);
        adventure.setFont(boldCheckboxFont);
        relaxation.setFont(boldCheckboxFont);
        photographic.setFont(boldCheckboxFont);
        nature.setFont(boldCheckboxFont);
        food.setFont(boldCheckboxFont);

        checkboxesPanel.add(beach);
        checkboxesPanel.add(adventure);
        checkboxesPanel.add(mountain);
        checkboxesPanel.add(relaxation);
        checkboxesPanel.add(urban);
        checkboxesPanel.add(photographic);
        checkboxesPanel.add(historical);
        checkboxesPanel.add(nature);
        checkboxesPanel.add(culture);
        checkboxesPanel.add(food);
        checkboxesPanel.add(ecotourism);
        checkboxesPanel.add(new JLabel());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(preferencebox.getBackground());

        mainPanel.add(titlePanel);
        mainPanel.add(separatorPanel);
        mainPanel.add(checkboxesPanel);
        mainPanel.add(Box.createVerticalGlue());

        preferencebox.add(mainPanel, BorderLayout.CENTER);
    }

    private void rebuildStatisticsBox() {
        showstatisticsbox.removeAll();
        showstatisticsbox.setLayout(new BorderLayout(0, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        titlePanel.setBackground(showstatisticsbox.getBackground());
        statistics.setText("STATISTICS");
        titlePanel.add(statistics);

        JSeparator titleSeparator = new JSeparator();
        titleSeparator.setBackground(new Color(255, 153, 51));
        titleSeparator.setForeground(new Color(255, 153, 51));
        titleSeparator.setPreferredSize(new Dimension(150, 2));

        JPanel separatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        separatorPanel.setBackground(showstatisticsbox.getBackground());
        separatorPanel.add(titleSeparator);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(showstatisticsbox.getBackground());
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        String[][] stats;

        if (currentUser != null) {
            String joinDate = "N/A";
            if (currentUser.getCreatedAt() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM yyyy");
                joinDate = sdf.format(currentUser.getCreatedAt());
            }

            int visitedCount = 0;
            if (currentUser.getVisitedPlaces() != null) {
                visitedCount = currentUser.getVisitedPlaces().size();
            }

            stats = new String[][]{
                {"Member Since", joinDate},
                {"Total Travels", String.valueOf(visitedCount)},
                {"Total Spent", "0 tk"},
                {"Active Tours", "0"},
                {"Places Visited", String.valueOf(visitedCount)},
                {"Last Travel", "Never"}
            };
        } else {
            stats = new String[][]{
                {"Member Since", "Not logged in"},
                {"Total Travels", "0"},
                {"Total Spent", "0 tk"},
                {"Active Tours", "0"},
                {"Places Visited", "0"},
                {"Last Travel", "Never"}
            };
        }

        for (String[] stat : stats) {
            JPanel statRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            statRow.setBackground(showstatisticsbox.getBackground());

            JLabel categoryLabel = new JLabel(stat[0] + ":");
            categoryLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
            categoryLabel.setForeground(new Color(70, 58, 47));

            JLabel valueLabel = new JLabel(stat[1]);
            valueLabel.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
            valueLabel.setForeground(new Color(100, 80, 60));

            statRow.add(categoryLabel);
            statRow.add(valueLabel);
            statsPanel.add(statRow);
            statsPanel.add(Box.createVerticalStrut(8));
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(showstatisticsbox.getBackground());

        mainPanel.add(titlePanel);
        mainPanel.add(separatorPanel);
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createVerticalGlue());

        showstatisticsbox.add(mainPanel, BorderLayout.CENTER);
    }

    private void adjustForResize() {
        int width = getWidth();
        int height = getHeight();

        boolean isSmallScreen = width < 1000;
        boolean isVerySmallScreen = width < 850;

        int baseFontSize;
        if (isVerySmallScreen) {
            baseFontSize = Math.max(10, width / 90);
        } else if (isSmallScreen) {
            baseFontSize = Math.max(11, width / 85);
        } else {
            baseFontSize = Math.max(13, width / 75);
        }
        baseFontSize = Math.min(baseFontSize, 16);

        updateComponentFonts(baseFontSize);

        int fieldWidth;
        if (isVerySmallScreen) {
            fieldWidth = Math.max(180, width / 5);
        } else if (isSmallScreen) {
            fieldWidth = Math.max(200, width / 5);
        } else {
            fieldWidth = Math.max(250, width / 4);
        }
        fieldWidth = Math.min(fieldWidth, 300);

        java.awt.Component[] textFields = {
            jTextField1, jTextField2, jTextField4, jTextField5,
            jTextField6, jTextField7
        };

        for (java.awt.Component comp : textFields) {
            int compWidth = isVerySmallScreen ? fieldWidth - 30 : fieldWidth;
            comp.setPreferredSize(new java.awt.Dimension(compWidth, 30));
            comp.setMinimumSize(new java.awt.Dimension(compWidth - 40, 28));
        }

        int genderWidth;
        if (isVerySmallScreen) {
            genderWidth = Math.max(fieldWidth, width / 4);
            genderWidth = Math.max(fieldWidth + 50, width / 3);
        } else {
            genderWidth = Math.max(fieldWidth + 80, width / 2);
        }
        genderWidth = Math.min(genderWidth, 400);

        gender.setPreferredSize(new java.awt.Dimension(genderWidth, 32));
        gender.setMinimumSize(new java.awt.Dimension(genderWidth - 50, 30));

        int dateWidth;
        int yearWidth;

        if (isVerySmallScreen) {
            dateWidth = 60;  // Reduced from 70
            yearWidth = 65;  // Year gets a bit more width
        } else if (isSmallScreen) {
            dateWidth = 70;  // Reduced from 80
            yearWidth = 75;  // Year gets a bit more width
        } else {
            dateWidth = 80;  // Reduced from 90
            yearWidth = 85;  // Year gets a bit more width
        }

        day.setPreferredSize(new java.awt.Dimension(dateWidth, 30));
        day.setMinimumSize(new java.awt.Dimension(dateWidth - 10, 28));
        month.setPreferredSize(new java.awt.Dimension(dateWidth, 30));  // Same as day
        month.setMinimumSize(new java.awt.Dimension(dateWidth - 10, 28));
        year.setPreferredSize(new java.awt.Dimension(yearWidth, 30));   // Year gets extra width
        year.setMinimumSize(new java.awt.Dimension(yearWidth - 10, 28));

        int sliderWidth;
        if (isVerySmallScreen) {
            sliderWidth = Math.max(180, width / 5);
        } else if (isSmallScreen) {
            sliderWidth = Math.max(200, width / 4);
        } else {
            sliderWidth = Math.max(250, width / 3);
        }
        sliderWidth = Math.min(sliderWidth, 300);

        jSlider1.setPreferredSize(new java.awt.Dimension(sliderWidth, 35));
        jSlider1.setMinimumSize(new java.awt.Dimension(sliderWidth - 60, 30));
        jSlider2.setPreferredSize(new java.awt.Dimension(sliderWidth, 35));
        jSlider2.setMinimumSize(new java.awt.Dimension(sliderWidth - 60, 30));

        int photoWidth, photoHeight;
        if (isVerySmallScreen) {
            photoWidth = Math.max(120, width / 7);
            photoHeight = (int) (photoWidth * 1.5);
        } else if (isSmallScreen) {
            photoWidth = Math.max(140, width / 6);
            photoHeight = (int) (photoWidth * 1.5);
        } else {
            photoWidth = Math.max(150, width / 5);
            photoHeight = (int) (photoWidth * 1.5);
        }

        profilephoto.setPreferredSize(new java.awt.Dimension(photoWidth, photoHeight));
        profilephoto.setMinimumSize(new java.awt.Dimension(100, 150));

        if (profilephoto.getIcon() != null) {
            ImageIcon currentIcon = (ImageIcon) profilephoto.getIcon();
            if (currentIcon.getImage() != null) {
                Image scaledImage = currentIcon.getImage().getScaledInstance(
                        photoWidth,
                        photoHeight,
                        Image.SCALE_SMOOTH
                );
                profilephoto.setIcon(new ImageIcon(scaledImage));
            }
        } else if (profilephoto.getText().isEmpty()) {

            profilephoto.setText("PROFILE\nPHOTO");
            profilephoto.setFont(new Font("Comic Sans MS", Font.BOLD,
                    isVerySmallScreen ? 12 : 14));
            profilephoto.setForeground(new Color(120, 100, 80));
            profilephoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }

        int labelWidth;
        if (isVerySmallScreen) {
            labelWidth = 120;
        } else if (isSmallScreen) {
            labelWidth = 130;
        } else {
            labelWidth = 140;
        }

        name.setPreferredSize(new java.awt.Dimension(labelWidth, 28));
        email.setPreferredSize(new java.awt.Dimension(labelWidth, 28));
        nationality.setPreferredSize(new java.awt.Dimension(labelWidth, 28));
        contact.setPreferredSize(new java.awt.Dimension(labelWidth, 28));
        contactinfo.setPreferredSize(new java.awt.Dimension(labelWidth, 28));
        nid.setPreferredSize(new java.awt.Dimension(labelWidth, 28));
        bday.setPreferredSize(new java.awt.Dimension(labelWidth, 28));
        jLabel1.setPreferredSize(new java.awt.Dimension(labelWidth, 28));

        int budgetLabelWidth = isVerySmallScreen ? 80 : 90;
        int budgetValueWidth = isVerySmallScreen ? 90 : 100;

        jLabel4.setPreferredSize(new java.awt.Dimension(budgetLabelWidth, 28));
        jLabel5.setPreferredSize(new java.awt.Dimension(budgetLabelWidth, 28));
        jLabel3.setPreferredSize(new java.awt.Dimension(budgetValueWidth, 28));
        jLabel6.setPreferredSize(new java.awt.Dimension(budgetValueWidth, 28));

        int editButtonWidth = isVerySmallScreen ? 100 : 120;
        editprofilebutton.setPreferredSize(new java.awt.Dimension(editButtonWidth, 32));

        int idFontSize = isVerySmallScreen ? baseFontSize + 2 : baseFontSize + 4;
        idfromdatabase.setFont(new Font("Comic Sans MS", Font.BOLD, idFontSize));

        revalidate();
        repaint();
    }

    private void updateComponentFonts(int baseFontSize) {

        java.awt.Font textFont = new java.awt.Font("Bookman Old Style", java.awt.Font.PLAIN, baseFontSize);
        jTextField1.setFont(textFont);
        jTextField2.setFont(textFont);
        jTextField4.setFont(textFont);
        jTextField5.setFont(textFont);
        jTextField6.setFont(textFont);
        jTextField7.setFont(textFont);

        java.awt.Font labelFont = new java.awt.Font("Comic Sans MS", Font.BOLD, baseFontSize);
        name.setFont(labelFont);
        email.setFont(labelFont);
        nationality.setFont(labelFont);
        contact.setFont(new java.awt.Font("Comic Sans MS", Font.BOLD, baseFontSize));
        contactinfo.setFont(labelFont);
        nid.setFont(new java.awt.Font("Comic Sans MS", Font.BOLD, baseFontSize));
        bday.setFont(labelFont);
        jLabel1.setFont(labelFont);
        jLabel4.setFont(labelFont);
        jLabel5.setFont(labelFont);
        userid.setFont(labelFont);
        jLabel3.setFont(labelFont);
        jLabel6.setFont(labelFont);

        jLabel3.setForeground(new Color(255, 153, 51));
        jLabel6.setForeground(new Color(255, 153, 51));

        java.awt.Font comboFont = new java.awt.Font("Bookman Old Style", java.awt.Font.PLAIN, baseFontSize);
        day.setFont(comboFont);
        month.setFont(comboFont);
        year.setFont(comboFont);
        gender.setFont(comboFont);

        java.awt.Font checkFont = new java.awt.Font("Comic Sans MS", Font.BOLD, baseFontSize);
        beach.setFont(checkFont);
        mountain.setFont(checkFont);
        urban.setFont(checkFont);
        historical.setFont(checkFont);
        culture.setFont(checkFont);
        ecotourism.setFont(checkFont);
        adventure.setFont(checkFont);
        relaxation.setFont(checkFont);
        photographic.setFont(checkFont);
        nature.setFont(checkFont);
        food.setFont(checkFont);

        java.awt.Font titleFont = new java.awt.Font("Comic Sans MS", Font.BOLD, baseFontSize + 2);
        preferencetitle.setFont(titleFont);
        statistics.setFont(titleFont);

        java.awt.Font mainTitleFont = new java.awt.Font("Comic Sans MS", Font.BOLD, baseFontSize + 6);
        jLabel2.setFont(mainTitleFont);

        java.awt.Font buttonFont = new java.awt.Font("Comic Sans MS", Font.BOLD, baseFontSize);
        editprofilebutton.setFont(buttonFont);
        backbutton.setFont(buttonFont);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        minpane = new javax.swing.JPanel();
        infobox = new com.ExploBD.Frame.RoundedPanel();
        profilephoto = new javax.swing.JLabel();
        editprofilebutton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        namepanel = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        name = new javax.swing.JLabel();
        idfromdatabase = new javax.swing.JLabel();
        userid = new javax.swing.JLabel();
        emailpanel = new javax.swing.JPanel();
        email = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        nationalpanel = new javax.swing.JPanel();
        nationality = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        emergencypanel = new javax.swing.JPanel();
        contact = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        contactpanel = new javax.swing.JPanel();
        jTextField7 = new javax.swing.JTextField();
        contactinfo = new javax.swing.JLabel();
        nidPanel = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        nid = new javax.swing.JLabel();
        bdaypanel = new javax.swing.JPanel();
        bday = new javax.swing.JLabel();
        day = new javax.swing.JComboBox<>();
        month = new javax.swing.JComboBox<>();
        year = new javax.swing.JComboBox<>();
        genderpanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        gender = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jPanel5 = new javax.swing.JPanel();
        jSlider2 = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        preferencebox = new com.ExploBD.Frame.RoundedPanel();
        preferencetitle = new javax.swing.JLabel();
        beach = new javax.swing.JCheckBox();
        mountain = new javax.swing.JCheckBox();
        urban = new javax.swing.JCheckBox();
        historical = new javax.swing.JCheckBox();
        culture = new javax.swing.JCheckBox();
        ecotourism = new javax.swing.JCheckBox();
        adventure = new javax.swing.JCheckBox();
        relaxation = new javax.swing.JCheckBox();
        photographic = new javax.swing.JCheckBox();
        nature = new javax.swing.JCheckBox();
        food = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        showstatisticsbox = new com.ExploBD.Frame.RoundedPanel();
        statistics = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        myprofile = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        backbutton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        minpane.setBackground(new java.awt.Color(255, 255, 255));
        minpane.setForeground(new java.awt.Color(88, 74, 60));
        minpane.setMinimumSize(new java.awt.Dimension(800, 600));
        minpane.setPreferredSize(new java.awt.Dimension(800, 600));
        minpane.setRequestFocusEnabled(false);
        minpane.setVerifyInputWhenFocusTarget(false);
        minpane.setLayout(new java.awt.GridBagLayout());

        infobox.setBackground(new java.awt.Color(255, 255, 255));
        infobox.setMinimumSize(new java.awt.Dimension(525, 400));
        infobox.setPreferredSize(new java.awt.Dimension(525, 400));

        profilephoto.setBackground(new java.awt.Color(255, 255, 255));
        profilephoto.setForeground(new java.awt.Color(255, 255, 255));
        profilephoto.setText("jLabel1");

        editprofilebutton.setBackground(new java.awt.Color(70, 58, 47));
        editprofilebutton.setFont(new java.awt.Font("Comic Sans MS", 3, 14)); // NOI18N
        editprofilebutton.setForeground(new java.awt.Color(255, 215, 0));
        editprofilebutton.setText("Edit");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 250));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 250));

        jTextField1.setFont(new java.awt.Font("Bookman Old Style", 1, 14)); // NOI18N

        name.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        name.setForeground(new java.awt.Color(88, 74, 60));
        name.setText("Name:");
        name.setMaximumSize(new java.awt.Dimension(32676, 32676));
        name.setMinimumSize(new java.awt.Dimension(100, 30));
        name.setName(""); // NOI18N
        name.setPreferredSize(new java.awt.Dimension(100, 30));

        idfromdatabase.setText("jLabel5");

        userid.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        userid.setText("ID:");
        userid.setMinimumSize(new java.awt.Dimension(25, 25));
        userid.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout namepanelLayout = new javax.swing.GroupLayout(namepanel);
        namepanel.setLayout(namepanelLayout);
        namepanelLayout.setHorizontalGroup(
            namepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(namepanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1)
                .addContainerGap())
            .addGroup(namepanelLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(idfromdatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        namepanelLayout.setVerticalGroup(
            namepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(namepanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(namepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idfromdatabase)
                    .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(namepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(namepanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        email.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        email.setText("E-mail:");
        email.setMaximumSize(new java.awt.Dimension(32676, 32676));
        email.setPreferredSize(new java.awt.Dimension(51, 25));

        jTextField2.setFont(new java.awt.Font("Bookman Old Style", 1, 14)); // NOI18N
        jTextField2.addActionListener(this::jTextField2ActionPerformed);

        javax.swing.GroupLayout emailpanelLayout = new javax.swing.GroupLayout(emailpanel);
        emailpanel.setLayout(emailpanelLayout);
        emailpanelLayout.setHorizontalGroup(
            emailpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emailpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2)
                .addContainerGap())
        );
        emailpanelLayout.setVerticalGroup(
            emailpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emailpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(emailpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        nationality.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        nationality.setText("Nationality:");
        nationality.setMaximumSize(new java.awt.Dimension(32676, 32676));

        jTextField6.setFont(new java.awt.Font("Bookman Old Style", 1, 14)); // NOI18N
        jTextField6.addActionListener(this::jTextField6ActionPerformed);

        javax.swing.GroupLayout nationalpanelLayout = new javax.swing.GroupLayout(nationalpanel);
        nationalpanel.setLayout(nationalpanelLayout);
        nationalpanelLayout.setHorizontalGroup(
            nationalpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nationalpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField6)
                .addContainerGap())
        );
        nationalpanelLayout.setVerticalGroup(
            nationalpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nationalpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nationalpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nationality, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        contact.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        contact.setText("EmergencyContact:");
        contact.setMaximumSize(new java.awt.Dimension(32676, 32676));

        jTextField5.setFont(new java.awt.Font("Bookman Old Style", 1, 14)); // NOI18N
        jTextField5.addActionListener(this::jTextField5ActionPerformed);

        javax.swing.GroupLayout emergencypanelLayout = new javax.swing.GroupLayout(emergencypanel);
        emergencypanel.setLayout(emergencypanelLayout);
        emergencypanelLayout.setHorizontalGroup(
            emergencypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emergencypanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField5)
                .addContainerGap())
        );
        emergencypanelLayout.setVerticalGroup(
            emergencypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emergencypanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(emergencypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contact, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTextField7.setFont(new java.awt.Font("Bookman Old Style", 1, 14)); // NOI18N

        contactinfo.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        contactinfo.setText("Contact:");
        contactinfo.setMaximumSize(new java.awt.Dimension(32676, 32676));

        javax.swing.GroupLayout contactpanelLayout = new javax.swing.GroupLayout(contactpanel);
        contactpanel.setLayout(contactpanelLayout);
        contactpanelLayout.setHorizontalGroup(
            contactpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contactinfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField7)
                .addContainerGap())
        );
        contactpanelLayout.setVerticalGroup(
            contactpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contactinfo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTextField4.setFont(new java.awt.Font("Bookman Old Style", 1, 14)); // NOI18N

        nid.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        nid.setText("NID/Passport No.:");
        nid.setMaximumSize(new java.awt.Dimension(32676, 32676));

        javax.swing.GroupLayout nidPanelLayout = new javax.swing.GroupLayout(nidPanel);
        nidPanel.setLayout(nidPanelLayout);
        nidPanelLayout.setHorizontalGroup(
            nidPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nidPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4)
                .addContainerGap())
        );
        nidPanelLayout.setVerticalGroup(
            nidPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nidPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nidPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nid, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        bday.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        bday.setText("Date of Birth:");
        bday.setMaximumSize(new java.awt.Dimension(32676, 32676));
        bday.setMinimumSize(new java.awt.Dimension(100, 25));
        bday.setPreferredSize(new java.awt.Dimension(100, 25));

        day.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        month.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        year.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        year.setMinimumSize(new java.awt.Dimension(150, 25));
        year.setPreferredSize(new java.awt.Dimension(150, 25));

        javax.swing.GroupLayout bdaypanelLayout = new javax.swing.GroupLayout(bdaypanel);
        bdaypanel.setLayout(bdaypanelLayout);
        bdaypanelLayout.setHorizontalGroup(
            bdaypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bdaypanelLayout.createSequentialGroup()
                .addComponent(bday, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(day, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(month, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bdaypanelLayout.setVerticalGroup(
            bdaypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bdaypanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(bdaypanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bday, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(day, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(month, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        genderpanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel1.setText("Gender:");
        jLabel1.setMaximumSize(new java.awt.Dimension(32676, 32676));

        gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout genderpanelLayout = new javax.swing.GroupLayout(genderpanel);
        genderpanel.setLayout(genderpanelLayout);
        genderpanelLayout.setHorizontalGroup(
            genderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genderpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gender, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        genderpanelLayout.setVerticalGroup(
            genderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genderpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genderpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gender, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(namepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(emailpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nationalpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(emergencypanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nidPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bdaypanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(genderpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contactpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(namepanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(emailpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(nationalpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(emergencypanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(contactpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(nidPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(bdaypanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genderpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel4.setText("Minimum Budget:");

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel3.setText("jLabel3");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(9, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel5.setText("Maximum Budget:");

        jLabel6.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel6.setText("jLabel6");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        javax.swing.GroupLayout infoboxLayout = new javax.swing.GroupLayout(infobox);
        infobox.setLayout(infoboxLayout);
        infoboxLayout.setHorizontalGroup(
            infoboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoboxLayout.createSequentialGroup()
                .addGroup(infoboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoboxLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(editprofilebutton)
                        .addGap(46, 46, 46))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoboxLayout.createSequentialGroup()
                        .addComponent(profilephoto, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(infoboxLayout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addGroup(infoboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        infoboxLayout.setVerticalGroup(
            infoboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoboxLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(infoboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoboxLayout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(profilephoto, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(editprofilebutton))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = -1;
        gridBagConstraints.ipady = 59;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        minpane.add(infobox, gridBagConstraints);

        preferencetitle.setFont(new java.awt.Font("Comic Sans MS", 3, 18)); // NOI18N
        preferencetitle.setText("Preference");

        beach.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        beach.setText("Beach");
        beach.setBorder(null);
        beach.setMaximumSize(new java.awt.Dimension(150, 30));
        beach.setMinimumSize(new java.awt.Dimension(100, 25));
        beach.setPreferredSize(new java.awt.Dimension(100, 25));
        beach.addActionListener(this::beachActionPerformed);

        mountain.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        mountain.setText("Mountain");

        urban.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        urban.setText("Urban");

        historical.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        historical.setText("Historical");

        culture.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        culture.setText("Culture");

        ecotourism.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        ecotourism.setText("Eco-tourism");

        adventure.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        adventure.setText("Adventure");

        relaxation.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        relaxation.setText("Relaxation");

        photographic.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        photographic.setText("PhotoGraphy");

        nature.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        nature.setText("Nature");
        nature.addActionListener(this::natureActionPerformed);

        food.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        food.setText("Food");
        food.addActionListener(this::foodActionPerformed);

        javax.swing.GroupLayout preferenceboxLayout = new javax.swing.GroupLayout(preferencebox);
        preferencebox.setLayout(preferenceboxLayout);
        preferenceboxLayout.setHorizontalGroup(
            preferenceboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(preferencetitle, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(beach, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(adventure, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(mountain, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(relaxation, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(urban, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(photographic))
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(historical, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(nature, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(culture, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(food, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(ecotourism, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        preferenceboxLayout.setVerticalGroup(
            preferenceboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferenceboxLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(preferencetitle, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(preferenceboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(beach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adventure))
                .addGap(5, 5, 5)
                .addGroup(preferenceboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mountain)
                    .addComponent(relaxation))
                .addGap(5, 5, 5)
                .addGroup(preferenceboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(urban)
                    .addComponent(photographic))
                .addGap(5, 5, 5)
                .addGroup(preferenceboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(historical)
                    .addComponent(nature))
                .addGap(5, 5, 5)
                .addGroup(preferenceboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(culture)
                    .addComponent(food))
                .addGap(6, 6, 6)
                .addComponent(ecotourism)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 0);
        minpane.add(preferencebox, gridBagConstraints);

        statistics.setFont(new java.awt.Font("Comic Sans MS", 3, 18)); // NOI18N
        statistics.setText("Statistics");

        javax.swing.GroupLayout showstatisticsboxLayout = new javax.swing.GroupLayout(showstatisticsbox);
        showstatisticsbox.setLayout(showstatisticsboxLayout);
        showstatisticsboxLayout.setHorizontalGroup(
            showstatisticsboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showstatisticsboxLayout.createSequentialGroup()
                .addGroup(showstatisticsboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(showstatisticsboxLayout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(statistics, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(showstatisticsboxLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        showstatisticsboxLayout.setVerticalGroup(
            showstatisticsboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showstatisticsboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statistics, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 52;
        gridBagConstraints.ipady = 207;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        minpane.add(showstatisticsbox, gridBagConstraints);

        myprofile.setBackground(new java.awt.Color(88, 74, 60));
        myprofile.setMinimumSize(new java.awt.Dimension(775, 50));

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 215, 0));
        jLabel2.setText("My Profile");

        backbutton.setText("← Back");
        backbutton.addActionListener(this::backbuttonActionPerformed);

        javax.swing.GroupLayout myprofileLayout = new javax.swing.GroupLayout(myprofile);
        myprofile.setLayout(myprofileLayout);
        myprofileLayout.setHorizontalGroup(
            myprofileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(myprofileLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(backbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(161, 161, 161)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                .addContainerGap())
        );
        myprofileLayout.setVerticalGroup(
            myprofileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, myprofileLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(myprofileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(myprofileLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel2))
                    .addComponent(backbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        minpane.add(myprofile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(minpane, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void beachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beachActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_beachActionPerformed

    private void foodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foodActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_foodActionPerformed

    private void natureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_natureActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_natureActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void backbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backbuttonActionPerformed

        RefreshManager.getInstance().refreshAll();

        // Close UserProfile
        this.dispose();

//           RefreshManager.getInstance().refreshAll();
//        if (currentUser != null) {
//            new HomeFrame(currentUser).setVisible(true);
//        } else {
//            new HomeFrame().setVisible(true);
//        }
//        this.dispose();
    }//GEN-LAST:event_backbuttonActionPerformed

    /**
     * @param args the command line arguments
     */
    private void initializeDateComponents() {

        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = String.valueOf(i + 1);
        }
        day.setModel(new javax.swing.DefaultComboBoxModel<>(days));

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        month.setModel(new javax.swing.DefaultComboBoxModel<>(months));

        int currentYear = java.time.Year.now().getValue();
        String[] years = new String[currentYear - 1950 + 1];
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(1950 + i);
        }
        year.setModel(new javax.swing.DefaultComboBoxModel<>(years));

        day.setSelectedIndex(0);
        month.setSelectedIndex(0);
        year.setSelectedIndex(years.length - 1);
    }

    private void initializeGenderComboBox() {
        String[] genders = {"Select Gender", "Male", "Female", "Other", "Prefer not to say"};
        gender.setModel(new javax.swing.DefaultComboBoxModel<>(genders));
    }

    private void initializeBudgetSlider() {

        jSlider1.setMinimum(500);
        jSlider1.setMaximum(20000);
        jSlider1.setValue(500);
        jSlider1.setPaintTicks(false);
        jSlider1.setPaintLabels(false);
        jSlider1.setSnapToTicks(false);
        jSlider1.setBackground(new Color(250, 245, 240));
        jSlider1.setForeground(new Color(88, 74, 60));

        jSlider2.setMinimum(500);
        jSlider2.setMaximum(20000);
        jSlider2.setValue(10000);
        jSlider2.setPaintTicks(false);
        jSlider2.setPaintLabels(false);
        jSlider2.setSnapToTicks(false);
        jSlider2.setBackground(new Color(250, 245, 240));
        jSlider2.setForeground(new Color(88, 74, 60));

        jLabel3.setText(formatBudgetValue(jSlider1.getValue()) + " tk");
        jLabel6.setText(formatBudgetValue(jSlider2.getValue()) + " tk");

        jSlider1.addChangeListener(evt -> {
            if (!jSlider1.getValueIsAdjusting()) {
                jLabel3.setText(formatBudgetValue(jSlider1.getValue()) + " tk");
                if (jSlider1.getValue() > jSlider2.getValue()) {
                    jSlider2.setValue(jSlider1.getValue());
                    jLabel6.setText(formatBudgetValue(jSlider1.getValue()) + " tk");
                }
            }
        });

        jSlider2.addChangeListener(evt -> {
            if (!jSlider2.getValueIsAdjusting()) {
                jLabel6.setText(formatBudgetValue(jSlider2.getValue()) + " tk");
                if (jSlider2.getValue() < jSlider1.getValue()) {
                    jSlider1.setValue(jSlider2.getValue());
                    jLabel3.setText(formatBudgetValue(jSlider2.getValue()) + " tk");
                }
            }
        });
    }

    private String formatBudgetValue(int value) {
        if (value >= 1000) {
            return String.format("%,d", value);
        } else {
            return String.valueOf(value);
        }
    }

    private void loadUserData() {
        if (currentUser == null) {
            return;
        }

        try {
            // CRITICAL: Reload the user from database to get complete profile data
            // This ensures we have phone, preferences, etc. even if the passed user object is incomplete
            UserDatabaseObject userDAO = new UserDatabaseObject();
            User completeUser = userDAO.findById(currentUser.getUserId());
            
            

            if (completeUser != null) {
                currentUser = completeUser;
            }

            // Now load all the data
            idfromdatabase.setText(currentUser.getUserId());
            jTextField1.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
            jTextField2.setText(currentUser.getEmail());
            jTextField6.setText(currentUser.getNationality() != null ? currentUser.getNationality() : "Bangladeshi");
            jTextField5.setText(currentUser.getEmergencyContact() != null ? currentUser.getEmergencyContact() : "");
            jTextField7.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            jTextField4.setText(currentUser.getNidPassport() != null ? currentUser.getNidPassport() : "");

            // Date of Birth
            if (currentUser.getDateOfBirth() != null && !currentUser.getDateOfBirth().isEmpty()) {
                DateHelper.toComponents(currentUser.getDateOfBirth(), day, month, year);
            }

            // Gender
            if (currentUser.getGender() != null && !currentUser.getGender().isEmpty()) {
                gender.setSelectedItem(currentUser.getGender());
            }

            // Budget
            int minBudget = currentUser.getMinBudget();
            int maxBudget = currentUser.getMaxBudget();
            jSlider1.setValue(minBudget);
            jSlider2.setValue(maxBudget);
            jLabel3.setText(formatBudgetValue(minBudget) + " tk");
            jLabel6.setText(formatBudgetValue(maxBudget) + " tk");

            // Travel Preferences
            TravelStyleHelper.applyToCheckboxes(currentUser,
                    beach, mountain, urban, historical, culture, ecotourism,
                    adventure, relaxation, photographic, nature, food);

            // Profile Image
            loadProfileImage();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading profile data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProfileImage() {
        String imagePath = currentUser.getProfileImagePath();

        // Clear current image
        profilephoto.setIcon(null);

        // If no image path, load default
        if (imagePath == null || imagePath.trim().isEmpty()) {
            loadDefaultImage();
            return;
        }

        imagePath = imagePath.trim();

        try {
            // Try to load from file system first
            java.io.File imageFile = new java.io.File(imagePath);

            if (imageFile.exists()) {
                ImageIcon imageIcon = new ImageIcon(imagePath);
                if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    int photoWidth = profilephoto.getWidth();
                    int photoHeight = profilephoto.getHeight();

                    if (photoWidth <= 0) {
                        photoWidth = 150;
                    }
                    if (photoHeight <= 0) {
                        photoHeight = 200;
                    }

                    Image image = imageIcon.getImage().getScaledInstance(
                            photoWidth, photoHeight, Image.SCALE_SMOOTH);
                    profilephoto.setIcon(new ImageIcon(image));
                    profilephoto.setText("");
                    return;
                }
            }

            // Try to load from resources
            java.net.URL url = getClass().getResource(imagePath);
            if (url == null && imagePath.startsWith("/")) {
                url = getClass().getResource(imagePath);
            }
            if (url != null) {
                ImageIcon imageIcon = new ImageIcon(url);
                int photoWidth = profilephoto.getWidth();
                int photoHeight = profilephoto.getHeight();

                if (photoWidth <= 0) {
                    photoWidth = 150;
                }
                if (photoHeight <= 0) {
                    photoHeight = 200;
                }

                Image image = imageIcon.getImage().getScaledInstance(
                        photoWidth, photoHeight, Image.SCALE_SMOOTH);
                profilephoto.setIcon(new ImageIcon(image));
                profilephoto.setText("");
                return;
            }

            loadDefaultImage();

        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultImage();
        }
    }

//    private void loadUserData() {
//        if (currentUser == null) {
//            return;
//        }
//
//        try {
//            // Basic Info
//            idfromdatabase.setText(currentUser.getUserId());
//            jTextField1.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
//            jTextField2.setText(currentUser.getEmail());
//            jTextField6.setText(currentUser.getNationality() != null ? currentUser.getNationality() : "Bangladeshi");
//            jTextField5.setText(currentUser.getEmergencyContact() != null ? currentUser.getEmergencyContact() : "");
//            jTextField7.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
//            jTextField4.setText(currentUser.getNidPassport() != null ? currentUser.getNidPassport() : "");
//
//            // Date of Birth
//            if (currentUser.getDateOfBirth() != null && !currentUser.getDateOfBirth().isEmpty()) {
//                DateHelper.toComponents(currentUser.getDateOfBirth(), day, month, year);
//            }
//
//            // Gender
//            if (currentUser.getGender() != null && !currentUser.getGender().isEmpty()) {
//                gender.setSelectedItem(currentUser.getGender());
//            }
//
//            // Budget
//            int minBudget = currentUser.getMinBudget();
//            int maxBudget = currentUser.getMaxBudget();
//            jSlider1.setValue(minBudget);
//            jSlider2.setValue(maxBudget);
//            jLabel3.setText(formatBudgetValue(minBudget) + " tk");
//            jLabel6.setText(formatBudgetValue(maxBudget) + " tk");
//
//            // Travel Preferences
//            TravelStyleHelper.applyToCheckboxes(currentUser,
//                    beach, mountain, urban, historical, culture, ecotourism,
//                    adventure, relaxation, photographic, nature, food);
//
//            // Profile Image - FIXED VERSION
//            loadProfileImage();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this,
//                    "Error loading profile data: " + e.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//        }
//    }
//    private void loadProfileImage() {
//        String imagePath = currentUser.getProfileImagePath();
//
//        // Clear current image
//        profilephoto.setIcon(null);
//
//        // If no image path, load default
//        if (imagePath == null || imagePath.trim().isEmpty()) {
//            loadDefaultImage();
//            return;
//        }
//
//        imagePath = imagePath.trim();
//        System.out.println("Attempting to load image from: " + imagePath);
//
//        try {
//            File imageFile = new File(imagePath);
//
//            // Check if file exists
//            if (!imageFile.exists()) {
//                System.out.println("Image file does not exist: " + imagePath);
//                loadDefaultImage();
//                return;
//            }
//
//            // Check if it's a valid image file
//            String fileName = imageFile.getName().toLowerCase();
//            if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
//                    || fileName.endsWith(".png") || fileName.endsWith(".gif"))) {
//                System.out.println("Not a valid image file: " + fileName);
//                loadDefaultImage();
//                return;
//            }
//
//            // Load the image
//            ImageIcon imageIcon = new ImageIcon(imagePath);
//
//            // Verify image loaded
//            if (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
//                System.out.println("Image failed to load properly: " + imagePath);
//                loadDefaultImage();
//                return;
//            }
//
//            // Get dimensions
//            int photoWidth = profilephoto.getWidth();
//            int photoHeight = profilephoto.getHeight();
//
//            if (photoWidth <= 0) {
//                photoWidth = 150;
//            }
//            if (photoHeight <= 0) {
//                photoHeight = 200;
//            }
//
//            // Scale and set image
//            Image image = imageIcon.getImage().getScaledInstance(
//                    photoWidth,
//                    photoHeight,
//                    Image.SCALE_SMOOTH);
//
//            profilephoto.setIcon(new ImageIcon(image));
//            profilephoto.setText("");
//
//            System.out.println("Image loaded successfully from: " + imagePath);
//
//        } catch (Exception e) {
//            System.out.println("Error loading image: " + e.getMessage());
//            e.printStackTrace();
//            loadDefaultImage();
//        }
//    }
    private void loadDefaultImage() {
        try {
            // Try to load default image from resources
            java.net.URL imageUrl = getClass().getResource("/images/nophoto.jpg");

            if (imageUrl != null) {
                ImageIcon defaultIcon = new ImageIcon(imageUrl);

                if (defaultIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    int photoWidth = profilephoto.getWidth();
                    int photoHeight = profilephoto.getHeight();

                    if (photoWidth <= 0) {
                        photoWidth = 150;
                    }
                    if (photoHeight <= 0) {
                        photoHeight = 200;
                    }

                    Image image = defaultIcon.getImage().getScaledInstance(
                            photoWidth,
                            photoHeight,
                            Image.SCALE_SMOOTH);

                    profilephoto.setIcon(new ImageIcon(image));
                    profilephoto.setText("");
                    return;
                }
            }

            // If default image fails, show text placeholder
            showTextPlaceholder();

        } catch (Exception e) {
            e.printStackTrace();
            showTextPlaceholder();
        }
    }

    private void showTextPlaceholder() {
        profilephoto.setIcon(null);
        profilephoto.setText("NO PHOTO");
        profilephoto.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        profilephoto.setForeground(new Color(120, 100, 80));
        profilephoto.setHorizontalAlignment(SwingConstants.CENTER);
        profilephoto.setVerticalAlignment(SwingConstants.CENTER);
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // ... rest of your main method remains the same     /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            //ogger.log(java.util.logging.Level.SEVERE, null, ex);
        }//]]
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new UserProfile().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox adventure;
    private javax.swing.JButton backbutton;
    private javax.swing.JLabel bday;
    private javax.swing.JPanel bdaypanel;
    private javax.swing.JCheckBox beach;
    private javax.swing.JLabel contact;
    private javax.swing.JLabel contactinfo;
    private javax.swing.JPanel contactpanel;
    private javax.swing.JCheckBox culture;
    private javax.swing.JComboBox<String> day;
    private javax.swing.JCheckBox ecotourism;
    private javax.swing.JButton editprofilebutton;
    private javax.swing.JLabel email;
    private javax.swing.JPanel emailpanel;
    private javax.swing.JPanel emergencypanel;
    private javax.swing.JCheckBox food;
    private javax.swing.JComboBox<String> gender;
    private javax.swing.JPanel genderpanel;
    private javax.swing.JCheckBox historical;
    private javax.swing.JLabel idfromdatabase;
    private com.ExploBD.Frame.RoundedPanel infobox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JPanel minpane;
    private javax.swing.JComboBox<String> month;
    private javax.swing.JCheckBox mountain;
    private javax.swing.JPanel myprofile;
    private javax.swing.JLabel name;
    private javax.swing.JPanel namepanel;
    private javax.swing.JLabel nationality;
    private javax.swing.JPanel nationalpanel;
    private javax.swing.JCheckBox nature;
    private javax.swing.JLabel nid;
    private javax.swing.JPanel nidPanel;
    private javax.swing.JCheckBox photographic;
    private com.ExploBD.Frame.RoundedPanel preferencebox;
    private javax.swing.JLabel preferencetitle;
    private javax.swing.JLabel profilephoto;
    private javax.swing.JCheckBox relaxation;
    private com.ExploBD.Frame.RoundedPanel showstatisticsbox;
    private javax.swing.JLabel statistics;
    private javax.swing.JCheckBox urban;
    private javax.swing.JLabel userid;
    private javax.swing.JComboBox<String> year;
    // End of variables declaration//GEN-END:variables
}
