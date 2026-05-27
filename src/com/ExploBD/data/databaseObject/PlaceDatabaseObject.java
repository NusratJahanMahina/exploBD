package com.ExploBD.data.databaseObject;

import com.ExploBD.data.DatabaseConnection;
import com.ExploBD.object.HistoricalPlace;
import com.ExploBD.object.ManmadePlace;
import com.ExploBD.object.NaturePlace;
import com.ExploBD.object.Place;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaceDatabaseObject {

    public PlaceDatabaseObject() {
    }

    public List<Place> findAllByDivision(String divisionName) {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE division = ? ORDER BY name";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, divisionName);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Place place = createPlaceFromResultSet(rs, conn);
                if (place != null) {
                    places.add(place);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return places;
    }

    public List<HistoricalPlace> findHistoricalByDivision(String divisionName) {
        List<HistoricalPlace> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE division = ? AND category = 'HISTORICAL' ORDER BY name";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, divisionName);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Place place = createPlaceFromResultSet(rs, conn);
                if (place instanceof HistoricalPlace) {
                    places.add((HistoricalPlace) place);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return places;
    }

    public List<NaturePlace> findNatureByDivision(String divisionName) {
        List<NaturePlace> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE division = ? AND category = 'NATURE' ORDER BY name";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, divisionName);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Place place = createPlaceFromResultSet(rs, conn);
                if (place instanceof NaturePlace) {
                    places.add((NaturePlace) place);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return places;
    }

    public List<ManmadePlace> findEntertainmentByDivision(String divisionName) {
        List<ManmadePlace> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE division = ? AND category = 'ENTERTAINMENT' ORDER BY name";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, divisionName);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Place place = createPlaceFromResultSet(rs, conn);
                if (place instanceof ManmadePlace) {
                    places.add((ManmadePlace) place);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return places;
    }

    public Place findById(String placeId) {
        String sql = "SELECT * FROM places WHERE place_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placeId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createPlaceFromResultSet(rs, conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Place createPlaceFromResultSet(ResultSet rs, Connection conn) throws SQLException {
        String category = rs.getString("category");
        String id = rs.getString("place_id");
        String name = rs.getString("name");
        String division = rs.getString("division");
        String district = rs.getString("district");

        Place place = null;

        if ("HISTORICAL".equals(category)) {
            HistoricalPlace hp = new HistoricalPlace(id, name, division, district);
            loadHistoricalDetails(hp, conn);
            place = hp;
        } else if ("NATURE".equals(category)) {
            NaturePlace np = new NaturePlace(id, name, division, district);
            loadNatureDetails(np, conn);
            place = np;
        } else if ("ENTERTAINMENT".equals(category)) {
            ManmadePlace mp = new ManmadePlace(id, name, division, district);
            loadEntertainmentDetails(mp, conn);
            place = mp;
        }

        if (place != null) {
            setCommonPlaceFields(place, rs);
        }

        return place;
    }

    private void loadHistoricalDetails(HistoricalPlace place, Connection conn) throws SQLException {
        String sql = "SELECT * FROM historical_places WHERE place_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, place.getId());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                place.setYearBuilt(rs.getString("year_built"));
                place.setTimePeriod(rs.getString("time_period"));
                place.setArchitectureStyle(rs.getString("architecture_style"));
                place.setHistoricalFacts(rs.getString("historical_facts"));
                place.setUNESCO(rs.getBoolean("is_unesco"));
                
                place.setImage1(rs.getString("image1")); 
                place.setImage2(rs.getString("image2")); 
                place.setImage3(rs.getString("image3")); 
                place.setImage4(rs.getString("image4")); 
                place.setImage5(rs.getString("image5")); 
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void loadNatureDetails(NaturePlace place, Connection conn) throws SQLException {
        String sql = "SELECT * FROM nature_places WHERE place_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, place.getId());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                place.setNatureType(rs.getString("nature_type"));
                place.setAreaSize(rs.getString("area_size"));
                place.setActivities(rs.getString("activities"));
                place.setConservationStatus(rs.getString("conservation_status"));
                
                place.setImage1(rs.getString("image1")); 
                place.setImage2(rs.getString("image2")); 
                place.setImage3(rs.getString("image3")); 
                place.setImage4(rs.getString("image4")); 
                place.setImage5(rs.getString("image5")); 
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void loadEntertainmentDetails(ManmadePlace place, Connection conn) throws SQLException {
        String sql = "SELECT * FROM entertainment_places WHERE place_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, place.getId());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                place.setEntertainmentType(rs.getString("entertainment_type"));
                place.setOpeningHours(rs.getString("opening_hours"));
                place.setSpecialEvents(rs.getString("special_events"));
                place.setAverageSpending(rs.getDouble("average_spending"));
                place.setContactInfo(rs.getString("contact_info"));
                place.setHasEntryFee(rs.getBoolean("has_entry_fee"));
                
                place.setImage1(rs.getString("image1")); 
                place.setImage2(rs.getString("image2")); 
                place.setImage3(rs.getString("image3")); 
                place.setImage4(rs.getString("image4")); 
                place.setImage5(rs.getString("image5")); 
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void setCommonPlaceFields(Place place, ResultSet rs) throws SQLException {
        place.setAddress(rs.getString("address"));
        place.setDescription(rs.getString("description"));
        place.setEntryCost(rs.getDouble("entry_cost"));
        place.setBestTime(rs.getString("best_time"));
        place.setWhyVisit(rs.getString("why_visit"));
        place.setImagePath(rs.getString("image_path")); 
        place.setTags(rs.getString("tags"));
    }

    public boolean addToWishlist(String userId, String placeId) {
        return addPreference(userId, placeId, "WISHLIST");
    }

    public boolean addToFavorite(String userId, String placeId) {
        return addPreference(userId, placeId, "FAVORITE");
    }

    public boolean removeFromWishlist(String userId, String placeId) {
        return removePreference(userId, placeId, "WISHLIST");
    }

    public boolean removeFromFavorite(String userId, String placeId) {
        return removePreference(userId, placeId, "FAVORITE");
    }

    public boolean isInWishlist(String userId, String placeId) {
        return hasPreference(userId, placeId, "WISHLIST");
    }

    public boolean isFavorite(String userId, String placeId) {
        return hasPreference(userId, placeId, "FAVORITE");
    }

    public List<Place> getUserWishlist(String userId) {
        return getUserPreferences(userId, "WISHLIST");
    }

    public List<Place> getUserFavorites(String userId) {
        return getUserPreferences(userId, "FAVORITE");
    }

    private boolean addPreference(String userId, String placeId, String preferenceType) {
        String columnName = preferenceType.equals("FAVORITE") ? "favorite_places" : "wishlist_places";
        Connection conn = null;
        PreparedStatement prefStmt = null;
        PreparedStatement getStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String prefSql = "INSERT OR IGNORE INTO user_preferences (user_id, place_id, preference_type) VALUES (?, ?, ?)";
            prefStmt = conn.prepareStatement(prefSql);
            prefStmt.setString(1, userId);
            prefStmt.setString(2, placeId);
            prefStmt.setString(3, preferenceType);
            prefStmt.executeUpdate();

            String currentValue = "";
            String getSql = "SELECT " + columnName + " FROM users WHERE user_id = ?";
            getStmt = conn.prepareStatement(getSql);
            getStmt.setString(1, userId);
            rs = getStmt.executeQuery();
            if (rs.next()) {
                currentValue = rs.getString(columnName);
                if (currentValue == null) {
                    currentValue = "";
                }
            }

            String newValue;
            if (currentValue.isEmpty()) {
                newValue = placeId;
            } else if (!currentValue.contains(placeId)) {
                newValue = currentValue + "," + placeId;
            } else {
                conn.commit();
                return true;
            }

            String updateSql = "UPDATE users SET " + columnName + " = ?, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?";
            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newValue.isEmpty() ? null : newValue);
            updateStmt.setString(2, userId);
            updateStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (prefStmt != null) prefStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (getStmt != null) getStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean removePreference(String userId, String placeId, String preferenceType) {
        String columnName = preferenceType.equals("FAVORITE") ? "favorite_places" : "wishlist_places";
        Connection conn = null;
        PreparedStatement prefStmt = null;
        PreparedStatement getStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String prefSql = "DELETE FROM user_preferences WHERE user_id = ? AND place_id = ? AND preference_type = ?";
            prefStmt = conn.prepareStatement(prefSql);
            prefStmt.setString(1, userId);
            prefStmt.setString(2, placeId);
            prefStmt.setString(3, preferenceType);
            prefStmt.executeUpdate();

            String currentValue = "";
            String getSql = "SELECT " + columnName + " FROM users WHERE user_id = ?";
            getStmt = conn.prepareStatement(getSql);
            getStmt.setString(1, userId);
            rs = getStmt.executeQuery();
            if (rs.next()) {
                currentValue = rs.getString(columnName);
                if (currentValue == null) {
                    currentValue = "";
                }
            }

            if (currentValue.isEmpty() || !currentValue.contains(placeId)) {
                conn.commit();
                return true;
            }

            String[] places = currentValue.split(",");
            StringBuilder sb = new StringBuilder();
            for (String place : places) {
                if (!place.trim().equals(placeId) && !place.trim().isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(place.trim());
                }
            }
            String newValue = sb.toString();

            String updateSql = "UPDATE users SET " + columnName + " = ?, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?";
            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newValue.isEmpty() ? null : newValue);
            updateStmt.setString(2, userId);
            updateStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (prefStmt != null) prefStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (getStmt != null) getStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean hasPreference(String userId, String placeId, String preferenceType) {
        String sql = "SELECT COUNT(*) FROM user_preferences WHERE user_id = ? AND place_id = ? AND preference_type = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, placeId);
            pstmt.setString(3, preferenceType);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private List<Place> getUserPreferences(String userId, String preferenceType) {
        List<Place> places = new ArrayList<>();
        String sql = """
            SELECT p.* FROM places p
            JOIN user_preferences up ON p.place_id = up.place_id
            WHERE up.user_id = ? AND up.preference_type = ?
            ORDER BY up.added_at DESC
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, preferenceType);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Place place = createPlaceFromResultSet(rs, conn);
                if (place != null) {
                    places.add(place);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return places;
    }

    public List<String> getFavoritePlaceIds(String userId) {
        List<String> placeIds = new ArrayList<>();
        String sql = "SELECT favorite_places FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String placesStr = rs.getString("favorite_places");
                if (placesStr != null && !placesStr.isEmpty()) {
                    String[] ids = placesStr.split(",");
                    for (String id : ids) {
                        String trimmed = id.trim();
                        if (!trimmed.isEmpty()) {
                            placeIds.add(trimmed);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return placeIds;
    }

    public List<String> getWishlistPlaceIds(String userId) {
        List<String> placeIds = new ArrayList<>();
        String sql = "SELECT wishlist_places FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String placesStr = rs.getString("wishlist_places");
                if (placesStr != null && !placesStr.isEmpty()) {
                    String[] ids = placesStr.split(",");
                    for (String id : ids) {
                        String trimmed = id.trim();
                        if (!trimmed.isEmpty()) {
                            placeIds.add(trimmed);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return placeIds;
    }

    public boolean insertHistoricalPlace(HistoricalPlace place) {
        return insertPlaceCommon(place, "HISTORICAL") && insertHistoricalDetails(place);
    }

    public boolean insertNaturePlace(NaturePlace place) {
        return insertPlaceCommon(place, "NATURE") && insertNatureDetails(place);
    }

    public boolean insertEntertainmentPlace(ManmadePlace place) {
        return insertPlaceCommon(place, "ENTERTAINMENT") && insertEntertainmentDetails(place);
    }

    private boolean insertPlaceCommon(Place place, String category) {
        String sql = """
            INSERT INTO places (place_id, name, division, district, category,
                              address, description, entry_cost, best_time, 
                              why_visit, image_path, tags)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getId());
            pstmt.setString(2, place.getName());
            pstmt.setString(3, place.getDivision());
            pstmt.setString(4, place.getDistrict());
            pstmt.setString(5, category);
            pstmt.setString(6, place.getAddress());
            pstmt.setString(7, place.getDescription());
            pstmt.setDouble(8, place.getEntryCost());
            pstmt.setString(9, place.getBestTime());
            pstmt.setString(10, place.getWhyVisit());
            pstmt.setString(11, place.getImagePath()); 
            pstmt.setString(12, place.getTags());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean insertHistoricalDetails(HistoricalPlace place) {
        String sql = """
            INSERT INTO historical_places (place_id, year_built, time_period,
                                          architecture_style, historical_facts, is_unesco,
                                          image1, image2, image3, image4, image5)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getId());
            pstmt.setString(2, place.getYearBuilt());
            pstmt.setString(3, place.getTimePeriod());
            pstmt.setString(4, place.getArchitectureStyle());
            pstmt.setString(5, place.getHistoricalFacts());
            pstmt.setBoolean(6, place.isUNESCO());
            pstmt.setString(7, place.getImage1()); 
            pstmt.setString(8, place.getImage2()); 
            pstmt.setString(9, place.getImage3()); 
            pstmt.setString(10, place.getImage4()); 
            pstmt.setString(11, place.getImage5()); 

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean insertNatureDetails(NaturePlace place) {
        String sql = """
            INSERT INTO nature_places (place_id, nature_type, area_size,
                                      activities, conservation_status, 
                                      image1, image2, image3, image4, image5)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getId());
            pstmt.setString(2, place.getNatureType());
            pstmt.setString(3, place.getAreaSize());
            pstmt.setString(4, place.getActivities());
            pstmt.setString(5, place.getConservationStatus());
            pstmt.setString(6, place.getImage1()); 
            pstmt.setString(7, place.getImage2()); 
            pstmt.setString(8, place.getImage3()); 
            pstmt.setString(9, place.getImage4()); 
            pstmt.setString(10, place.getImage5()); 

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean insertEntertainmentDetails(ManmadePlace place) {
        String sql = """
            INSERT INTO entertainment_places (place_id, entertainment_type, opening_hours,
                                             special_events, average_spending, contact_info, has_entry_fee,
                                             image1, image2, image3, image4, image5)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getId());
            pstmt.setString(2, place.getEntertainmentType());
            pstmt.setString(3, place.getOpeningHours());
            pstmt.setString(4, place.getSpecialEvents());
            pstmt.setDouble(5, place.getAverageSpending());
            pstmt.setString(6, place.getContactInfo());
            pstmt.setBoolean(7, place.hasEntryFee());
            pstmt.setString(8, place.getImage1()); 
            pstmt.setString(9, place.getImage2()); 
            pstmt.setString(10, place.getImage3()); 
            pstmt.setString(11, place.getImage4()); 
            pstmt.setString(12, place.getImage5()); 

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    
    public boolean updateHistoricalPlace(HistoricalPlace place) {
        return updatePlaceCommon(place) && updateHistoricalDetails(place);
    }

    public boolean updateNaturePlace(NaturePlace place) {
        return updatePlaceCommon(place) && updateNatureDetails(place);
    }

    public boolean updateEntertainmentPlace(ManmadePlace place) {
        return updatePlaceCommon(place) && updateEntertainmentDetails(place);
    }

    private boolean updatePlaceCommon(Place place) {
        String sql = """
            UPDATE places SET 
                name = ?, division = ?, district = ?,
                address = ?, description = ?, entry_cost = ?,
                best_time = ?, why_visit = ?, image_path = ?, tags = ?
            WHERE place_id = ?
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getName());
            pstmt.setString(2, place.getDivision());
            pstmt.setString(3, place.getDistrict());
            pstmt.setString(4, place.getAddress());
            pstmt.setString(5, place.getDescription());
            pstmt.setDouble(6, place.getEntryCost());
            pstmt.setString(7, place.getBestTime());
            pstmt.setString(8, place.getWhyVisit());
            pstmt.setString(9, place.getImagePath());
            pstmt.setString(10, place.getTags());
            pstmt.setString(11, place.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean updateHistoricalDetails(HistoricalPlace place) {
        String sql = """
            UPDATE historical_places SET
                year_built = ?, time_period = ?, architecture_style = ?,
                historical_facts = ?, is_unesco = ?,
                image1 = ?, image2 = ?, image3 = ?, image4 = ?, image5 = ?
            WHERE place_id = ?
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getYearBuilt());
            pstmt.setString(2, place.getTimePeriod());
            pstmt.setString(3, place.getArchitectureStyle());
            pstmt.setString(4, place.getHistoricalFacts());
            pstmt.setBoolean(5, place.isUNESCO());
            pstmt.setString(6, place.getImage1());
            pstmt.setString(7, place.getImage2());
            pstmt.setString(8, place.getImage3());
            pstmt.setString(9, place.getImage4());
            pstmt.setString(10, place.getImage5());
            pstmt.setString(11, place.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean updateNatureDetails(NaturePlace place) {
        String sql = """
            UPDATE nature_places SET
                nature_type = ?, area_size = ?, activities = ?,
                conservation_status = ?,
                image1 = ?, image2 = ?, image3 = ?, image4 = ?, image5 = ?
            WHERE place_id = ?
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getNatureType());
            pstmt.setString(2, place.getAreaSize());
            pstmt.setString(3, place.getActivities());
            pstmt.setString(4, place.getConservationStatus());
            pstmt.setString(5, place.getImage1());
            pstmt.setString(6, place.getImage2());
            pstmt.setString(7, place.getImage3());
            pstmt.setString(8, place.getImage4());
            pstmt.setString(9, place.getImage5());
            pstmt.setString(10, place.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean updateEntertainmentDetails(ManmadePlace place) {
        String sql = """
            UPDATE entertainment_places SET
                entertainment_type = ?, opening_hours = ?, special_events = ?,
                average_spending = ?, contact_info = ?, has_entry_fee = ?,
                image1 = ?, image2 = ?, image3 = ?, image4 = ?, image5 = ?
            WHERE place_id = ?
            """;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, place.getEntertainmentType());
            pstmt.setString(2, place.getOpeningHours());
            pstmt.setString(3, place.getSpecialEvents());
            pstmt.setDouble(4, place.getAverageSpending());
            pstmt.setString(5, place.getContactInfo());
            pstmt.setBoolean(6, place.hasEntryFee());
            pstmt.setString(7, place.getImage1());
            pstmt.setString(8, place.getImage2());
            pstmt.setString(9, place.getImage3());
            pstmt.setString(10, place.getImage4());
            pstmt.setString(11, place.getImage5());
            pstmt.setString(12, place.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
       //Nazifa
//    public static ArrayList<Place> getAllPlaces() {
//   
//
//    ArrayList<Place> places = new ArrayList<>();
//
//    try (Connection conn = DatabaseConnection.getConnection()) {
//
//        String sql = "SELECT * FROM places ORDER BY name";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        ResultSet rs = pstmt.executeQuery();
//
//        while (rs.next()) {
//            Place place = createPlaceFromResultSet(rs, conn);
//            if (place != null) {
//                places.add(place);
//            }
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//
//    return places;
//}
    
    //Nazifa
public static ArrayList<Place> getAllPlaces() {
    ArrayList<Place> places = new ArrayList<>();

    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "SELECT * FROM places ORDER BY name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        // Create instance to access non-static method
        PlaceDatabaseObject dao = new PlaceDatabaseObject();
        
        while (rs.next()) {
            Place place = dao.createPlaceFromResultSet(rs, conn);
            if (place != null) {
                places.add(place);
            }
        }

        rs.close();
        pstmt.close();
        
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return places;
}
    



    
}