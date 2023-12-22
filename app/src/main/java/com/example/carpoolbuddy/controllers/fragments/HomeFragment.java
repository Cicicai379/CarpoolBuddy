package com.example.carpoolbuddy.controllers.fragments;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.explore.CarsActivity;
import com.example.carpoolbuddy.controllers.explore.VehicleProfileActivity;
import com.example.carpoolbuddy.models.Vehicle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment implements OnMapReadyCallback {


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            System.out.println("getInfoContents");

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.titleTextView));
            tvTitle.setText(marker.getTitle());
            TextView tvTime = ((TextView)myContentsView.findViewById(R.id.timeTextView));
            tvTime.setText(marker.getSnippet());
            DocumentSnapshot document = (DocumentSnapshot) marker.getTag();
            tvTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("onclick window");
                    Intent intent = new Intent(requireContext(), VehicleProfileActivity.class);
                    intent.putExtra("type", document.getString("vehicleType"));
                    intent.putExtra("vehicleId", document.getString("vehicleID"));
                    startActivity(intent);
                }
            });
            return myContentsView;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private ProgressDialog progressDialog;

    private FrameLayout map;
    private GoogleMap gMap;
    private Location currentLocation;
    private Marker marker;
    private FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;
    private SearchView searchView;
    private PlacesClient placesClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getCurrentLocation();
        map = view.findViewById(R.id.map);
        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();
        Places.initialize(view.getContext(), "AIzaSyAZ4dtpOHzJAe0DZMwQQMxinVvpDGNj64c");
         placesClient = Places.createClient(view.getContext());
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLocation();
        getCarObjectsFromFirestore();
        getBikeObjectsFromFirestore();
        getHelicopterObjectsFromFirestore();
        getSegwayObjectsFromFirestore();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String loc = searchView.getQuery().toString();
                if (loc == null) {
                    Toast.makeText(requireContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(loc, 1);
                        if (addressList.size() > 0) {
                            LatLng latLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            if (marker != null) {
                                marker.remove();
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(loc);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                            gMap.animateCamera(cameraUpdate);
                            marker = gMap.addMarker(markerOptions);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedClient.getLastLocation();

        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    if (supportMapFragment != null) {
                        supportMapFragment.getMapAsync(HomeFragment.this);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Current Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMap.addMarker(markerOptions);
        // Enable the user's current location on the map
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

            // Get the view of the My Location button
            View locationButton = ((View) getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // Set the desired size of the My Location button
            int desiredSize = 186; // Set the desired size in pixels

            // Modify the layout parameters of the button
            ViewGroup.LayoutParams layoutParams = locationButton.getLayoutParams();
            layoutParams.width = desiredSize;
            layoutParams.height = desiredSize;
            locationButton.setLayoutParams(layoutParams);

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NonNull Marker marker) {
                    DocumentSnapshot document = (DocumentSnapshot) marker.getTag();
                    System.out.println("onclick window");
                    Intent intent = new Intent(requireContext(), VehicleProfileActivity.class);
                    intent.putExtra("type", document.getString("vehicleType"));
                    intent.putExtra("vehicleId", document.getString("vehicleID"));
                    startActivity(intent);
                }
            });

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // Permission is granted, proceed to retrieve the current location
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        Task<Location> task = fusedClient.getLastLocation();

        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    // TODO: Handle the retrieved location
                }
            }
        });

        task.addOnFailureListener(requireActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO: Handle the failure to retrieve the location
            }
        });
    }

    private void getCarObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db.collection("vehicles")
                .document("cars")
                .collection("cars")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                        progressDialog.dismiss(); // Dismiss the progress dialog
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getBikeObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vehicles")
                .document("bikes")
                .collection("bikes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getSegwayObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vehicles")
                .document("segways")
                .collection("segways")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getHelicopterObjectsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vehicles")
                .document("helicopters")
                .collection("helicopters")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placeId = document.getString("pickUpLocation.placeId");
                            getLatLngFromPlaceId(placeId,document);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLatLngFromPlaceId(String placeId, QueryDocumentSnapshot document) {
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request).addOnCompleteListener((responseTask) -> {
            if (responseTask.isSuccessful()) {
                FetchPlaceResponse response = responseTask.getResult();
                Place place = response.getPlace();
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    Drawable pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.car_marker);
                    if(document.getString("vehicleType").equals("Helicopter")){
                        pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.helicopter_marker);
                    }else if (document.getString("vehicleType").equals("Bike")){
                        pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bike_marker);
                    }else if (document.getString("vehicleType").equals("Segway")){
                        pngDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.segway_marker);
                    }
                    int markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.25);
                    if (document.getString("vehicleType").equals("Bike")){
                        markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.08);
                    }else if (document.getString("vehicleType").equals("Helicopter")){
                        markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.2);
                    }else if (document.getString("vehicleType").equals("Segway")){
                        markerSize = (int) (pngDrawable.getIntrinsicWidth() * 0.15);
                    }
                    Bitmap bitmap = Bitmap.createBitmap(markerSize, markerSize, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    pngDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

                    pngDrawable.draw(canvas);
                    float rotationDegree = getRandomRotationDegree();
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(place.getName())
                            .rotation(rotationDegree)
                            .icon(markerIcon);
                    if (document.getString("vehicleType").equals("Bike")){
                        markerOptions.rotation(0);
                    }else if (document.getString("vehicleType").equals("Segway")){
                        markerOptions.rotation(0);
                    }

                    Marker newMarker =  gMap.addMarker(markerOptions);
                    newMarker.setTitle(document.getString("pickUpLocation.address") + " to " + document.getString("dropOffLocation.address"));
                    newMarker.setSnippet("Departure time: "+document.getLong("time.hour") +":"+document.getLong("time.minute")+" "+document.getLong("time.day")+"/"
                            +document.getLong("time.month"));
                    newMarker.setTag(document);

                }
            } else {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float getRandomRotationDegree() {
        return new Random().nextFloat() * 360;
    }

}