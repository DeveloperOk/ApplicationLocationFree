package com.enterprise.pc.applicationlocation;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.enterprise.pc.applicationlocation.db.entity.LocationData;
import com.enterprise.pc.applicationlocation.vm.DateInformation;
import com.enterprise.pc.applicationlocation.vm.LocationDataViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationGraphActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    LocationDataViewModel locationDataViewModel;

    SurfaceView surfaceView;
    private ScaleGestureDetector scaleDetectorGraph;
    private ScaleGestureDetector.OnScaleGestureListener scaleGestureListener;

    private GestureDetector gestureDetector;
    private GestureDetector.SimpleOnGestureListener gestureListener;

    AppExecutors executors;

    DataRepository dataRepository;

    TextView textViewMinLongitudeValue;
    TextView textViewMaxLongitudeValue;
    TextView textViewMinLatitudeValue;
    TextView textViewMaxLatitudeValue;

    DateInformation startDate;
    DateInformation endDate;

    List<LocationData> mlocationDataList;

    boolean showInformationFlag = false;
    ToggleButton toggleButtonShowInformation;

    boolean initializedFlag = false;

    AppLocationManager appLocationManager;

    Button buttonStart;
    Button buttonStop;

    Observer<List<LocationData>> locationDataListObserver = null;
    boolean liveDataFlag = false;

    boolean locationUpdateActiveOnCreate = false;

    ToggleButton toggleButtonLiveGraph;

    Button buttonSetStartTime;

    boolean scalingInProgress = false;

    ConstraintLayout surfaceViewGraphOutlineOuterUpperPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_graph);


        setTitle(R.string.locationGraphActivityTitle);

        executors = new AppExecutors();

        locationDataViewModel = ViewModelProviders.of(this).get(LocationDataViewModel.class);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceViewGraph);
        surfaceView.getHolder().addCallback(this);

        textViewMinLongitudeValue = (TextView) findViewById(R.id.textViewMinLongitudeValue);
        textViewMaxLongitudeValue = (TextView) findViewById(R.id.textViewMaxLongitudeValue);
        textViewMinLatitudeValue = (TextView) findViewById(R.id.textViewMinLatitudeValue);
        textViewMaxLatitudeValue = (TextView) findViewById(R.id.textViewMaxLatitudeValue);

        DataBindingUtil.inflate(getLayoutInflater(), R.layout.select_date_and_time_layout, findViewById(R.id.controlsStartTime), true);

        DataBindingUtil.inflate(getLayoutInflater(), R.layout.select_date_and_time_layout, findViewById(R.id.controlsEndTime), true);

        ((TextView) findViewById(R.id.controlsStartTime).findViewById(R.id.textViewHeader)).setText(R.string.controls_start_time_label);
        ((TextView) findViewById(R.id.controlsEndTime).findViewById(R.id.textViewHeader)).setText(R.string.controls_end_time_label);

        toggleButtonShowInformation = (ToggleButton) findViewById(R.id.toggleButtonInformation);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        toggleButtonLiveGraph = (ToggleButton) findViewById(R.id.toggleButtonLiveGraph);
        buttonSetStartTime = (Button) findViewById(R.id.buttonSetStartTime);


        startDate = new DateInformation();
        endDate = new DateInformation();

        appLocationManager = ((BasicApp) getApplication()).getAppLocationManager();

        addListeners();

        removeLocationListenerOnCreate();

        initializeScaleGraph();
        initializeScrollGraph();

        surfaceViewGraphOutlineOuterUpperPart = findViewById(R.id.surfaceViewGraphOutlineOuterUpperPart);


    }

    private void initializeScrollGraph() {

        gestureListener
                = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {

                if(!scalingInProgress){

                    if(surfaceView != null){

                        surfaceView.setX(surfaceView.getX() - distanceX);
                        surfaceView.setY(surfaceView.getY() - distanceY);
                    }

                }

                return true;
            }

        };

        if(surfaceView != null) {

            if(gestureListener != null) {

                gestureDetector = new GestureDetector(surfaceView.getContext(), gestureListener);
            }
        }

    }

    private void initializeScaleGraph() {

        scaleGestureListener
                = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            private float lastSpanX;
            private float lastSpanY;

            float lengthX;
            float lengthY;

            float scaleFactorX = 1;
            float scaleFactorY = 1;

            float xOfPointOfInterest;
            float yOfPointOfInterest;

            float pivotX;
            float pivotY;

            float maxWidth = 8000;
            float maxHeight = 8000;

            float minWidth = 10;
            float minHeight = 10;

            float xOfScaleCenter;
            float yOfScaleCenter;

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {

                scalingInProgress = true;

                if(scaleGestureDetector != null){

                    if(surfaceView != null){

                        surfaceView.setPivotX(0);
                        surfaceView.setPivotY(0);

                        lastSpanX = scaleGestureDetector.
                                getCurrentSpanX();
                        lastSpanY = scaleGestureDetector.
                                getCurrentSpanY();

                        float focusX = scaleGestureDetector.getFocusX();
                        float focusY = scaleGestureDetector.getFocusY();

                        int[] outLocationSurfaceViewGraphOutlineOuterUpperPart = new int[2];

                        if(surfaceViewGraphOutlineOuterUpperPart != null){

                            surfaceViewGraphOutlineOuterUpperPart.getLocationOnScreen(outLocationSurfaceViewGraphOutlineOuterUpperPart);
                            float xPositionOfSurfaceViewGraphOutlineOuterUpperPart = outLocationSurfaceViewGraphOutlineOuterUpperPart[0];
                            float yPositionOfSurfaceViewGraphOutlineOuterUpperPart = outLocationSurfaceViewGraphOutlineOuterUpperPart[1];

                            xOfPointOfInterest = focusX - xPositionOfSurfaceViewGraphOutlineOuterUpperPart;
                            yOfPointOfInterest = focusY - yPositionOfSurfaceViewGraphOutlineOuterUpperPart;

                            if(areFocusXandFocusYOnSurfaceView(xOfPointOfInterest, yOfPointOfInterest)){

                                lengthX = xOfPointOfInterest - surfaceView.getX();
                                lengthY = yOfPointOfInterest - surfaceView.getY();

                                xOfScaleCenter = xOfPointOfInterest;
                                yOfScaleCenter = yOfPointOfInterest;

                            }else{

                                lengthX = (float) (surfaceView.getMeasuredWidth()/2.0);
                                lengthY = (float) (surfaceView.getMeasuredHeight()/2.0);

                                xOfScaleCenter = surfaceView.getX() + lengthX;
                                yOfScaleCenter = surfaceView.getY() + lengthY;

                            }

                            pivotX = lengthX;
                            pivotY = lengthY;

                            surfaceView.setPivotX(pivotX);
                            surfaceView.setPivotY(pivotY);


                        }
                    }
                }

                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

                if(scaleGestureDetector != null){

                    float spanX = scaleGestureDetector.
                            getCurrentSpanX();
                    float spanY = scaleGestureDetector.
                            getCurrentSpanY();

                    if(lastSpanX != 0 && lastSpanY != 0){

                        scaleFactorX = spanX / lastSpanX;
                        scaleFactorY = spanY / lastSpanY;

                        if(surfaceView != null) {

                            surfaceView.setScaleX(scaleFactorX);
                            surfaceView.setScaleY(scaleFactorY);
                        }
                    }

                }

                return true;
            }


            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

                if(scaleGestureDetector != null){

                    if(surfaceView != null){

                        surfaceView.setPivotX(0);
                        surfaceView.setPivotY(0);

                        scaleFactorX = surfaceView.getScaleX();
                        scaleFactorY = surfaceView.getScaleY();

                        surfaceView.setScaleX(1);
                        surfaceView.setScaleY(1);

                        int surfaceViewMeasuredWidth = surfaceView.getMeasuredWidth();
                        int surfaceViewMeasuredHeight = surfaceView.getMeasuredHeight();

                        int scaledWidth = (int) (surfaceViewMeasuredWidth * scaleFactorX);
                        int scaledHeight = (int) (surfaceViewMeasuredHeight * scaleFactorY);

                        if(scaledWidth < minWidth){

                            if(surfaceViewMeasuredWidth != 0){
                                scaleFactorX = minWidth / surfaceViewMeasuredWidth;
                            }

                            surfaceView.getLayoutParams().width = (int) minWidth;

                        }else if(scaledWidth <= maxWidth){

                            surfaceView.getLayoutParams().width = scaledWidth;

                        }else{

                            if(surfaceViewMeasuredWidth != 0){
                                scaleFactorX = maxWidth / surfaceViewMeasuredWidth;
                            }

                            surfaceView.getLayoutParams().width = (int) maxWidth;

                        }


                        if(scaledHeight < minHeight){

                            if(surfaceViewMeasuredHeight != 0) {
                                scaleFactorY = minHeight / surfaceViewMeasuredHeight;
                            }

                            surfaceView.getLayoutParams().height = (int) minHeight;

                        }else if(scaledHeight <= maxHeight){

                            surfaceView.getLayoutParams().height = scaledHeight;

                        }else{

                            if(surfaceViewMeasuredHeight != 0) {
                                scaleFactorY = maxHeight / surfaceViewMeasuredHeight;
                            }

                            surfaceView.getLayoutParams().height = (int) maxHeight;

                        }


                        surfaceView.requestLayout();

                        surfaceView.setX(xOfScaleCenter - lengthX*scaleFactorX);
                        surfaceView.setY(yOfScaleCenter - lengthY*scaleFactorY);

                    }
                }

                scalingInProgress = false;

            }


        };


        if(surfaceView != null){

            if(scaleGestureListener != null){

                scaleDetectorGraph = new ScaleGestureDetector(surfaceView.getContext(), scaleGestureListener);
            }
        }


    }


    private boolean areFocusXandFocusYOnSurfaceView(float xOfPointOfInterest, float yOfPointOfInterest) {

        boolean retVal = false;

        if(surfaceView != null){

            float surfaceViewX =surfaceView.getX();
            float surfaceViewY =surfaceView.getY();

            if((surfaceViewX <= xOfPointOfInterest && xOfPointOfInterest <= (surfaceViewX+surfaceView.getMeasuredWidth())) &&
                    (surfaceViewY<= yOfPointOfInterest && yOfPointOfInterest <= (surfaceViewY+surfaceView.getMeasuredHeight()))) {

                retVal = true;
            }
        }

        return retVal;

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean retVal = scaleDetectorGraph.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }


    private void removeLocationListenerOnCreate(){

        if (appLocationManager != null) {

            if (appLocationManager.isLocationUpdateActive() == true){

                locationUpdateActiveOnCreate = true;
                appLocationManager.removeListenerForGPS();
            }

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        addDataListeners();
        setLimitsOfControls();
        addNumberPickerListenersOfStartTime();
        addNumberPickerListenersOfEndTime();

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {

        drawGraph(mlocationDataList, showInformationFlag);

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    private void addListeners() {
        addButtonListeners();
    }

    private void addButtonListeners() {
        addToggleButtonShowInformationListener();
        addStartButtonListener();
        addStopButtonListener();
        addToggleButtonLiveGraphListener();
        addSetStartTimeButtonListener();
    }

    private void addToggleButtonShowInformationListener() {

        toggleButtonShowInformation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    showInformationFlag = true;
                    drawGraph(mlocationDataList, showInformationFlag);

                } else {

                    showInformationFlag = false;
                    drawGraph(mlocationDataList, showInformationFlag);

                }
            }
        });

    }

    private void addToggleButtonLiveGraphListener() {

        toggleButtonLiveGraph.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    liveDataFlag = true;
                    readDatabasePeriodically(AppConstants.DatabaseReadSleepDurationMillisecond);

                } else {

                    liveDataFlag = false;

                }
            }
        });

    }

    private void readDatabasePeriodically(int sleepDuration) {

        executors.databaseReadPeriodicallyThread().execute(() -> {

            while (liveDataFlag) {

                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                updateGraphAndValuesOfControlsForFixedStartTime();

            }

        });

    }


    private void addStartButtonListener() {

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                if(appLocationManager != null){

                    appLocationManager.registerListenerForGPS();
                }

            }
        });

    }

    private void addStopButtonListener() {

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                if(appLocationManager != null){

                    appLocationManager.removeListenerForGPS();
                }

            }
        });

    }

    private void addSetStartTimeButtonListener() {

        buttonSetStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                Date date = new Date();
                setValuesOfStartTimeControls(date.getTime(), findViewById(R.id.controlsStartTime));
                updateGraphAndValuesOfControlsForFixedStartTime();

            }
        });

    }



    private void addNumberPickerListenersOfStartTime() {

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setYear(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setMonth(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setDay(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setHour(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setMinute(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setSecond(newValue);
                doOnValueChange();

            }
        });

    }

    private void addNumberPickerListenersOfEndTime() {

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setYear(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setMonth(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setDay(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setHour(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setMinute(newValue);
                doOnValueChange();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setSecond(newValue);
                doOnValueChange();

            }
        });

    }

    private void addDataListeners() {

        if(locationDataViewModel != null){

            executors.storageIO().execute(() -> {

                if(dataRepository == null){

                    dataRepository = ((BasicApp) getApplication()).getRepository();
                }

                locationDataViewModel.setLocationData(dataRepository.getLocationData());

                locationDataListObserver = new Observer<List<LocationData>>() {

                    @Override
                    public void onChanged(@Nullable final List<LocationData> locationDataList) {


                        executors.mainThread().execute(() -> {

                            if (locationDataList != null && !locationDataList.isEmpty()) {

                                mlocationDataList = locationDataList;

                                drawGraph(locationDataList, showInformationFlag);
                                initializedFlag = true;

                                if (appLocationManager != null) {
                                    if (locationUpdateActiveOnCreate == true) {
                                        appLocationManager.registerListenerForGPS();
                                    }
                                }


                                if (locationDataViewModel != null) {
                                    if (locationDataListObserver != null) {

                                        locationDataViewModel.getLocationData().removeObserver(locationDataListObserver);
                                    }

                                }


                            }
                        });

                    }
                };

                locationDataViewModel.getLocationData().observe(this, locationDataListObserver);

            });
        }

    }

    private void updateValuesOfControls(){

        executors.storageIO().execute(() -> {

            if(dataRepository == null){
                dataRepository = ((BasicApp) getApplication()).getRepository();
            }

            long oldestTimeMs = dataRepository.getOldestTimeMsSync();
            long newestTimeMs = dataRepository.getNewestTimeMsSync();

            executors.mainThread().execute(() -> {

                setValuesOfStartTimeControls(oldestTimeMs, findViewById(R.id.controlsStartTime));
                setValuesOfEndTimeControls(newestTimeMs, findViewById(R.id.controlsEndTime));

            });

        });

    }



    private void doOnValueChange(){
        updateGraph();
    }

    private void updateGraphAndValuesOfControlsForFixedStartTime(){

        if(startDate !=null ) {

            String separatorStr = getString(R.string.controls_string_merge_str);

            String startDateStr = Integer.toString(startDate.getYear()) + separatorStr +
                    Integer.toString(startDate.getMonth()) + separatorStr +
                    Integer.toString(startDate.getDay()) + separatorStr +
                    Integer.toString(startDate.getHour()) + separatorStr +
                    Integer.toString(startDate.getMinute()) + separatorStr +
                    Integer.toString(startDate.getSecond()) + separatorStr +
                    Integer.toString(startDate.getMillisecond());


            DateFormat dateFormat = new SimpleDateFormat(getString(R.string.controls_time_format_str_to_date));

            Date dateStartDate;

            try {
                dateStartDate = dateFormat.parse(startDateStr);

                long startDateMs = dateStartDate.getTime();

                executors.storageIO().execute(() -> {

                    if (dataRepository == null) {
                        dataRepository = ((BasicApp) getApplication()).getRepository();
                    }

                    long endDateMs = dataRepository.getNewestTimeMsSync();

                    List<LocationData> locationDataList = dataRepository.getLocationDataBetweenStartAndEndTimeSync(startDateMs, endDateMs);

                    executors.mainThread().execute(() -> {

                        setValuesOfEndTimeControls(endDateMs, findViewById(R.id.controlsEndTime));

                    });

                    mlocationDataList = locationDataList;
                    drawGraph(locationDataList, showInformationFlag);

                });


            } catch (ParseException e) {

            }

        }

    }



    private void updateGraph(){

        if(startDate !=null && endDate != null) {

            String separatorStr = getString(R.string.controls_string_merge_str);

            String startDateStr = Integer.toString(startDate.getYear()) + separatorStr +
                    Integer.toString(startDate.getMonth()) + separatorStr +
                    Integer.toString(startDate.getDay()) + separatorStr +
                    Integer.toString(startDate.getHour()) + separatorStr +
                    Integer.toString(startDate.getMinute()) + separatorStr +
                    Integer.toString(startDate.getSecond()) + separatorStr +
                    Integer.toString(startDate.getMillisecond());

            String endDateStr = Integer.toString(endDate.getYear()) + separatorStr +
                    Integer.toString(endDate.getMonth()) + separatorStr +
                    Integer.toString(endDate.getDay()) + separatorStr +
                    Integer.toString(endDate.getHour()) + separatorStr +
                    Integer.toString(endDate.getMinute()) + separatorStr +
                    Integer.toString(endDate.getSecond()) + separatorStr +
                    Integer.toString(endDate.getMillisecond());

            DateFormat dateFormat = new SimpleDateFormat(getString(R.string.controls_time_format_str_to_date));

            Date dateStartDate;

            try {
                dateStartDate = dateFormat.parse(startDateStr);

                Date dateEndDate;
                dateEndDate = dateFormat.parse(endDateStr);

                long startDateMs = dateStartDate.getTime();
                long endDateMs = dateEndDate.getTime();

                executors.storageIO().execute(() -> {

                    if (dataRepository == null) {
                        dataRepository = ((BasicApp) getApplication()).getRepository();
                    }

                    List<LocationData> locationDataList = dataRepository.getLocationDataBetweenStartAndEndTimeSync(startDateMs, endDateMs);

                    mlocationDataList = locationDataList;
                    drawGraph(locationDataList, showInformationFlag);

                });


            } catch (ParseException e) {

            }

        }

    }


    private synchronized void drawGraph(List<LocationData> locationDataList, boolean showInformationFlag) {

        executors.mainThread().execute(() -> {

            if (surfaceView != null) {

                SurfaceHolder holder = surfaceView.getHolder();

                if (holder != null) {

                    Canvas canvas = holder.lockCanvas();


                    if (canvas != null) {

                        Paint paint = new Paint();
                        paint.setARGB(255, 0, 255, 0);
                        paint.setTextSize(32);
                        paint.setTextAlign(Paint.Align.LEFT);

                        int horizontalMargin = 20;
                        int verticalMargin = 20;

                        Paint paintInformation = new Paint();
                        paintInformation.setARGB(255, 0, 255, 0);
                        int textSize = 32;
                        paintInformation.setTextSize(textSize);
                        paintInformation.setTextAlign(Paint.Align.LEFT);

                        canvas.drawARGB(255, 0, 0, 0);

                        if(locationDataList != null && !locationDataList.isEmpty()) {

                            int widthOfCanvas = canvas.getWidth();
                            int heightOfCanvas = canvas.getHeight();

                            int widthOfCanvasMinusTwoHorizontalMargin = widthOfCanvas - 2*horizontalMargin;
                            int heightOfCanvasMinusTwoHorizontalMargin = heightOfCanvas - 2*verticalMargin;

                            ExtremumValues extremumValuesOfLatitude = getMaxAndMinOfLatitude(locationDataList);
                            ExtremumValues extremumValuesOfLongitude = getMaxAndMinOfLongitude(locationDataList);

                            double scaleFactorOfLatitude =
                                    Math.floor(heightOfCanvasMinusTwoHorizontalMargin /
                                            (extremumValuesOfLatitude.getMaxValue() - extremumValuesOfLatitude.getMinValue()));

                            double scaleFactorOfLongitude =
                                    Math.floor(widthOfCanvasMinusTwoHorizontalMargin /
                                            (extremumValuesOfLongitude.getMaxValue() - extremumValuesOfLongitude.getMinValue()));

                            int sizeOfLocationDataList = locationDataList.size();

                            float[] pts = new float[2 * sizeOfLocationDataList];

                            int index = 0;

                            double minValueOfLongitude = extremumValuesOfLongitude.getMinValue();
                            double minValueOfLatitude = extremumValuesOfLatitude.getMinValue();

                            float xCoordinate;
                            float yCoordinate;

                            for (LocationData locationData : locationDataList) {

                                if (locationData != null) {

                                    pts[index] = (float) ((locationData.getLongitude() - minValueOfLongitude) * scaleFactorOfLongitude);
                                    pts[index] = pts[index] + horizontalMargin;
                                    xCoordinate = pts[index];
                                    index++;

                                    pts[index] = (float) ((locationData.getLatitude() - minValueOfLatitude) * scaleFactorOfLatitude);
                                    pts[index] = heightOfCanvasMinusTwoHorizontalMargin - pts[index];
                                    pts[index] = pts[index] + verticalMargin;
                                    yCoordinate = pts[index];
                                    index++;

                                    if(showInformationFlag == true) {

                                        String information = locationData.getInformation();

                                        if (information != null) {

                                            if (!information.isEmpty()) {

                                                canvas.drawText(information, xCoordinate, yCoordinate, paintInformation);

                                            }
                                        }

                                    }

                                }

                            }

                            setGraphLabels(extremumValuesOfLongitude, extremumValuesOfLatitude);

                            canvas.drawPoints(pts, paint);

                            drawRectangleAroundGraph(canvas, horizontalMargin, verticalMargin, widthOfCanvasMinusTwoHorizontalMargin, heightOfCanvasMinusTwoHorizontalMargin);



                        }else{

                            if(initializedFlag == true){

                                String noDataToDisplay = getString(R.string.no_data_to_display_label);

                                Rect bounds = new Rect();
                                paint.getTextBounds(noDataToDisplay, 0, noDataToDisplay.length(), bounds);
                                int height = bounds.height();

                                canvas.drawText(noDataToDisplay, 2*height, 2*height, paint);

                                setValuesOfGraphLimitsForNoData();

                            }

                        }

                        holder.unlockCanvasAndPost(canvas);

                    }


                }

            }

        });

    }

    private void drawRectangleAroundGraph(Canvas canvas, int horizontalMargin, int verticalMargin, int widthOfCanvas, int heightOfCanvas) {

        if(canvas != null) {

            Paint paintRectangle = new Paint();

            paintRectangle.setARGB(255, 255, 0, 0);

            int marginBetweenGraphAndRectangle = 10;

            float[] pointLeftTop = new float[2];
            pointLeftTop[0] = horizontalMargin - marginBetweenGraphAndRectangle;
            pointLeftTop[1] = verticalMargin - marginBetweenGraphAndRectangle;

            float[] pointRightTop = new float[2];
            pointRightTop[0] = horizontalMargin + widthOfCanvas + marginBetweenGraphAndRectangle;
            pointRightTop[1] = verticalMargin - marginBetweenGraphAndRectangle;

            float[] pointRightBottom = new float[2];
            pointRightBottom[0] = horizontalMargin + widthOfCanvas + marginBetweenGraphAndRectangle;
            pointRightBottom[1] = verticalMargin + heightOfCanvas + marginBetweenGraphAndRectangle;

            float[] pointLeftBottom = new float[2];
            pointLeftBottom[0] = horizontalMargin - marginBetweenGraphAndRectangle;
            pointLeftBottom[1] = verticalMargin + heightOfCanvas + marginBetweenGraphAndRectangle;


            canvas.drawLine(pointLeftTop[0], pointLeftTop[1], pointRightTop[0], pointRightTop[1], paintRectangle);
            canvas.drawLine(pointRightTop[0], pointRightTop[1], pointRightBottom[0], pointRightBottom[1], paintRectangle);
            canvas.drawLine(pointRightBottom[0], pointRightBottom[1], pointLeftBottom[0], pointLeftBottom[1], paintRectangle);
            canvas.drawLine(pointLeftBottom[0], pointLeftBottom[1], pointLeftTop[0], pointLeftTop[1], paintRectangle);

        }

    }


    private void setValuesOfGraphLimitsForNoData(){

        textViewMinLongitudeValue.setText(getString(R.string.graph_longitude_min));
        textViewMaxLongitudeValue.setText(getString(R.string.graph_longitude_max));
        textViewMinLatitudeValue.setText(getString(R.string.graph_latitude_min));
        textViewMaxLatitudeValue.setText(getString(R.string.graph_latitude_max));

    }

    private void setLimitsOfControls(){

        executors.storageIO().execute(() -> {

            if(dataRepository == null){
                dataRepository = ((BasicApp) getApplication()).getRepository();
            }

            long oldestTimeMs = dataRepository.getOldestTimeMsSync();
            long newestTimeMs = dataRepository.getNewestTimeMsSync();

            executors.mainThread().execute(() -> {

                setLimitsOfControls(oldestTimeMs, newestTimeMs, findViewById(R.id.controlsStartTime));
                setLimitsOfControls(oldestTimeMs, newestTimeMs, findViewById(R.id.controlsEndTime));

                setValuesOfStartTimeControls(oldestTimeMs, findViewById(R.id.controlsStartTime));
                setValuesOfEndTimeControls(newestTimeMs, findViewById(R.id.controlsEndTime));

            });

        });

    }

    private <T extends View> void setLimitsOfControls(long oldestTimeMs, long newestTimeMs, T viewById) {

        Date oldestDate = new Date(oldestTimeMs);
        Date newestDate = new Date(newestTimeMs);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.controls_time_format_year));
        String oldestDateFormatted = simpleDateFormat.format(oldestDate);
        String newestDateFormatted = simpleDateFormat.format(newestDate);

        //Year
        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setMaxValue(Integer.parseInt(newestDateFormatted));

        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setMinValue(Integer.parseInt(oldestDateFormatted));

        //Month
        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setMaxValue(Integer.parseInt(getString(R.string.controls_month_max)));

        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setMinValue(Integer.parseInt(getString(R.string.controls_month_min)));

        //Day
        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setMaxValue(Integer.parseInt(getString(R.string.controls_day_max)));

        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setMinValue(Integer.parseInt(getString(R.string.controls_day_min)));

        //Hour
        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setMaxValue(Integer.parseInt(getString(R.string.controls_hour_max)));

        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setMinValue(Integer.parseInt(getString(R.string.controls_hour_min)));

        //Minute
        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setMaxValue(Integer.parseInt(getString(R.string.controls_minute_max)));

        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setMinValue(Integer.parseInt(getString(R.string.controls_minute_min)));

        //Second
        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setMaxValue(Integer.parseInt(getString(R.string.controls_second_max)));

        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setMinValue(Integer.parseInt(getString(R.string.controls_second_min)));

    }

    private void setValuesOfStartTimeControls(long oldestTimeMs, View viewByIdStartTime) {

        Date oldestDate = new Date(oldestTimeMs);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.controls_time_format));
        String oldestDateFormatted = simpleDateFormat.format(oldestDate);

        String[] oldestDateFormattedSplitted = oldestDateFormatted.split(getString(R.string.controls_string_split_str));

        //Year
        int startYear = Integer.parseInt(oldestDateFormattedSplitted[0]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setValue(startYear);
        startDate.setYear(startYear);


        //Month
        int startMonth = Integer.parseInt(oldestDateFormattedSplitted[1]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setValue(startMonth);
        startDate.setMonth(startMonth);


        //Day
        int startDay = Integer.parseInt(oldestDateFormattedSplitted[2]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setValue(startDay);
        startDate.setDay(startDay);


        //Hour
        int startHour = Integer.parseInt(oldestDateFormattedSplitted[3]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setValue(startHour);
        startDate.setHour(startHour);


        //Minute
        int startMinute = Integer.parseInt(oldestDateFormattedSplitted[4]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setValue(startMinute);
        startDate.setMinute(startMinute);


        //Second
        int startSecond = Integer.parseInt(oldestDateFormattedSplitted[5]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setValue(startSecond);
        startDate.setSecond(startSecond);


        //Millisecond
        startDate.setMillisecond(0);

    }


    private void setValuesOfEndTimeControls(long newestTimeMs, View viewByIdEndTime) {

        Date newestDate = new Date(newestTimeMs);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.controls_time_format));
        String newestDateFormatted = simpleDateFormat.format(newestDate);

        String[] newestDateFormattedSplitted = newestDateFormatted.split(getString(R.string.controls_string_split_str));


        //Year
        int endYear = Integer.parseInt(newestDateFormattedSplitted[0]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setValue(endYear);
        endDate.setYear(endYear);

        //Month
        int endMonth = Integer.parseInt(newestDateFormattedSplitted[1]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setValue(endMonth);
        endDate.setMonth(endMonth);

        //Day
        int endDay = Integer.parseInt(newestDateFormattedSplitted[2]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setValue(endDay);
        endDate.setDay(endDay);

        //Hour
        int endHour = Integer.parseInt(newestDateFormattedSplitted[3]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setValue(endHour);
        endDate.setHour(endHour);

        //Minute
        int endMinute = Integer.parseInt(newestDateFormattedSplitted[4]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setValue(endMinute);
        endDate.setMinute(endMinute);

        //Second
        int endSecond = Integer.parseInt(newestDateFormattedSplitted[5]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setValue(endSecond);
        endDate.setSecond(endSecond);


        //Millisecond
        endDate.setMillisecond(999);


    }


    private void setGraphLabels(ExtremumValues extremumValuesOfLongitude, ExtremumValues extremumValuesOfLatitude) {

        if(extremumValuesOfLongitude != null){

            Double minValueOfLongitude = extremumValuesOfLongitude.getMinValue();
            textViewMinLongitudeValue.setText(Double.toString(minValueOfLongitude));

            Double maxValueOfLongitude = extremumValuesOfLongitude.getMaxValue();
            textViewMaxLongitudeValue.setText(Double.toString(maxValueOfLongitude));

        }

        if(extremumValuesOfLatitude != null){

            Double minValueOfLatitude = extremumValuesOfLatitude.getMinValue();
            textViewMinLatitudeValue.setText(Double.toString(minValueOfLatitude));

            Double maxValueOfLatitude = extremumValuesOfLatitude.getMaxValue();
            textViewMaxLatitudeValue.setText(Double.toString(maxValueOfLatitude));

        }


    }


    private ExtremumValues getMaxAndMinOfLatitude(List<LocationData> locationDataList){

        ExtremumValues extremumValues = new ExtremumValues();

        double maxValue = -1000;
        double minValue = 1000;

        if(locationDataList != null && !locationDataList.isEmpty()) {

            maxValue = locationDataList.get(0).getLatitude();
            minValue = locationDataList.get(0).getLatitude();

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

            maxValue = locationDataList.get(0).getLongitude();
            minValue = locationDataList.get(0).getLongitude();

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
