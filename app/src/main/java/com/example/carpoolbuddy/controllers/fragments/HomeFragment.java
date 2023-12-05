package com.example.carpoolbuddy.controllers.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.carpoolbuddy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, SearchView.OnQueryTextListener {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private MapView mapView;
    private GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude;
    private double currentLongitude;

    private SearchView searchView;
    private String searchQuery;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the MapView
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Request location permissions from the user
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, proceed with map initialization
            initializeMap();
        }
        searchView = view.findViewById(R.id.search_view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(this);  // Set the OnQueryTextListener
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        System.out.println(query+"query!!!!");
        searchQuery = query;
        performSearch(query);  // Call performSearch() method
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Handle text changes in the search bar, if needed
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Enable the user's current location on the map
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Add a marker on a sample location
        LatLng location = new LatLng(37.7749, -122.4194);
        googleMap.addMarker(new MarkerOptions().position(location).title("Sample Marker"));

        // Get the current location and move the camera
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    // Add a marker for the current location
                    LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                    googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    // Zoom in on the current location
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
                }
            }
        });
    }
    private void initializeMap() {
        mapView.getMapAsync(this);
    }

    private void performSearch(String query) {
        // Create a new PlacesClient
        PlacesClient placesClient = Places.createClient(requireContext());

        // Create a new AutocompleteSessionToken
        AutocompleteSessionToken sessionToken = AutocompleteSessionToken.newInstance();

        // Create a FindAutocompletePredictionsRequest
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(query)
                .build();

        // Perform the search
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            // Handle the search results
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            // Process the predictions as needed

            if (!predictions.isEmpty()) {
                // Get the first prediction
                AutocompletePrediction prediction = predictions.get(0);

                // Get the place ID of the prediction
                String placeId = prediction.getPlaceId();

                // Fetch the place details using the place ID
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, Arrays.asList(Place.Field.LAT_LNG)).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((fetchPlaceResponse) -> {
                    // Get the fetched place
                    Place place = fetchPlaceResponse.getPlace();

                    // Get the LatLng of the place
                    LatLng location = place.getLatLng();

                    // Add a marker for the searched location
                    googleMap.addMarker(new MarkerOptions().position(location).title(query));

                    // Zoom in on the searched location
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
                }).addOnFailureListener((exception) -> {
                    // Handle any errors
                    Toast.makeText(requireContext(), "Failed to fetch place details: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(requireContext(), "No search results found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener((exception) -> {
            // Handle any errors
            Toast.makeText(requireContext(), "Search failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }}