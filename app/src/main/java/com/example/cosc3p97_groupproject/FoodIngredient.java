package com.example.cosc3p97_groupproject;

import java.io.Serializable;

/** Class for ingredient/additive detected from ingredients. includes a name and a rating based on health category (1-3)
 *
 * @author Mason De Fazio and Chris Orr
 * @course      COSC 3P97
 * @version     1.0  */
public class FoodIngredient implements Serializable {

    public String name;
    public int rating;


    public FoodIngredient(String name, int rating){
        this.name = name;
        this.rating = rating;
    }


    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }
}
