package com.example.carpoolbuddy.controllers.explore;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.carpoolbuddy.controllers.MainActivity;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carpoolbuddy.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class VehicleProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private TextView plField;
    private TextView dlField;
    private TextView nameField;
    private TextView phoneField;
    private TextView priceField;
    private TextView hField;
    private String type;
    private TextView dField;

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
            type = intent.getStringExtra("type");
        }
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Spinner spinner = findViewById(R.id.spinner);
        nameField = findViewById(R.id.vehicle_name);
        phoneField = findViewById(R.id.vehicle_phone);
        priceField = findViewById(R.id.vehicle_price);
        plField = findViewById(R.id.dl);
        dlField = findViewById(R.id.pl);
        hField = findViewById(R.id.hour);
        dField = findViewById(R.id.date);

        String path = "vehicles/cars/cars";
        switch (type) {
            case "Car":
                path = "vehicles/cars/cars";
                break;
            case "Bike":
                path = "vehicles/bikes/bikes";
                break;
            case "Helicopter":
                path = "vehicles/helicopters/helicopters";
                break;
            case "Segway":
                path = "vehicles/segways/segways";
                break;
        }

        DocumentReference vehicleRef = firestore.collection(path).document(vehicleId);
        vehicleRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    if (vehicle != null) {
                        nameField.setText(vehicle.getOwner().getName());
                        phoneField.setText(vehicle.getOwner().getPhone());
                        hField.setText(vehicle.getTime().getHour() + ":" + vehicle.getTime().getMinute());
                        dField.setText(vehicle.getTime().getDay() + "/" + vehicle.getTime().getMonth() + "/" + vehicle.getTime().getYear());
                        plField.setText(vehicle.getPickUpLocation().getAddress());
                        dlField.setText(vehicle.getDropOffLocation().getAddress());
                        priceField.setText(Double.toString(vehicle.getPrice()) + " HKD");
                        addImage(vehicle);

                        // Set the spinner items to numbers from 1 to the vehicle's capacity
                        int capacity = vehicle.getCapacity();
                        List<String> spinnerItems = new ArrayList<>();
                        for (int i = 1; i <= capacity; i++) {
                            spinnerItems.add(String.valueOf(i));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item2, spinnerItems);
                        adapter.setDropDownViewResource(R.layout.spinner_text2);
                        spinner.setAdapter(adapter);
                    }
                }
            } else {
                // Handle the error
                Exception exception = task.getException();
                Toast.makeText(VehicleProfileActivity.this, "Load ride info failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addImage(Vehicle vehicle){
        // Load the image using the vehicle ID
        ImageView imageView = findViewById(R.id.vehicle_image);
        String imageName = vehicle.getVehicleID()+".png";
        System.out.println(imageName);
        StorageReference imageRef = storageReference.child("vehicles")
                .child(imageName);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(null);
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(uri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        }).addOnFailureListener(e -> {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.rectangle_grey);
            imageView.setImageDrawable(d);
            System.out.println(e+" error");
        });

    }

    public void back(View w) {
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