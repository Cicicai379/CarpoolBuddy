package com.example.carpoolbuddy.controllers;

import static android.app.PendingIntent.getActivity;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.adapters.ThemedAutocompleteSupportFragment;
import com.example.carpoolbuddy.models.Bike;
import com.example.carpoolbuddy.models.Car;
import com.example.carpoolbuddy.models.Helicopter;
import com.example.carpoolbuddy.models.Segway;
import com.example.carpoolbuddy.models.User;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class AddVehicleActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private User user;
    private FirebaseFirestore firestore;
    private EditText capacityField;
    private EditText timeField;

    private EditText priceField;
    private ImageView pickTimeBtn;
    private EditText selectedTimeTV;
    private Spinner typeField;

    private AutocompleteSupportFragment autocompleteFragment;

    private String owner;
    private double price;
    private int capacity;
    private String contact;
    private String type;
    private Place pl;
    private Place dl;
    private Calendar time;

    private EditText pLocation;
    private EditText dLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //get all input data
        capacityField = findViewById(R.id.capacity);
        priceField = findViewById(R.id.price);
        timeField = (EditText) findViewById(R.id.time);
        pLocation = (EditText) findViewById(R.id.pLocation);
        dLocation = (EditText) findViewById(R.id.dLocation);
        typeField = findViewById(R.id.spinner2);
        Setup();

    }



    private void Setup()
    {
        // auto complete location (pick up)
        Places.initialize(getApplicationContext(), "AIzaSyAZ4dtpOHzJAe0DZMwQQMxinVvpDGNj64c");
        AutocompleteSupportFragment autocompleteFragment = new ThemedAutocompleteSupportFragment();
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.autocomplete_fragment, autocompleteFragment)
                .commit();
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                pl = place;
                pLocation.setHint("");
                CharSequence c = "        ";
                autocompleteFragment.setText(c);
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
                Log.e(TAG, "Error: " + status.getStatusMessage());
            }
        });
        CharSequence c = "        ";
        autocompleteFragment.setText(c);

        // auto complete location (drop off)
        AutocompleteSupportFragment autocompleteFragment2 = new ThemedAutocompleteSupportFragment();
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.autocomplete_fragment2, autocompleteFragment2)
                .commit();
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                dl = place;
                dLocation.setHint("");
                CharSequence c = "        ";
                autocompleteFragment2.setText(c);
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "Error: " + status.getStatusMessage());
            }
        });
        autocompleteFragment2.setText(c);


        // Setup calendar time picker
        pickTimeBtn = findViewById(R.id.timepicker);
        selectedTimeTV = findViewById(R.id.time); // Assuming you have a TextView for displaying the selected time and day

        pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a TimePickerDialog for selecting the time
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddVehicleActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time = Calendar.getInstance();
                                time.set(Calendar.YEAR, year);
                                time.set(Calendar.MONTH, month);
                                time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                time.set(Calendar.MINUTE, minute);
                                // Update the selectedTimeTV with the selected time
                                String selectedDateTime = hourOfDay + ":" + minute + " " + dayOfMonth + "/" + (month + 1) + "/" + year;
                                selectedTimeTV.setText(selectedDateTime);
                            }
                        }, hour, minute, false);

                // Create a DatePickerDialog for selecting the day
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddVehicleActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Update the selectedDateTimeTV with the selected time and day
                                String selectedDateTime = hour + ":" + minute + " " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                selectedTimeTV.setText(selectedDateTime);

                                // Show the TimePickerDialog after the date is selected
                                timePickerDialog.show();
                            }
                        }, year, month, dayOfMonth);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);

    }

    public void addNewVehicle(View v) {
        price = Double.parseDouble(String.valueOf(priceField.getText()));
        capacity = Integer.parseInt(String.valueOf(capacityField.getText()));

        if (!formValid()) return;
        type = "Car";
        type = typeField.getSelectedItem().toString();

        FirebaseUser curuser = mAuth.getCurrentUser();
        String userId = curuser.getUid();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        System.out.println(userId);
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            System.out.println("getting user on success...");
            if (documentSnapshot.exists()) {
                 user = documentSnapshot.toObject(User.class);
            }
            Vehicle vehicle = new Vehicle(UUID.randomUUID().toString(), user, capacity,  price,  type, pl, dl,  time);
            switch (type) {
                case "Car":
                    firestore.collection("vehicles")
                            .document("cars")
                            .collection("cars")
                            .document(vehicle.getVehicleID())
                            .set(vehicle);
                    break;
                case "Bike":
                    firestore.collection("vehicles")
                            .document("bikes")
                            .collection("bikes")
                            .document(vehicle.getVehicleID())
                            .set(vehicle);
                    break;
                case "Helicopter":
                    firestore.collection("vehicles")
                            .document("helicopters")
                            .collection("helicopters")
                            .document(vehicle.getVehicleID())
                            .set(vehicle);
                    break;
                case "Segway":
                    firestore.collection("vehicles")
                            .document("segways")
                            .collection("segways")
                            .document(vehicle.getVehicleID())
                            .set(vehicle);
                    break;
            }
            progressDialog.dismiss();
            Toast.makeText(AddVehicleActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   back(null);
                }
            }, 2000);
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(AddVehicleActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            return;
        });


    }




    private boolean formValid(){
        if(dl == null || pl == null || time == null ||capacity==0|| price == 0){
            System.out.println(" drop:"+dl.getAddress() + " pick:"+pl.getAddress()+" time:"+time.getTime()+" cap:"+capacity+" price:"+price);
            Toast.makeText(AddVehicleActivity.this, "Upload failed. Please check your input is valid.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i(TAG, "Place: ${place.getName()}, ${place.getId()}");
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });

        public void back(View w){
            Intent intent = new Intent();
            intent.putExtra("to", "rides");
            startActivity(intent);
            finish();
        }

}