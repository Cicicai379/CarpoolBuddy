package com.example.carpoolbuddy.controllers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
        getDataAndDisplay();
    }



    private void getDataAndDisplay(){
        //get data from firebase
        CollectionReference carsCollectionRef = firestore.collection("vehicles/cars/cars");
        List<Vehicle>  vehicleList = new ArrayList<>();
        carsCollectionRef.get().addOnCompleteListener(task -> {
            System.out.println("Getting vehicles...");
            if (task.isSuccessful()) {
                System.out.println("Task is successful");
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    System.out.println("Query Snapshot is not null");
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        System.out.println("document: " + document);
                        Vehicle vehicle = document.toObject(Vehicle.class);
                        System.out.println("vehicle: " + vehicle.getOwner());
                        if(vehicle.getCapacity()>0) vehicleList.add(vehicle);
                        System.out.println(vehicleList.size());
                    }
                }


                //display data
                System.out.println("Displaying data, data size: "+vehicleList.size());
                TableLayout tableLayout = findViewById(R.id.tableLayout);
                deleteAllRowsExceptHeader(tableLayout);
                for (final Vehicle vehicle : vehicleList) {
                    // Create a new row
                    final TableRow newRow = new TableRow(this);

                    // Create owner TextView
                    TextView ownerTextView = new TextView(this);
                    ownerTextView.setText(vehicle.getOwner());
                    ownerTextView.setPadding(8, 8, 8, 8);
                    ownerTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
                    ownerTextView.setTextColor(Color.WHITE);
                    ownerTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Set layout weight

                    // Create capacity TextView
                    TextView capacityTextView = new TextView(this);
                    capacityTextView.setText(Integer.toString(vehicle.getCapacity()) + " people");
                    capacityTextView.setPadding(8, 8, 8, 8);
                    capacityTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
                    capacityTextView.setTextColor(Color.WHITE);
                    capacityTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Set layout weight

                    // Add views to the new row
                    newRow.addView(ownerTextView);
                    newRow.addView(capacityTextView);

                    // Set an onClick listener for the row
                    newRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Switch to VehicleProfileActivity and pass the vehicle ID
                            Intent intent = new Intent(MainActivity.this, VehicleProfileActivity.class);
                            intent.putExtra("vehicleId", vehicle.getVehicleID());
                            startActivity(intent);
                        }
                    });


                    // Add the new row to the table layout
                    tableLayout.addView(newRow);

                    // Create a divider drawable
                    Drawable dividerDrawable = getResources().getDrawable(R.drawable.divider_line);

                    // Set the divider drawable for the table layout
                    tableLayout.setDividerDrawable(dividerDrawable);

                    // Show dividers between rows
                    tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
                }
            } else {
                System.err.println("Error getting documents: " + task.getException());
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void reload(View v)
    {
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        getDataAndDisplay();
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

    private void deleteAllRowsExceptHeader(TableLayout tableLayout) {
        int childCount = tableLayout.getChildCount();
        for (int i = childCount-1; i >= 0; i--) {
            View childView = tableLayout.getChildAt(i);
            tableLayout.removeView(childView);
        }
    }



}
