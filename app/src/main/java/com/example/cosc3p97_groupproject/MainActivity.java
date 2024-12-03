package com.example.cosc3p97_groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/** Main Activity for Ingredient scanner app. Directs to scan function or stats activity
 *
 * @author Mason De Fazio and Chris Orr
 * @course      COSC 3P97
 * @version     1.0  */

public class MainActivity extends AppCompatActivity {

    private Button scanButton;
    private Button statsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing button
        scanButton = findViewById(R.id.scanButton);
        statsButton = findViewById(R.id.statsButton);



        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreScanView(); // Open prescan_view
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openStatsView();}
        });
    }

    public void openPreScanView() {
        Intent intent = new Intent(this, PreScanActivity.class);
        startActivity(intent);
    }

    public void openStatsView(){
        Intent intent = new Intent(this, Stats.class);
        startActivity(intent);
    }
}

