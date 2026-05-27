package com.ExploBD.util;

import com.ExploBD.object.User;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;

public class TravelStyleHelper {

    public static void applyToCheckboxes(User user,
            JCheckBox beach, JCheckBox mountain, JCheckBox urban,
            JCheckBox historical, JCheckBox culture, JCheckBox ecotourism,
            JCheckBox adventure, JCheckBox relaxation, JCheckBox photographic,
            JCheckBox nature, JCheckBox food) {

        beach.setSelected(user.hasTravelStyle("BEACH"));
        mountain.setSelected(user.hasTravelStyle("MOUNTAIN"));
        urban.setSelected(user.hasTravelStyle("URBAN"));
        historical.setSelected(user.hasTravelStyle("HISTORICAL"));
        culture.setSelected(user.hasTravelStyle("CULTURE"));
        ecotourism.setSelected(user.hasTravelStyle("ECOTOURISM"));
        adventure.setSelected(user.hasTravelStyle("ADVENTURE"));
        relaxation.setSelected(user.hasTravelStyle("RELAXATION"));
        photographic.setSelected(user.hasTravelStyle("PHOTOGRAPHIC"));
        nature.setSelected(user.hasTravelStyle("NATURE"));
        food.setSelected(user.hasTravelStyle("FOOD"));
    }

    public static List<String> fromCheckboxes(
            JCheckBox beach, JCheckBox mountain, JCheckBox urban,
            JCheckBox historical, JCheckBox culture, JCheckBox ecotourism,
            JCheckBox adventure, JCheckBox relaxation, JCheckBox photographic,
            JCheckBox nature, JCheckBox food) {

        List<String> styles = new ArrayList<>();

        if (beach.isSelected()) {
            styles.add("BEACH");
        }
        if (mountain.isSelected()) {
            styles.add("MOUNTAIN");
        }
        if (urban.isSelected()) {
            styles.add("URBAN");
        }
        if (historical.isSelected()) {
            styles.add("HISTORICAL");
        }
        if (culture.isSelected()) {
            styles.add("CULTURE");
        }
        if (ecotourism.isSelected()) {
            styles.add("ECOTOURISM");
        }
        if (adventure.isSelected()) {
            styles.add("ADVENTURE");
        }
        if (relaxation.isSelected()) {
            styles.add("RELAXATION");
        }
        if (photographic.isSelected()) {
            styles.add("PHOTOGRAPHIC");
        }
        if (nature.isSelected()) {
            styles.add("NATURE");
        }
        if (food.isSelected()) {
            styles.add("FOOD");
        }

        return styles;
    }

    public static void updateUserFromCheckboxes(User user,
            JCheckBox beach, JCheckBox mountain, JCheckBox urban,
            JCheckBox historical, JCheckBox culture, JCheckBox ecotourism,
            JCheckBox adventure, JCheckBox relaxation, JCheckBox photographic,
            JCheckBox nature, JCheckBox food) {

        user.getTravelStyles().clear();

        if (beach.isSelected()) {
            user.addTravelStyle("BEACH");
        }
        if (mountain.isSelected()) {
            user.addTravelStyle("MOUNTAIN");
        }
        if (urban.isSelected()) {
            user.addTravelStyle("URBAN");
        }
        if (historical.isSelected()) {
            user.addTravelStyle("HISTORICAL");
        }
        if (culture.isSelected()) {
            user.addTravelStyle("CULTURE");
        }
        if (ecotourism.isSelected()) {
            user.addTravelStyle("ECOTOURISM");
        }
        if (adventure.isSelected()) {
            user.addTravelStyle("ADVENTURE");
        }
        if (relaxation.isSelected()) {
            user.addTravelStyle("RELAXATION");
        }
        if (photographic.isSelected()) {
            user.addTravelStyle("PHOTOGRAPHIC");
        }
        if (nature.isSelected()) {
            user.addTravelStyle("NATURE");
        }
        if (food.isSelected()) {
            user.addTravelStyle("FOOD");
        }
    }
}
