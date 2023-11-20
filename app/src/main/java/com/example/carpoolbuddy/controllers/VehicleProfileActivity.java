package com.example.carpoolbuddy.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.carpoolbuddy.R;

public class VehicleProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);
    }

    public void MainActivity(View w){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}