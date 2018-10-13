package com.enterprise.pc.applicationlocation;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
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
    Button buttonList;

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


    LocationData currentLocationData;

    AppLocationManager appLocationManager;
    LocationListener locationListener;

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
        buttonList = (Button)findViewById(R.id.buttonList);

        executors.storageIO().execute(() -> {

            dataRepository = ((BasicApp) getApplication()).getRepository();

            appDatabase = ((BasicApp) getApplication()).getDatabase();

        });

        appLocationManager = ((BasicApp) getApplication()).getAppLocationManager(this);

        setLocationDataOnCreateAndOnResume();

        addListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setLocationDataOnCreateAndOnResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(appLocationManager != null){

            appLocationManager.onActivityResult(requestCode, resultCode, data);
        }

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
        addListButtonListener();

    }

    private void setLocationDataOnCreateAndOnResume() {

        executors.storageIO().execute(() -> {

            if(dataRepository == null){

                dataRepository = ((BasicApp) getApplication()).getRepository();
            }

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

            String speed = Float.toString(locationDataElement.getSpeed());

            if(speed != null){
                textViewSpeedValue.setText(speed);
            }

            String accuracy = Float.toString(locationDataElement.getAccuracy());

            if(accuracy != null){
                textViewAccuracyValue.setText(accuracy);
            }

            String bearing = Float.toString(locationDataElement.getBearing());

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

                appLocationManager.registerListenerForGPS(MainActivity.this);

            }
        });

    }


    private void addStopButtonListener() {

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                appLocationManager.removeListenerForGPS();

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

    private void addListButtonListener() {

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                Intent intent = new Intent(getApplicationContext(), ListDataActivity.class);
                startActivity(intent);

            }
        });

    }


    private void addListenerForGPS() {

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

        if(appLocationManager != null){

            appLocationManager.setLocationListener(locationListener);

            appLocationManager.checkPermissionAndRegisterListenerForGPS(this);

        }

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


        executors.mainThread().execute(() -> {

            currentLocationData = locationData;
            setTextsOfTextViews(locationData);
        });

        executors.storageIO().execute(() -> {

            if(appDatabase == null){

                appDatabase = ((BasicApp) getApplication()).getDatabase();
            }

            AppDatabase.insert(appDatabase, locationData);

        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(appLocationManager != null){

            appLocationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


}
