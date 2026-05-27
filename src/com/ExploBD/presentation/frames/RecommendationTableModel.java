/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ExploBD.presentation.frames;

import com.ExploBD.object.Place;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author iaziz
 */


public class RecommendationTableModel extends AbstractTableModel {

    private List<Place> recommendations;

    // Table Column Names
   ;

    private final String[] columnNames = {
        "Place Name", "Category", "Cost (TK)", "Division", "Match Type"
    };

   
    public RecommendationTableModel(List<Place> recommendations) {
        if (recommendations == null) {
            this.recommendations = new ArrayList<>();
        } else {
            this.recommendations = recommendations;
        }
    }

    // Row count
    @Override
    public int getRowCount() {
        return recommendations.size();
    }

    // Column count
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    // Column names
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Data show
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (rowIndex < 0 || rowIndex >= recommendations.size()) {
            return null;
        }

        Place place = recommendations.get(rowIndex);

        switch (columnIndex) {

            case 0:
                return place.getName();

            case 1:
                return place.getCategory();

            case 2:
                return place.getEntryCost();

            case 3:
                return place.getDivision();

            case 4:
                return place.getMatchType(); 

            default:
                return "";
        }
    }

 
    public void setRecommendations(List<Place> recommendations) {
        this.recommendations = recommendations;
        fireTableDataChanged();
    }
}
       