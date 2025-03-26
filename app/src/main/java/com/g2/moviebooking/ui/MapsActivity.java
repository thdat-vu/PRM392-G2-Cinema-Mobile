package com.g2.moviebooking.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Theatre;
import com.g2.moviebooking.data.repository.TheatreRepository;
import com.g2.moviebooking.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<Theatre> theatreList; // from DB or API
    private TheatreRepository theatreRepository;

    // Variables to hold theatre data (if provided)
    private double theatreLat;
    private double theatreLng;
    private String theatreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        theatreRepository = new TheatreRepository();

        // Get passed theatre data, if any
        theatreLat = getIntent().getDoubleExtra("theatre_lat", 0);
        theatreLng = getIntent().getDoubleExtra("theatre_lng", 0);
        theatreName = getIntent().getStringExtra("theatre_name");

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize location client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Load cinema data from Firebase
        // loadTheatresFromDb();

        // Check location permission
        // checkLocationPermission();
    }

    private void loadTheatresFromDb() {
        theatreRepository.getAllTheatres(new TheatreRepository.TheatreCallback<>() {
            @Override
            public void onSuccess(List<Theatre> result) {
                theatreList = result;
            }

            @Override
            public void onFailure(String error) {
                Utils.showToast(MapsActivity.this, error);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // If theatre coordinates are provided, center on the theatre and add its marker.
        if (theatreLat != 0 && theatreLng != 0) {
            LatLng theatreLatLng = new LatLng(theatreLat, theatreLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(theatreLatLng, 16f));
            mMap.addMarker(new MarkerOptions()
                    .position(theatreLatLng)
                    .title(theatreName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        } else {
            getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                // Permission denied
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double userLat = location.getLatitude();
                        double userLng = location.getLongitude();

                        // Now that we have user location, show cinemas on map
                        showCinemasOnMap(userLat, userLng);
                    }
                });
    }

    private void showCinemasOnMap(double userLat, double userLng) {
        if (mMap == null) return;

        // Center camera on user
        LatLng userLatLng = new LatLng(userLat, userLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f));

        // Mark user location
        mMap.addMarker(new MarkerOptions()
                .position(userLatLng)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        // Add markers for cinemas
        for (Theatre c : theatreList) {
            float distance = calculateDistance(userLat, userLng, c.getLatitude(), c.getLongitude());
            c.setDistance(distance);

            LatLng cinemaLatLng = new LatLng(c.getLatitude(), c.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(cinemaLatLng)
                    .title(c.getName())
                    .snippet(String.format("%.2f km away", distance / 1000f)));
        }
    }

    private float calculateDistance(double userLat, double userLng, double cinemaLat, double cinemaLng) {
        float[] results = new float[1];
        Location.distanceBetween(userLat, userLng, cinemaLat, cinemaLng, results);
        return results[0]; // meters
    }
}


