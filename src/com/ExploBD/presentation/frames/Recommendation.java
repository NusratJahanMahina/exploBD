/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ExploBD.presentation.frames;
import com.ExploBD.data.*;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.NaturePlace;
import com.ExploBD.object.User;
import com.ExploBD.object.Place;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author iaziz
 */
public class Recommendation {
  


    private int groupId;
    private List<User> groupMembers;
    private List<Place> allPlaces;

    public Recommendation(int groupId) {
        this.groupId = groupId;
        this.groupMembers = new ArrayList<>();
        this.allPlaces = new ArrayList<>();
        fetchGroupMembers();
        fetchAllPlaces();
    }

    private void fetchGroupMembers() {
      
        
   
    User u1 = new User("U001","Rahim", "Nature", 5000);
    User u2 = new User("U002","Karim", "Historical", 2000);
    
    u1.setDivision("Dhaka");
    u2.setDivision("Dhaka");
    
    groupMembers.add(u1);
    groupMembers.add(u2);
}
    
   
    
    //mahi
    
    private void fetchAllPlaces() {
    allPlaces = new ArrayList<>();
    
    HistoricalPlace p1 = new HistoricalPlace("P001", "Lalbagh Fort", "Dhaka", "Dhaka");
    p1.setEntryCost(1000);
    p1.setArchitectureStyle("Mughal");
    p1.setCategory("Historical");
    
    HistoricalPlace p2 = new HistoricalPlace("P002", "Ahsan Manzil", "Dhaka", "Dhaka");
    p2.setEntryCost(1200);
    p2.setTimePeriod("British Era");
    p2.setCategory("Palace");
    
    HistoricalPlace p3 = new HistoricalPlace("P003", "Shat Gambuj Mosque", "Khulna", "Bagerhat");
    p3.setEntryCost(800);
    p3.setArchitectureStyle("Islamic");
    p3.setCategory("Religious");
    
    NaturePlace p4 = new NaturePlace("P004", "Cox's Bazar", "Chittagong", "Cox's Bazar");
    p4.setEntryCost(4000);
    p4.setNatureType("Beach");
    p4.setCategory("Nature");
    
    NaturePlace p5 = new NaturePlace("P005", "Sundarbans", "Khulna", "Khulna");
    p5.setEntryCost(3000);
    p5.setNatureType("Mangrove Forest");
    p5.setCategory("Nature");
    
    allPlaces.add(p1);
    allPlaces.add(p2);
    allPlaces.add(p3);
    allPlaces.add(p4);
    allPlaces.add(p5);
}
    
  //till this  
    
    
    
    
//    private void fetchAllPlaces() {
//         
//    allPlaces = new ArrayList<>();
//     
//    
//    Place p1 = new Place("P001", "Lalbagh Fort", "Dhaka", "Lalbagh Fort");
//        p1.setEntryCost(1000);
//        p1.setCategory("Historical");
//        
//    Place p2 = new Place("P002", "Ahsan Manzil", "Dhaka", "Ahsan Manzil");
//        p2.setEntryCost(1200);
//        p2.setCategory("Palace"); 
//        
//    
//        Place p3 = new Place("P003", "Shat Gambuj Mosque", "Dhaka", "Shat Gambuj Mosque");
//        p3.setEntryCost(800);
//        p3.setCategory("Religious");
//        
//        
//    Place p4 = new Place("P004", "Cox's Bazar", "Chittagong", "Cox's Bazar");
//        p4.setEntryCost(4000);         
//        p4.setCategory("Nature");    
//
//        Place p5 = new Place("P005", "Sundarbans", "Khulna", "Sundarbans");
//        p5.setEntryCost(3000);
//        p5.setCategory("Nature");
//
//      
//
//        allPlaces.add(p1);
//        allPlaces.add(p2);
//        allPlaces.add(p3);
//        allPlaces.add(p4);
//        allPlaces.add(p5);
//}
   

    // Budget-wise
    public Map<String, List<Place>> budgetWiseRecommendation() {

        Map<String, List<Place>> budgetMap = new HashMap<>();

        for (User u : groupMembers) {

            List<Place> recommended = new ArrayList<>();

            for (Place p : allPlaces) {
                if (p.getEntryCost() <= u.getBudget()) {
                     recommended.add(p);
                }
            }

          budgetMap.put(u.getId(),recommended); 
        }

        return budgetMap;
    }

    // Preference-wise 
    public Map<String, List<Place>> preferenceWiseRecommendation() {

        Map<String, List<Place>> preferenceMap = new HashMap<>();

        for (User u : groupMembers) {

            List<Place> recommended = new ArrayList<>();
            String pref = u.getPreference();

            for (Place p : allPlaces) {
                if (p.getCategory().equalsIgnoreCase(pref)) {
                    recommended.add(p);
                }
            }

            preferenceMap.put(u.getId(), recommended); 
        }

        return preferenceMap;
    }

    //  Place-wise Recommendation
    public Map<String, List<Place>> placeWiseRecommendation() {

        Map<String, List<Place>> placeMap = new HashMap<>();

        for (User u : groupMembers) {

            List<Place> recommended = new ArrayList<>();

            for (Place p : allPlaces) {
                if (p.getDivision().equalsIgnoreCase(u.getDivision())) {
                    recommended.add(p);
                }
            }

            placeMap.put(u.getId(), recommended); 
        }

        return placeMap;
    }

    // Print 
    public void printRecommendation() {

        Map<String, List<Place>> budgetRec = budgetWiseRecommendation();
        Map<String, List<Place>> prefRec   = preferenceWiseRecommendation();
        Map<String, List<Place>> placeRec  = placeWiseRecommendation();

        for (User u : groupMembers) {

            System.out.println("User: " + u.getName());

            System.out.println("Budget Recommendations:");
            List<Place> bList = budgetRec.get(u.getId());
            if (bList != null && !bList.isEmpty()) {
                for (Place p : bList) {
                    System.out.println("- " + p.getName());
                }
            }else {
                System.out.println("No Budget Match");
            }
            
            System.out.println("Preference Recommendations:");
            List<Place> pList = prefRec.get(u.getId());
            if (pList != null &&!pList.isEmpty()) {
                for (Place p : pList) {
                    System.out.println("- " + p.getName());
                }
            }else {
                System.out.println("No Preference Match");
            }

            System.out.println("Place Recommendations:");
            List<Place> plList = placeRec.get(u.getId());
            if (plList != null && !plList.isEmpty()) {
                for (Place p : plList) {
                    System.out.println("- " + p.getName());
                }
            }
            else {
                System.out.println("No Place Match");
            }


            System.out.println("====================================");
        }
    }

    public List<User> getGroupMembers() {
        return groupMembers;
    }
}