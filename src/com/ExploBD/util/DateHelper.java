package com.ExploBD.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JComboBox;

public class DateHelper {
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    
    public static String fromComponents(JComboBox<String> day, JComboBox<String> month, 
                                        JComboBox<String> year) {
        String dayStr = (String) day.getSelectedItem();
        String monthStr = (String) month.getSelectedItem();
        String yearStr = (String) year.getSelectedItem();
        
        if (dayStr == null || monthStr == null || yearStr == null) {
            return "";
        }
        
        int monthNum = 1;
        for (int i = 0; i < MONTHS.length; i++) {
            if (monthStr.equals(MONTHS[i])) {
                monthNum = i + 1;
                break;
            }
        }
        
        String formattedDay = String.format("%02d", Integer.parseInt(dayStr));
        String formattedMonth = String.format("%02d", monthNum);
        
        return formattedDay + "-" + formattedMonth + "-" + yearStr;
    }
    
    public static void toComponents(String date, JComboBox<String> day, 
                                    JComboBox<String> month, JComboBox<String> year) {
        if (date == null || date.isEmpty()) return;
        
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                day.setSelectedItem(String.valueOf(Integer.parseInt(parts[0])));
                
                int monthNum = Integer.parseInt(parts[1]);
                if (monthNum >= 1 && monthNum <= 12) {
                    month.setSelectedItem(MONTHS[monthNum - 1]);
                }
                
                year.setSelectedItem(parts[2]);
            }
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
    }
    
    public static LocalDate parseToLocalDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String formatForDisplay(LocalDate date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return date.format(formatter);
    }
}