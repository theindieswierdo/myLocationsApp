package com.example.mylocationsapp;

import java.io.Serializable;

public class Item implements Serializable {
    private long id;
    private String name, country, latitude, longitude, date, rating;

    public Item(long id, String name, String country, String latitude, String longitude, String date, String rating) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.rating = rating;
    }
    //getters
    public long getId() { return id; }
    public String getNameOfLocation() { return name; }
    public String getCountry() { return country; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getDate() { return date; }
    public String getRating() { return rating; }

    @Override
    //what is to be displayed in the listview
    public String toString() {
        return name + " - " + country + "\n Coordinates: ( " + latitude + "," + longitude + " )" + "\n Date & Rating: " + date + ", " + rating; // Display title and date in the ListView
    }
}
