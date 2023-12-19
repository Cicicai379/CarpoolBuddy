package com.example.carpoolbuddy.controllers.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.MainActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void back(View view) {
        System.out.println("back from help!");
        Intent intent = new Intent(HelpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}