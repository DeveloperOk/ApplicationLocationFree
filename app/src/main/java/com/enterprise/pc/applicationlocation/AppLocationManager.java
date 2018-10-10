package com.enterprise.pc.applicationlocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by PC on 2018-05-06.
 */

public class AppLocationManager implements ActivityCompat.OnRequestPermissionsResultCallback {


    private static AppLocationManager sAppLocationManager;
    private static LocationManager sLocationManager;
    private static Context context;
    private static Activity activity;

    private static LocationListener locationListener;

    private static boolean locationUpdateActive = false;

    private static LocationSettingsRequest.Builder locationSettingsRequestBuilder;
    private static LocationRequest locationRequestPriorityHighAccuracy;
    private static Task<LocationSettingsResponse> taskLocationSettingsResponse;

    public static AppLocationManager getInstance(Context inputContext, Activity inputActivity) {

        context = inputContext;
        activity = inputActivity;

        if (sLocationManager == null) {

            sLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        if (sAppLocationManager == null) {

            sAppLocationManager = new AppLocationManager();
        }

        return sAppLocationManager;

    }

    public static boolean isProviderEnabled(String provider){

        boolean retVal = false;

        if(sLocationManager != null){

            if(sLocationManager.isProviderEnabled(provider)){

                retVal = true;
            }

        }

        return retVal;

    }

    private static void registerListenerForGPSAndIfGPSIsOffAskUserToTurnOn() {

        locationRequestPriorityHighAccuracy = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(AppConstants.SmallestDisplacement)
                .setInterval(AppConstants.Interval)
                .setFastestInterval(AppConstants.FastestInterval);

        locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequestPriorityHighAccuracy);

        locationSettingsRequestBuilder.setAlwaysShow(true);

        taskLocationSettingsResponse =
                LocationServices.getSettingsClient(context).checkLocationSettings(locationSettingsRequestBuilder.build());


        taskLocationSettingsResponse.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {

                    registerListenerForGPSWhenGPSIsOn();

                    if(!isProviderEnabled(LocationManager.GPS_PROVIDER)){

                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        registerListenerForGPSWhenGPSIsOn();

                    }

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        activity,
                                        AppConstants.REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            break;

                    }
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case AppConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        registerListenerForGPSWhenGPSIsOn();

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to

                        break;
                    default:
                        break;
                }
                break;
        }
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

        registerListenerForGPSAndIfGPSIsOffAskUserToTurnOn();

    }

    private static void registerListenerForGPSWhenGPSIsOn() {

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Register the listener with the Location Manager to receive location updates

                if (sLocationManager != null) {

                    locationUpdateActive = true;
                    sLocationManager.requestLocationUpdates
                            (LocationManager.GPS_PROVIDER, AppConstants.MinTime, AppConstants.MinDistance, locationListener);

                }

            }

    }

    public void removeListenerForGPS() {

        if(sLocationManager != null && locationListener != null){

            sLocationManager.removeUpdates(locationListener);
            locationUpdateActive = false;

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
