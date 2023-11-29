package com.example.carpoolbuddy.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.carpoolbuddy.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

//    public void SignIn(View w){
//        System.out.println("sign in");
//        String emailString = emailField.getText().toString();
//        String passwordString = passwordField.getText().toString();
//
//        //check validity
//        if(passwordString.equals("")  || emailString.equals("")){
//            Toast.makeText(AuthActivity.this, "Sign in failed. Please check your email is valid and password length is at least 6.",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mAuth.signInWithEmailAndPassword(emailString, passwordString)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("LOGIN", "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
//                            Toast.makeText(AuthActivity.this, "Sign in failed. Please check your email is valid and password length is at least 6.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//
//    }
}