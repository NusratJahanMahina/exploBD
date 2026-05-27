package com.ExploBD.data;

import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class TestData {
    
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
            
           
           
            stmt.execute("DELETE FROM historical_places");
            stmt.execute("DELETE FROM nature_places");
            stmt.execute("DELETE FROM entertainment_places");
            stmt.execute("DELETE FROM places");
            
        }
    }
    
    private static void addHistoricalPlaces() {
        
       
        HistoricalPlace lalbagh = new HistoricalPlace("DHA-HIST-001", "Lalbagh Fort", "Dhaka", "Dhaka");
        lalbagh.setAddress("Lalbagh, Dhaka-1211");
        lalbagh.setDescription("A 17th-century Mughal fort complex with mosque, tomb of Pari Bibi, and Diwan-i-Aam. Features gardens, river views, and museum with Mughal artifacts.");
        lalbagh.setEntryCost(200.0);
        lalbagh.setBestTime("November-February (8:00 AM - 5:00 PM)");
        lalbagh.setWhyVisit("Explore Bangladesh's Mughal heritage at this beautifully incomplete fort with scenic gardens and river views. Perfect for history photography.");
        lalbagh.setImagePath("dhaka_division/rsz_lalbagfort2.jpg");
        lalbagh.setTags("historical, photography, family-friendly, Mughal, cultural heritage, architectural");
        lalbagh.setYearBuilt("1678");
        lalbagh.setTimePeriod("Mughal Era");
        lalbagh.setArchitectureStyle("Mughal");
        lalbagh.setHistoricalFacts("Construction started in 1678 by Prince Muhammad Azam. Work stopped after Pari Bibi's death, leaving the fort incomplete. Now a major archaeological site.");
        lalbagh.setUNESCO(false);
        lalbagh.setImage1("dhaka_division/lalbagfort1.jpg");
        lalbagh.setImage2("dhaka_division/lalbagfort5.jpg");
        lalbagh.setImage3("dhaka_division/lalbagfort4.jpg");
        lalbagh.setImage4("dhaka_division/lalbagfort3.jpg");
        
       
        placeDAO.insertHistoricalPlace(lalbagh);
        
       
        HistoricalPlace ahsanManzil = new HistoricalPlace("DHA-HIST-002", "Ahsan Manzil", "Dhaka", "Dhaka");
        ahsanManzil.setAddress("Kumartoli, Dhaka-1000");
        ahsanManzil.setDescription("The Pink Palace of Dhaka Nawabs, now a national museum with 23 galleries showcasing aristocratic life during British era.");
        ahsanManzil.setEntryCost(200.0);
        ahsanManzil.setBestTime("October-March (10:30 AM - 5:30 PM, closed Sunday)");
        ahsanManzil.setWhyVisit("Step into Dhaka's royal past at this iconic pink palace overlooking the Buriganga River. Museum displays Nawabi treasures.");
        ahsanManzil.setImagePath("dhaka_division/ahsanmanzil4_1.jpg");
        ahsanManzil.setTags("palace, museum, river view, photography, cultural, pink architecture, historical");
        ahsanManzil.setYearBuilt("1872");
        ahsanManzil.setTimePeriod("British Colonial");
        ahsanManzil.setArchitectureStyle("Indo-Saracenic Revival");
        ahsanManzil.setHistoricalFacts("Originally a French trading house, converted to palace in 1872. Center of Dhaka's social and political life during British rule.");
        ahsanManzil.setUNESCO(false);
        ahsanManzil.setImage1("dhaka_division/ahsanmanzil3.jpg");
        ahsanManzil.setImage2("dhaka_division/ahsanmanzil5.jpg");
        ahsanManzil.setImage3("dhaka_division/ahsanmanzil1.jpg");
        ahsanManzil.setImage4("dhaka_division/ahsanmanzil2.jpg");
        
        placeDAO.insertHistoricalPlace(ahsanManzil);
        
        
        HistoricalPlace martyrsMemorial = new HistoricalPlace("DHA-HIST-003", "National Martyrs Memorial", "Dhaka", "Savar");
        martyrsMemorial.setAddress("Savar, Dhaka");
        martyrsMemorial.setDescription("National monument honoring 1971 Liberation War martyrs. Features seven triangular structures, reflecting pool, and 84-acre gardens.");
        martyrsMemorial.setEntryCost(0.0);
        martyrsMemorial.setBestTime("October-March (6:00 AM - 6:00 PM)");
        martyrsMemorial.setWhyVisit("Pay respects at Bangladesh's most significant national monument. Powerful architecture symbolizing freedom struggle and sacrifice.");
        martyrsMemorial.setImagePath("dhaka_division/martyrsmemorial2.jpg");
        martyrsMemorial.setTags("patriotic, architectural, photography, symbolic, national pride, reflective");
        martyrsMemorial.setYearBuilt("1982");
        martyrsMemorial.setTimePeriod("Post-Liberation");
        martyrsMemorial.setArchitectureStyle("Modern Monumental");
        martyrsMemorial.setHistoricalFacts("Inaugurated in 1982. Seven triangle pairs represent liberation movement phases. Designed by architect Syed Mainul Hossain.");
        martyrsMemorial.setUNESCO(false);
        martyrsMemorial.setImage1("dhaka_division/martyrsmemorial6.jpg");
        martyrsMemorial.setImage2("dhaka_division/martyrsmemorial4.jpg");
        martyrsMemorial.setImage3("dhaka_division/martyrsmemorial5.jpg");
        martyrsMemorial.setImage4("dhaka_division/martyrsmemorial1.jpg");
        
        placeDAO.insertHistoricalPlace(martyrsMemorial);
        
     
        HistoricalPlace parliament = new HistoricalPlace("DHA-HIST-004", "Jatiya Sangsad Bhaban", "Dhaka", "Dhaka");
        parliament.setAddress("Sher-e-Bangla Nagar, Dhaka");
        parliament.setDescription("Louis Kahn's architectural masterpiece - world's largest legislative complex with geometric concrete structures and water features.");
        parliament.setEntryCost(100.0);
        parliament.setBestTime("Friday-Saturday (when not in session)");
        parliament.setWhyVisit("Marvel at one of the 20th century's greatest architectural achievements. Geometric perfection meets Bangladeshi context.");
        parliament.setImagePath("dhaka_division/parlament8_1.jpg");
        parliament.setTags("architectural wonder, modern, photography, geometric, urban, iconic");
        parliament.setYearBuilt("1982");
        parliament.setTimePeriod("Modern");
        parliament.setArchitectureStyle("Brutalist/Modern");
        parliament.setHistoricalFacts("Construction 1961-1982. Louis Kahn's magnum opus blending modernism with local elements. Houses Bangladesh Parliament.");
        parliament.setUNESCO(false);
        parliament.setImage1("dhaka_division/parlament3.jpg");
        parliament.setImage2("dhaka_division/parlament4.jpg");
        parliament.setImage3("dhaka_division/parlament6.jpg");
        parliament.setImage4("dhaka_division/parlament5.jpg");
        
        placeDAO.insertHistoricalPlace(parliament);
       
        HistoricalPlace panamNagar = new HistoricalPlace("DHA-HIST-005", "Panam Nagar", "Dhaka", "Narayanganj");
        panamNagar.setAddress("Sonargaon, Narayanganj");
        panamNagar.setDescription("19th-century Hindu merchant town with 52 abandoned mansions showcasing Greco-Roman and Mughal architectural fusion.");
        panamNagar.setEntryCost(50.0);
        panamNagar.setBestTime("November-February (9:00 AM - 5:00 PM)");
        panamNagar.setWhyVisit("Wander through a hauntingly beautiful ghost town frozen in time. Capture colonial-era architecture and merchant history.");
        panamNagar.setImagePath("dhaka_division/panamnagar7_1.jpg");
        panamNagar.setTags("abandoned, photography, colonial, ruins, atmospheric, heritage walk");
        panamNagar.setYearBuilt("Early 19th Century");
        panamNagar.setTimePeriod("Colonial Merchant Era");
        panamNagar.setArchitectureStyle("Indo-Colonial");
        panamNagar.setHistoricalFacts("Built by Hindu cloth merchants, abandoned after 1947 partition. On UNESCO World Heritage Tentative List.");
        panamNagar.setUNESCO(true);
        panamNagar.setImage1("dhaka_division/panamnagar8.jpg");
        panamNagar.setImage2("dhaka_division/panamnagar5.jpg");
        panamNagar.setImage3("dhaka_division/panamnagar1.jpg");
        panamNagar.setImage4("dhaka_division/panamnagar9.jpg");
        
        placeDAO.insertHistoricalPlace(panamNagar);
        
       
        HistoricalPlace oldDhakaCluster = new HistoricalPlace("DHA-HIST-006", "Old Dhaka Cluster", "Dhaka", "Dhaka");
        oldDhakaCluster.setAddress("Old Dhaka area");
        oldDhakaCluster.setDescription("Three iconic sites: Sadarghat (busy river port), Tara Masjid (star-decorated mosque), Armenian Church (oldest Christian site).");
        oldDhakaCluster.setEntryCost(0.0);
        oldDhakaCluster.setBestTime("November-February (mornings)");
        oldDhakaCluster.setWhyVisit("Experience Dhaka's living history in one walkable area - river life, mosaic art, and multicultural heritage.");
        oldDhakaCluster.setImagePath("dhaka_division/sadarghat1_1.jpg");
        oldDhakaCluster.setTags("cultural immersion, photography, street food, river life, mosaic art, religious diversity");
        oldDhakaCluster.setYearBuilt("Various (17th-19th Century)");
        oldDhakaCluster.setTimePeriod("Mughal to Colonial Period");
        oldDhakaCluster.setArchitectureStyle("Mixed (Mughal, Colonial, Traditional)");
        oldDhakaCluster.setHistoricalFacts("Sadarghat: 400+ years operating. Tara Masjid: 19th century with Japanese stars. Armenian Church: 1781 by merchants.");
        oldDhakaCluster.setUNESCO(false);
        oldDhakaCluster.setImage1("dhaka_division/sadarghat2.jpg");
        oldDhakaCluster.setImage2("dhaka_division/arnenianchurch.jpg");
        oldDhakaCluster.setImage3("dhaka_division/taramashjib.jpg");
        oldDhakaCluster.setImage4("dhaka_division/purandhaka.jpg");
        
        placeDAO.insertHistoricalPlace(oldDhakaCluster);
        
       
        HistoricalPlace shahbaghPrecinct = new HistoricalPlace("DHA-HIST-007", "Shahbagh Cultural Precinct", "Dhaka", "Dhaka");
        shahbaghPrecinct.setAddress("Shahbagh area, Dhaka");
        shahbaghPrecinct.setDescription("Cultural hub with Shahid Minar (language movement), National Museum, Liberation Museum, Curzon Hall, and Romna Park.");
        shahbaghPrecinct.setEntryCost(0.0);
        shahbaghPrecinct.setBestTime("October-March (museum hours)");
        shahbaghPrecinct.setWhyVisit("Dive into Bangladesh's cultural heart - from language movement to liberation war, all in one educational district.");
        shahbaghPrecinct.setImagePath("dhaka_division/curzonhall_1.jpg");
        shahbaghPrecinct.setTags("educational, cultural hub, photography, family-friendly, patriotic, museum complex");
        shahbaghPrecinct.setYearBuilt("Various (1904-1996)");
        shahbaghPrecinct.setTimePeriod("20th Century");
        shahbaghPrecinct.setArchitectureStyle("Mixed (Colonial, Modern, Monumental)");
        shahbaghPrecinct.setHistoricalFacts("Shahid Minar (1952), Curzon Hall (1904), National Museum (1913), Liberation Museum (1996) - representing key historical periods.");
        shahbaghPrecinct.setUNESCO(false);
        shahbaghPrecinct.setImage1("dhaka_division/shahidminar.jpg");
        shahbaghPrecinct.setImage2("dhaka_division/shahbag.jpg");
        shahbaghPrecinct.setImage3("dhaka_division/ramnapark.jpg");
        shahbaghPrecinct.setImage4("dhaka_division/museum.jpg");
        
        placeDAO.insertHistoricalPlace(shahbaghPrecinct);
    }
    
    private static void addNaturePlaces() {
        
        NaturePlace bhawalPark = new NaturePlace("DHA-NAT-001", "Bhawal National Park", "Dhaka", "Gazipur");
        bhawalPark.setAddress("Gazipur, Dhaka Division");
        bhawalPark.setDescription("5,022-acre national park with Sal forest, spotted deer, monkeys, lakes, and walking trails near Dhaka.");
        bhawalPark.setEntryCost(150.0);
        bhawalPark.setBestTime("November-February (8:00 AM - 5:00 PM)");
        bhawalPark.setWhyVisit("Escape city chaos to Dhaka's closest forest retreat. Spot wildlife, boat on lakes, and breathe fresh air.");
        bhawalPark.setImagePath("dhaka_division/bhawal.jpg");
        bhawalPark.setTags("nature escape, wildlife, photography, eco-tourism, adventure, family-friendly");
        bhawalPark.setNatureType("Deciduous Forest");
        bhawalPark.setAreaSize("5,022 acres");
        bhawalPark.setActivities("wildlife watching, boating, hiking, photography, bird watching");
        bhawalPark.setConservationStatus("National Park (protected)");
        bhawalPark.setImage1("dhaka_division/bhawal3.jpg");
        bhawalPark.setImage2("dhaka_division/bhawal4.jpg");
        bhawalPark.setImage3("dhaka_division/bhawal1.jpg");
        
        placeDAO.insertNaturePlace(bhawalPark);
    }
    
    private static void addEntertainmentPlaces() {
        
        ManmadePlace pohelaBoishakh = new ManmadePlace("DHA-ENT-001", "Pohela Boishakh", "Dhaka", "Dhaka");
        pohelaBoishakh.setAddress("Ramna Park, Dhaka University, nationwide");
        pohelaBoishakh.setDescription("Bengali New Year festival with Mongol Shobhajatra procession, cultural shows, traditional food, and nationwide celebrations.");
        pohelaBoishakh.setEntryCost(0.0);
        pohelaBoishakh.setBestTime("April 14 (all day)");
        pohelaBoishakh.setWhyVisit("Join Bangladesh's biggest cultural celebration - colorful processions, traditional feasts, and joyful community spirit.");
        pohelaBoishakh.setImagePath("dhaka_division/pohela.jpg");
        pohelaBoishakh.setTags("festival, cultural immersion, photography, family-friendly, traditional, food exploration");
        pohelaBoishakh.setEntertainmentType("Cultural Festival");
        pohelaBoishakh.setOpeningHours("6:00 AM - 12:00 AM (April 14)");
        pohelaBoishakh.setSpecialEvents("Mongol Shobhajatra procession, cultural shows, traditional fairs");
        pohelaBoishakh.setAverageSpending(500.0);
        pohelaBoishakh.setContactInfo("Not applicable - public festival");
        pohelaBoishakh.setHasEntryFee(false);
        pohelaBoishakh.setImage1("dhaka_division/pohela2.jpg");
        pohelaBoishakh.setImage2("dhaka_division/pohela11.jpg");
        pohelaBoishakh.setImage3("dhaka_division/pohela7.jpg");
        pohelaBoishakh.setImage4("dhaka_division/pohela3.jpg");
        
        placeDAO.insertEntertainmentPlace(pohelaBoishakh);
        
        ManmadePlace boiMela = new ManmadePlace("DHA-ENT-002", "Ekushey Boi Mela", "Dhaka", "Dhaka");
        boiMela.setAddress("Bangla Academy/Suhrawardy Udyan");
        boiMela.setDescription("Month-long national book fair honoring language martyrs with thousands of book stalls, author meets, and cultural programs.");
        boiMela.setEntryCost(0.0);
        boiMela.setBestTime("February (entire month)");
        boiMela.setWhyVisit("Immerse in Bangladesh's literary heart - browse books, meet authors, and celebrate intellectual heritage.");
        boiMela.setImagePath("dhaka_division/bookfair.jpg");
        boiMela.setTags("literary, educational, cultural, photography, intellectual, family-friendly");
        boiMela.setEntertainmentType("Book Fair/Literary Festival");
        boiMela.setOpeningHours("3:00 PM - 9:00 PM (Weekdays), 11:00 AM - 9:00 PM (Weekends)");
        boiMela.setSpecialEvents("Author meets, book launches, literary discussions, cultural programs");
        boiMela.setAverageSpending(1000.0);
        boiMela.setContactInfo("Bangla Academy: +880-2-8619666");
        boiMela.setHasEntryFee(false);
        boiMela.setImage1("dhaka_division/bookfair1.jpg");
        boiMela.setImage2("dhaka_division/boimela.jpg");
        boiMela.setImage3("dhaka_division/boimela33.jpg");
        
        placeDAO.insertEntertainmentPlace(boiMela);
    }
}