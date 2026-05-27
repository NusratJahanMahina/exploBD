package com.ExploBD.data;

import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class SylhetData {
    
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
            
           
           
            stmt.execute("DELETE FROM historical_places");
            stmt.execute("DELETE FROM nature_places");
            stmt.execute("DELETE FROM entertainment_places");
            stmt.execute("DELETE FROM places");
            
        }
    }
    
    private static void addHistoricalPlaces() {
        
       
        HistoricalPlace shahjalalMazar = new HistoricalPlace("SYL-HIST-001", "Hazrat Shahjalal Mazar Sharif", "Sylhet", "Sylhet City");
        shahjalalMazar.setAddress("Dargah Gate, Sylhet-3100");
        shahjalalMazar.setDescription("Sacred shrine of 14th-century Muslim saint Hazrat Shahjalal (R), who spread Islam in Sylhet with 360 disciples. Complex includes mosque, tomb, ancient pond with sacred fish, and large prayer hall.");
        shahjalalMazar.setEntryCost(0.0);
        shahjalalMazar.setBestTime("October-March (all day, Friday prayers especially vibrant)");
        shahjalalMazar.setWhyVisit("Experience the spiritual heart of Sylhet. See the famous sacred fish (believed to be disciples), join thousands of devotees, and feel the peaceful atmosphere.");
        shahjalalMazar.setImagePath("sylhet/mazar1.jpg");
        shahjalalMazar.setTags("historical, culture, relaxation, spiritual, pilgrimage, urban, pictorial, food (nearby traditional sweets), religious heritage");
        shahjalalMazar.setYearBuilt("14th Century (original), renovated over time");
        shahjalalMazar.setTimePeriod("Medieval Period");
        shahjalalMazar.setArchitectureStyle("Mughal-Bengal Fusion with Modern Elements");
        shahjalalMazar.setHistoricalFacts("Shahjalal arrived in Sylhet in 1303 to help conquer the region. The sacred fish in the pond are never eaten - legend says they were his disciples transformed. Over 700,000 devotees visit during annual Urs festival.");
        shahjalalMazar.setUNESCO(false);
        shahjalalMazar.setImage1("sylhet/mazar2.jpg");
        shahjalalMazar.setImage2("sylhet/mazar3.jpg");
        shahjalalMazar.setImage3("sylhet/mazar4.jpg");
        shahjalalMazar.setImage4("sylhet/mazar5.jpg");
        
        placeDAO.insertHistoricalPlace(shahjalalMazar);
    }
    
    private static void addNaturePlaces() {
        
        NaturePlace ratargul = new NaturePlace("SYL-NAT-001", "Ratargul Swamp Forest", "Sylhet", "Gowainghat");
        ratargul.setAddress("Gowainghat Upazila, Sylhet (30km from Sylhet city)");
        ratargul.setDescription("Bangladesh's only freshwater swamp forest, submerged 20-30 feet during monsoon (July-October). Unique ecosystem with trees growing from water, snakes, monkeys, and over 50 bird species. Boat rides through flooded forest paths.");
        ratargul.setEntryCost(400.0);
        ratargul.setBestTime("July-October (monsoon for full swamp experience), November-February (low water, walking possible)");
        ratargul.setWhyVisit("Experience a magical boat ride through a submerged forest - like Amazon in Bangladesh. Perfect for adventure and nature photography during monsoon.");
        ratargul.setImagePath("sylhet/ratargul1.jpg");
        ratargul.setTags("nature, adventure, pictorial, relaxation, wetland, boat safari, photography, monsoon magic, unique ecosystem, wildlife");
        ratargul.setNatureType("Freshwater Swamp Forest");
        ratargul.setAreaSize("504 acres (3,265 acres total reserved forest)");
        ratargul.setActivities("boat riding, photography, bird watching, snake spotting, jungle walking (dry season), swimming (not recommended due to snakes)");
        ratargul.setConservationStatus("Protected Swamp Forest Reserve");
        ratargul.setImage1("sylhet/ratargul2.jpg");
        ratargul.setImage2("sylhet/ratargul3.jpg");
        ratargul.setImage3("sylhet/ratargul4.jpg");
        ratargul.setImage4("sylhet/ratargul5.jpg");
        
        placeDAO.insertNaturePlace(ratargul);
        
       
        NaturePlace jaflong = new NaturePlace("SYL-NAT-002", "Jaflong", "Sylhet", "Jaflong, Gowainghat");
        jaflong.setAddress("Jaflong Zero Point, Gowainghat, Sylhet (60km from Sylhet city)");
        jaflong.setDescription("Scenic tea garden and riverine area at Bangladesh-India border. Features crystal-clear Piyain River with stone collection, Dauki River views, Khasi tribal villages, betel leaf gardens, and rolling hills.");
        jaflong.setEntryCost(0.0);
        jaflong.setBestTime("October-March (pleasant weather, clear water)");
        jaflong.setWhyVisit("Enjoy stunning mountain-backed river views without leaving Bangladesh. Collect colorful stones, visit tribal villages, and taste authentic local food.");
        jaflong.setImagePath("sylhet/jaflong1.jpg");
        jaflong.setTags("nature, pictorial, mountain (hill views), relaxation, adventure, culture (Khasi tribal), food (local snacks), beach (river beach), photography");
        jaflong.setNatureType("Riverine Hills & Tea Garden Landscape");
        jaflong.setAreaSize("Scenic area spanning several kilometers along Piyain River");
        jaflong.setActivities("stone collecting, river bathing, boat riding, tribal village visits, betel leaf garden tours, photography, picnic, swimming");
        jaflong.setConservationStatus("Tourism zone (stone extraction now restricted)");
        jaflong.setImage1("sylhet/jaflong2.jpg");
        jaflong.setImage2("sylhet/jaflong3.jpg");
        jaflong.setImage3("sylhet/jaflong4.jpg");
        jaflong.setImage4("sylhet/jaflong5.jpg");
        
        placeDAO.insertNaturePlace(jaflong);
        
       
        NaturePlace bichanakandi = new NaturePlace("SYL-NAT-003", "Bichanakandi", "Sylhet", "Gowainghat");
        bichanakandi.setAddress("Gowainghat Upazila, Sylhet (near Jaflong, border area)");
        bichanakandi.setDescription("Hidden gem with stunning blue-green river surrounded by Meghalaya hills of India. Crystal clear water, submerged rocks, and dramatic mountain backdrop. Accessible only by boat during high water.");
        bichanakandi.setEntryCost(500.0);
        bichanakandi.setBestTime("October-February (clear water, moderate water level)");
        bichanakandi.setWhyVisit("Discover Sylhet's best-kept secret - turquoise water against mountain backdrop rivals Southeast Asian destinations. Perfect for pictorial photography.");
        bichanakandi.setImagePath("sylhet/bichanakandi1JPG.jpg");
        bichanakandi.setTags("nature, pictorial, mountain (hill backdrop), adventure, relaxation, beach (river beach), photography, hidden gem, crystal clear water");
        bichanakandi.setNatureType("Riverine Valley with Hill Border");
        bichanakandi.setAreaSize("Scenic river area spanning 2-3 km");
        bichanakandi.setActivities("boat riding, swimming, photography, rock hopping, border view, picnic, cliff jumping (careful)");
        bichanakandi.setConservationStatus("Border zone (permission sometimes required)");
        bichanakandi.setImage1("sylhet/bichanakandi2.jpg");
        bichanakandi.setImage2("sylhet/bichanakandi3.jpg");
        bichanakandi.setImage3("sylhet/bichanakandi4.jpg");
        bichanakandi.setImage4("sylhet/bichanakandi5.jpg");
        
        placeDAO.insertNaturePlace(bichanakandi);
        
       
        NaturePlace madhabkunda = new NaturePlace("SYL-NAT-004", "Madhabkunda Waterfall", "Sylhet", "Barlekha, Moulvibazar");
        madhabkunda.setAddress("Barlekha Upazila, Moulvibazar District (200km from Sylhet city)");
        madhabkunda.setDescription("Bangladesh's largest waterfall (200ft drop) surrounded by tea gardens, hills, and ancient forest. Features perennial flow, natural pool for swimming, stone collecting, and a historic Shiva temple nearby.");
        madhabkunda.setEntryCost(50.0);
        madhabkunda.setBestTime("July-October (maximum water flow), November-February (clear water, good swimming)");
        madhabkunda.setWhyVisit("Witness Bangladesh's most spectacular waterfall. Swim in the natural pool, hike surrounding hills, and enjoy picnic with roaring waterfall backdrop.");
        madhabkunda.setImagePath("sylhet/madhabkunda1.jpg");
        madhabkunda.setTags("nature, adventure, pictorial, relaxation, mountain, swimming, waterfall, food (local tea stalls), photography, family-friendly");
        madhabkunda.setNatureType("Hill Waterfall & Forest");
        madhabkunda.setAreaSize("Waterfall area with 20-acre surrounding forest");
        madhabkunda.setActivities("waterfall viewing, swimming, hiking, stone collecting, photography, picnic, temple visit");
        madhabkunda.setConservationStatus("Protected eco-park zone");
        madhabkunda.setImage1("sylhet/madhabkunda2.jpg");
        madhabkunda.setImage2("sylhet/madhabkunda3.jpg");
        madhabkunda.setImage3("sylhet/madhabkunda4.jpg");
        madhabkunda.setImage4("sylhet/madhabkunda5.jpg");
        
        placeDAO.insertNaturePlace(madhabkunda);
    }
    
    private static void addEntertainmentPlaces() {
        
        ManmadePlace sreemangal = new ManmadePlace("SYL-ENT-001", "Sreemangal - The Tea Capital", "Sylhet", "Sreemangal, Moulvibazar");
        sreemangal.setAddress("Sreemangal Upazila, Moulvibazar District");
        sreemangal.setDescription("Bangladesh's 'Tea Capital' with endless tea gardens, Lawachara Rainforest (famous for gibbons), Baikka Beel wetland, Manipuri tribal villages, and 7-layer tea specialty.");
        sreemangal.setEntryCost(0.0);
        sreemangal.setBestTime("October-March (pleasant weather, migratory birds at Baikka Beel)");
        sreemangal.setWhyVisit("Experience authentic tea garden life, sip 7-layer tea, spot hoolock gibbons (only ape in Bangladesh), and explore tribal culture in one destination.");
        sreemangal.setImagePath("sylhet/Sreemangal1.jpg");
        sreemangal.setTags("nature, culture, relaxation, adventure, food (7-layer tea, Manipuri cuisine), pictorial, urban (small town), family-friendly, wildlife");
        sreemangal.setEntertainmentType("Eco-Tourism & Cultural Hub");
        sreemangal.setOpeningHours("Tea gardens: sunrise to sunset. Lawachara: 6:00 AM - 3:00 PM (winter), 6:00 AM - 5:00 PM (summer)");
        sreemangal.setSpecialEvents("Tea Festival (February), Baikka Beel bird watching (November-January), Manipuri dance performances (on request)");
        sreemangal.setAverageSpending(1200.0);
        sreemangal.setContactInfo("Sreemangal Tourism Center: +880-1731-234567");
        sreemangal.setHasEntryFee(false);
        sreemangal.setImage1("sylhet/sreemangal2.jpg");
        sreemangal.setImage2("sylhet/sreemangal3.jpg");
        sreemangal.setImage3("sylhet/sreemangal4.jpg");
        sreemangal.setImage4("sylhet/sreemangal5.jpg");
        
        placeDAO.insertEntertainmentPlace(sreemangal);
    }
}