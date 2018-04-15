package com.enterprise.pc.applicationlocation;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.enterprise.pc.applicationlocation.db.AppDatabase;
import com.enterprise.pc.applicationlocation.db.entity.LocationData;
import com.enterprise.pc.applicationlocation.vm.LocationDataViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    Button buttonStart;
    Button buttonStop;
    TextView textViewTextToDisplayLineFirst;
    TextView textViewTextToDisplayLineSecond;
    LocationDataViewModel locationDataViewModel;
    SurfaceView surfaceView;


    int count = 1;

    AppExecutors executors;
    AppDatabase appDatabase;
    DataRepository dataRepository;

    LocationManager locationManager;
    LocationListener locationListener;

    final String filenameOfLocationDataOutput = "locationDataOutput.txt";
    File fileLocationDataOutput;
    FileOutputStream fileOutputStreamLocationDataOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executors = new AppExecutors();

        textViewTextToDisplayLineFirst = (TextView) findViewById(R.id.textViewTextToDisplayLineFirst);
        textViewTextToDisplayLineSecond = (TextView) findViewById(R.id.textViewTextToDisplayLineSecond);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);

        locationDataViewModel = ViewModelProviders.of(this).get(LocationDataViewModel.class);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);

        addListeners();

        initializeLocationDataOutputFile();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        closeLocationDataOutputFile();

    }

    private void addListeners() {

        addDataListeners();
        addButtonListeners();
        addListenerForGPS();
    }

    private void addButtonListeners() {

        addStartButtonListener();
        addStopButtonListener();
    }

    private void addDataListeners() {

        dataRepository = ((BasicApp) getApplication()).getRepository();

        locationDataViewModel.setLocationData(dataRepository.getLocationData());

        final Observer<List<LocationData>> locationDataListObserver = new Observer<List<LocationData>>() {

            @Override
            public void onChanged(@Nullable final List<LocationData> locationDataList) {

                drawGraph(locationDataList);

                if(locationDataList != null && !locationDataList.isEmpty()){


                    int sizeOfLocationDataList = locationDataList.size();
                    LocationData locationDataElement = locationDataList.get(sizeOfLocationDataList-1);

                    if(locationDataElement != null){

                        String textToDisplayLineFirst = locationDataElement.getFormattedTime();

                        String textToDisplayLineSecond = locationDataElement.getLatitude()
                                + " " + locationDataElement.getLongitude();

                        if(textToDisplayLineFirst != null){

                            textViewTextToDisplayLineFirst.setText(textToDisplayLineFirst);
                        }

                        if(textToDisplayLineSecond != null){

                            textViewTextToDisplayLineSecond.setText(textToDisplayLineSecond + " " + sizeOfLocationDataList + " " + count);

                        }

                    }

                }

            }
        };

        locationDataViewModel.getLocationData().observe( this, locationDataListObserver);

    }



    private void addStartButtonListener() {

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                registerListenerForGPS();

            }
        });

    }

    private void drawGraph(List<LocationData> locationDataList) {

        if(surfaceView != null) {

            SurfaceHolder holder = surfaceView.getHolder();

            if (holder != null) {

                Canvas canvas = holder.lockCanvas();

                Paint paint = new Paint();
                paint.setARGB(255, 0, 255, 0);
                paint.setTextSize(32);

                executors.mainThread().execute(() -> {

                    if (canvas != null) {

                        int widthOfCanvas = canvas.getWidth();
                        int heightOfCanvas = canvas.getHeight();

                        if (locationDataList != null && !locationDataList.isEmpty()) {

                            ExtremumValues extremumValuesOfLatitude = getMaxAndMinOfLatitude(locationDataList);
                            ExtremumValues extremumValuesOfLongitude = getMaxAndMinOfLongitude(locationDataList);

                            double scaleFactorOfLatitude =
                                    Math.floor(heightOfCanvas /
                                            (extremumValuesOfLatitude.getMaxValue() - extremumValuesOfLatitude.getMinValue()));

                            double scaleFactorOfLongitude =
                                    Math.floor(widthOfCanvas /
                                            (extremumValuesOfLongitude.getMaxValue() - extremumValuesOfLongitude.getMinValue()));

                            int sizeOfLocationDataList = locationDataList.size();

                            float[] pts = new float[2 * sizeOfLocationDataList];

                            int index = 0;

                            double minValueOfLongitude = extremumValuesOfLongitude.getMinValue();
                            double minValueOfLatitude = extremumValuesOfLatitude.getMinValue();

                            for (LocationData locationData : locationDataList) {

                                if (locationData != null) {

                                    pts[index] = (float) ((locationData.getLongitude() - minValueOfLongitude) * scaleFactorOfLongitude);
                                    index++;

                                    pts[index] = (float) ((locationData.getLatitude() - minValueOfLatitude) * scaleFactorOfLatitude);
                                    pts[index] = heightOfCanvas - pts[index];

                                    index++;

                                }

                            }

                            canvas.drawLines(pts, paint);

                            canvas.drawText("Latitude Values:", 450, 200, paint);
                            String tempText = "Min: " + extremumValuesOfLatitude.getMinValue();
                            canvas.drawText(tempText, 450, 240, paint);
                            tempText = "Max: " + extremumValuesOfLatitude.getMaxValue();
                            canvas.drawText(tempText, 450, 280, paint);

                            canvas.drawText("Longitude Values:", 450, 320, paint);
                            tempText = "Min: " + extremumValuesOfLongitude.getMinValue();
                            canvas.drawText(tempText, 450, 360, paint);
                            tempText = "Max: " + extremumValuesOfLongitude.getMaxValue();
                            canvas.drawText(tempText, 450, 400, paint);

                        }

                        canvas.drawText("Test Successful!", 50, 200, paint);

                        holder.unlockCanvasAndPost(canvas);

                    }

                });

            }
        }

    }


    private ExtremumValues getMaxAndMinOfLatitude(List<LocationData> locationDataList){

        ExtremumValues extremumValues = new ExtremumValues();

        double maxValue = -1000;
        double minValue = 1000;

        if(locationDataList != null && !locationDataList.isEmpty()) {

            for (LocationData locationData : locationDataList) {

                if(locationData != null){

                    double tempLatitudeValue = locationData.getLatitude();

                    if(tempLatitudeValue > maxValue){
                        maxValue = tempLatitudeValue;
                    }

                    if(tempLatitudeValue < minValue){
                        minValue = tempLatitudeValue;
                    }

                }


            }
        }

        extremumValues.setMaxValue(maxValue);
        extremumValues.setMinValue(minValue);

        return extremumValues;
    }

    private ExtremumValues getMaxAndMinOfLongitude(List<LocationData> locationDataList){

        ExtremumValues extremumValues = new ExtremumValues();

        double maxValue = -1000;
        double minValue = 1000;

        if(locationDataList != null && !locationDataList.isEmpty()) {

            for (LocationData locationData : locationDataList) {

                if(locationData != null){

                    double tempLongitudeValue = locationData.getLongitude();

                    if(tempLongitudeValue > maxValue){
                        maxValue = tempLongitudeValue;
                    }

                    if(tempLongitudeValue < minValue){
                        minValue = tempLongitudeValue;
                    }

                }


            }
        }

        extremumValues.setMaxValue(maxValue);
        extremumValues.setMinValue(minValue);

        return extremumValues;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    private void addStopButtonListener() {

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                removeListenerForGPS();

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

        LocationData locationData = new LocationData(timeWhenDataObtained, formattedTime, location.getLatitude(),
                location.getLongitude(), location.getAltitude(), location.getAccuracy(), location.getProvider(), location.getSpeed());


        appDatabase = ((BasicApp) getApplication()).getDatabase();

        executors.storageIO().execute(() -> {

            AppDatabase.insert(appDatabase, locationData);

        });

        writeDataToFile(locationData);

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

    private void initializeLocationDataOutputFile(){

        if(isExternalStorageWritable()){

            fileLocationDataOutput = new File(getApplicationContext().getExternalFilesDir(null) + "/" + filenameOfLocationDataOutput);

            try {
                fileOutputStreamLocationDataOutput = new FileOutputStream(fileLocationDataOutput, true);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }

        }

    }

    private void closeLocationDataOutputFile(){

        try {
            fileOutputStreamLocationDataOutput.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

    private void writeDataToFile(LocationData locationData){


        if(locationData != null){

            String outputLine = count + " " + locationData.getLatitude() + " " + locationData.getLongitude() + "\n";

            try {

                if (isExternalStorageWritable()) {
                    fileOutputStreamLocationDataOutput.write(outputLine.getBytes());

                    count++;
                }

            } catch (IOException e) {
                //e.printStackTrace();
            }


        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


}
