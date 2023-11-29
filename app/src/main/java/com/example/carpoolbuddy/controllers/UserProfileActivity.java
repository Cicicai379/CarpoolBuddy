package com.example.carpoolbuddy.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Set up firebase auth and firestore instances
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        getUser();
    }

    public void SignOut(View w){
        mAuth.signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void MainActivity(View w){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void updateUI(FirebaseUser currentUser){
        if(currentUser == null){
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        }
    }

    //set user info
    @SuppressLint("SetTextI18n")
    private void getUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("email: " + user.getEmail());

        // get user info, set in the textview
        TextView myAwesomeTextView = (TextView)findViewById(R.id.user_info);
        myAwesomeTextView.setText("User Id: " + user.getUid() +
                "\n\nEmail: " + user.getEmail());
    }
}