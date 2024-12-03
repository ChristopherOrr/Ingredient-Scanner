package com.example.cosc3p97_groupproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;


/** Activity that displays info about a recorded item. (name, score, and list of ingredients/additives)
 *
 * @author Mason De Fazio and Chris Orr
 * @course      COSC 3P97
 * @version     1.0  */
public class FoodItemStatsActivity extends AppCompatActivity implements RecyclerViewInterface {

    private TextView label;
    private TextView score;
    private RecyclerView recyclerView;
    private ArrayList<FoodIngredient> foodIngredientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_stats);

        //initialize
        label = findViewById(R.id.foodItemLabelTextView);
        score = findViewById(R.id.foodItemScoreTextView);
        recyclerView = findViewById(R.id.foodItemRecyclerView);

        //get item passed from previous activity
        ItemStat food = (ItemStat) getIntent().getSerializableExtra("item");
        foodIngredientsList = food.getIngredients();

        label.setText(food.getLabel());
        score.setText(String.valueOf(food.getScore()));
        updateScoreColour(food.getScore());

        updateAdapter();


    }


    private void updateAdapter(){
        FoodIngredientListAdapter adapter = new FoodIngredientListAdapter(this, foodIngredientsList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateScoreColour(int overallScore){


        //set colour
        //set colour of score
        if(overallScore>70){
            score.setTextColor(Color.GREEN);
        }
        else if(overallScore>=50){
            score.setTextColor(Color.rgb(255,215,0)); //orange
        }
        else{
            score.setTextColor(Color.RED);
        }
    }

    @Override
    public void onItemClick(int position) {

        //searches browser
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        String keyword= "is "+foodIngredientsList.get(position).getName()+" healthy?";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SearchManager.QUERY, keyword);
        startActivity(intent);

    }

    @Override
    public void onItemHold(int position) {

    }
}