package com.enterprise.pc.applicationlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.enterprise.pc.applicationlocation.db.AppDatabase;
import com.enterprise.pc.applicationlocation.db.entity.LocationData;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity{

    Button buttonStart;
    Button buttonStop;
    Button buttonGraph;
    Button buttonExport;
    Button buttonAdd;

    TextView textViewTimeValue;
    TextView textViewLatitudeValue;
    TextView textViewLongitudeValue;
    TextView textViewAltitudeValue;
    TextView textViewAccuracyValue;
    TextView textViewSpeedValue;
    TextView textViewBearingValue;
    TextView textViewProviderValue;


    AppExecutors executors;
    AppDatabase appDatabase;
    DataRepository dataRepository;

    LocationManager locationManager;
    LocationListener locationListener;

    LocationData currentLocationData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executors = new AppExecutors();

        initializeTextViews();

        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);
        buttonGraph = (Button)findViewById(R.id.buttonGraph);
        buttonExport = (Button)findViewById(R.id.buttonExport);
        buttonAdd = (Button)findViewById(R.id.buttonAdd);

        dataRepository = ((BasicApp) getApplication()).getRepository();

        appDatabase = ((BasicApp) getApplication()).getDatabase();

        setLocationDataOnCreate();

        addListeners();

    }

    private void initializeTextViews() {
        textViewTimeValue = (TextView) findViewById(R.id.textViewTimeValue);
        textViewLatitudeValue = (TextView) findViewById(R.id.textViewLatitudeValue);
        textViewLongitudeValue = (TextView) findViewById(R.id.textViewLongitudeValue);
        textViewAltitudeValue = (TextView) findViewById(R.id.textViewAltitudeValue);
        textViewAccuracyValue = (TextView) findViewById(R.id.textViewAccuracyValue);
        textViewSpeedValue = (TextView) findViewById(R.id.textViewSpeedValue);
        textViewBearingValue = (TextView) findViewById(R.id.textViewBearingValue);
        textViewProviderValue = (TextView) findViewById(R.id.textViewProviderValue);
    }


    private void addListeners() {

        addButtonListeners();
        addListenerForGPS();
    }

    private void addButtonListeners() {

        addStartButtonListener();
        addStopButtonListener();
        addGraphButtonListener();
        addExportButtonListener();
        addAddButtonListener();

    }

    private void setLocationDataOnCreate() {

        if(dataRepository == null){

            dataRepository = ((BasicApp) getApplication()).getRepository();
        }

        executors.storageIO().execute(() -> {

            LocationData locationData = dataRepository.loadLocationDataHavingNewestTimeSync();

            executors.mainThread().execute(() -> {

                currentLocationData = locationData;
                setTextsOfTextViews(locationData);
            });

        });

    }

    private void setTextsOfTextViews(LocationData locationDataElement) {

        if(locationDataElement != null){

            String formattedTime = locationDataElement.getFormattedTime();

            if(formattedTime != null){
                textViewTimeValue.setText(formattedTime.replace(getString(R.string.str_old_value), getString(R.string.str_new_value)));
            }

            String latitude = Double.toString(locationDataElement.getLatitude());

            if(latitude != null){
                textViewLatitudeValue.setText(latitude);
            }

            String longitude = Double.toString(locationDataElement.getLongitude());

            if(longitude != null){
                textViewLongitudeValue.setText(longitude);
            }

            String altitude = Double.toString(locationDataElement.getAltitude());

            if(altitude != null){
                textViewAltitudeValue.setText(altitude);
            }

            String speed = Double.toString(locationDataElement.getSpeed());

            if(speed != null){
                textViewSpeedValue.setText(speed);
            }

            String accuracy = Double.toString(locationDataElement.getAccuracy());

            if(accuracy != null){
                textViewAccuracyValue.setText(accuracy);
            }

            String bearing = Double.toString(locationDataElement.getBearing());

            if(bearing != null){
                textViewBearingValue.setText(bearing);
            }

            String provider = locationDataElement.getProvider();

            if(provider != null){
                textViewProviderValue.setText(provider);
            }

        }

    }


    private void addStartButtonListener() {

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                registerListenerForGPS();

            }
        });

    }


    private void addStopButtonListener() {

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                removeListenerForGPS();

            }
        });

    }


    private void addGraphButtonListener() {

        buttonGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                Intent intent = new Intent(getApplicationContext(), LocationGraphActivity.class);
                startActivity(intent);

            }
        });

    }

    private void addExportButtonListener() {

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                Intent intent = new Intent(getApplicationContext(), DataExportActivity.class);
                startActivity(intent);

            }
        });

    }

    private void addAddButtonListener() {

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                Intent intent = new Intent(getApplicationContext(), DataAddInformationActivity.class);
                intent.putExtra(AppConstants.CurrentLocationData, currentLocationData);
                startActivity(intent);

            }
        });

    }

    private void addListenerForGPS() {

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                useObtainedLocationData(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        checkPermissionAndRegisterListenerForGPS();

    }


    private void checkPermissionAndRegisterListenerForGPS() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {

            // Permission has already been granted

            registerListenerForGPS();

        }

    }


    private void registerListenerForGPS() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }


    private void removeListenerForGPS() {

        // Remove the listener you previously added
        locationManager.removeUpdates(locationListener);

    }


    private void useObtainedLocationData(Location location) {

        long timeWhenDataObtained = location.getTime();

        Date date = new Date(timeWhenDataObtained);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        String formattedTime = simpleDateFormat.format(date);

        //TODO: Replace 0's with methods: location.getVerticalAccuracyMeters(), location.getSpeedAccuracyMetersPerSecond(), location.getBearingAccuracyDegrees()
        LocationData locationData = new LocationData(timeWhenDataObtained, formattedTime, location.getLatitude(), location.getLongitude(),
                location.getAltitude(), location.getAccuracy(), location.getProvider(), location.getSpeed(),
                location.getBearing(), 0, 0, 0, "");

        if(appDatabase == null){

            appDatabase = ((BasicApp) getApplication()).getDatabase();
        }

        executors.mainThread().execute(() -> {

            currentLocationData = locationData;
            setTextsOfTextViews(locationData);
        });

        executors.storageIO().execute(() -> {

            AppDatabase.insert(appDatabase, locationData);

        });


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
