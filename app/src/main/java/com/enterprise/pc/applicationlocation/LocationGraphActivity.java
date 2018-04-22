package com.enterprise.pc.applicationlocation;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

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

    AppExecutors executors;

    DataRepository dataRepository;

    TextView textViewMinLongitudeLineTwo;
    TextView textViewMaxLongitudeLineTwo;
    TextView textViewMinLatitudeLineTwo;
    TextView textViewMaxLatitudeLineTwo;

    DateInformation startDate;
    DateInformation endDate;

    List<LocationData> mlocationDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_graph);

        setTitle(R.string.locationGraphActivityTitle);

        executors = new AppExecutors();

        locationDataViewModel = ViewModelProviders.of(this).get(LocationDataViewModel.class);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceViewGraph);
        surfaceView.getHolder().addCallback(this);

        textViewMinLongitudeLineTwo = (TextView) findViewById(R.id.textViewMinLongitudeLineTwo);
        textViewMaxLongitudeLineTwo = (TextView) findViewById(R.id.textViewMaxLongitudeLineTwo);
        textViewMinLatitudeLineTwo = (TextView) findViewById(R.id.textViewMinLatitudeLineTwo);
        textViewMaxLatitudeLineTwo = (TextView) findViewById(R.id.textViewMaxLatitudeLineTwo);

        DataBindingUtil.inflate(getLayoutInflater(), R.layout.select_date_and_time_layout, findViewById(R.id.controlsStartTime), true);

        DataBindingUtil.inflate(getLayoutInflater(), R.layout.select_date_and_time_layout, findViewById(R.id.controlsEndTime), true);

        ((TextView) findViewById(R.id.controlsStartTime).findViewById(R.id.textViewHeader)).setText(R.string.controls_start_time_label);
        ((TextView) findViewById(R.id.controlsEndTime).findViewById(R.id.textViewHeader)).setText(R.string.controls_end_time_label);

        addListeners();

        startDate = new DateInformation();
        endDate = new DateInformation();

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

        drawGraph(mlocationDataList);

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    private void addListeners() {

    }

    private void addNumberPickerListenersOfStartTime() {

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setYear(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setMonth(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setDay(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setHour(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsStartTime).findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                startDate.setMinute(newValue);
                updateGraph();

            }
        });

    }

    private void addNumberPickerListenersOfEndTime() {

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setYear(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setMonth(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setDay(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setHour(newValue);
                updateGraph();

            }
        });

        ((NumberPicker) findViewById(R.id.controlsEndTime).findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                endDate.setMinute(newValue);
                updateGraph();

            }
        });

    }

    private void addDataListeners() {

        dataRepository = ((BasicApp) getApplication()).getRepository();

        locationDataViewModel.setLocationData(dataRepository.getLocationData());

        final Observer<List<LocationData>> locationDataListObserver = new Observer<List<LocationData>>() {


            @Override
            public void onChanged(@Nullable final List<LocationData> locationDataList) {

                if(locationDataList != null && !locationDataList.isEmpty()){

                    mlocationDataList = locationDataList;
                    drawGraph(locationDataList);

                }

            }
        };

        locationDataViewModel.getLocationData().observe( this, locationDataListObserver);

    }

    private void updateGraph(){

        if(startDate !=null && endDate != null) {

            String seperatorStr = getString(R.string.controls_string_merge_str);

            String startDateStr = Integer.toString(startDate.getYear()) + seperatorStr +
                    Integer.toString(startDate.getMonth()) + seperatorStr +
                    Integer.toString(startDate.getDay()) + seperatorStr +
                    Integer.toString(startDate.getHour()) + seperatorStr +
                    Integer.toString(startDate.getMinute());

            String endDateStr = Integer.toString(endDate.getYear()) + seperatorStr +
                    Integer.toString(endDate.getMonth()) + seperatorStr +
                    Integer.toString(endDate.getDay()) + seperatorStr +
                    Integer.toString(endDate.getHour()) + seperatorStr +
                    Integer.toString(endDate.getMinute());

            DateFormat dateFormat = new SimpleDateFormat(getString(R.string.controls_time_format_str_to_date));

            Date dateStartDate;

            try {
                dateStartDate = dateFormat.parse(startDateStr);

                Date dateEndDate;
                dateEndDate = dateFormat.parse(endDateStr);

                long startDateMs = dateStartDate.getTime();
                long endDateMs = dateEndDate.getTime();

                if (dataRepository == null) {
                    dataRepository = ((BasicApp) getApplication()).getRepository();
                }

                executors.storageIO().execute(() -> {

                    List<LocationData> locationDataList = dataRepository.getLocationDataBetweenStartAndEndTimeSync(startDateMs, endDateMs);

                    mlocationDataList = locationDataList;
                    drawGraph(locationDataList);

                });


            } catch (ParseException e) {

            }

        }

    }

    private void drawGraph(List<LocationData> locationDataList) {

        executors.mainThread().execute(() -> {

            if (surfaceView != null) {

                SurfaceHolder holder = surfaceView.getHolder();

                if (holder != null) {

                    Canvas canvas = holder.lockCanvas();

                    Paint paint = new Paint();
                    paint.setARGB(255, 0, 255, 0);
                    paint.setTextSize(32);

                        if (canvas != null) {

                            canvas.drawARGB(255, 0, 0, 0);

                            if(locationDataList != null && !locationDataList.isEmpty()) {

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

                            setGraphLabels(extremumValuesOfLongitude, extremumValuesOfLatitude);

                            canvas.drawLines(pts, paint);


                        }else{

                                canvas.drawText(getString(R.string.no_data_to_display_label), 50, 50, paint);

                                setValuesOfGraphLimitsForNoData();

                            }

                            holder.unlockCanvasAndPost(canvas);

                        }

                }

            }

        });
    }

    private void setValuesOfGraphLimitsForNoData(){

        textViewMinLongitudeLineTwo.setText(getString(R.string.graph_longitude_min));
        textViewMaxLongitudeLineTwo.setText(getString(R.string.graph_longitude_max));
        textViewMinLatitudeLineTwo.setText(getString(R.string.graph_latitude_min));
        textViewMaxLatitudeLineTwo.setText(getString(R.string.graph_latitude_max));

    }

    private void setLimitsOfControls(){

        if(dataRepository == null){
            dataRepository = ((BasicApp) getApplication()).getRepository();
        }

        executors.storageIO().execute(() -> {

            long oldestTimeMs = dataRepository.getOldestTimeMsSync();
            long newestTimeMs = dataRepository.getNewestTimeMsSync();

            executors.mainThread().execute(() -> {

                setLimitsOfControls(oldestTimeMs, newestTimeMs, findViewById(R.id.controlsStartTime));
                setLimitsOfControls(oldestTimeMs, newestTimeMs, findViewById(R.id.controlsEndTime));
                setValuesOfControls(oldestTimeMs, newestTimeMs, findViewById(R.id.controlsStartTime), findViewById(R.id.controlsEndTime));

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

    }


    private void setValuesOfControls(long oldestTimeMs, long newestTimeMs, View viewByIdStartTime, View viewByIdEndTime) {

        Date oldestDate = new Date(oldestTimeMs);
        Date newestDate = new Date(newestTimeMs);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.controls_time_format));
        String oldestDateFormatted = simpleDateFormat.format(oldestDate);
        String newestDateFormatted = simpleDateFormat.format(newestDate);

        String[] oldestDateFormattedSplitted = oldestDateFormatted.split(getString(R.string.controls_string_split_str));
        String[] newestDateFormattedSplitted = newestDateFormatted.split(getString(R.string.controls_string_split_str));


        //Year
        int startYear = Integer.parseInt(oldestDateFormattedSplitted[0]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setValue(startYear);
        startDate.setYear(startYear);

        int endYear = Integer.parseInt(newestDateFormattedSplitted[0]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerYear)
                .findViewById(R.id.numberPicker)).setValue(endYear);
        endDate.setYear(endYear);

        //Month
        int startMonth = Integer.parseInt(oldestDateFormattedSplitted[1]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setValue(startMonth);
        startDate.setMonth(startMonth);

        int endMonth = Integer.parseInt(newestDateFormattedSplitted[1]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerMonth)
                .findViewById(R.id.numberPicker)).setValue(endMonth);
        endDate.setMonth(endMonth);

        //Day
        int startDay = Integer.parseInt(oldestDateFormattedSplitted[2]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setValue(startDay);
        startDate.setDay(startDay);

        int endDay = Integer.parseInt(newestDateFormattedSplitted[2]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerDay)
                .findViewById(R.id.numberPicker)).setValue(endDay);
        endDate.setDay(endDay);

        //Hour
        int startHour = Integer.parseInt(oldestDateFormattedSplitted[3]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setValue(startHour);
        startDate.setHour(startHour);

        int endHour = Integer.parseInt(newestDateFormattedSplitted[3]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerHour)
                .findViewById(R.id.numberPicker)).setValue(endHour);
        endDate.setHour(endHour);

        //Minute
        int startMinute = Integer.parseInt(oldestDateFormattedSplitted[4]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setValue(startMinute);
        startDate.setMinute(startMinute);

        int endMinute = Integer.parseInt(newestDateFormattedSplitted[4]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerMinute)
                .findViewById(R.id.numberPicker)).setValue(endMinute);
        endDate.setMinute(endMinute);

    }


    private void setGraphLabels(ExtremumValues extremumValuesOfLongitude, ExtremumValues extremumValuesOfLatitude) {

        if(extremumValuesOfLongitude != null){

            Double minValueOfLongitude = extremumValuesOfLongitude.getMinValue();
            textViewMinLongitudeLineTwo.setText(Double.toString(minValueOfLongitude));

            Double maxValueOfLongitude = extremumValuesOfLongitude.getMaxValue();
            textViewMaxLongitudeLineTwo.setText(Double.toString(maxValueOfLongitude));

        }

        if(extremumValuesOfLatitude != null){

            Double minValueOfLatitude = extremumValuesOfLatitude.getMinValue();
            textViewMinLatitudeLineTwo.setText(Double.toString(minValueOfLatitude));

            Double maxValueOfLatitude = extremumValuesOfLatitude.getMaxValue();
            textViewMaxLatitudeLineTwo.setText(Double.toString(maxValueOfLatitude));

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
