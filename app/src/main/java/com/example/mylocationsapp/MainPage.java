package com.example.mylocationsapp;
//Activity to input the location details and save them

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;//image
    //coordinates
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    //name and country
    private EditText nameEditText;
    private EditText countryEditText;
    //date and rating
    private EditText dateEditText;
    private EditText ratingEditText;


    //database related variables
    private long locationId = -1;
    private boolean isNewItem;
    private Object view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        //setting up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //rating bar
        //binding MainActivity.java with activity_main.xml file
        RatingBar rt = (RatingBar) findViewById(R.id.ratingBar);

        //finding the specific RatingBar with its unique ID
        LayerDrawable stars=(LayerDrawable) rt.getProgressDrawable();

        //Use for changing the color of RatingBar
        stars.getDrawable(2).setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

        //requesting entered data from the Activity layout for the new Location
        latitudeEditText = findViewById(R.id.latitude);
        longitudeEditText = findViewById(R.id.longitude);
        nameEditText = findViewById(R.id.locationName);
        countryEditText = findViewById(R.id.locationCountry);
        dateEditText = findViewById(R.id.locationDate);
        ratingEditText = findViewById(R.id.locationRate);
        //locations list view
        ListView listView = findViewById(R.id.locationsListView);
        //rating bar value


        // Get the entry ID passed from the previous activity (if editing an entry)
        Intent intent = getIntent();
        locationId = intent.getLongExtra("location_id", -1);
        isNewItem = locationId == -1;//a new item is added to the end of the list

        //adapter to link the details of the location the next page
        List<Item> locationsList = Collections.emptyList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, extractDetails(locationsList));
        listView.setAdapter(adapter);

        //if a new item is added a new field is created and populated with the user input data
        if (!isNewItem) {
            LocationDatabaseHelper dbHelper = new LocationDatabaseHelper(this);
            SQLiteDatabase db = null;

            try {
                db = dbHelper.getReadableDatabase();
                List<Item> entriesList = dbHelper.getAllLocations(db, "date");

                for (Item item : entriesList) {
                    if (item.getId() == locationId) {
                        populateFields(item);
                        break;
                    }
                }
            } catch (Exception e) {
                // Log the error
                Log.e("MainActivity", "Error loading journal entry with ID: " + locationId, e);
                Toast.makeText(this, "Failed to load entry. Please try again.", Toast.LENGTH_SHORT).show();
            } finally {
                // Ensure the database is properly closed
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        }
    }
    //returns the user inputs of the location
    private List<String> extractDetails(List<Item> locationsList) {
        List<String> details = new ArrayList<>();
        for (Item item : locationsList) {
            String newLocation = item.getNameOfLocation() + ", " + item.getCountry() + "\n(" + item.getLatitude() + "," + item.getLongitude() + ") \n" + item.getDate() + "\n Rating: " + item.getRating();
            details.add(newLocation);
        }
        return details;
    }
    //updates the database
    private void populateFields(Item item) {
        nameEditText.setText(item.getNameOfLocation());
        latitudeEditText.setText(item.getLatitude());
        longitudeEditText.setText(item.getLongitude());
        countryEditText.setText(item.getCountry());
        dateEditText.setText(item.getDate());
        ratingEditText.setText(item.getRating());
    }
    //menu creation and navigation bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainpagemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_item) {
            saveLocation();
            return true;
        }else if (id == R.id.add_img) {
            pickImage();
            return true;
        } else if(id == R.id.home){
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }
    //a method that stores the information in the database
    private void saveLocation() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Location name cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String latitude = latitudeEditText.getText().toString().trim();
        String longitude = longitudeEditText.getText().toString().trim();
        String country = countryEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String rating = ratingEditText.getText().toString().trim();

        if (latitude.isEmpty() || longitude.isEmpty() || country.isEmpty() || date.isEmpty() || rating.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }
        //date format validation
        if (date.length() >2){
            date = date.substring(0,2)+ "/" + date.substring(2);// make sure day and month have '/' after 2 digits
        }
        if(date.length() >5){//validating the year
            date = date.substring(0, 5) + "/" + date.substring(5);
        }
        //opening and saving the database
        LocationDatabaseHelper dbHelper = new LocationDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (isNewItem) {
            dbHelper.insertLocation(db, name, country, latitude, longitude, date, rating);
            Toast.makeText(this, "Location saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateLocation(db, locationId, name, country, latitude, longitude, date, rating);
            Toast.makeText(this, "Location updated successfully!",Toast.LENGTH_SHORT).show();
        }
        // After saving, pass the updated list of locations to the SavedLocations Activity
        Intent intent = new Intent(this, SavedLocations.class);
        List<Item> locationsList = dbHelper.getAllLocations(db, "date");
        intent.putExtra(SavedLocations.EXTRA_LOCATIONS_LIST, (ArrayList<Item>) locationsList);
        startActivity(intent);
    }
    //when clicking the attachment icon, the user can be redirected to their gallery
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    //displaying the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(selectedImageUri);
        }
    }
}
