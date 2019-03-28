package com.enterprise.pc.applicationlocationfree;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
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

public class AppLocationManager {


    private static AppLocationManager sAppLocationManager;
    private static LocationManager sLocationManager;
    private static Context context;

    private static Activity mActivity;

    private static LocationListener locationListener;

    private static boolean locationUpdateActive = false;

    private static LocationSettingsRequest.Builder locationSettingsRequestBuilder;
    private static LocationRequest locationRequestPriorityHighAccuracy;
    private static Task<LocationSettingsResponse> taskLocationSettingsResponse;
    private static ResolvableApiException resolvable;

    private static boolean previousStatusIsProviderEnabled = false;
    private static boolean currentStatusIsProviderEnabled = false;

    private static AlertDialog.Builder alertDialogBuilderToTurnOnGPS;
    private static AlertDialog alertDialogToTurnOnGPS;

    private static boolean comingFromLocationSettingsPageFlag = false;

    public static AppLocationManager getInstance(Context inputContext, Activity activity) {

        context = inputContext;
        mActivity = activity;

        if (sLocationManager == null) {

            sLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        if (sAppLocationManager == null) {

            sAppLocationManager = new AppLocationManager();
        }

        return sAppLocationManager;

    }

    public void registerListenerForGPSWhenReturningFromLocationSettingsPage(Activity activity){

        mActivity = activity;

        if(isComingFromLocationSettingsPageFlag() == true){

            registerListenerForGPS(activity);

        }

    }

    public static boolean isComingFromLocationSettingsPageFlag() {
        return comingFromLocationSettingsPageFlag;
    }

    public static void setComingFromLocationSettingsPageFlag(boolean comingFromLocationSettingsPageFlag) {
        AppLocationManager.comingFromLocationSettingsPageFlag = comingFromLocationSettingsPageFlag;
    }

    private static void instantiateAlertDialogAndShow(Activity activity) {

        alertDialogBuilderToTurnOnGPS = new AlertDialog.Builder(activity);

        alertDialogBuilderToTurnOnGPS.setTitle(R.string.turn_on_gps_dialog_title);
        alertDialogBuilderToTurnOnGPS.setMessage(R.string.turn_on_gps_dialog_message);
        alertDialogBuilderToTurnOnGPS.setPositiveButton(R.string.turn_on_gps_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                comingFromLocationSettingsPageFlag = true;
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);

            }
        });
        alertDialogBuilderToTurnOnGPS.setNegativeButton(R.string.turn_on_gps_dialog_negative_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialogToTurnOnGPS = alertDialogBuilderToTurnOnGPS.create();

        alertDialogToTurnOnGPS.show();


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


    private static void registerListenerForGPSAndIfGPSIsOffAskUserToTurnOn(Activity activity) {

        mActivity = activity;

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

                     previousStatusIsProviderEnabled = isProviderEnabled(LocationManager.GPS_PROVIDER);

                     if(previousStatusIsProviderEnabled == true){

                         setComingFromLocationSettingsPageFlag(false);
                         registerListenerForGPSWhenGPSIsOn();
                     }else{

                         LocationSettingsResponse response = task.getResult(ApiException.class);
                         // All location settings are satisfied. The client can initialize location
                         // requests here.

                     }

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.

                            try {

                                // Cast to a resolvable exception.
                                resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().

                                if(activity != null){

                                    if(isComingFromLocationSettingsPageFlag() != true){

                                        instantiateAlertDialogAndShow(activity);
                                    }
                                    setComingFromLocationSettingsPageFlag(false);

                                }


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

                currentStatusIsProviderEnabled = isProviderEnabled(LocationManager.GPS_PROVIDER);

                if(currentStatusIsProviderEnabled == true){

                    registerListenerForGPSWhenGPSIsOn();
                }

                //There is problem in getting right resultCode so solution above is implemented.

                //switch (resultCode) {
                //    case Activity.RESULT_OK:
                //        // All required changes were successfully made
//
                //        registerListenerForGPSWhenGPSIsOn();
//
                //        break;
                //    case Activity.RESULT_CANCELED:
                //        // The user was asked to change settings, but chose not to
//
                //        String test = "test";
                //        String test2 = test;
//
                //        break;
                //    default:
                //        break;
                //}

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

        mActivity = activity;

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

            registerListenerForGPS(activity);

        }

    }

    public void registerListenerForGPS(Activity activity) {

        mActivity = activity;
        registerListenerForGPSAndIfGPSIsOffAskUserToTurnOn(activity);

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

            }else{

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }

    }

    public static void removeListenerForGPS() {

        if(sLocationManager != null && locationListener != null){

            sLocationManager.removeUpdates(locationListener);
            locationUpdateActive = false;

        }

    }



    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // ACCESS_FINE_LOCATION-related task you need to do.

                    registerListenerForGPS(mActivity);


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
