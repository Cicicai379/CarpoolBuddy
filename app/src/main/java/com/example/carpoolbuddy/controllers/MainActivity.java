package com.example.carpoolbuddy.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.carpoolbuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up firebase auth and firestore instances
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void userProfile(View w){
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void AddVehicle(View v){
        Intent intent = new Intent(this, AddVehicleActivity.class);
        startActivity(intent);
    }
    public void VehicleProfile(View v){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        startActivity(intent);
    }

    public void AppInfo(View v){
        System.out.println("app info!");
        Intent intent = new Intent(this, AppInfoActivity.class);
        startActivity(intent);
    }


    public void SignOut(View w){
        mAuth.signOut();
        System.out.println("loging out!");
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }
}