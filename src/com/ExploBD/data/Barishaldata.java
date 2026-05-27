package com.ExploBD.data;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class Barishaldata
{
    
    private static PlaceDatabaseObject placeDAO = new PlaceDatabaseObject();
    
    public static void main(String[] args) {
        addTestData();
    }
    
    public static void addTestData() {
        try {
            
           // clearOldData();
            
           
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
            
            // Only delete Barishal division data
            stmt.execute("DELETE FROM historical_places WHERE place_id LIKE 'BAR-%'");
            stmt.execute("DELETE FROM nature_places WHERE place_id LIKE 'BAR-%'");
            stmt.execute("DELETE FROM entertainment_places WHERE place_id LIKE 'BAR-%'");
            stmt.execute("DELETE FROM places WHERE place_id LIKE 'BAR-%'");
            
        }
    }
    
    private static void addHistoricalPlaces() {
        
        // Lakutia Jamidar Bari (Lakutia Zamindar House)
        HistoricalPlace lakutiaBari = new HistoricalPlace("BAR-HIST-001", "Lakutia Jamidar Bari", "Barishal", "Bakerganj");
        lakutiaBari.setAddress("Lakutia Village, Bakerganj Upazila, Barishal");
        lakutiaBari.setDescription("Historic 19th-century zamindar palace with stunning Indo-European architecture. Features large pond, ancient temple, guest house, and sprawling gardens. One of the best-preserved zamindar houses in Barishal.");
        lakutiaBari.setEntryCost(50.0);
        lakutiaBari.setBestTime("November-February (9:00 AM - 5:00 PM)");
        lakutiaBari.setWhyVisit("Experience the grandeur of Bengal's zamindars. The palace's architecture and peaceful pondside setting make it a hidden gem.");
        lakutiaBari.setImagePath("barishal/lalkutia1.jpg");
        lakutiaBari.setTags("historical, zamindar, palace, architecture, photography, culture, heritage, pond");
        lakutiaBari.setYearBuilt("Late 19th Century");
        lakutiaBari.setTimePeriod("British Colonial Period");
        lakutiaBari.setArchitectureStyle("Indo-European Colonial");
        lakutiaBari.setHistoricalFacts("The Lakutia Zamindar family was one of the wealthiest in Barishal. The palace grounds include a temple dedicated to the family deity.");
        lakutiaBari.setUNESCO(false);
        lakutiaBari.setImage1("barishal/lalkutia2.jpg");
        lakutiaBari.setImage2("barishal/lalkutia3.jpg");
        lakutiaBari.setImage3("barishal/lalkutia4.jpg");
        lakutiaBari.setImage4("barishal/lalkutia5.jpg");
        
        placeDAO.insertHistoricalPlace(lakutiaBari);
        
        // Durba Sagar Dighi (Historical Pond)
        HistoricalPlace durbaSagar = new HistoricalPlace("BAR-HIST-002", "Durba Sagar Dighi", "Barishal", "Barishal City");
        durbaSagar.setAddress("Barishal City, near Kasba Road");
        durbaSagar.setDescription("Ancient large man-made pond (dighi) dating back to the 16th century. Surrounded by old trees, temples, and bathing ghats. Believed to be dug by a local ruler named Durbar Singh.");
        durbaSagar.setEntryCost(0.0);
        durbaSagar.setBestTime("November-February (morning or sunset)");
        durbaSagar.setWhyVisit("Relax by this historic pond that has served Barishal for 500+ years. The peaceful atmosphere and surrounding temples offer a spiritual escape.");
        durbaSagar.setImagePath("barishal/durga1.jpg");
        durbaSagar.setTags("historical, pond, heritage, relaxation, photography, cultural, spiritual");
        durbaSagar.setYearBuilt("16th Century");
        durbaSagar.setTimePeriod("Medieval Period");
        durbaSagar.setArchitectureStyle("Traditional Bengali (Ghats and Temples)");
        durbaSagar.setHistoricalFacts("Locals believe the pond was dug overnight by a king's soldiers. The water never dries up even in severe drought. Annual religious ceremonies held here.");
        durbaSagar.setUNESCO(false);
        durbaSagar.setImage1("barishal/durga2.jpg");
        durbaSagar.setImage2("barishal/durga3.jpg");
        durbaSagar.setImage3("barishal/durga4.jpg");
        durbaSagar.setImage4("barishal/durga5.jpg");
        
        placeDAO.insertHistoricalPlace(durbaSagar);
    }
    
    private static void addNaturePlaces() {
        
        // Kuakata Beach
        NaturePlace kuakata = new NaturePlace("BAR-NAT-001", "Kuakata Beach", "Barishal", "Kuakata, Patuakhali");
        kuakata.setAddress("Kuakata, Patuakhali District, Barishal Division");
        kuakata.setDescription("The only beach in South Asia where you can watch both sunrise and sunset from the same spot. Features 30 km of sandy beach, mangrove forests, fishing villages, and the unique 'Fatrar Char' island view.");
        kuakata.setEntryCost(50.0);
        kuakata.setBestTime("October-March (sunrise 5:30 AM, sunset 5:30 PM)");
        kuakata.setWhyVisit("Witness the magical experience of seeing both sunrise and sunset over the Bay of Bengal from the same beach. The 'daughter of the sea' is a paradise for photographers.");
        kuakata.setImagePath("barishal/kuakata1.jpg");
        kuakata.setTags("beach, sunset, sunrise, photography, relaxation, adventure, family-friendly, pictorial");
        kuakata.setNatureType("Sea Beach (Bay of Bengal)");
        kuakata.setAreaSize("30 km long beach");
        kuakata.setActivities("sunrise viewing, sunset viewing, swimming, beach walking, fishing village visits, boat riding, photography");
        kuakata.setConservationStatus("Protected beach zone");
        kuakata.setImage1("barishal/kuakara2.jpg");
        kuakata.setImage2("barishal/kuakata3.jpg");
        kuakata.setImage3("barishal/kuakata4.jpg");
        kuakata.setImage4("barishal/kuakata5.jpg");
        
        placeDAO.insertNaturePlace(kuakata);
        
        // Lal Shapla Bill (Flower Scenario / Red Water Lily Field)
        NaturePlace lalShapla = new NaturePlace("BAR-NAT-002", "Lal Shapla Bill (Red Water Lily Field)", "Barishal", "Wazirpur");
        lalShapla.setAddress("Wazirpur Upazila, Barishal Division");
        lalShapla.setDescription("Vast wetland (bill) covered with thousands of red water lilies (Lal Shapla - the national flower of Bangladesh) during monsoon and winter. Creates a stunning red carpet effect on the water surface.");
        lalShapla.setEntryCost(50.0);
        lalShapla.setBestTime("July-October (monsoon peak flowering), November-January (winter flowering)");
        lalShapla.setWhyVisit("Experience the breathtaking sight of thousands of red water lilies blooming together - the famous 'flower scenario' of Barishal that looks like a red carpet on water.");
        lalShapla.setImagePath("barishal/shapla1.jpg");
        lalShapla.setTags("nature, flower scenario, wetland, photography, pictorial, relaxation, boat ride, national flower");
        lalShapla.setNatureType("Freshwater Wetland (Bill)");
        lalShapla.setAreaSize("Approximately 500+ acres of water lily fields");
        lalShapla.setActivities("boat riding, flower photography, water lily picking, bird watching, sunset viewing");
        lalShapla.setConservationStatus("Protected wetland area");
        lalShapla.setImage1("barishal/shapla2.jpg");
        lalShapla.setImage2("barishal/shapla3.jpg");
        lalShapla.setImage3("barishal/shapla4.jpg");
        lalShapla.setImage4("barishal/shapla5.jpg");
        
        placeDAO.insertNaturePlace(lalShapla);
    }
    
    private static void addEntertainmentPlaces() {
        
        // Floating Guava Market (Swarupkati Floating Market)
        ManmadePlace floatingMarket = new ManmadePlace("BAR-ENT-001", "Floating Guava Market", "Barishal", "Swarupkati");
        floatingMarket.setAddress("Swarupkati Upazila, Barishal Division (on the river near Jhalokati border)");
        floatingMarket.setDescription("Unique floating market where farmers sell fresh guavas and other fruits from wooden boats on the river. Hundreds of boats gather daily creating a vibrant floating bazaar.");
        floatingMarket.setEntryCost(0.0);
        floatingMarket.setBestTime("July-October (guava season peak), 7:00 AM - 11:00 AM");
        floatingMarket.setWhyVisit("Experience the world-famous floating market of Bangladesh - buy fresh guavas directly from farmers while floating on the river!");
        floatingMarket.setImagePath("barishal/market1.jpg");
        floatingMarket.setTags("food, floating market, unique experience, photography, adventure, culture, shopping, guava");
        floatingMarket.setEntertainmentType("Floating Market");
        floatingMarket.setOpeningHours("6:00 AM - 12:00 PM (daily during guava season)");
        floatingMarket.setSpecialEvents("Guava Festival (August), boat racing events");
        floatingMarket.setAverageSpending(300.0);
        floatingMarket.setContactInfo("Swarupkati Upazila Office: +880-4428-123456");
        floatingMarket.setHasEntryFee(false);
        floatingMarket.setImage1("barishal/market2.jpg");
        floatingMarket.setImage2("barishal/market3.jpg");
        floatingMarket.setImage3("barishal/market4.jpg");
        floatingMarket.setImage4("barishal/market5.jpg");
        
        placeDAO.insertEntertainmentPlace(floatingMarket);
    }
}