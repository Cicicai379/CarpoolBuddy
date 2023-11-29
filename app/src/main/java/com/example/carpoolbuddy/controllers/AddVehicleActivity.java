package com.example.carpoolbuddy.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.Bike;
import com.example.carpoolbuddy.models.Car;
import com.example.carpoolbuddy.models.Helicopter;
import com.example.carpoolbuddy.models.Segway;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AddVehicleActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText ownerField;
    private EditText carModelField;
    private EditText capacityField;
    private EditText contactField;
    private EditText priceField;

    private Spinner typeField;


    private String owner;
    private String price;
    private String model;
    private String capacity;
    private String contact;
    private String type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        firestore = FirebaseFirestore.getInstance();

        //get all input data
        ownerField = findViewById(R.id.add_owner);
        carModelField = findViewById(R.id.add_model);
        capacityField = findViewById(R.id.add_capacity);
        contactField = findViewById(R.id.add_contact);
        priceField = findViewById(R.id.profile_price);
        typeField = findViewById(R.id.add_type);
    }

    public void addNewVehicle(View v) {
        if (!formValid()) return;
        type = "Car"; // Provide a default value
        typeField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = "Car";
            }
        });
        System.out.println("type: " + type);

        switch (type) {
            case "Car":
                Car c = new Car(UUID.randomUUID().toString(), owner, model, Integer.parseInt(capacity), contact, Double.parseDouble(price),type);
                firestore.collection("vehicles")
                        .document("cars")
                        .collection("cars")
                        .document(c.getVehicleID())
                        .set(c);
                break;
            case "Bike":
                Bike b = new Bike(UUID.randomUUID().toString(), owner, model, Integer.parseInt(capacity), contact, Double.parseDouble(price),type);
                firestore.collection("vehicles")
                        .document("bikes")
                        .collection("bikes")
                        .document(b.getVehicleID())
                        .set(b);
                break;
            case "Helicopter":
                Helicopter h = new Helicopter(UUID.randomUUID().toString(), owner, model, Integer.parseInt(capacity), contact, Double.parseDouble(price),type);
                firestore.collection("vehicles")
                        .document("helicopters")
                        .collection("helicopters")
                        .document(h.getVehicleID())
                        .set(h);
                break;
            case "Segway":
                Segway s = new Segway(UUID.randomUUID().toString(), owner, model, Integer.parseInt(capacity), contact, Double.parseDouble(price),type);
                firestore.collection("vehicles")
                        .document("segways")
                        .collection("segways")
                        .document(s.getVehicleID())
                        .set(s);
                break;
        }
        Toast.makeText(AddVehicleActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AddVehicleActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);


    }


    private boolean formValid(){
        owner = ownerField.getText().toString();
        model = carModelField.getText().toString();
        capacity = capacityField.getText().toString();
        contact = contactField.getText().toString();
        price = priceField.getText().toString();
        if(owner.equals("")||contact.equals("")||capacity.equals("")|| price.equals("")||model.equals("")){
            Toast.makeText(AddVehicleActivity.this, "Upload failed. Please check your input is not null.",
                    Toast.LENGTH_SHORT).show();
            System.out.println(type+" type; "+owner+" owner; "+model+" model; "+capacity+" capacity; "+" "+contact+" contact; "+price);
            return false;
        }
        return true;
    }
    public void MainActivity(View w){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}