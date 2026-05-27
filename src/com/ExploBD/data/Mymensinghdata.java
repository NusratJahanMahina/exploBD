package com.ExploBD.data;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class Mymensinghdata
{
    
    private static PlaceDatabaseObject placeDAO = new PlaceDatabaseObject();
    
    public static void main(String[] args) {
        addTestData();
    }
    
    public static void addTestData() {
        try {
            
          //  clearOldData();
            
           
            addHistoricalPlaces();
            
            
            addNaturePlaces();
            
           
            addEntertainmentPlaces();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void clearOldData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Only delete Mymensingh division data
            stmt.execute("DELETE FROM historical_places WHERE place_id LIKE 'MYM-%'");
            stmt.execute("DELETE FROM nature_places WHERE place_id LIKE 'MYM-%'");
            stmt.execute("DELETE FROM entertainment_places WHERE place_id LIKE 'MYM-%'");
            stmt.execute("DELETE FROM places WHERE place_id LIKE 'MYM-%'");
            
        }
    }
    
    private static void addHistoricalPlaces() {
        
        // Sashi Jamidar House (Sashi Lodge)
        HistoricalPlace sashiLodge = new HistoricalPlace("MYM-HIST-001", "Sashi Jamidar House", "Mymensingh", "Mymensingh City");
        sashiLodge.setAddress("Near Brahmaputra River, Mymensingh City");
        sashiLodge.setDescription("Beautiful 19th-century palace built by Maharaja Sashi Kanta Sanyal, the last zamindar of Mymensingh. Features stunning Indo-European architecture, large gardens, river views, and a private zoo.");
        sashiLodge.setEntryCost(50.0);
        sashiLodge.setBestTime("November-February (9:00 AM - 5:00 PM)");
        sashiLodge.setWhyVisit("Step into the opulent life of Bengal's zamindars. This riverside palace offers a glimpse of royal Bangladesh.");
        sashiLodge.setImagePath("mymensingh/sashi1.jpg");
        sashiLodge.setTags("historical, palace, zamindar, architecture, photography, culture, heritage");
        sashiLodge.setYearBuilt("1895");
        sashiLodge.setTimePeriod("British Colonial Period");
        sashiLodge.setArchitectureStyle("Indo-European Renaissance");
        sashiLodge.setHistoricalFacts("Maharaja Sashi Kanta Sanyal was the last zamindar of Mymensingh. The palace had its own zoo with exotic animals.");
        sashiLodge.setUNESCO(false);
        sashiLodge.setImage1("mymensingh/sashi2.jpg");
        sashiLodge.setImage2("mymensingh/sashi3.jpg");
        sashiLodge.setImage3("mymensingh/sashi4.jpg");
        sashiLodge.setImage4("mymensingh/sashi5.jpg");
        
        placeDAO.insertHistoricalPlace(sashiLodge);
    }
    
    private static void addNaturePlaces() {
        
        // Garo Hills
        NaturePlace garoHills = new NaturePlace("MYM-NAT-001", "Garo Hills", "Mymensingh", "Haluaghat/Dhobaura");
        garoHills.setAddress("Haluaghat and Dhobaura Upazilas, Northern Mymensingh");
        garoHills.setDescription("Beautiful low mountain range bordering India. Features rolling hills, dense forests, waterfalls, tribal Garo villages, orange orchards, and stunning viewpoints.");
        garoHills.setEntryCost(0.0);
        garoHills.setBestTime("November-February (cool weather, clear views)");
        garoHills.setWhyVisit("Experience the only mountain range in central Bangladesh. Meet Garo tribal people, taste fresh oranges, and enjoy panoramic views.");
        garoHills.setImagePath("mymensingh/garo1.jpg");
        garoHills.setTags("nature, mountain, adventure, photography, tribal culture, relaxation, pictorial, hiking");
        garoHills.setNatureType("Low Mountain Range");
        garoHills.setAreaSize("Approximately 50 km along Mymensingh border");
        garoHills.setActivities("hiking, trekking, tribal village visits, orange picking, photography, bird watching");
        garoHills.setConservationStatus("Protected forest zone with tribal settlements");
        garoHills.setImage1("mymensingh/garo2.jpg");
        garoHills.setImage2("mymensingh/garo3.jpg");
        garoHills.setImage3("mymensingh/garo4.jpg");
        garoHills.setImage4("mymensingh/garo5.jpg");
        
        placeDAO.insertNaturePlace(garoHills);
    }
    
    private static void addEntertainmentPlaces() {
        
        // Janul Abedin Museum (Zainul Abedin Museum)
        ManmadePlace zainulMuseum = new ManmadePlace("MYM-ENT-001", "Janul Abedin Museum", "Mymensingh", "Mymensingh City");
        zainulMuseum.setAddress("Near Mymensingh Girls' Cadet College, Mymensingh City");
        zainulMuseum.setDescription("Dedicated to the legendary artist Shilpacharya Zainul Abedin. Houses his original artworks including the famous 'Famine Sketches' (1943), watercolors, and historical photographs.");
        zainulMuseum.setEntryCost(50.0);
        zainulMuseum.setBestTime("October-March (10:00 AM - 5:00 PM, Friday closed)");
        zainulMuseum.setWhyVisit("See the original 'Famine Sketches' that changed Bengali art forever. Experience the masterpieces of Bangladesh's greatest artist.");
        zainulMuseum.setImagePath("mymensingh/jainul1.jpg");
        zainulMuseum.setTags("culture, art museum, educational, historical, photography, pictorial, heritage");
        zainulMuseum.setEntertainmentType("Art Museum");
        zainulMuseum.setOpeningHours("10:00 AM - 5:00 PM (Saturday-Thursday), Closed Friday");
        zainulMuseum.setSpecialEvents("Art exhibitions, art workshops for children");
        zainulMuseum.setAverageSpending(200.0);
        zainulMuseum.setContactInfo("Mymensingh District Administration: +880-91-123456");
        zainulMuseum.setHasEntryFee(true);
        zainulMuseum.setImage1("mymensingh/jainul2.jpg");
        zainulMuseum.setImage2("mymensingh/jainul3.jpg");
        zainulMuseum.setImage3("mymensingh/jainul4.jpg");
        zainulMuseum.setImage4("mymensingh/jainul5.jpg");
        
        placeDAO.insertEntertainmentPlace(zainulMuseum);
    }
}