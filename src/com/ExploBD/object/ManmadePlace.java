package com.ExploBD.object;

public class ManmadePlace extends Place {

    private String Type;
    private String openingHours;
    private String specialEvents;
    private double averageSpending;
    private String contactInfo;
    private boolean hasEntryFee;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
  private String image5;
    public ManmadePlace(String id, String name, String division, String district) {
        super(id, name, division, district);
    }
    
    
    
    
    
    
    public ManmadePlace() {
    super("unknown", "unknown", "unknown", "unknown");
}
    
    

    @Override
    public String getObjectType() {
        return "ENTERTAINMENT_PLACE";
    }

    @Override
    public String getPlaceCategory() {
        return "Entertainment";
    }

    @Override
    public String getPlaceSpecificDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Entertainment Details:\n");

        if (Type != null && !Type.isEmpty()) {
            details.append("   Type: ").append(Type).append("\n");
        }

        if (openingHours != null && !openingHours.isEmpty()) {
            details.append("   Hours: ").append(openingHours).append("\n");
        }

        if (averageSpending > 0) {
            details.append("   Avg Spending: ৳").append(averageSpending).append("\n");
        }

        if (specialEvents != null && !specialEvents.isEmpty()) {
            details.append("   Events: ").append(specialEvents).append("\n");
        }

        if (contactInfo != null && !contactInfo.isEmpty()) {
            details.append("   Contact: ").append(contactInfo).append("\n");
        }

        if (!hasEntryFee) {
            details.append("   Free Entry\n");
        }

        return details.toString();
    }

   
    public String getEntertainmentType() {
        return Type;
    }

    public void setEntertainmentType(String Type) {
        this.Type = Type;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getSpecialEvents() {
        return specialEvents;
    }

    public void setSpecialEvents(String specialEvents) {
        this.specialEvents = specialEvents;
    }

    public double getAverageSpending() {
        return averageSpending;
    }

    public void setAverageSpending(double averageSpending) {
        this.averageSpending = averageSpending;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public boolean hasEntryFee() {
        return hasEntryFee;
    }

    public void setHasEntryFee(boolean hasEntryFee) {
        this.hasEntryFee = hasEntryFee;
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
    public void setImage5(String image4) {
        this.image5 = image4;
    }
        public String getImage5() {
        return image5;
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
