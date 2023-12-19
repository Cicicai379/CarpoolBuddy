package com.example.carpoolbuddy.controllers.explore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.AddVehicleActivity;
import com.example.carpoolbuddy.controllers.MainActivity;
import com.example.carpoolbuddy.models.CLocation;
import com.example.carpoolbuddy.models.User;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SegwaysActivity extends AppCompatActivity {
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segways);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        CollectionReference carsCollectionRef = firestore.collection("vehicles").document("segways").collection("segways");

        carsCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Vehicle> vehicles = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    vehicles.add(vehicle);
                }

                renderLayoutRows(vehicles);
            } else {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void renderLayoutRows(List<Vehicle> vehicles) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout linearLayout = findViewById(R.id.linear);

        for (Vehicle vehicle : vehicles) {
            View rowView = inflater.inflate(R.layout.vehicle_row, null);
            TextView ownerTextView = rowView.findViewById(R.id.owner);
            TextView locationTextView = rowView.findViewById(R.id.location);
            TextView infoTextView = rowView.findViewById(R.id.info);


            locationTextView.setText(vehicle.getPickUpLocation().getAddress() + " to "+vehicle.getDropOffLocation().getAddress());
            ownerTextView.setText(vehicle.getOwner().getName() + ": " + vehicle.getOwner().getRating());
            infoTextView.setText(vehicle.getPrice() + " HKD | " + vehicle.getTime().toString() + " | " + vehicle.getCapacity() + " seats");

            // Load the image using the vehicle ID
            ImageView imageView = rowView.findViewById(R.id.image);
            String imageName = vehicle.getVehicleID()+".png";
            System.out.println(imageName);
            StorageReference imageRef = storageReference.child("vehicles")
                    .child(imageName);
            System.out.println("ref:"+imageRef);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                System.out.println("URL:"+uri);

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
                Drawable d = getResources().getDrawable(R.drawable.rectangle_grey);
                imageView.setImageDrawable(d);
                System.out.println(e+" error");
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start another activity and pass the vehicleId as extra
                    Intent intent = new Intent(SegwaysActivity.this, VehicleProfileActivity.class);
                    intent.putExtra("vehicleId", vehicle.getVehicleID());
                    intent.putExtra("type", "Segway");

                    startActivity(intent);
                }
            });

            linearLayout.addView(rowView);
        }
    }

    public void back(View w){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}