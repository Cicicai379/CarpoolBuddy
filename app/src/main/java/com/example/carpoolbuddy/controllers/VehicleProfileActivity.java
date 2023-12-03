package com.example.carpoolbuddy.controllers;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carpoolbuddy.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class VehicleProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private TextView ownerField;
    private TextView carModelField;
    private TextView capacityField;
    private TextView contactField;
    private TextView priceField;
    private TextView typeField;

    private String vehicleId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);

        // Retrieve the vehicleId from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            vehicleId = intent.getStringExtra("vehicleId");
        }
        firestore = FirebaseFirestore.getInstance();

        //get all input data
        ownerField = findViewById(R.id.edit_email);
        carModelField = findViewById(R.id.edit_password);
        capacityField = findViewById(R.id.profile_capacity);
        contactField = findViewById(R.id.profile_contact);
        priceField = findViewById(R.id.profile_price);
        typeField = findViewById(R.id.profile_type);

        DocumentReference vehicleRef = firestore.collection("vehicles/cars/cars").document(vehicleId);
        vehicleRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    if (vehicle != null) {
                        ownerField.setText("Owner: "+vehicle.getOwner());
                        carModelField.setText("Model: "+vehicle.getModel());
                        capacityField.setText("Capacity: "+Integer.toString(vehicle.getCapacity()));
                        contactField.setText("Contact: "+vehicle.getContact());
                        priceField.setText("Price: "+Double.toString(vehicle.getPrice()));
                        typeField.setText("Type: "+vehicle.getVehicleType());
                    }
                }
            } else {
                // Handle the error
                Exception exception = task.getException();
                Toast.makeText(VehicleProfileActivity.this, "Load ride info failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void MainActivity(View w) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void reserve(View w){
        String collectionPath = "vehicles/cars/cars";
        DocumentReference documentRef = firestore.collection(collectionPath).document(vehicleId);
        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            int currentCapacity = document.getLong("capacity").intValue();
                            int newCapacity = currentCapacity - 1;

                            documentRef.update("capacity", newCapacity)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> subTask) {
                                            if (subTask.isSuccessful()) {
                                                Toast.makeText(VehicleProfileActivity.this, "Reserve ride info successful", Toast.LENGTH_SHORT).show();
                                                Log.d("Firestore", "Capacity updated successfully for vehicle with ID: " + vehicleId);
                                            } else {
                                                Log.e("Firestore", "Error updating capacity", task.getException());
                                                Toast.makeText(VehicleProfileActivity.this, "Reserve ride info failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Log.d("Firestore", "Document with ID " + vehicleId + " does not exist.");
                        }
                    } else {
                        Log.e("Firestore", "Error getting document", task.getException());
                    }
                }
            });
        }

    }
