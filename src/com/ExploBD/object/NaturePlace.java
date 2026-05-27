package com.ExploBD.object;

public class NaturePlace extends Place {

    private String natureType;
    private String areaSize;
    private String activities;

    private String conservationStatus;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
  private String image5;
    public NaturePlace(String id, String name, String division, String district) {
        super(id, name, division, district);
    }

    
    
    
    
    
    
    public NaturePlace() {
    super("unknown", "unknown", "unknown", "unknown");
}
    
    
    
    
    
    @Override
    public String getObjectType() {
        return "NATURE_PLACE";
    }

    @Override
    public String getPlaceCategory() {
        return "Nature";
    }

    @Override
    public String getPlaceSpecificDetails() {
        StringBuilder details = new StringBuilder();
        details.append(" Nature Details:\n");

        if (natureType != null && !natureType.isEmpty()) {
            details.append("   Type: ").append(natureType).append("\n");
        }

        if (areaSize != null && !areaSize.isEmpty()) {
            details.append("   Area: ").append(areaSize).append("\n");
        }

        if (activities != null && !activities.isEmpty()) {
            details.append("   Activities: ").append(activities).append("\n");
        }

        if (conservationStatus != null && !conservationStatus.isEmpty()) {
            details.append("   Conservation: ").append(conservationStatus).append("\n");
        }

        return details.toString();
    }

    public String getNatureType() {
        return natureType;
    }

    public void setNatureType(String natureType) {
        this.natureType = natureType;
    }

    public String getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(String areaSize) {
        this.areaSize = areaSize;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public void setImage5(String image4) {
        this.image5 = image4;
    }

    public String getImage5() {
        return image5;
    }

    public String getConservationStatus() {
        return conservationStatus;
    }

    public void setConservationStatus(String conservationStatus) {
        this.conservationStatus = conservationStatus;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }
}
