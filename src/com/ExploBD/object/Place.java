//package com.ExploBD.object;
//
//public abstract class Place extends ExploBDObject {
//
//    private String division;
//    private String district;
//    private String address;
//    private String description;
//    private double entryCost;
//    private String bestTime;
//    private String whyVisit;
//    private String imagePath;
//    private String tags;
//
//    protected Place(String placeId, String name, String division, String district) {
//        super(placeId, name);
//        this.division = division;
//        this.district = district;
//    }
//    
//    
//    
//    //nazifa
//  public Place() {
//        super("unknown", "unknown");
//    }
//    
//  
//  
//  
//  
//  
//  
//  
//  
//  
//  
//    @Override
//    public abstract String getObjectType();
//
//    public abstract String getPlaceCategory();
//
//    public abstract String getPlaceSpecificDetails();
//
//    public String getDivision() {
//        return division;
//    }
//
//    public String getDistrict() {
//        return district;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public double getEntryCost() {
//        return entryCost;
//    }
//
//    public String getBestTime() {
//        return bestTime;
//    }
//
//    public String getWhyVisit() {
//        return whyVisit;
//    }
//
//    public String getImagePath() {
//        return imagePath;
//    }
//
//    public String getTags() {
//        return tags;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public void setEntryCost(double entryCost) {
//        this.entryCost = entryCost;
//    }
//
//    public void setBestTime(String bestTime) {
//        this.bestTime = bestTime;
//    }
//
//    public void setWhyVisit(String whyVisit) {
//        this.whyVisit = whyVisit;
//    }
//
//    public void setImagePath(String imagePath) {
//        this.imagePath = imagePath;
//    }
//
//    public void setTags(String tags) {
//        this.tags = tags;
//    }
//
//    public String getLocation() {
//        return division + " -> " + district;
//    }
//
//    public boolean hasTag(String tag) {
//        return tags != null && tags.toLowerCase().contains(tag.toLowerCase());
//    }
//
//    public final String getFullInfo() {
//        StringBuilder info = new StringBuilder();
//        info.append(" ").append(getName()).append("\n");
//        info.append("🏷️ Type: ").append(getObjectType()).append("\n");
//        info.append(" Category: ").append(getPlaceCategory()).append("\n");
//        info.append(" Location: ").append(getLocation()).append("\n");
//
//        if (address != null && !address.isEmpty()) {
//            info.append(" Address: ").append(address).append("\n");
//        }
//
//        info.append(" Entry Cost: ").append(entryCost).append("\n");
//        info.append(" Best Time: ").append(bestTime).append("\n");
//
//        if (description != null && !description.isEmpty()) {
//            info.append("Description: ").append(description).append("\n");
//        }
//
//        if (whyVisit != null && !whyVisit.isEmpty()) {
//            info.append("Why Visit: ").append(whyVisit).append("\n");
//        }
//
//        info.append(getPlaceSpecificDetails());
//
//        if (tags != null && !tags.isEmpty()) {
//            info.append("🏷️ Tags: ").append(tags);
//        }
//
//        return info.toString();
//    }
//}
package com.ExploBD.object;

public abstract class Place extends ExploBDObject {

    private String division;
    private String district;
    private String address;
    private String description;
    private double entryCost;
    private String bestTime;
    private String whyVisit;
    private String imagePath;
    private String tags;

    
    
    
    
    
    
    // === NEW FIELDS FOR COMPATIBILITY ===
    private String category;        // For setCategory()/getCategory()
    private String matchType;       // For getMatchType()/setMatchType()
   private int budget;//nazifa
    
    
    protected Place(String placeId, String name, String division, String district) {
        super(placeId, name);
        this.division = division;
        this.district = district;
    }

    // Nazifa's default constructor
    public Place() {
        super("unknown", "unknown");
    }

    // === NEW METHODS FOR COMPATIBILITY ===
    // For setCategory() calls in Recommendation.java
    public void setCategory(String category) {
        this.category = category;
    }

    // For getCategory() calls in RecommendationFrame and RecommendationTableModel
    public String getCategory() {
        if (category != null && !category.isEmpty()) {
            return category;
        }
        // Fallback to getPlaceCategory() if category not set
        return getPlaceCategory();
    }

    // For getMatchType() calls in RecommendationTableModel
    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    // Optional: Helper method to add match types
    public void addMatchType(String type) {
        if (matchType == null || matchType.isEmpty()) {
            matchType = type;
        } else if (!matchType.contains(type)) {
            matchType = matchType + " + " + type;
        }
    }

    // === END NEW METHODS ===
    @Override
    public abstract String getObjectType();

    public abstract String getPlaceCategory();

    public abstract String getPlaceSpecificDetails();

    // ... rest of your existing getters/setters remain the same ...
    public String getDivision() {
        return division;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public double getEntryCost() {
        return entryCost;
    }

    public String getBestTime() {
        return bestTime;
    }

    public String getWhyVisit() {
        return whyVisit;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTags() {
        return tags;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEntryCost(double entryCost) {
        this.entryCost = entryCost;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }

    public void setWhyVisit(String whyVisit) {
        this.whyVisit = whyVisit;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return division + " -> " + district;
    }

    public boolean hasTag(String tag) {
        return tags != null && tags.toLowerCase().contains(tag.toLowerCase());
    }

    public final String getFullInfo() {
        StringBuilder info = new StringBuilder();
        info.append(" ").append(getName()).append("\n");
        info.append("🏷️ Type: ").append(getObjectType()).append("\n");
        info.append(" Category: ").append(getPlaceCategory()).append("\n");
        info.append(" Location: ").append(getLocation()).append("\n");

        if (address != null && !address.isEmpty()) {
            info.append(" Address: ").append(address).append("\n");
        }

        info.append(" Entry Cost: ").append(entryCost).append("\n");
        info.append(" Best Time: ").append(bestTime).append("\n");

        if (description != null && !description.isEmpty()) {
            info.append("Description: ").append(description).append("\n");
        }

        if (whyVisit != null && !whyVisit.isEmpty()) {
            info.append("Why Visit: ").append(whyVisit).append("\n");
        }

        info.append(getPlaceSpecificDetails());

        if (tags != null && !tags.isEmpty()) {
            info.append("🏷️ Tags: ").append(tags);
        }

        return info.toString();
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getBudget() {
        return budget;
    }
}
