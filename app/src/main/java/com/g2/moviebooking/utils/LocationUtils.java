package com.g2.moviebooking.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

public class LocationUtils {

    public interface LocationCallback {
        void onLocationResult(double latitude, double longitude);
        void onLocationFailure(Exception e);
    }

    public static void getCurrentLocation(Context context,
                                          FusedLocationProviderClient fusedLocationProviderClient,
                                          LocationCallback callback) {
        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
            callback.onLocationFailure(new SecurityException("Location permission not granted"));
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocationResult(location.getLatitude(), location.getLongitude());
                    } else {
                        callback.onLocationFailure(new Exception("Location is null"));
                    }
                })
                .addOnFailureListener(callback::onLocationFailure);
    }
}

