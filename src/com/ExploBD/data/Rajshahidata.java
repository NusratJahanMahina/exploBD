package com.ExploBD.data;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class Rajshahidata
{
    
    private static PlaceDatabaseObject placeDAO = new PlaceDatabaseObject();
    
    public static void main(String[] args) {
        addTestData();
    }
    
    public static void addTestData() {
        try {
            
            //clearOldData();
            
           
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
            
            // Only delete Rajshahi division data
            stmt.execute("DELETE FROM historical_places WHERE place_id LIKE 'RAJ-%'");
            stmt.execute("DELETE FROM nature_places WHERE place_id LIKE 'RAJ-%'");
            stmt.execute("DELETE FROM entertainment_places WHERE place_id LIKE 'RAJ-%'");
            stmt.execute("DELETE FROM places WHERE place_id LIKE 'RAJ-%'");
            
        }
    }
    
    private static void addHistoricalPlaces() {
        
        // Puthia Temple Complex (Main Temple)
        HistoricalPlace puthiaTemple = new HistoricalPlace("RAJ-HIST-001", "Puthia Temple Complex", "Rajshahi", "Puthia");
        puthiaTemple.setAddress("Puthia Upazila, Rajshahi Division");
        puthiaTemple.setDescription("Largest collection of historic Hindu temples in Bangladesh. Features the ornate Shiva Temple (Govinda Mandir) with stunning terracotta, Pancha Ratna Govinda Temple, and the unique Dol Mancha. Built by the Puthia Zamindar family.");
        puthiaTemple.setEntryCost(50.0);
        puthiaTemple.setBestTime("October-March (8:00 AM - 5:00 PM)");
        puthiaTemple.setWhyVisit("Marvel at the most spectacular terracotta artwork in Bangladesh. The Shiva Temple's intricate stone carvings and the unique 'dancing hall' architecture are unparalleled.");
        puthiaTemple.setImagePath("rajshahi/puthia1.jpg");
        puthiaTemple.setTags("historical, temple, terracotta, architecture, photography, culture, religious, heritage");
        puthiaTemple.setYearBuilt("1823");
        puthiaTemple.setTimePeriod("British Colonial Period");
        puthiaTemple.setArchitectureStyle("Bengal Temple Architecture (Pancha Ratna style)");
        puthiaTemple.setHistoricalFacts("Built by Rani Bhubanmoyee Devi. The complex has 5 major temples including the only 'Dol Mancha' in Bangladesh. The Shiva Temple survived the 1897 earthquake with minimal damage.");
        puthiaTemple.setUNESCO(false);
        puthiaTemple.setImage1("rajshahi/puthia2.jpg");
        puthiaTemple.setImage2("rajshahi/puthia3.jpg");
        puthiaTemple.setImage3("rajshahi/puthia4.jpg");
        puthiaTemple.setImage4("rajshahi/puthia5.jpg");
        
        placeDAO.insertHistoricalPlace(puthiaTemple);
        
        // Puthia Shiva Temple (Govinda Mandir)
//        HistoricalPlace shivaTemple = new HistoricalPlace("RAJ-HIST-002", "Puthia Shiva Temple (Govinda Mandir)", "Rajshahi", "Puthia");
//        shivaTemple.setAddress("Puthia Temple Complex, Puthia, Rajshahi");
//        shivaTemple.setDescription("The most impressive temple in Puthia complex. Features a 4.5m tall lingam, stunning stone carvings of gods and goddesses, and a unique octagonal shape with three interconnected structures.");
//        shivaTemple.setEntryCost(50.0);
//        shivaTemple.setBestTime("October-March (9:00 AM - 4:00 PM)");
//        shivaTemple.setWhyVisit("See the tallest stone lingam in Bangladesh and some of the finest stone carvings in South Asia. A masterpiece of Bengal Hindu architecture.");
//        shivaTemple.setImagePath("rajshahi/shivatemple1.jpg");
//        shivaTemple.setTags("historical, temple, photography, architecture, religious, stone carving, heritage");
//        shivaTemple.setYearBuilt("1823");
//        shivaTemple.setTimePeriod("British Colonial Period");
//        shivaTemple.setArchitectureStyle("Bengal Ratna Style with Octagonal Design");
//        shivaTemple.setHistoricalFacts("The temple's lingam is 4.5m high. The temple survived multiple earthquakes and floods. Local legends say the temple was built overnight by the zamindar.");
//        shivaTemple.setUNESCO(false);
//        shivaTemple.setImage1("rajshahi/shivatemple2.jpg");
//        shivaTemple.setImage2("rajshahi/shivatemple3.jpg");
//        shivaTemple.setImage3("rajshahi/shivatemple4.jpg");
//        shivaTemple.setImage4("rajshahi/shivatemple5.jpg");
//
//        placeDAO.insertHistoricalPlace(shivaTemple);
        
        // Puthia Dol Mancha
//        HistoricalPlace dolMancha = new HistoricalPlace("RAJ-HIST-003", "Puthia Dol Mancha", "Rajshahi", "Puthia");
//        dolMancha.setAddress("Puthia Temple Complex, Puthia, Rajshahi");
//        dolMancha.setDescription("Unique 'swing pavilion' built for the Dol Yatra (Holi) festival. Features 32 ornate pillars, arched entrances, and a raised platform where Radha-Krishna idols were placed during celebrations.");
//        dolMancha.setEntryCost(50.0);
//        dolMancha.setBestTime("October-March (during Dol Purnima festival in March is spectacular)");
//        dolMancha.setWhyVisit("The only structure of its kind in Bangladesh. Imagine the colorful Holi celebrations at this architecturally unique pavilion.");
//        dolMancha.setImagePath("rajshahi/dolmancha1.jpg");
//        dolMancha.setTags("historical, unique architecture, photography, festival venue, cultural heritage, instagrammable");
//        dolMancha.setYearBuilt("19th Century");
//        dolMancha.setTimePeriod("British Colonial Period");
//        dolMancha.setArchitectureStyle("Bengal Pavilion Architecture");
//        dolMancha.setHistoricalFacts("The only surviving Dol Mancha in Bangladesh. During Dol Purnima, the entire complex becomes a massive Holi celebration ground.");
//        dolMancha.setUNESCO(false);
//        dolMancha.setImage1("rajshahi/dolmancha2.jpg");
//        dolMancha.setImage2("rajshahi/dolmancha3.jpg");
//        dolMancha.setImage3("rajshahi/dolmancha4.jpg");
//        dolMancha.setImage4("rajshahi/dolmancha5.jpg");
//        
//        placeDAO.insertHistoricalPlace(dolMancha);
        
        // Small Shiva Temple (Puthia)
//        HistoricalPlace smallShiva = new HistoricalPlace("RAJ-HIST-004", "Small Shiva Temple (Puthia)", "Rajshahi", "Puthia");
//        smallShiva.setAddress("Puthia Temple Complex, Puthia, Rajshahi");
//        smallShiva.setDescription("Intricately carved smaller Shiva temple with detailed terracotta panels depicting scenes from Hindu mythology, including the Ramayana and Mahabharata.");
//        smallShiva.setEntryCost(50.0);
//        smallShiva.setBestTime("October-March (morning light best for photography)");
//        smallShiva.setWhyVisit("Admire the most detailed terracotta work in Bangladesh. Each panel tells a different mythological story.");
//        smallShiva.setImagePath("rajshahi/smallshiva1.jpg");
//        smallShiva.setTags("historical, terracotta art, photography, mythology, religious, detailed carvings");
//        smallShiva.setYearBuilt("Early 19th Century");
//        smallShiva.setTimePeriod("British Colonial Period");
//        smallShiva.setArchitectureStyle("Bengal Terracotta Temple Style");
//        smallShiva.setHistoricalFacts("The terracotta panels here are considered the finest in Bangladesh. Restoration work has preserved these 200-year-old artworks.");
//        smallShiva.setUNESCO(false);
//        smallShiva.setImage1("rajshahi/smallshiva2.jpg");
//        smallShiva.setImage2("rajshahi/smallshiva3.jpg");
//        smallShiva.setImage3("rajshahi/smallshiva4.jpg");
//        smallShiva.setImage4("rajshahi/smallshiva5.jpg");
//        
//        placeDAO.insertHistoricalPlace(smallShiva);
        
//        // Bagha Mosque
//        HistoricalPlace baghaMosque = new HistoricalPlace("RAJ-HIST-005", "Bagha Mosque", "Rajshahi", "Bagha");
//        baghaMosque.setAddress("Bagha Upazila, Rajshahi Division (40km from Rajshahi city)");
//        baghaMosque.setDescription("16th-century Sultanate mosque with exquisite terracotta floral motifs, geometric patterns, and a large tank. Built during the reign of Sultan Nusrat Shah.");
//        baghaMosque.setEntryCost(30.0);
//        baghaMosque.setBestTime("November-February (9:00 AM - 4:00 PM)");
//        baghaMosque.setWhyVisit("Discover a hidden gem of Sultanate architecture. The terracotta flowers and geometric patterns are some of the finest in Bangladesh.");
//        baghaMosque.setImagePath("rajshahi/baghamosque1.jpg");
//        baghaMosque.setTags("historical, mosque, terracotta, photography, Sultanate architecture, hidden gem");
//        baghaMosque.setYearBuilt("1523");
//        baghaMosque.setTimePeriod("Sultanate Period (Hussain Shahi dynasty)");
//        baghaMosque.setArchitectureStyle("Bengal Sultanate");
//        baghaMosque.setHistoricalFacts("Named after the Bagha (tiger) forest that once surrounded it. The mosque has 10 domes and survived 400+ years of floods and earthquakes.");
//        baghaMosque.setUNESCO(false);
//        baghaMosque.setImage1("rajshahi/baghamosque2.jpg");
//        baghaMosque.setImage2("rajshahi/baghamosque3.jpg");
//        baghaMosque.setImage3("rajshahi/baghamosque4.jpg");
//        baghaMosque.setImage4("rajshahi/baghamosque5.jpg");
//        
//        placeDAO.insertHistoricalPlace(baghaMosque);
//        
//        // Varendra Research Museum
//        HistoricalPlace varendraMuseum = new HistoricalPlace("RAJ-HIST-006", "Varendra Research Museum", "Rajshahi", "Rajshahi City");
//        varendraMuseum.setAddress("Rajshahi University Campus, Rajshahi");
//        varendraMuseum.setDescription("Oldest museum in Bangladesh (1910). Houses the largest collection of ancient Bengal artifacts including Pala-Sena sculptures, inscriptions, coins, and manuscripts.");
//        varendraMuseum.setEntryCost(20.0);
//        varendraMuseum.setBestTime("October-March (10:00 AM - 5:00 PM, Friday closed)");
//        varendraMuseum.setWhyVisit("Explore 1,000+ years of Bengal history under one roof. The black stone sculptures of Hindu and Buddhist deities are world-class.");
//        varendraMuseum.setImagePath("rajshahi/varendra1.jpg");
//        varendraMuseum.setTags("historical, museum, archaeology, educational, photography, culture, heritage");
//        varendraMuseum.setYearBuilt("1910");
//        varendraMuseum.setTimePeriod("Multiple (Ancient to Colonial)");
//        varendraMuseum.setArchitectureStyle("Colonial with Indo-Saracenic elements");
//        varendraMuseum.setHistoricalFacts("Established by the Maharaja of Dighapatia. Over 20,000 artifacts including the famous 'Black Buddha' sculpture. Saved many artifacts from being looted.");
//        varendraMuseum.setUNESCO(false);
//        varendraMuseum.setImage1("rajshahi/varendra2.jpg");
//        varendraMuseum.setImage2("rajshahi/varendra3.jpg");
//        varendraMuseum.setImage3("rajshahi/varendra4.jpg");
//        varendraMuseum.setImage4("rajshahi/varendra5.jpg");
//        
//        placeDAO.insertHistoricalPlace(varendraMuseum);
    }
    
    private static void addNaturePlaces() {
        
        // Padma River
        NaturePlace padmaRiver = new NaturePlace("RAJ-NAT-001", "Padma River", "Rajshahi", "Rajshahi/Shibganj");
        padmaRiver.setAddress("Rajshahi city waterfront and Shibganj, Rajshahi Division");
        padmaRiver.setDescription("Mighty Padma River (main channel of the Ganges) flowing through Rajshahi. Features stunning sunsets, sandy beaches, fishing villages, and the iconic 'Padma Bridge' view near Rajshahi.");
        padmaRiver.setEntryCost(0.0);
        padmaRiver.setBestTime("November-February (sunset views, 4:00 PM - 6:00 PM)");
        padmaRiver.setWhyVisit("Witness spectacular sunsets over one of South Asia's greatest rivers. Take a boat ride, eat fresh Hilsa fish, and experience riverine Bangladesh.");
        padmaRiver.setImagePath("rajshahi/Padma1.jpg");
        padmaRiver.setTags("nature, river, beach (river beach), relaxation, pictorial, sunset, food (Hilsa fish), boat ride, photography");
        padmaRiver.setNatureType("Large River (Ganges Distributary)");
        padmaRiver.setAreaSize("Approximately 6km wide at Rajshahi point");
        padmaRiver.setActivities("sunset viewing, boat riding, fishing, photography, riverside walking, swimming (caution), eating fresh Hilsa");
        padmaRiver.setConservationStatus("Major river, erosion a concern");
        padmaRiver.setImage1("rajshahi/padma2.jpg");
        padmaRiver.setImage2("rajshahi/padma3.jpg");
        padmaRiver.setImage3("rajshahi/padma4.jpg");
        padmaRiver.setImage4("rajshahi/Padma5.jpg");
        
        placeDAO.insertNaturePlace(padmaRiver);
        
//        // Barind Tract (Ancient alluvial terrace)
//        NaturePlace barindTract = new NaturePlace("RAJ-NAT-002", "Barind Tract", "Rajshahi", "Chapai Nawabganj/Naogaon");
//        barindTract.setAddress("Barind region, Rajshahi Division");
//        barindTract.setDescription("Unique geological formation - an ancient elevated alluvial terrace from Pleistocene era (50,000+ years old). Features red soil, dry forests, and ancient archaeological sites.");
//        barindTract.setEntryCost(0.0);
//        barindTract.setBestTime("October-March (cooler weather for walking)");
//        barindTract.setWhyVisit("Walk on land that is over 50,000 years old! This unique geological formation is found nowhere else in Bangladesh.");
//        barindTract.setImagePath("rajshahi/barind1.jpg");
//        barindTract.setTags("nature, geological, unique landscape, photography, educational, ancient, offbeat");
//        barindTract.setNatureType("Pleistocene Alluvial Terrace");
//        barindTract.setAreaSize("Approximately 10,000 sq km across Rajshahi division");
//        barindTract.setActivities("geological observation, photography, walking, archaeological exploration");
//        barindTract.setConservationStatus("Protected geological formation");
//        barindTract.setImage1("rajshahi/barind2.jpg");
//        barindTract.setImage2("rajshahi/barind3.jpg");
//        barindTract.setImage3("rajshahi/barind4.jpg");
//        barindTract.setImage4("rajshahi/barind5.jpg");
//        
//        placeDAO.insertNaturePlace(barindTract);
    }
    
    private static void addEntertainmentPlaces() {
        
////        // Rajshahi Silk Village
////        ManmadePlace silkVillage = new ManmadePlace("RAJ-ENT-001", "Rajshahi Silk Village (BSCIC)", "Rajshahi", "Rajshahi City");
////        silkVillage.setAddress("BSCIC Silk Industrial Area, Rajshahi");
////        silkVillage.setDescription("Experience traditional silk production from silkworm to finished saree. Watch live silk weaving, buy authentic Rajshahi silk products, and learn about Bangladesh's silk heritage.");
////        silkVillage.setEntryCost(50.0);
////        silkVillage.setBestTime("November-February (9:00 AM - 4:00 PM)");
////        silkVillage.setWhyVisit("Buy authentic Rajshahi silk directly from weavers. Watch the fascinating process of silk making - a dying traditional art form.");
////        silkVillage.setImagePath("rajshahi/silk1.jpg");
////        silkVillage.setTags("culture, shopping, educational, traditional craft, food (local snacks), family-friendly, photography");
////        silkVillage.setEntertainmentType("Cultural & Shopping Experience");
////        silkVillage.setOpeningHours("9:00 AM - 5:00 PM (Saturday-Thursday)");
////        silkVillage.setSpecialEvents("Silk exhibition in December, weaving demonstrations daily");
////        silkVillage.setAverageSpending(1000.0);
////        silkVillage.setContactInfo("BSCIC Rajshahi: +880-721-123456");
////        silkVillage.setHasEntryFee(false);
////        silkVillage.setImage1("rajshahi/silk2.jpg");
////        silkVillage.setImage2("rajshahi/silk3.jpg");
////        silkVillage.setImage3("rajshahi/silk4.jpg");
////        silkVillage.setImage4("rajshahi/silk5.jpg");
////        
////        placeDAO.insertEntertainmentPlace(silkVillage);
//        
////        // Rajshahi Riverside Promenade
////        ManmadePlace riverPromenade = new ManmadePlace("RAJ-ENT-002", "Rajshahi Riverside Promenade (Padma Ghat)", "Rajshahi", "Rajshahi City");
////        riverPromenade.setAddress("Padma Riverfront, Rajshahi City");
////        riverPromenade.setDescription("Beautifully developed riverfront walkway along the Padma River. Features seating areas, food stalls selling fresh Hilsa, boat rides, and stunning sunset views.");
////        riverPromenade.setEntryCost(0.0);
////        riverPromenade.setBestTime("October-March (sunset time 4:30 PM - 6:30 PM)");
////        riverPromenade.setWhyVisit("Enjoy the best sunset views in Bangladesh. Take a relaxing evening walk, eat grilled Hilsa, and ride on traditional wooden boats.");
////        riverPromenade.setImagePath("rajshahi/riverfront1.jpg");
////        riverPromenade.setTags("relaxation, food (Hilsa), sunset, photography, family-friendly, urban, beach (river beach)");
////        riverPromenade.setEntertainmentType("Riverfront Promenade");
////        riverPromenade.setOpeningHours("Sunrise to sunset (open daily)");
////        riverPromenade.setSpecialEvents("Boat racing during monsoon, Hilsa festival in September-October");
////        riverPromenade.setAverageSpending(300.0);
////        riverPromenade.setContactInfo("Rajshahi City Corporation: +880-721-775555");
////        riverPromenade.setHasEntryFee(false);
////        riverPromenade.setImage1("rajshahi/riverfront2.jpg");
////        riverPromenade.setImage2("rajshahi/riverfront3.jpg");
////        riverPromenade.setImage3("rajshahi/riverfront4.jpg");
////        riverPromenade.setImage4("rajshahi/riverfront5.jpg");
////        
////        placeDAO.insertEntertainmentPlace(riverPromenade);
//        
//        // Rajshahi City Center & Shopping
////        ManmadePlace cityCenter = new ManmadePlace("RAJ-ENT-003", "Rajshahi City Center & Shaheb Bazar", "Rajshahi", "Rajshahi City");
////        cityCenter.setAddress("Shaheb Bazar, Rajshahi City");
////        cityCenter.setDescription("Vibrant urban center with traditional markets, modern shopping malls, street food stalls selling 'Rajshahi's famous mangoes and sweets, and colonial-era buildings.");
////        cityCenter.setEntryCost(0.0);
////        cityCenter.setBestTime("October-March (evenings 5:00 PM - 9:00 PM best)");
////        cityCenter.setWhyVisit("Experience authentic urban Bangladesh. Taste the famous Rajshahi mango (seasonal), shop for silk, and enjoy street food culture.");
////        cityCenter.setImagePath("rajshahi/citycenter1.jpg");
////        cityCenter.setTags("urban, shopping, food (mangoes, sweets), culture, photography, family-friendly");
////        cityCenter.setEntertainmentType("Urban Shopping & Food District");
////        cityCenter.setOpeningHours("10:00 AM - 10:00 PM (daily)");
////        cityCenter.setSpecialEvents("Mango Festival (June-July), Eid shopping fairs");
////        cityCenter.setAverageSpending(800.0);
////        cityCenter.setContactInfo("Rajshahi City Corporation: +880-721-775555");
////        cityCenter.setHasEntryFee(false);
////        cityCenter.setImage1("rajshahi/citycenter2.jpg");
////        cityCenter.setImage2("rajshahi/citycenter3.jpg");
////        cityCenter.setImage3("rajshahi/citycenter4.jpg");
////        cityCenter.setImage4("rajshahi/citycenter5.jpg");
////        
////        placeDAO.insertEntertainmentPlace(cityCenter);
    }
}