/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.ExploBD.presentation.frames;

import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableRowSorter;
import java.util.HashSet;
import java.util.Set;
import javax.swing.RowFilter;


/**
 *
 * @author iaziz
 */
public class RecommendationFrame extends javax.swing.JFrame {
    private DefaultTableModel model;
    
   
    public RecommendationFrame() {
     
        initComponents();

        // JTable model assign 
        model = (DefaultTableModel) jTable1.getModel();
       
    btnLoad.addActionListener(e -> loadRecommendations());
    cmbFilter.addActionListener(e -> applyFilter());
    
    
    }

     private void loadRecommendations() {
       
  
    model.setRowCount(0); 

    Recommendation rec = new Recommendation(1);

    Map<String, List<Place>> budgetRec = rec.budgetWiseRecommendation();
    Map<String, List<Place>> prefRec = rec.preferenceWiseRecommendation();
    Map<String, List<Place>> placeRec = rec.placeWiseRecommendation();

    List<User> users = rec.getGroupMembers();
    String filter = cmbFilter.getSelectedItem().toString().trim();

    for (User u : users) {
        String userKey = u.getId(); 

        List<Place> allPlaces = new ArrayList<>();
        if (budgetRec.get(userKey) != null) allPlaces.addAll(budgetRec.get(userKey));
        if (prefRec.get(userKey) != null) allPlaces.addAll(prefRec.get(userKey));
        if (placeRec.get(userKey) != null) allPlaces.addAll(placeRec.get(userKey));

     
            Set<String> seen = new HashSet<>();
            List<Place> uniquePlaces = new ArrayList<>();
            for (Place p : allPlaces) {
                String key=p.getName()+"-"+p.getCategory()+"-"+p.getEntryCost();
                
                if (!seen.contains(key)) {
                    seen.add(p.getName());
                    uniquePlaces.add(p);
                }
            }

            // Add rows according to filter
            for (Place p : uniquePlaces) {
                boolean addRow = false;
                switch (filter) {
                    case "All":
                        addRow = true;
                        break;
                    case "Budget":
                        addRow = budgetRec.get(userKey).contains(p);//chnged
                        break;
                    case "Preference":
                        addRow = prefRec.get(userKey).contains(p);
                        break;
                    case "Place":
                        addRow = placeRec.get(userKey).contains(p);
                        break;
                }

                if (addRow) { //changed
                    // Match type
                    String matchType = "";
                    if (budgetRec.get(userKey).contains(p)) matchType += "✓ Budget";
                    if (prefRec.get(userKey).contains(p))
                        matchType += (matchType.isEmpty() ? "" : " + ") + "Preference";
                    if (placeRec.get(userKey).contains(p))
                        matchType += (matchType.isEmpty() ? "" : " + ") + "Location";

                    if (matchType.isEmpty()) matchType = "General";
                    
                    model.addRow(new Object[]{
                            u.getName(),
                         p.getName(),
                            p.getCategory(),
                            p.getEntryCost(),
                            matchType
                           
                    });
                }
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No Recommendations Found");
        }
    }

     
    private void applyFilter() {
      
    
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);
        String filterType = cmbFilter.getSelectedItem().toString().trim();
        
        switch (filterType) {
            case "Budget":
                sorter.setRowFilter(RowFilter.regexFilter("Budget", 4));
                break;
            case "Preference":
                sorter.setRowFilter(RowFilter.regexFilter("Preference", 4));
                break;
            case "Place":
                sorter.setRowFilter(RowFilter.regexFilter("Location", 4));
                break;
            default:
                sorter.setRowFilter(null);
        }
    }
     
    
    
    @SuppressWarnings("unchecked")
     
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnLoad = new javax.swing.JButton();
        cmbFilter = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Recommendation System");

        btnLoad.setText("Load Recommendations");
        btnLoad.addActionListener(this::btnLoadActionPerformed);

        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All ", "Budget ", "Preference  ", "Place" }));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "User", "Place", "Category", "Cost"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(173, 173, 173)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(67, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoad)
                    .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 
    
    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        // TODO add your handling code here:
        
      
    loadRecommendations();  

    // Optional
    if(model.getRowCount() == 0){
        JOptionPane.showMessageDialog(this, "No Recommendations Found");
    }

    }//GEN-LAST:event_btnLoadActionPerformed

   
     
       
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        
        //</editor-fold>

        /* Create and display the form */
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new RecommendationFrame().setVisible(true));
    }

        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoad;
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}