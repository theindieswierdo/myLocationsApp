package com.example.mylocationsapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocationDatabaseHelper extends SQLiteOpenHelper{
    // Database and Table Constants
    private static final String DB_NAME = "LocationData";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "LOCATION";

    // Column Names
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_RATING = "rating";

    public LocationDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the LOCATION table
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_COUNTRY + " TEXT, "
                + COLUMN_LATITUDE + " TEXT, "
                + COLUMN_LONGITUDE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_RATING + " TEXT);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new location
    public void insertLocation(SQLiteDatabase db, String name, String country, String latitude, String longitude, String date, String rating) {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_COUNTRY, country);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_RATING, rating);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Retrieve all locations ordered by a specific column
    public List<Item> getAllLocations(SQLiteDatabase db, String orderBy) {
        List<Item> locations = new ArrayList<>();
        db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderBy // Order by specified column
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));
                String latitude = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String rating = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RATING));

                locations.add(new Item(id, name, country, latitude, longitude, date, rating));
            }
            cursor.close();
        }

        db.close();
        return locations;
    }

    // Update a location
    public void updateLocation(SQLiteDatabase db, long id, String name, String country, String latitude, String longitude, String date, String rating) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_COUNTRY, country);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_RATING, rating);

        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete a location by ID
    public void deleteLocation(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
