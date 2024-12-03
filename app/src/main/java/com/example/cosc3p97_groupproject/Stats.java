package com.example.cosc3p97_groupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Date;


/**
 * activity to display the stats of all records the user has scanned.
 *
 * @author Mason De Fazio and Chris Orr
 * @version 1.0
 * @course COSC 3P97
 */

public class Stats extends AppCompatActivity implements RecyclerViewInterface {

    public ArrayList<ItemStat> itemStats = new ArrayList<>();
    public RecyclerView recyclerView;
    public ImageButton deleteHistoryButton;
    public ItemStatListAdapter adapter;

    public TextView overallScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        recyclerView = findViewById(R.id.itemStatRecyclerView);
        overallScore = findViewById(R.id.overallScoreTextView);
        deleteHistoryButton = findViewById(R.id.deleteHistoryButton);


        //list
        readItems();
        updateAdapter();


        updateScore();


        deleteHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Stats.this);
                builder.setMessage("Clear History?");
                builder.setTitle("Delete History");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    deleteAllItems();
                    itemStats.clear();
                    updateAdapter();
                    overallScore.setText("0");
                    finish();
                });

                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

                    dialog.cancel();
                });


                AlertDialog alertDialog = builder.create();

                alertDialog.show();


            }
        });


    }

    private void readItems() {
        ObjectInputStream input;
        try {
            input = new ObjectInputStream(new FileInputStream(new File(new File(getFilesDir(), "") + File.separator + "health_data.srl")));
            //read all items
            try {
                for (; ; ) {
                    ItemStat i = (ItemStat) input.readObject();
                    itemStats.add(i);
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


    }


    private void deleteAllItems() {

        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir(), "") + File.separator + "health_data.srl"));

            out.flush();

            out.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void updateAdapter() {
        adapter = new ItemStatListAdapter(this, itemStats, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateScore() {
        //overall score
        int overall = 0;
        for (ItemStat i : itemStats) {
            overall = overall + i.getScore();
        }
        if (itemStats.size() != 0) {
            overall = overall / itemStats.size();
        }

        overallScore.setText(String.valueOf(overall));

        //set colour
        //set colour of score
        if (overall > 70) {
            overallScore.setTextColor(Color.GREEN);
        } else if (overall >= 50) {
            overallScore.setTextColor(Color.rgb(255, 215, 0)); //orange
        } else {
            overallScore.setTextColor(Color.RED);
        }
    }


    @Override
    public void onItemClick(int position) {
        //open info activity
        Intent intent = new Intent(getApplicationContext(), FoodItemStatsActivity.class);
        intent.putExtra("item", itemStats.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemHold(int position) {
        //delete option

        AlertDialog.Builder builder = new AlertDialog.Builder(Stats.this);
        builder.setMessage("Delete " + itemStats.get(position).getLabel() + " from history?");
        builder.setTitle("Delete History");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

            itemStats.remove(position);
            adapter.notifyItemRemoved(position);
            writeItems(itemStats);
            updateScore();
        });

        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

            dialog.cancel();
        });


        AlertDialog alertDialog = builder.create();

        alertDialog.show();


    }
}