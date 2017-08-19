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

public class LocationHelper implements LocationListener {

    LocationManager mLocationManager;
    OnLocationReady mOnLocationReady;
    Context mContext;

    public LocationHelper(Context context) {
        this.mContext = context;
    }

    public void getCurrentLocation(OnLocationReady onLocationReady) {

        mOnLocationReady = onLocationReady;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // ignore the permission err we ask it in the main activity
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // if location exists and last time we ask for location is less then two min return last know location
        if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            // Do something with the recent location fix
            //  otherwise wait for the update below
            if(onLocationReady != null){
                onLocationReady.onLocationReady(location);
            }
        }

        // else ask for new location
        else {

            // ignore the permission err we ask it in the main activity
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

    }


    public void onLocationChanged(Location location) {
        if (location != null) {
            mLocationManager.removeUpdates(this);
            if(mOnLocationReady != null){
                mOnLocationReady.onLocationReady(location);
            }
        }
    }

    // Required functions
    public void onProviderDisabled(String arg0) {
         if(mOnLocationReady != null){
             mOnLocationReady.onLocationDisabled();
         }
    }

    public void onProviderEnabled(String arg0) {}

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

    /**
     * On Location Ready interface
     * Will invoked when location is ready
     */
    public interface OnLocationReady{
       void onLocationReady(Location location);
       void onLocationDisabled();
    }

}