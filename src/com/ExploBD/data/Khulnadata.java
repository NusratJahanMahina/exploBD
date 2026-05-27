package com.ExploBD.data;

import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class Khulnadata
{
    
    private static PlaceDatabaseObject placeDAO = new PlaceDatabaseObject();
    
    public static void main(String[] args) {
        addTestData();
    }
    
    public static void addTestData() {
        try {
            
         //   clearOldData();
            
           
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
            
           
           
            stmt.execute("DELETE FROM historical_places");
            stmt.execute("DELETE FROM nature_places");
            stmt.execute("DELETE FROM entertainment_places");
            stmt.execute("DELETE FROM places");
            
        }
    }
    
    private static void addHistoricalPlaces() {
        
       
        HistoricalPlace shatGambuj = new HistoricalPlace("KHU-HIST-001", "Shat Gambuj Masjid", "Khulna", "Bagerhat");
        shatGambuj.setAddress("Bagerhat Sadar, Bagerhat, Khulna Division");
        shatGambuj.setDescription("15th-century UNESCO World Heritage mosque with 60 pillars and 77 domes, built by Khan Jahan Ali. Features thick stone walls, geometric terracotta decorations, and ancient water tanks.");
        shatGambuj.setEntryCost(100.0);
        shatGambuj.setBestTime("November-February (8:00 AM - 5:00 PM)");
        shatGambuj.setWhyVisit("Witness one of the world's most impressive medieval mosques and a masterpiece of Muslim architecture in Bangladesh. Perfect for history and photography enthusiasts.");
        shatGambuj.setImagePath("khulna/mosque1.jpg");
        shatGambuj.setTags("historical, UNESCO, photography, architectural marvel, religious heritage, medieval, terracotta");
        shatGambuj.setYearBuilt("1459");
        shatGambuj.setTimePeriod("Medieval Sultanate Period");
        shatGambuj.setArchitectureStyle("Bengal Sultanate (Tughlaq influence)");
        shatGambuj.setHistoricalFacts("Built by saint-general Khan Jahan Ali. The mosque has 77 domes, not 60. The name 'Shat' (sixty) refers to the 60 pillars. Survived centuries of weather without major damage.");
        shatGambuj.setUNESCO(true);
        shatGambuj.setImage1("khulna/mosque2.jpg");
        shatGambuj.setImage2("khulna/mosque3.jpg");
        shatGambuj.setImage3("khulna/mosque4.jpg");
        shatGambuj.setImage4("khulna/mosque5.jpg");
        
        placeDAO.insertHistoricalPlace(shatGambuj);
        
       
//        HistoricalPlace khanJahanTomb = new HistoricalPlace("KHU-HIST-002", "Khan Jahan Ali Tomb Complex", "Khulna", "Bagerhat");
//        khanJahanTomb.setAddress("Near Shat Gambuj Mosque, Bagerhat");
//        khanJahanTomb.setDescription("Mausoleum of Khan Jahan Ali, the 15th-century Muslim saint and founder of Bagerhat. Features a black stone sarcophagus, ancient mosque, and crocodile-filled pond.");
//        khanJahanTomb.setEntryCost(50.0);
//        khanJahanTomb.setBestTime("November-February (8:00 AM - 5:00 PM)");
//        khanJahanTomb.setWhyVisit("Pay respects at the shrine of Bagerhat's legendary founder. See sacred crocodiles and learn about the region's Islamic heritage.");
//        khanJahanTomb.setImagePath("khulna/mosque1.jpg");
//        khanJahanTomb.setTags("religious, historical, spiritual, photography, pilgrimage, cultural heritage");
//        khanJahanTomb.setYearBuilt("1459");
//        khanJahanTomb.setTimePeriod("Medieval Sultanate Period");
//        khanJahanTomb.setArchitectureStyle("Bengal Sultanate");
//        khanJahanTomb.setHistoricalFacts("Khan Jahan Ali cleared Sundarbans forests to establish Bagerhat. The adjacent pond 'Ghora Dighi' has sacred crocodiles believed to be his disciples.");
//        khanJahanTomb.setUNESCO(true);
//        khanJahanTomb.setImage1("khulna_division/khanjahantomb1.jpg");
//        khanJahanTomb.setImage2("khulna_division/khanjahantomb2.jpg");
//        khanJahanTomb.setImage3("khulna_division/ghoradighi.jpg");
//        khanJahanTomb.setImage4("khulna_division/bagerhatmosque.jpg");
//        
//        placeDAO.insertHistoricalPlace(khanJahanTomb);
        
     
        HistoricalPlace nineDomeMosque = new HistoricalPlace("KHU-HIST-003", "Nine Dome Mosque", "Khulna", "Bagerhat");
        nineDomeMosque.setAddress("Bagerhat Sadar, near Shat Gambuj");
        nineDomeMosque.setDescription("Elegant 15th-century mosque with nine domes and intricate terracotta floral motifs. Part of the UNESCO Bagerhat mosque city complex.");
        nineDomeMosque.setEntryCost(50.0);
        nineDomeMosque.setBestTime("November-February (9:00 AM - 4:00 PM)");
        nineDomeMosque.setWhyVisit("Admire delicate terracotta artwork preserved for 500+ years. A smaller but more ornate companion to Shat Gambuj Mosque.");
        nineDomeMosque.setImagePath("khulna/ninedom.jpg");
        nineDomeMosque.setTags("UNESCO, terracotta art, photography, historical, architectural gem, religious");
        nineDomeMosque.setYearBuilt("15th Century");
        nineDomeMosque.setTimePeriod("Medieval Sultanate");
        nineDomeMosque.setArchitectureStyle("Bengal Sultanate");
        nineDomeMosque.setHistoricalFacts("Part of the historic 'Mosque City of Bagerhat' UNESCO site. Features some of the finest surviving terracotta work in Bangladesh.");
        nineDomeMosque.setUNESCO(true);
//        nineDomeMosque.setImage1("khulna_division/ninedome1.jpg");
//        nineDomeMosque.setImage2("khulna_division/ninedome2.jpg");
//        nineDomeMosque.setImage3("khulna_division/bagerhatterracotta.jpg");
//        nineDomeMosque.setImage4("khulna_division/ninedome3.jpg");
//        
//        placeDAO.insertHistoricalPlace(nineDomeMosque);
//        
      
//        HistoricalPlace singairMosque = new HistoricalPlace("KHU-HIST-004", "Singair Mosque", "Khulna", "Bagerhat");
//        singairMosque.setAddress("Bagerhat District, Khulna");
//        singairMosque.setDescription("Single-domed 15th-century mosque with unique octagonal design and original terracotta panels depicting floral and geometric patterns.");
//        singairMosque.setEntryCost(30.0);
//        singairMosque.setBestTime("November-February (9:00 AM - 4:00 PM)");
//        singairMosque.setWhyVisit("Discover a hidden gem of medieval Bengali architecture. The octagonal shape and preserved terracotta work are rare finds.");
//        singairMosque.setImagePath("khulna_division/singairmosque.jpg");
//        singairMosque.setTags("hidden gem, archaeology, photography, historical, terracotta, offbeat");
//        singairMosque.setYearBuilt("15th Century");
//        singairMosque.setTimePeriod("Medieval Sultanate");
//        singairMosque.setArchitectureStyle("Bengal Sultanate with octagonal design");
//        singairMosque.setHistoricalFacts("One of the few surviving octagonal mosques in Bangladesh. Represents experimental architecture of the Khan Jahan Ali style.");
//        singairMosque.setUNESCO(true);
//        singairMosque.setImage1("khulna_division/singair1.jpg");
//        singairMosque.setImage2("khulna_division/singair2.jpg");
//        singairMosque.setImage3("khulna_division/bagerhatruins.jpg");
//        singairMosque.setImage4("khulna_division/singair3.jpg");
//        
//        placeDAO.insertHistoricalPlace(singairMosque);
        
//     
//        HistoricalPlace khulnaCityCenter = new HistoricalPlace("KHU-HIST-005", "Khulna City Center Heritage", "Khulna", "Khulna City");
//        khulnaCityCenter.setAddress("Khulna City Center, Khulna");
//        khulnaCityCenter.setDescription("Colonial-era landmarks including Khulna Court Building, Railway Station (1905), and 'GPO' area showcasing British period architecture.");
//        khulnaCityCenter.setEntryCost(0.0);
//        khulnaCityCenter.setBestTime("November-February (afternoon for best light)");
//        khulnaCityCenter.setWhyVisit("Walk through Khulna's colonial past with well-preserved British-era buildings, still used daily as courthouses and post offices.");
//        khulnaCityCenter.setImagePath("khulna_division/khulnacourt.jpg");
//        khulnaCityCenter.setTags("colonial architecture, photography, urban heritage, walkable history, vintage");
//        khulnaCityCenter.setYearBuilt("1905-1920");
//        khulnaCityCenter.setTimePeriod("British Colonial Period");
//        khulnaCityCenter.setArchitectureStyle("Indo-Colonial & Victorian");
//        khulnaCityCenter.setHistoricalFacts("Khulna was declared a municipality in 1884. The railway station connected Khulna to Calcutta in 1905. These buildings survived the 1971 Liberation War.");
//        khulnaCityCenter.setUNESCO(false);
//        khulnaCityCenter.setImage1("khulna_division/khulnacity1.jpg");
//        khulnaCityCenter.setImage2("khulna_division/khulnarailway.jpg");
//        khulnaCityCenter.setImage3("khulna_division/khulnagpo.jpg");
//        khulnaCityCenter.setImage4("khulna_division/khulnaheritage.jpg");
//        
//        placeDAO.insertHistoricalPlace(khulnaCityCenter);
    }
    
    private static void addNaturePlaces() {
        
        NaturePlace sundarbans = new NaturePlace("KHU-NAT-001", "Sundarbans Mangrove Forest", "Khulna", "Bagerhat/Satkhira/Khulna");
        sundarbans.setAddress("Southwest Bangladesh, Khulna Division (access via Mongla, Dacope, or Shyamnagar)");
        sundarbans.setDescription("World's largest mangrove forest, UNESCO World Heritage site, home to Royal Bengal Tigers, spotted deer, crocodiles, and diverse bird species. Network of rivers, creeks, and islands.");
        sundarbans.setEntryCost(1200.0);
        sundarbans.setBestTime("November-February (8:00 AM - 5:00 PM for day trips, multi-day tours available)");
        sundarbans.setWhyVisit("Experience the mystical beauty of the world's only mangrove tiger habitat. Boat through narrow creeks, spot wildlife, and witness nature's raw power.");
        sundarbans.setImagePath("khulna/Sundarban1.jpg");
        sundarbans.setTags("UNESCO, wildlife, tiger, boat safari, adventure, photography, eco-tourism, mangrove, biodiversity hotspot");
        sundarbans.setNatureType("Mangrove Forest (Tidal Wetland)");
        sundarbans.setAreaSize("6,017 sq km (Bangladesh portion)");
        sundarbans.setActivities("boat safari, wildlife watching, bird watching, photography, hiking on forest trails, village visits, fishing");
        sundarbans.setConservationStatus("UNESCO World Heritage Site & Ramsar Wetland");
        sundarbans.setImage1("khulna/sunderbon2.jpg");
        sundarbans.setImage2("khulna/sundorban3.jpg");
        sundarbans.setImage3("khulna/Sundarbans4.jpg");
        sundarbans.setImage4("khulna/Sundarban5.jpg");
        
        placeDAO.insertNaturePlace(sundarbans);
        
       
//        NaturePlace kotkaWildlife = new NaturePlace("KHU-NAT-002", "Kotka Wildlife Sanctuary", "Khulna", "Sundarbans East (Kochikhali)");
//        kotkaWildlife.setAddress("Sundarbans East Sanctuary Zone, near Kochikhali");
//        kotkaWildlife.setDescription("Prime tiger-spotting zone in Sundarbans with watchtowers, crocodile breeding grounds, and spotting deer herds on beach areas.");
//        kotkaWildlife.setEntryCost(1500.0);
//        kotkaWildlife.setBestTime("December-March (best tiger visibility)");
//        kotkaWildlife.setWhyVisit("Highest probability of Royal Bengal Tiger sightings in Sundarbans. Climb the watchtower for panoramic mangrove views.");
//        kotkaWildlife.setImagePath("khulna_division/kotka.jpg");
//        kotkaWildlife.setTags("tiger spotting, adventure, photography, wildlife sanctuary, boat safari, premium eco-tourism");
//        kotkaWildlife.setNatureType("Mangrove Forest & Coastal Beach");
//        kotkaWildlife.setAreaSize("Protected zone within Sundarbans");
//        kotkaWildlife.setActivities("watchtower viewing, tiger tracking, crocodile watching, beach walks, bird watching, night safari (permitted with guide)");
//        kotkaWildlife.setConservationStatus("Wildlife Sanctuary (strictly protected)");
//        kotkaWildlife.setImage1("khulna_division/kotka_tower.jpg");
//        kotkaWildlife.setImage2("khulna_division/kotka_beach.jpg");
//        kotkaWildlife.setImage3("khulna_division/kotka_crocodile.jpg");
//        
//        placeDAO.insertNaturePlace(kotkaWildlife);
        
//       
        NaturePlace karomjol = new NaturePlace("KHU-NAT-003", "Karamjal Wildlife Breeding Center", "Khulna", "Mongla, Sundarbans");
        karomjol.setAddress("Mongla Upazila, Sundarbans entry point");
        karomjol.setDescription("Accessible crocodile and deer breeding center inside Sundarbans. Features boardwalks, watchtowers, and captive crocs for safe viewing.");
        karomjol.setEntryCost(300.0);
        karomjol.setBestTime("November-February (9:00 AM - 4:00 PM)");
        karomjol.setWhyVisit("See Sundarbans wildlife up close without deep forest trekking. Perfect for families and first-time visitors.");
        karomjol.setImagePath("khulna/karamjal.jpg");
        karomjol.setTags("family-friendly, easy access, crocodiles, deer, educational, photography");
        karomjol.setNatureType("Mangrove Forest with Breeding Center");
        karomjol.setAreaSize("Breeding facility within Sundarbans");
        karomjol.setActivities("boardwalk walking, crocodile feeding shows, deer watching, boat rides, photography");
        karomjol.setConservationStatus("Protected breeding center");
//        karomjol.setImage1("khulna_division/karamjol_croc.jpg");
//        karomjol.setImage2("khulna_division/karamjol_deer.jpg");
//        karomjol.setImage3("khulna_division/karamjol_boardwalk.jpg");
//        
      placeDAO.insertNaturePlace(karomjol);
        
//       
        NaturePlace dublarChar = new NaturePlace("KHU-NAT-004", "Dublar Char Island", "Khulna", "Sundarbans South");
        dublarChar.setAddress("Southern Sundarbans, accessible from Mongla");
        dublarChar.setDescription("Seasonal island famous for red crab migrations, fishing communities, and wintering migratory birds. Remote beach with sunset views.");
        dublarChar.setEntryCost(1000.0);
        dublarChar.setBestTime("November-February (crab season December-January)");
        dublarChar.setWhyVisit("Witness millions of red crabs during migration season. Experience authentic fisherman life on a remote island.");
        dublarChar.setImagePath("khulna/dublar.jpg");
        dublarChar.setTags("island, crab migration, offbeat, photography, fishing community, beach, sunset");
        dublarChar.setNatureType("Tidal Island & Coastal Beach");
        dublarChar.setAreaSize("Seasonal island (varies with tide)");
        dublarChar.setActivities("crab watching, fishing community visits, beach walking, sunset photography, bird watching");
        dublarChar.setConservationStatus("Part of Sundarbans Protected Area");
//        dublarChar.setImage1("khulna_division/dublar_crab.jpg");
//        dublarChar.setImage2("khulna_division/dublar_fishermen.jpg");
//        dublarChar.setImage3("khulna_division/dublar_sunset.jpg");
        
        placeDAO.insertNaturePlace(dublarChar);
    }
    
    private static void addEntertainmentPlaces() {
        
//        ManmadePlace monglaBoatTour = new ManmadePlace("KHU-ENT-001", "Mongla River Cruise & Sundarbans Gateway", "Khulna", "Mongla");
//        monglaBoatTour.setAddress("Mongla Port, Mongla, Bagerhat");
//        monglaBoatTour.setDescription("Gateway to Sundarbans with sunset cruises on Passur River, local fish market visits, and boat tours to surrounding mangrove creeks.");
//        monglaBoatTour.setEntryCost(500.0);
//        monglaBoatTour.setBestTime("October-March (2:00 PM sunset cruise, 8:00 AM full-day tours)");
//        monglaBoatTour.setWhyVisit("Start your Sundarbans adventure here. Enjoy a relaxing river cruise with fresh seafood and stunning mangrove skyline views.");
//        monglaBoatTour.setImagePath("khulna_division/mongla_cruise.jpg");
//        monglaBoatTour.setTags("boat tour, sunset, seafood, adventure gateway, photography, family-friendly");
//        monglaBoatTour.setEntertainmentType("Boat Cruise & Eco-Tourism Hub");
//        monglaBoatTour.setOpeningHours("6:00 AM - 6:00 PM (cruise bookings required)");
//        monglaBoatTour.setSpecialEvents("Sunset music cruises, fishing competitions, Sundarbans orientation programs");
//        monglaBoatTour.setAverageSpending(1200.0);
//        monglaBoatTour.setContactInfo("Mongla Tourist Center: +880-1912-345678");
//        monglaBoatTour.setHasEntryFee(false);
//        monglaBoatTour.setImage1("khulna_division/mongla_port.jpg");
//        monglaBoatTour.setImage2("khulna_division/mongla_river.jpg");
//        monglaBoatTour.setImage3("khulna_division/mongla_market.jpg");
//        monglaBoatTour.setImage4("khulna_division/mongla_cruise2.jpg");
//        
//        placeDAO.insertEntertainmentPlace(monglaBoatTour);
        
//        ManmadePlace khulnaCityFestival = new ManmadePlace("KHU-ENT-002", "Khulna City Festivals - Rash Mela & Folklore Fair", "Khulna", "Khulna City");
//        khulnaCityFestival.setAddress("Khulna Circuit House Ground & Shiromoni, Khulna");
//        khulnaCityFestival.setDescription("Annual Rash Mela (November) celebrating Hindu-Muslim harmony, plus traditional folk fairs with puppet shows, folk songs, and village crafts.");
//        khulnaCityFestival.setEntryCost(0.0);
//        khulnaCityFestival.setBestTime("November (Rash Purnima) and March (Spring Fair)");
//        khulnaCityFestival.setWhyVisit("Experience Khulna's unique syncretic culture - Hindu and Muslim communities celebrating together with music, food, and crafts.");
//        khulnaCityFestival.setImagePath("khulna_division/rashmela.jpg");
//        khulnaCityFestival.setTags("cultural festival, folk music, harmony celebration, photography, traditional food, crafts fair");
//        khulnaCityFestival.setEntertainmentType("Religious & Folk Festival");
//        khulnaCityFestival.setOpeningHours("November: 10:00 AM - 10:00 PM (3 days)");
//        khulnaCityFestival.setSpecialEvents("Rash Mela procession, folk music competition, puppet shows, traditional circus, handicraft stalls");
//        khulnaCityFestival.setAverageSpending(800.0);
//        khulnaCityFestival.setContactInfo("Khulna City Corporation: +880-41-724321");
//        khulnaCityFestival.setHasEntryFee(false);
//        khulnaCityFestival.setImage1("khulna_division/rashmela_procession.jpg");
//        khulnaCityFestival.setImage2("khulna_division/khulna_folk.jpg");
//        khulnaCityFestival.setImage3("khulna_division/rashmela_craft.jpg");
//        khulnaCityFestival.setImage4("khulna_division/khulna_fair.jpg");
//        
//        placeDAO.insertEntertainmentPlace(khulnaCityFestival);
//        
//        ManmadePlace sundarbansEcoTour = new ManmadePlace("KHU-ENT-003", "Sundarbans Eco-Tourism Camp - Dacope", "Khulna", "Dacope");
//        sundarbansEcoTour.setAddress("Dacope Upazila, Khulna (Sundarbans entry point)");
//        sundarbansEcoTour.setDescription("Eco-resort offering guided night walks, traditional village stays, fishing trips, and Sundarbans orientation programs.");
//        sundarbansEcoTour.setEntryCost(800.0);
//        sundarbansEcoTour.setBestTime("November-February (day trips and overnight packages)");
//        sundarbansEcoTour.setWhyVisit("Stay overnight on Sundarbans' edge. Join night walks to see bioluminescent plankton and nocturnal wildlife.");
//        sundarbansEcoTour.setImagePath("khulna_division/dacope_camp.jpg");
//        sundarbansEcoTour.setTags("eco-lodge, night walk, village stay, adventure, educational, family-friendly, bioluminescence");
//        sundarbansEcoTour.setEntertainmentType("Eco-Resort & Adventure Camp");
//        sundarbansEcoTour.setOpeningHours("24/7 (booking required)");
//        sundarbansEcoTour.setSpecialEvents("Night jungle walks, stargazing, traditional fishing workshops, honey collection demos");
//        sundarbansEcoTour.setAverageSpending(2500.0);
//        sundarbansEcoTour.setContactInfo("Sundarbans Eco Camp: +880-1712-345678");
//        sundarbansEcoTour.setHasEntryFee(true);
//        sundarbansEcoTour.setImage1("khulna_division/dacope_night.jpg");
//        sundarbansEcoTour.setImage2("khulna_division/dacope_village.jpg");
//        sundarbansEcoTour.setImage3("khulna_division/dacope_boat.jpg");
//        sundarbansEcoTour.setImage4("khulna_division/dacope_honey.jpg");
//        
//        placeDAO.insertEntertainmentPlace(sundarbansEcoTour);
    }
}