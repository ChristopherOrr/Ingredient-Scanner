package com.example.cosc3p97_groupproject;// PreScanActivity.java

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;

/**
 * activity to upload picture, use Google ML Kit to recognise text, then create a list of ingredients that match the database
 *
 * @author Mason De Fazio and Chris Orr
 * @version 1.0
 * @course COSC 3P97
 */

public class PreScanActivity extends AppCompatActivity {
    private Button takeImageButton;
    private Button recognizeTextButton;
    private ImageView insertImage;
    private static final String TAG = "MAIN_TAG";
    private Uri imageUri = null;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private ProgressDialog progressDialog;

    private TextRecognizer textRecognizer;
    private String recognizedText = null;

    //processing text

    private int totalScore = 0;
    private int totalIngredientsDetected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescan_view); // Set the layout for this activity

        // Initializing Button

        takeImageButton = findViewById(R.id.takeImageButton);
        recognizeTextButton = findViewById(R.id.recognizeTextButton);
        insertImage = findViewById(R.id.insertimage);

        //permissions
        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //process dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("PLEASE WAIT");
        progressDialog.setCanceledOnTouchOutside(false);

        //text recognizer

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        takeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showInputImageDialog();

            }
        });

        recognizeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recognizeTextFromImage();


            }
        });
    }

    public void openReportActivity() {
        Intent intent = new Intent(this, ReportActivity.class);

        //bundle arraylist
        Bundle b = new Bundle();
        b.putSerializable("list", getIngredientList(recognizedText));
        intent.putExtra("list", b);

        //calculate score
        if (totalIngredientsDetected != 0) {
            intent.putExtra("score", totalScore / totalIngredientsDetected);
        } else {
            intent.putExtra("score", 100);
        }


        startActivity(intent);
    }

    private void recognizeTextFromImage() {

        progressDialog.setTitle("Searching Image...");
        progressDialog.setMessage("Searching Image...");
        progressDialog.show();

        try {
            progressDialog.setMessage("Preparing Image...");
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);
            progressDialog.setMessage("Recognizing Text...");

            Task<Text> textTaskResult = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            progressDialog.dismiss();
                            recognizedText = text.getText();
                            System.out.println(recognizedText);
                            openReportActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PreScanActivity.this, "failed recognizing text  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, "failed to prepare image", Toast.LENGTH_SHORT).show();

        }
    }

    //pick image picker method and run permission checks
    private void showInputImageDialog() {
        PopupMenu popupMenu = new PopupMenu(this, takeImageButton);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();
                if (id == 1) {
                    if (checkCameraPermissions()) {
                        Toast.makeText(PreScanActivity.this, "camera permissions good", Toast.LENGTH_SHORT).show();
                        pickImageCamera();
                    } else {
                        Toast.makeText(PreScanActivity.this, "camera permissions bad", Toast.LENGTH_SHORT).show();
                        requestCameraPermissions();
                    }
                } else if (id == 2) {

                    if (checkStoragePermission()) {

                        pickImageGallery();

                    } else {
                        requestStoragePermission();
                    }
                }
                return true;
            }
        });
    }

    private void pickImageGallery() {

        Toast.makeText(this, "opening gallery....", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here will receive image if picked
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image picked
                        Intent data = result.getData();
                        imageUri = data.getData();
                        //set to image view
                        insertImage.setImageURI(imageUri);
                    } else {
                        //canceled
                        Toast.makeText(PreScanActivity.this, "CANCELED", Toast.LENGTH_SHORT).show();
                    }
                }
            }


    );


    private void pickImageCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "SAMPLE TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "SAMPLE DESCRIPTION");


        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Toast.makeText(this, "opening camera...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        cameraActivityLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here will receive image if captured
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image picked
                        insertImage.setImageURI(imageUri);
                    } else {
                        //canceled
                        Toast.makeText(PreScanActivity.this, "CANCELED", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    private boolean checkStoragePermission() {


        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }


    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {


        boolean cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return cameraResult && storageResult;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {

                        pickImageCamera();
                    } else {
                        Toast.makeText(this, "permissions required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "CANCELED", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    //check if storage permission is granted or not
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickImageGallery();
                    } else {
                        Toast.makeText(this, "Storage permission is needed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }


    //populate array with detected ingredients from database
    public ArrayList<FoodIngredient> getIngredientList(String scannedText) {
        //list to return
        ArrayList<FoodIngredient> list = new ArrayList<>();

        for (String ingredient : Ingredients.unHealthy3) {
            if (scannedText.toLowerCase().contains(ingredient.toLowerCase())) {
                list.add(new FoodIngredient(ingredient, 3));
                totalIngredientsDetected++;
            }
        }

        for (String ingredient : Ingredients.unHealthy2) {
            if (scannedText.toLowerCase().contains(ingredient.toLowerCase())) {
                list.add(new FoodIngredient(ingredient, 2));
                totalScore = totalScore + 50;
                totalIngredientsDetected++;
            }
        }

        for (String ingredient : Ingredients.healthy) {
            if (scannedText.toLowerCase().contains(ingredient.toLowerCase())) {
                list.add(new FoodIngredient(ingredient, 1));
                totalScore = totalScore + 100;
                totalIngredientsDetected++;
            }
        }

        return list;

    }

}
