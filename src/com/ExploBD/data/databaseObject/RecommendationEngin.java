/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ExploBD.data.databaseObject;


import com.ExploBD.object.*;
import java.util.ArrayList;

        
/**
 *
 * @author iaziz
 */
public class RecommendationEngin {
    

    public static ArrayList<Place> getRecommendedPlaces(User user) {

        ArrayList<Place> recommended = new ArrayList<>();
        PlaceDatabaseObject.getAllPlaces();
        
        
        ArrayList<Place> allPlaces = PlaceDatabaseObject.getAllPlaces();

        for (Place place : allPlaces) {

            // Budget match
            if (place.getBudget() <= user.getBudget()) {

                // Preference match
                if (user.getPreference().equalsIgnoreCase("Nature")
                        && place instanceof NaturePlace) {

                    recommended.add(place);

                } else if (user.getPreference().equalsIgnoreCase("Historical")
                        && place instanceof HistoricalPlace) {

                    recommended.add(place);

                } else if (user.getPreference().equalsIgnoreCase("Manmade")
                        && place instanceof ManmadePlace) {

                    recommended.add(place);
                }
            }
        }

        return recommended;
    }
}

