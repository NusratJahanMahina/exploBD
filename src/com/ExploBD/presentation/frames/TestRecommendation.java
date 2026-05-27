///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.ExploBD.presentation.frames;
//
//import com.ExploBD.object.Place;
//import com.ExploBD.object.User;
//import java.util.*;
//
//public class TestRecommendation {
//
//    public static void main(String[] args) {
//
//   
//        List<User> users = new ArrayList<>();
//        users.add(new User("U-001", "Rahim", "rahim@example.com", 0));
//        users.add(new User("U-002", "Karim", "karim@example.com", 0));
//
//       
//        List<Place> places = new ArrayList<>();
//
//        Place p1 = new Place();
//        p1.setName("Lalbagh Fort");
//        p1.setCategory("Historical");
//        p1.setEntryCost(500);
//        p1.setDivision("Dhaka");
//        places.add(p1);
//
//        Place p2 = new Place();
//        p2.setName("Ramna Park");
//        p2.setCategory("Nature");
//        p2.setEntryCost(300);
//        p2.setDivision("Dhaka");
//        places.add(p2);
//
//        Place p3 = new Place();
//        p3.setName("Cox's Bazar Beach");
//        p3.setCategory("Beach");
//        p3.setEntryCost(1000);
//        p3.setDivision("Chittagong");
//        places.add(p3);
//
//     
//        for (User user : users) {
//            System.out.println("User: " + user.getUsername());
//
//            // Budget Recommendation (Entry cost < 600)
//            System.out.println("Budget Recommendations:");
//            for (Place place : places) {
//                if (place.getEntryCost() <= 600) {
//                    System.out.println(" - " + place.getName() + " (" + place.getEntryCost() + " tk)");
//                }
//            }
//
//            // Preference Recommendation (Historical or Nature)
//            System.out.println("Preference Recommendations:");
//            for (Place place : places) {
//                if (place.getCategory().equalsIgnoreCase("Historical") ||
//                    place.getCategory().equalsIgnoreCase("Nature")) {
//                    System.out.println(" - " + place.getName() + " [" + place.getCategory() + "]");
//                }
//            }
//
//            // Place Recommendation (based on division "Dhaka")
//            System.out.println("Place Recommendations:");
//            for (Place place : places) {
//                if (place.getDivision().equalsIgnoreCase("Dhaka")) {
//                    System.out.println(" - " + place.getName() + " (" + place.getDivision() + ")");
//                }
//            }
//
//            System.out.println("====================================");
//        }
//    }
//}
// 
//   



package com.ExploBD.presentation.frames;

import com.ExploBD.object.*;
import java.util.*;

public class TestRecommendation {

    public static void main(String[] args) {
   
        List<User> users = new ArrayList<>();
        users.add(new User("U-001", "Rahim", "rahim@example.com", 0));
        users.add(new User("U-002", "Karim", "karim@example.com", 0));
       
        List<Place> places = new ArrayList<>();

        // Use concrete subclasses instead
        HistoricalPlace p1 = new HistoricalPlace("P001", "Lalbagh Fort", "Dhaka", "Dhaka");
        p1.setName("Lalbagh Fort");
        p1.setCategory("Historical");
        p1.setEntryCost(500);
        p1.setDivision("Dhaka");
        p1.setArchitectureStyle("Mughal");
        places.add(p1);

        NaturePlace p2 = new NaturePlace("P002", "Ramna Park", "Dhaka", "Dhaka");
        p2.setName("Ramna Park");
        p2.setCategory("Nature");
        p2.setEntryCost(300);
        p2.setDivision("Dhaka");
        p2.setNatureType("Urban Park");
        places.add(p2);

        NaturePlace p3 = new NaturePlace("P003", "Cox's Bazar Beach", "Chittagong", "Cox's Bazar");
        p3.setName("Cox's Bazar Beach");
        p3.setCategory("Beach");
        p3.setEntryCost(1000);
        p3.setDivision("Chittagong");
        p3.setNatureType("Beach");
        places.add(p3);

        // Rest of the code remains the same...
        for (User user : users) {
            System.out.println("User: " + user.getUsername());

            System.out.println("Budget Recommendations:");
            for (Place place : places) {
                if (place.getEntryCost() <= 600) {
                    System.out.println(" - " + place.getName() + " (" + place.getEntryCost() + " tk)");
                }
            }

            System.out.println("Preference Recommendations:");
            for (Place place : places) {
                if (place.getCategory().equalsIgnoreCase("Historical") ||
                    place.getCategory().equalsIgnoreCase("Nature")) {
                    System.out.println(" - " + place.getName() + " [" + place.getCategory() + "]");
                }
            }

            System.out.println("Place Recommendations:");
            for (Place place : places) {
                if (place.getDivision().equalsIgnoreCase("Dhaka")) {
                    System.out.println(" - " + place.getName() + " (" + place.getDivision() + ")");
                }
            }

            System.out.println("====================================");
        }
    }
}