package com.ExploBD.object;

public class HistoricalPlace extends Place {

    private String yearBuilt;
    private String timePeriod;
    private String architectureStyle;
    private String historicalFacts;
    private boolean isUNESCO;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String image5;

    public HistoricalPlace(String id, String name, String division, String district) {
        super(id, name, division, district);
    }

    public HistoricalPlace() {
        super("unknown", "unknown", "unknown", "unknown");
    }

    @Override
    public String getObjectType() {
        return "HISTORICAL_PLACE";
    }

    @Override
    public String getPlaceCategory() {
        return "Historical";
    }

    @Override
    public String getPlaceSpecificDetails() {
        StringBuilder details = new StringBuilder();
        details.append("🏛️ Historical Details:\n");

        if (yearBuilt != null && !yearBuilt.isEmpty()) {
            details.append("   Built: ").append(yearBuilt).append("\n");
        }

        if (timePeriod != null && !timePeriod.isEmpty()) {
            details.append("   Period: ").append(timePeriod).append("\n");
        }

        if (architectureStyle != null && !architectureStyle.isEmpty()) {
            details.append("   Architecture: ").append(architectureStyle).append("\n");
        }

        if (historicalFacts != null && !historicalFacts.isEmpty()) {
            details.append("   Facts: ").append(historicalFacts).append("\n");
        }

        if (isUNESCO) {
            details.append("  UNESCO World Heritage Site\n");
        }

        return details.toString();
    }

    public String getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(String yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getArchitectureStyle() {
        return architectureStyle;
    }

    public void setArchitectureStyle(String architectureStyle) {
        this.architectureStyle = architectureStyle;
    }

    public String getHistoricalFacts() {
        return historicalFacts;
    }

    public void setHistoricalFacts(String historicalFacts) {
        this.historicalFacts = historicalFacts;
    }

    public boolean isUNESCO() {
        return isUNESCO;
    }

    public void setUNESCO(boolean isUNESCO) {
        this.isUNESCO = isUNESCO;
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

    public void setImage5(String image4) {
        this.image5 = image4;
    }

    public String getImage5() {
        return image5;
    }
}
