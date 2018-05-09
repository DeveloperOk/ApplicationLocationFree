package com.enterprise.pc.applicationlocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by PC on 2018-05-06.
 */

public class AppLocationManager implements ActivityCompat.OnRequestPermissionsResultCallback{


    private static AppLocationManager sAppLocationManager;
    private static LocationManager sLocationManager;
    private static Context context;

    private static LocationListener locationListener;

    private static boolean locationUpdateActive = false;


    public static AppLocationManager getInstance(Context inputContext) {

        context = inputContext;

        if (sLocationManager == null) {

            sLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        if (sAppLocationManager == null) {

            sAppLocationManager = new AppLocationManager();
        }

        return sAppLocationManager;

    }

    public static LocationListener getLocationListener() {
        return locationListener;
    }

    public static void setLocationListener(LocationListener LocationListener) {
        AppLocationManager.locationListener = LocationListener;
    }

    public static boolean isLocationUpdateActive() {
        return locationUpdateActive;
    }

    //public static void setLocationUpdateActive(boolean locationUpdateActive) {
    //    AppLocationManager.locationUpdateActive = locationUpdateActive;
    //}

    public void checkPermissionAndRegisterListenerForGPS(Activity activity) {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {

            // Permission has already been granted

            registerListenerForGPS();

        }

    }

    public void registerListenerForGPS() {

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Register the listener with the Location Manager to receive location updates

                if (sLocationManager != null) {

                    locationUpdateActive = true;
                    sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }

            }

    }

    public void removeListenerForGPS() {

        if(sLocationManager != null && locationListener != null){

            locationUpdateActive = false;
            sLocationManager.removeUpdates(locationListener);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // ACCESS_FINE_LOCATION-related task you need to do.

                    registerListenerForGPS();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}
