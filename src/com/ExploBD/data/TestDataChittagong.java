package com.ExploBD.data;

import com.ExploBD.data.databaseObject.PlaceDatabaseObject;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import javax.swing.*;
import java.sql.*;

public class TestDataChittagong {

    private static PlaceDatabaseObject placeDAO = new PlaceDatabaseObject();

    public static void main(String[] args) {
        addTestData();
    }

    public static void addTestData() {
        try {
            // Uncomment only if you want to clear existing data
            // clearOldData();

            addHistoricalPlaces();
            addNaturePlaces();
            addEntertainmentPlaces();

            JOptionPane.showMessageDialog(null,
                    "Chittagong division data added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearOldData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM historical_places");
            stmt.execute("DELETE FROM nature_places");
            stmt.execute("DELETE FROM entertainment_places");
            stmt.execute("DELETE FROM places");
        }
    }

    private static void addHistoricalPlaces() {

        // 1. Buddha Dhatu Jadi (Golden Temple)
        HistoricalPlace buddhaDhatu = new HistoricalPlace("CTG-HIST-001", "Buddha Dhatu Jadi", "Chittagong", "Bandarban");
        buddhaDhatu.setAddress("Balaghata, Bandarban Sadar");
        buddhaDhatu.setDescription("Bangladesh's largest Theravada Buddhist temple. A gleaming golden pagoda atop a 200-foot hill, housing sacred Buddha relics. The complex offers panoramic hill views and serene meditation spaces.");
        buddhaDhatu.setEntryCost(50.0);
        buddhaDhatu.setBestTime("November-February (8:00 AM - 5:00 PM)");
        buddhaDhatu.setWhyVisit("Witness the stunning golden architecture against emerald hills. Experience spiritual tranquility at this sacred site where Myanmar-style design meets Bangladesh's indigenous Buddhist heritage.");
        buddhaDhatu.setImagePath("chittagong/buddha1.jpg");
        buddhaDhatu.setTags("spiritual, photography, architecture, cultural heritage, meditation, golden temple, instagrammable, peaceful");
        buddhaDhatu.setYearBuilt("2000");
        buddhaDhatu.setTimePeriod("Modern");
        buddhaDhatu.setArchitectureStyle("Arakanese (Myanmar)");
        buddhaDhatu.setHistoricalFacts("Built by Ven. U Pannya Jota Mahathera. Enshrines Buddha relics gifted from Myanmar in 1994. Second-largest Buddha statue in Bangladesh. A SAARC Buddhist Circuit site.");
        buddhaDhatu.setUNESCO(false);
        buddhaDhatu.setImage1("chittagong/buddha2.jpg");
        buddhaDhatu.setImage2("chittagong/buddha3.jpg");
        buddhaDhatu.setImage3("chittagong/buddha4.jpg");
        buddhaDhatu.setImage4("chittagong/buddha5.jpg");
        // buddhaDhatu.setImage5("chittagong/buddha_dhatu_jadi/5.jpg");
        placeDAO.insertHistoricalPlace(buddhaDhatu);

        // Add more historical places if needed
    }

    private static void addNaturePlaces() {

        // 2. Cox's Bazar Beach
        NaturePlace coxBazar = new NaturePlace("CTG-NAT-001", "Cox's Bazar Beach", "Chittagong", "Cox's Bazar");
        coxBazar.setAddress("Cox's Bazar Sadar");
        coxBazar.setDescription("The world's longest natural sea beach stretching 120km of golden sand. Gentle waves meet endless shoreline lined with swaying palm trees. Spectacular sunrises paint the Bay of Bengal in vibrant colors.");
        coxBazar.setEntryCost(0.0);
        coxBazar.setBestTime("November-March");
        coxBazar.setWhyVisit("Walk the infinite beach, watch fishermen at dawn, taste fresh seafood, and witness the most breathtaking sunrise over the Bay of Bengal. A paradise for beach lovers and photographers.");
        coxBazar.setImagePath("chittagong/coxbazar1.jpg");
        coxBazar.setTags("beach, longest beach, sunrise, seafood, photography, family-friendly, romantic, world record, instagrammable");
        coxBazar.setNatureType("Sea Beach");
        coxBazar.setAreaSize("120 km");
        coxBazar.setActivities("swimming, sunbathing, surfing, beach cricket, seafood tasting, photography, horse riding");
        coxBazar.setConservationStatus("Tourist Zone");
        coxBazar.setImage1("chittagong/coxbazar2.jpg");
        coxBazar.setImage2("chittagong/coxbazar3.jpg");
        coxBazar.setImage3("chittagong/coxbazar4.jpg");
        coxBazar.setImage4("chittagong/coxbazar5.jpg");
        // coxBazar.setImage5("chittagong/cox_bazar/5.jpg");
        placeDAO.insertNaturePlace(coxBazar);

        // 3. St. Martin's Island
        NaturePlace stMartin = new NaturePlace("CTG-NAT-002", "St. Martin's Island", "Chittagong", "Cox's Bazar");
        stMartin.setAddress("Teknaf, Cox's Bazar (ferry required)");
        stMartin.setDescription("Bangladesh's only coral island. Crystal-clear turquoise waters surround coconut-fringed shores. Unique marine life, coral colonies, and a laid-back island vibe make this a tropical paradise.");
        stMartin.setEntryCost(0.0);
        stMartin.setBestTime("November-February (permits required Dec-Jan)");
        stMartin.setWhyVisit("Snorkel in pristine waters, walk on coral beaches, and escape to Bangladesh's own tropical island. Perfect for honeymooners, nature lovers, and anyone seeking paradise.");
        stMartin.setImagePath("chittagong/stmartin1.jpg");
        stMartin.setTags("island, coral, snorkeling, beach, honeymoon, marine life, turquoise water, exotic, instagrammable, paradise");
        stMartin.setNatureType("Coral Island");
        stMartin.setAreaSize("8 sq km");
        stMartin.setActivities("snorkeling, beach walking, boat trips, coral viewing, seafood dining, photography");
        stMartin.setConservationStatus("Eco-Sensitive Zone");
        stMartin.setImage1("chittagong/stmartin2.jpg");
        stMartin.setImage2("chittagong/stmartin3.jpg");
        stMartin.setImage3("chittagong/stmartin4.jpg");
        stMartin.setImage4("chittagong/stmartin5.jpg");
        stMartin.setImage5("chittagong/stmartin6.jpg");
        placeDAO.insertNaturePlace(stMartin);

        // 4. Keokradong
        NaturePlace keokradong = new NaturePlace("CTG-NAT-003", "Keokradong", "Chittagong", "Bandarban");
        keokradong.setAddress("Ruma, Bandarban (trek required)");
        keokradong.setDescription("Bangladesh's third highest peak at 986m. A challenging trek through dense forests leads to panoramic views above the clouds. The summit offers a 360° view of rolling hills and Myanmar borders.");
        keokradong.setEntryCost(0.0);
        keokradong.setBestTime("November-March");
        keokradong.setWhyVisit("Conquer one of Bangladesh's highest peaks and stand above the clouds. An adventurous trek through indigenous villages and pristine forests rewards you with unforgettable mountain views.");
        keokradong.setImagePath("chittagong/keokradong1.jpg");
        keokradong.setTags("mountain, trekking, adventure, peak, clouds, hiking, sunrise, challenging, panoramic, instagrammable");
        keokradong.setNatureType("Mountain Peak");
        keokradong.setAreaSize("986 m elevation");
        keokradong.setActivities("trekking, camping, photography, cloud watching, sunrise viewing");
        keokradong.setConservationStatus("Protected Forest");
        keokradong.setImage1("chittagong/keokradong2.jpg");
        keokradong.setImage2("chittagong/keokradong3.jpg");
        keokradong.setImage3("chittagong/keokradong4.jpg");
        keokradong.setImage4("chittagong/keokradong5.jpg");
        keokradong.setImage5("chittagong/keokradong6.jpg");
        placeDAO.insertNaturePlace(keokradong);

        // 5. Nilgiri
        NaturePlace nilgiri = new NaturePlace("CTG-NAT-004", "Nilgiri", "Chittagong", "Bandarban");
        nilgiri.setAddress("Thanchi Road, Bandarban");
        nilgiri.setDescription("Bangladesh's highest hill resort at 2,300 feet. Managed by the army, this spot offers breathtaking panoramic views of cloud-kissed hills stretching to the horizon. On clear days, glimpse the Bay of Bengal.");
        nilgiri.setEntryCost(100.0);
        nilgiri.setBestTime("October-March");
        nilgiri.setWhyVisit("Wake up surrounded by clouds at Bangladesh's premier hill resort. Perfect for luxury travelers seeking comfort with nature - enjoy sunrise over endless hills without the difficult trek.");
        nilgiri.setImagePath("chittagong/nilgiri1.jpg");
        nilgiri.setTags("hill resort, clouds, sunrise, luxury, panoramic, army-managed, family-friendly, honeymoon, instagrammable");
        nilgiri.setNatureType("Hill Resort");
        nilgiri.setAreaSize("2,300 ft elevation");
        nilgiri.setActivities("sightseeing, photography, sunrise viewing, relaxing, nature walks");
        nilgiri.setConservationStatus("Managed Resort");
        nilgiri.setImage1("chittagong/nilgiri2.jpg");
        nilgiri.setImage2("chittagong/nilgiri3.jpg");
        nilgiri.setImage3("chittagong/nilgiri4.jpg");
        nilgiri.setImage4("chittagong/nilgiri5.jpg");
        nilgiri.setImage5("chittagong/nilgiri6.jpg");
        placeDAO.insertNaturePlace(nilgiri);

        // 6. Nafakhum Waterfall
        NaturePlace nafakhum = new NaturePlace("CTG-NAT-005", "Nafakhum Waterfall", "Chittagong", "Bandarban");
        nafakhum.setAddress("Thanchi, Bandarban (trek required)");
        nafakhum.setDescription("The widest and most powerful waterfall in Bangladesh. The Remaikree River plunges 8 meters but spans an impressive width, creating a thunderous spectacle. Surrounded by pristine jungle and tribal villages.");
        nafakhum.setEntryCost(0.0);
        nafakhum.setBestTime("July-October (peak flow), November-March (accessible)");
        nafakhum.setWhyVisit("Witness the raw power of Bangladesh's widest waterfall. The adventurous trek through the Sangu Valley and the thunderous roar of falling water creates an unforgettable wilderness experience.");
        nafakhum.setImagePath("chittagong/nafakhum1.jpg");
        nafakhum.setTags("waterfall, trekking, adventure, jungle, powerful, hidden gem, photography, wild, challenging");
        nafakhum.setNatureType("Waterfall");
        nafakhum.setAreaSize("8 m height, 30+ m width");
        nafakhum.setActivities("trekking, swimming, photography, jungle exploration");
        nafakhum.setConservationStatus("Forest Reserve");
        nafakhum.setImage1("chittagong/nafakhum2.jpg");
        nafakhum.setImage2("chittagong/nafakhum3.jpg");
        nafakhum.setImage3("chittagong/nafakhum4.jpg");
        nafakhum.setImage4("chittagong/nafakhum5.jpg");
        //nafakhum.setImage5("chittagong/nafakhum/5.jpg");
        placeDAO.insertNaturePlace(nafakhum);

        // 7. Kaptai Lake
        NaturePlace kaptai = new NaturePlace("CTG-NAT-006", "Kaptai Lake", "Chittagong", "Rangamati");
        kaptai.setAddress("Kaptai, Rangamati");
        kaptai.setDescription("Bangladesh's largest man-made lake, spanning 655 sq km. Emerald hills rise from crystal waters dotted with islands. Tribal villages, Buddhist temples, and floating markets line the shores.");
        kaptai.setEntryCost(0.0);
        kaptai.setBestTime("October-March");
        kaptai.setWhyVisit("Cruise through a stunning lake surrounded by hills. Visit the hanging bridge, explore indigenous villages, and witness the unique lifestyle of communities living on this vast waterbody.");
        kaptai.setImagePath("chittagong/kaptai1.jpg");
        kaptai.setTags("lake, boat ride, hills, tribal culture, hanging bridge, photography, serene, largest lake, family-friendly");
        kaptai.setNatureType("Freshwater Lake (Man-made)");
        kaptai.setAreaSize("655 sq km");
        kaptai.setActivities("boating, fishing, island hopping, cultural visits, photography");
        kaptai.setConservationStatus("Reservoir");
        kaptai.setImage1("chittagong/kaptai2.jpg");
        kaptai.setImage2("chittagong/kaptai3.jpg");
        kaptai.setImage3("chittagong/kaptai4.jpg");
        kaptai.setImage4("chittagong/kaptai5.jpg");
        //kaptai.setImage5("chittagong_division/kaptai_lake/5.jpg");
        placeDAO.insertNaturePlace(kaptai);

        // 8. Shuvolong Waterfall
        NaturePlace shuvolong = new NaturePlace("CTG-NAT-007", "Shuvolong Waterfall", "Chittagong", "Rangamati");
        shuvolong.setAddress("Kaptai Lake, Rangamati (boat access)");
        shuvolong.setDescription("A stunning waterfall cascading directly into Kaptai Lake. Accessible only by boat, this spot offers a refreshing swim where freshwater meets the lake. Surrounded by towering hills and dense jungle.");
        shuvolong.setEntryCost(0.0);
        shuvolong.setBestTime("July-October (peak flow)");
        shuvolong.setWhyVisit("Experience the unique thrill of swimming at the base of a waterfall that flows directly into a lake. A perfect refreshing stop during your Kaptai Lake cruise.");
        shuvolong.setImagePath("chittagong/shuvolong1.jpg");
        shuvolong.setTags("waterfall, swimming, boat ride, refreshing, hidden gem, photography, monsoon, adventure");
        shuvolong.setNatureType("Waterfall");
        shuvolong.setAreaSize("N/A");
        shuvolong.setActivities("swimming, photography, boat touring");
        shuvolong.setConservationStatus("N/A");
        shuvolong.setImage1("chittagong/shuvolong2.jpg");
        shuvolong.setImage2("chittagong/shuvolong3.jpg");
        shuvolong.setImage3("chittagong/shuvolong4.jpg");
        // shuvolong.setImage4("chittagong/shuvolong/4.jpg");
        // shuvolong.setImage5("chittagong_division/shuvolong/5.jpg");
        placeDAO.insertNaturePlace(shuvolong);

        // 9. Patenga Beach
        NaturePlace patenga = new NaturePlace("CTG-NAT-008", "Patenga Beach", "Chittagong", "Chittagong");
        patenga.setAddress("15 km from Chittagong city");
        patenga.setDescription("City beach at the confluence of Karnaphuli River and Bay of Bengal. Watch massive ships enter the port while enjoying sea breeze. Famous for sunset views, street food, and the nearby butterfly park.");
        patenga.setEntryCost(0.0);
        patenga.setBestTime("November-March (afternoons for sunset)");
        patenga.setWhyVisit("Experience a unique beach where river meets sea, with giant ships as your backdrop. Perfect for sunset lovers, foodies wanting fresh crab, and photographers seeking contrast of industry and nature.");
        patenga.setImagePath("chittagong/patenga1.jpg");
        patenga.setTags("beach, sunset, city beach, street food, seaport, ships, butterfly park, family-friendly, photography");
        patenga.setNatureType("Sea Beach");
        patenga.setAreaSize("N/A");
        patenga.setActivities("sunset watching, street food tasting, photography, walking, butterfly park visit");
        patenga.setConservationStatus("Public Beach");
        patenga.setImage1("chittagong/patenga2.jpg");
        patenga.setImage2("chittagong/patenga3.jpg");
        patenga.setImage3("chittagong/patenga4.jpg");
        patenga.setImage4("chittagong/patenga5.jpg");
        //patenga.setImage5("chittagong_division/patenga/5.jpg");
        placeDAO.insertNaturePlace(patenga);

        // 10. Sajek Valley
        NaturePlace sajek = new NaturePlace("CTG-NAT-009", "Sajek Valley", "Chittagong", "Rangamati");
        sajek.setAddress("Baghaichhari, Rangamati");
        sajek.setDescription("The 'Queen of Hills' where clouds float below your feet. This valley offers stunning sunrise views above a sea of clouds. Indigenous communities, winding mountain roads, and lush green hills create a magical escape.");
        sajek.setEntryCost(0.0);
        sajek.setBestTime("October-March");
        sajek.setWhyVisit("Wake up above the clouds in Bangladesh's most beautiful valley. Experience the thrill of winding mountain roads, interact with indigenous communities, and photograph landscapes that look like paintings.");
        sajek.setImagePath("chittagong/sajek1.jpg");
        sajek.setTags("valley, clouds, sunrise, hill station, indigenous culture, winding roads, photography, honeymoon, instagrammable, magical");
        sajek.setNatureType("Hill Valley");
        sajek.setAreaSize("1,800 ft elevation");
        sajek.setActivities("sunrise viewing, photography, cultural visits, trekking, cloud watching");
        sajek.setConservationStatus("Tourist Zone");
        sajek.setImage1("chittagong/sajek2.jpg");
        sajek.setImage2("chittagong/sajek3.jpg");
        sajek.setImage3("chittagong/sajek4.jpg");
        sajek.setImage4("chittagong/sajek5.jpg");
        sajek.setImage5("chittagong/sajek6.jpg");
        placeDAO.insertNaturePlace(sajek);

        NaturePlace sitakunda = new NaturePlace("CTG-NAT-010", "Sitakunda Eco Park", "Chittagong", "Sitakunda");
        sitakunda.setAddress("Sitakunda, Chittagong");
        sitakunda.setDescription("A 996-acre forest sanctuary with hanging bridges, serene lakes, and trekking trails. Home to the Suptadhara waterfall and rich biodiversity. Perfect nature escape just an hour from Chittagong city.");
        sitakunda.setEntryCost(30.0);
        sitakunda.setBestTime("October-March");
        sitakunda.setWhyVisit("Escape the city into a green paradise. Walk across hanging bridges, boat on peaceful lakes, and trek to hidden waterfalls. Perfect for families, nature lovers, and photographers seeking quick nature fix.");
        sitakunda.setImagePath("chittagong/ecopark1.jpg");
        sitakunda.setTags("eco park, forest, hanging bridge, lake, trekking, waterfall, bird watching, family-friendly, nature escape");
        sitakunda.setNatureType("Eco Park");
        sitakunda.setAreaSize("996 acres");
        sitakunda.setActivities("walking, boating, trekking, bird watching, photography, picnicking");
        sitakunda.setConservationStatus("Protected Eco Park");
        sitakunda.setImage1("chittagong/ecopark2.jpg");
        sitakunda.setImage2("chittagong/ecopark3.jpg");
        sitakunda.setImage3("chittagong/ecopark4.jpg");
        sitakunda.setImage4("chittagong/ecopark5.jpg");
        // sitakunda.setImage5("chittagong_division/sitakunda_eco_park/5.jpg");
        placeDAO.insertNaturePlace(sitakunda);
    }

    private static void addEntertainmentPlaces() {

        // 12. Biju Festival (Cultural Festival)
//        ManmadePlace bijuFestival = new ManmadePlace("CTG-ENT-001", "Biju Festival", "Chittagong", "Rangamati/Bandarban/Khagrachari");
//        bijuFestival.setAddress("All three hill districts");
//        bijuFestival.setDescription("The grandest festival of Chakma and Marma communities celebrating the Bengali New Year. Three days of colorful processions, traditional dances (Rheng), songs, and community feasts. A vibrant display of indigenous culture.");
//        bijuFestival.setEntryCost(0.0);
//        bijuFestival.setBestTime("April 13-15");
//        bijuFestival.setWhyVisit("Immerse yourself in the rich indigenous culture of the Hill Tracts. Witness colorful traditional attire, rhythmic dances, and join the joyful celebration of new beginnings with local communities.");
//        bijuFestival.setImagePath("chittagong_division/biju_festival/main.jpg");
//        bijuFestival.setTags("festival, cultural, indigenous, traditional dance, colorful, photography, cultural immersion, new year, chakma, marma");
//        bijuFestival.setEntertainmentType("Cultural Festival");
//        bijuFestival.setOpeningHours("All day during festival");
//        bijuFestival.setSpecialEvents("Traditional dances, processions, community feasts, cultural performances");
//        bijuFestival.setAverageSpending(500.0);
//        bijuFestival.setContactInfo("Local tourism offices in hill districts");
//        bijuFestival.setHasEntryFee(false);
//        bijuFestival.setImage1("chittagong_division/biju_festival/1.jpg");
//        bijuFestival.setImage2("chittagong_division/biju_festival/2.jpg");
//        bijuFestival.setImage3("chittagong_division/biju_festival/3.jpg");
//        bijuFestival.setImage4("chittagong_division/biju_festival/4.jpg");
//        bijuFestival.setImage5("chittagong_division/biju_festival/5.jpg");
//        placeDAO.insertEntertainmentPlace(bijuFestival);
    }
}
