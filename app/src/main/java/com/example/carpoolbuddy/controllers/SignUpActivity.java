package com.example.carpoolbuddy.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

        private FirebaseAuth mAuth;
        private FirebaseFirestore firestore;
        private EditText emailField;
        private EditText passwordField;

        private GoogleSignInClient mGoogleSignInClient;
        private ProgressDialog progressDialog;
        private int RC_SIGN_IN = 40;
        private Spinner addTypeSpinner;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            System.out.println("signup---");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);

            firestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

        }

        public void SignUp2(View w){
            System.out.println("sign up");
            String emailString = emailField.getText().toString();
            String passwordString = passwordField.getText().toString();

            //check validity
            if(passwordString.equals("")  || emailString.equals("")){
                Toast.makeText(com.example.carpoolbuddy.controllers.SignUpActivity.this, "Sign in failed. Please check your email is valid and password length is at least 6.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //google create the user
            mAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d("SIGN UP","sign up succeeded");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateFirebase(emailString, passwordString);
                        updateUI(user);
                    }else{
                        Log.w("SIGN UP", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(com.example.carpoolbuddy.controllers.SignUpActivity.this, "Sign up failed. Please make sure that gmail is valid and password length is at least 6 chars", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        public void updateUI(FirebaseUser currentUser){
            if(currentUser != null){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
        private void updateFirebase(String email, String password){
            User user = new User(UUID.randomUUID().toString(), email, password);
            firestore.collection("users").document(user.getUid()).set(user);
        }

}