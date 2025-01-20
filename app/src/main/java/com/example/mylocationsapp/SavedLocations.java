package com.example.mylocationsapp;
//the Activity that shows all the locations that the user has saved with the option to add or delete them

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class SavedLocations extends AppCompatActivity{
    public static final String EXTRA_LOCATIONS_LIST = "locationsList";//assigning MainPage localList to local intent
    private List<Item> locationsList;
    private LocationDatabaseHelper dbHelper;
    ArrayAdapter<Item> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savedlocationspage);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.savedlocationsListView);

        //retrieve database
        dbHelper = new LocationDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        locationsList = dbHelper.getAllLocations(db, "date");

        //set up the adapter to populate the listview
        ArrayAdapter<Item> adapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, locationsList);

        //checking if items are in the list
        //if (receivedList != null) {
            //locationsList.addAll(receivedList);
       // }
        listView.setAdapter(adapter);
        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //menu options for navigation bar
        getMenuInflater().inflate(R.menu.savedlocationsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_item) {
            // deleting items
            if (!locationsList.isEmpty()) {
                showDeleteConfirmationDialog();
            } else {
                Toast.makeText(this, "No items to delete", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.home) {//going home
            Intent intent = new Intent(SavedLocations.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.adding_item) {
            // Opens the MainPage to add a new location
            Intent intent = new Intent(SavedLocations.this, MainPage.class);
            intent.putExtra( SavedLocations.EXTRA_LOCATIONS_LIST, (ArrayList<Item>) locationsList);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // the confirmation dialog for deleting an item
    private void showDeleteConfirmationDialog() {
        Item itemToDelete = locationsList.get(0);//item position
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Location")
                .setMessage("Are you sure you want to delete this location?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the item from the database
                        dbHelper.deleteLocation(itemToDelete.getId());

                        // Remove location from the list and update the ListView
                        locationsList.remove(itemToDelete);
                        Toast.makeText(SavedLocations.this, "Location deleted", Toast.LENGTH_SHORT).show();
                        refreshListView();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Refresh the ListView after changes
    private void refreshListView() {
        adapter.notifyDataSetChanged();
    }
}
