package com.ExploBD.data;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class Rangpurdata
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
            
            // Only delete Rangpur division data
            stmt.execute("DELETE FROM historical_places WHERE place_id LIKE 'RAN-%'");
            stmt.execute("DELETE FROM nature_places WHERE place_id LIKE 'RAN-%'");
            stmt.execute("DELETE FROM entertainment_places WHERE place_id LIKE 'RAN-%'");
            stmt.execute("DELETE FROM places WHERE place_id LIKE 'RAN-%'");
            
        }
    }
    
    private static void addHistoricalPlaces() {
        
        // Tajhat Palace (Tajhat Rajbari)
        HistoricalPlace tajhatPalace = new HistoricalPlace("RAN-HIST-001", "Tajhat Palace", "Rangpur", "Rangpur City");
        tajhatPalace.setAddress("Tajhat, Rangpur City, Rangpur Division");
        tajhatPalace.setDescription("Stunning 19th-century palace built by a wealthy zamindar in Indo-European style. Features grand white dome, Corinthian columns, beautiful gardens, and a museum displaying artifacts from the region's history.");
        tajhatPalace.setEntryCost(50.0);
        tajhatPalace.setBestTime("November-February (9:00 AM - 5:00 PM, Thursday closed)");
        tajhatPalace.setWhyVisit("Marvel at one of Bangladesh's most beautiful palaces. The white dome and grand staircase offer perfect photo opportunities.");
        tajhatPalace.setImagePath("rangpur/tajhat1.jpg");
        tajhatPalace.setTags("historical, palace, zamindar, architecture, photography, culture, heritage, museum");
        tajhatPalace.setYearBuilt("Late 19th Century (c. 1890)");
        tajhatPalace.setTimePeriod("British Colonial Period");
        tajhatPalace.setArchitectureStyle("Indo-European with Corinthian elements");
        tajhatPalace.setHistoricalFacts("Built by Maharaja Kumar Gopal Lal Ray, a wealthy jeweler turned zamindar. The palace now houses the Rangpur Museum with artifacts from 18th-19th centuries.");
        tajhatPalace.setUNESCO(false);
        tajhatPalace.setImage1("rangpur/tajhat2.jpg");
        tajhatPalace.setImage2("rangpur/tajhat4.jpg");
        tajhatPalace.setImage3("rangpur/tajhat5.jpg");
     //   tajhatPalace.setImage4("rangpur/tajhat5.jpg");
        
        placeDAO.insertHistoricalPlace(tajhatPalace);
        
        // Carmichael College
        HistoricalPlace carmichaelCollege = new HistoricalPlace("RAN-HIST-002", "Carmichael College", "Rangpur", "Rangpur City");
        carmichaelCollege.setAddress("Carmichael College Road, Rangpur City");
        carmichaelCollege.setDescription("Historic 19th-century college established in 1916 by Lord Carmichael, the Governor of Bengal. Features stunning colonial architecture including a grand central building with red bricks, tall pillars, and sprawling green campus.");
        carmichaelCollege.setEntryCost(0.0);
        carmichaelCollege.setBestTime("November-February (10:00 AM - 4:00 PM, college hours)");
        carmichaelCollege.setWhyVisit("Walk through one of Bangladesh's oldest and most beautiful college campuses. The red-brick colonial buildings and manicured lawns feel like stepping back in time.");
        carmichaelCollege.setImagePath("rangpur/college1.jpg");
        carmichaelCollege.setTags("historical, colonial architecture, educational, photography, heritage, campus");
        carmichaelCollege.setYearBuilt("1916");
        carmichaelCollege.setTimePeriod("British Colonial Period");
        carmichaelCollege.setArchitectureStyle("Colonial Red Brick (Indo-Saracenic influence)");
        carmichaelCollege.setHistoricalFacts("Named after Lord Carmichael, Governor of Bengal (1912-1917). Produced many prominent political and cultural figures of Bangladesh and India.");
        carmichaelCollege.setUNESCO(false);
        carmichaelCollege.setImage1("rangpur/college2.jpg");
        carmichaelCollege.setImage2("rangpur/college3.jpg");
        carmichaelCollege.setImage3("rangpur/college4.jpg");
        carmichaelCollege.setImage4("rangpur/college5.jpg");
        
        placeDAO.insertHistoricalPlace(carmichaelCollege);
    }
    
    private static void addNaturePlaces() {
        // No nature places requested for Rangpur
    }
    
    private static void addEntertainmentPlaces() {
        // No entertainment places requested for Rangpur
    }
}