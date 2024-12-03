package com.example.cosc3p97_groupproject;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Date;

/**
 * activity to display the info of scanned ingredients list with option to save it to stats list
 *
 * @author Mason De Fazio and Chris Orr
 * @version 1.0
 * @course COSC 3P97
 */

public class ReportActivity extends AppCompatActivity implements RecyclerViewInterface {

    private Button scanButton;

    private TextView scoreTextView;
    private String scannedText = null;
    private int healthScore = 0;


    private ArrayList<FoodIngredient> foodIngredientsList;

    private RecyclerView recyclerView;

    private ArrayList<String> flaggedUnhealthy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_view); // Set the layout for this activity

        flaggedUnhealthy = new ArrayList<>();
        flaggedUnhealthy.add("FILLER");

        // Initializing
        scanButton = findViewById(R.id.scanButton);
        scoreTextView = findViewById(R.id.scoreTextview);

        recyclerView = findViewById(R.id.foodIngredientsRecyclerView);


        Intent intent = getIntent();
        healthScore = intent.getIntExtra("score", 999);


        Bundle b = intent.getBundleExtra("list");
        ArrayList<FoodIngredient> foodIngredients = (ArrayList<FoodIngredient>) b.getSerializable("list");


        populateFoodIngredientsList(foodIngredients);
        updateAdapter();


        scoreTextView.setText(String.valueOf(healthScore));
        updateScoreColour(healthScore);


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveAndExit(); // Open prescan_activity
                saveDialog();
            }
        });


    }


    public void saveAndExit(String itemLabel) {


        //get item stats that were previously saved to file
        ArrayList<ItemStat> list = readPreviousItems();

        //new item stat to save
        Date d = new Date();
        list.add(new ItemStat(healthScore, d, itemLabel, foodIngredientsList)); //add it to list

        //write array list with old items + new item
        writeItems(list);


        //exit all activities and go to main activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void populateFoodIngredientsList(ArrayList<FoodIngredient> newItems) {
        foodIngredientsList = newItems;
    }


    public void saveDialog() {
        EditText editText = new EditText(this);
        editText.setSingleLine(true);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Item Name")
                .setMessage("Enter name of item")
                .setView(editText)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String editTextInput = editText.getText().toString();
                    if (!editTextInput.equals("")) {
                        saveAndExit(editTextInput);
                    } else {
                        Toast.makeText(this, "Please Enter Valid Name", Toast.LENGTH_SHORT).show();
                    }


                })
                .setNegativeButton("DEFAULT", (dialogInterface, i) -> {
                    saveAndExit("Untitled");
                })
                .create();
        dialog.show();
    }


    private void updateScoreColour(int overallScore) {


        //set colour
        //set colour of score
        if (overallScore > 70) {
            scoreTextView.setTextColor(Color.GREEN);
        } else if (overallScore >= 50) {
            scoreTextView.setTextColor(Color.rgb(255, 215, 0)); //orange
        } else {
            scoreTextView.setTextColor(Color.RED);
        }
    }


    private void writeItems(ArrayList<ItemStat> listToWrite) {

        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir(), "") + File.separator + "health_data.srl"));

            for (ItemStat i : listToWrite) {
                out.writeObject(i);
            }


            out.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Error Writing to file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Error Writing to file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    //this is to keep previous items from file
    private ArrayList<ItemStat> readPreviousItems() {
        ArrayList<ItemStat> list = new ArrayList<>();
        ObjectInputStream input;
        try {
            input = new ObjectInputStream(new FileInputStream(new File(new File(getFilesDir(), "") + File.separator + "health_data.srl")));
            //read all items
            try {
                for (; ; ) {
                    ItemStat i = (ItemStat) input.readObject();
                    list.add(i);
                }
            } catch (EOFException e) {
                // End of stream
            }
            input.close();

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;


    }

    private void updateAdapter() {
        FoodIngredientListAdapter adapter = new FoodIngredientListAdapter(this, foodIngredientsList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onItemClick(int position) {
        //google search
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        String keyword = "is " + foodIngredientsList.get(position).getName() + " healthy?";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SearchManager.QUERY, keyword);
        startActivity(intent);
    }

    @Override
    public void onItemHold(int position) {

    }
}
