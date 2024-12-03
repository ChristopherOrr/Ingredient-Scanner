package com.example.cosc3p97_groupproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/** Class for recorded food item that has name, score, date recorded, and list of ingredients/additives
 *
 * @author Mason De Fazio and Chris Orr
 * @course      COSC 3P97
 * @version     1.0  */
public class ItemStat implements Serializable {

    public String label;
    public int score;
    public Date date;
    public ArrayList<FoodIngredient> ingredients;


    public ItemStat(int score, Date date, String label, ArrayList<FoodIngredient> ingredients){
        this.score = score;
        this.date = date;
        this.label = label;
        this.ingredients = ingredients;

    }

    public int getScore() {
        return score;
    }

    public Date getDate() {
        return date;
    }


    public ArrayList<FoodIngredient> getIngredients() {
        return ingredients;
    }

    public String getLabel() {
        return label;
    }
}
