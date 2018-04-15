package com.enterprise.pc.applicationlocation;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.enterprise.pc.applicationlocation.db.entity.LocationData;
import com.enterprise.pc.applicationlocation.vm.LocationDataViewModel;

import java.util.List;

public class LocationGraphActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    LocationDataViewModel locationDataViewModel;

    SurfaceView surfaceView;

    AppExecutors executors;

    DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_graph);

        setTitle(R.string.locationGraphActivityTitle);

        executors = new AppExecutors();

        locationDataViewModel = ViewModelProviders.of(this).get(LocationDataViewModel.class);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceViewGraph);
        surfaceView.getHolder().addCallback(this);

        addListeners();

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        addDataListeners();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    private void addListeners() {

    }


    private void addDataListeners() {

        dataRepository = ((BasicApp) getApplication()).getRepository();

        locationDataViewModel.setLocationData(dataRepository.getLocationData());

        final Observer<List<LocationData>> locationDataListObserver = new Observer<List<LocationData>>() {

            @Override
            public void onChanged(@Nullable final List<LocationData> locationDataList) {

                if(locationDataList != null && !locationDataList.isEmpty()){

                    drawGraph(locationDataList);

                }

            }
        };

        locationDataViewModel.getLocationData().observe( this, locationDataListObserver);

    }

    private void drawGraph(List<LocationData> locationDataList) {

        if(locationDataList != null && !locationDataList.isEmpty()) {

            if (surfaceView != null) {

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

                            canvas.drawText("Test Successful!", 50, 200, paint);

                            holder.unlockCanvasAndPost(canvas);

                        }

                    });

                }
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

}
