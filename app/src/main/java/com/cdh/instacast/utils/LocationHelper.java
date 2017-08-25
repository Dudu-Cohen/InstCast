package com.cdh.instacast.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.util.Calendar;

/**
 * Created by Dudu_Cohen on 19/08/2017.
 */

public class LocationHelper {

    public static Location getLocation(Context context) {
        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for passive status
        boolean isPassiveEnabled = false;

        Location location = null;

        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // getting passive status
            isPassiveEnabled = locationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);


            if (isPassiveEnabled) {
                if (location == null) {
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                }
            }
            // First get location from Network Provider
            if (isNetworkEnabled && location == null) {
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled && location == null) {
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return location;
    }
}