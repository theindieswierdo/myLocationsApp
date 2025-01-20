package com.example.mylocationsapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.content.Intent;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void nextPage(View view){
        Intent intent = new Intent( this, MainPage.class);
        startActivity(intent);//add location page
    }
    public void savedPage(View view){
        Intent intent = new Intent(this, SavedLocations.class);
        startActivity(intent);//saved locations page
    }
}