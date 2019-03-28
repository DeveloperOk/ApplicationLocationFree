package com.enterprise.pc.applicationlocationfree;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;
import com.enterprise.pc.applicationlocationfree.vm.DateInformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataExportActivity extends AppCompatActivity {

    AppExecutors executors;
    DataRepository dataRepository;

    DateInformation startDate;
    DateInformation endDate;

    Button buttonExport;

    TextView textViewProgress;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    AlertDialog.Builder alertDialogBuilderForNoData;
    AlertDialog alertDialogForNoData;

    String pathOfOutputFile;

    TextView textViewNumberOfSelectedData;
    long numberOfSelectedData = 0;
    long totalNumberOfData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_export);

        setTitle(getString(R.string.data_export_activity_title));

        executors = new AppExecutors();
        dataRepository = ((BasicApp) getApplication()).getRepository();

        DataBindingUtil.inflate(getLayoutInflater(), R.layout.select_date_and_time_layout, findViewById(R.id.controlsStartTime), true);

        DataBindingUtil.inflate(getLayoutInflater(), R.layout.select_date_and_time_layout, findViewById(R.id.controlsEndTime), true);

        ((TextView) findViewById(R.id.controlsStartTime).findViewById(R.id.textViewHeader)).setText(R.string.controls_start_time_label);
        ((TextView) findViewById(R.id.controlsEndTime).findViewById(R.id.textViewHeader)).setText(R.string.controls_end_time_label);

        startDate = new DateInformation();
        endDate = new DateInformation();

        setLimitsOfControls();

        buttonExport = (Button)findViewById(R.id.buttonExport);
        textViewProgress = (TextView) findViewById(R.id.textViewProgress);
        textViewNumberOfSelectedData = (TextView) findViewById(R.id.textViewNumberOfSelectedData);

        addListeners();

        instantiateAlertDialog();
        instantiateAlertDialogForNoData();

    }


    private void setTextViewNumberOfSelectedData() {

        executors.storageIO().execute(() -> {

            if (dataRepository == null) {
                dataRepository = ((BasicApp) getApplication()).getRepository();
            }

            totalNumberOfData = dataRepository.getTotalNumberOfData();

            if(startDate !=null && endDate != null) {

                String separatorStr = getString(R.string.controls_string_merge_str);

                String startDateStr = getDateString(separatorStr, startDate);

                String endDateStr = getDateString(separatorStr, endDate);

                DateFormat dateFormat = new SimpleDateFormat(getString(R.string.controls_time_format_str_to_date));

                Date dateStartDate;

                try {

                    dateStartDate = dateFormat.parse(startDateStr);

                    Date dateEndDate;
                    dateEndDate = dateFormat.parse(endDateStr);

                    long startDateMs = dateStartDate.getTime();
                    long endDateMs = dateEndDate.getTime();

                    numberOfSelectedData = dataRepository.getNumberOfDataBetweenStartTimeMsAndEndDateMs(startDateMs, endDateMs);

                    executors.mainThread().execute(() -> {

                        if (textViewNumberOfSelectedData != null) {

                            String outputText = getString(R.string.data_export_selected_number_of_data)
                                    + numberOfSelectedData + "/" + totalNumberOfData;

                            textViewNumberOfSelectedData.setText(outputText);

                        }

                    });

                } catch (ParseException e) {

                }

            }

        });

    }

    @NonNull
    private static String getDateString(String separatorStr, DateInformation dateInformation) {

        return Integer.toString(dateInformation.getYear()) + separatorStr +
                Integer.toString(dateInformation.getMonth()) + separatorStr +
                Integer.toString(dateInformation.getDay()) + separatorStr +
                Integer.toString(dateInformation.getHour()) + separatorStr +
                Integer.toString(dateInformation.getMinute()) + separatorStr +
                Integer.toString(dateInformation.getSecond()) + separatorStr +
                Integer.toString(dateInformation.getMillisecond());
    }

    private void instantiateAlertDialog() {

        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.data_export_dialog_title);
        alertDialogBuilder.setMessage(R.string.data_export_dialog_message_first_line);
        alertDialogBuilder.setNeutralButton(R.string.data_export_dialog_neutral_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialog = alertDialogBuilder.create();

    }

    private void instantiateAlertDialogForNoData() {

        alertDialogBuilderForNoData = new AlertDialog.Builder(this);

        alertDialogBuilderForNoData.setTitle(R.string.data_export_dialog_title_for_no_data);
        alertDialogBuilderForNoData.setMessage(R.string.data_export_dialog_message_for_no_data);
        alertDialogBuilderForNoData.setNeutralButton(R.string.data_export_dialog_neutral_button_for_no_data, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialogForNoData = alertDialogBuilderForNoData.create();

    }


    private void addListeners(){

        addNumberPickerListenersOfStartTime();
        addNumberPickerListenersOfEndTime();
        addExportButtonListener();

    }

    private void addExportButtonListener(){

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                if(areThereSelectedData()){

                    exportData();
                }else{

                    executors.mainThread().execute(() -> {

                        alertDialogForNoData.show();
                    });
                }

            }
        });

    }

    private boolean areThereSelectedData(){

        boolean retVal = false;

        if(numberOfSelectedData > 0){

            retVal = true;
        }

        return retVal;

    }

    private void exportData(){

        executors.mainThread().execute(() -> {

            buttonExport.setClickable(false);
            buttonExport.setVisibility(View.INVISIBLE);
            textViewNumberOfSelectedData.setVisibility(View.INVISIBLE);

            textViewProgress.setVisibility(View.VISIBLE);
            textViewProgress.setText(getString(R.string.data_export_progress_started));

        });

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

                if (dataRepository == null) {
                    dataRepository = ((BasicApp) getApplication()).getRepository();
                }

                executors.storageIO().execute(() -> {

                    List<LocationData> locationDataList = dataRepository.getLocationDataBetweenStartAndEndTimeSync(startDateMs, endDateMs);

                    if(locationDataList != null && !locationDataList.isEmpty()){

                        FileOutputStream fileOutputStreamLocationDataOutput = initializeLocationDataOutputFile();

                        String progressStr = getString(R.string.data_export_progress);
                        int index = 0;
                        int sizeOfLocationDataList = locationDataList.size();

                        int numberOfDataToOutputAtATime = getNumberOfDataToOutputAtATime(sizeOfLocationDataList);

                        while(index < sizeOfLocationDataList){

                            String formattedData = "";

                            int indexLimit = index + numberOfDataToOutputAtATime;

                            int lineIndex = 1;
                            while(index < indexLimit && index < sizeOfLocationDataList){

                                lineIndex = index + 1;
                                formattedData = formattedData + getFormattedData(lineIndex, locationDataList.get(index));

                                index++;
                            }

                            writeDataToFile(formattedData, fileOutputStreamLocationDataOutput);

                            final int progressIndex = lineIndex;

                            executors.mainThread().execute(() -> {
                                textViewProgress.setText(progressStr + progressIndex + "/" + sizeOfLocationDataList);
                            });

                        }


                        closeLocationDataOutputFile(fileOutputStreamLocationDataOutput);

                        executors.mainThread().execute(() -> {

                            buttonExport.setClickable(true);
                            buttonExport.setVisibility(View.VISIBLE);
                            textViewNumberOfSelectedData.setVisibility(View.VISIBLE);

                            textViewProgress.setText(getString(R.string.data_export_progress_completed));
                            textViewProgress.setVisibility(View.INVISIBLE);

                            alertDialog.setMessage(getString(R.string.data_export_dialog_message_first_line)
                                    + getString(R.string.data_export_dialog_message_second_line) + pathOfOutputFile);
                            alertDialog.show();

                        });

                    }


                });


            } catch (ParseException e) {

            }

        }

    }

    private int getNumberOfDataToOutputAtATime(int sizeOfLocationDataList) {

        int numberOfDataToOutputAtATime = (int)(sizeOfLocationDataList * AppConstants.NumberOfDataToOutputAtATimeRatio);

        if(numberOfDataToOutputAtATime > AppConstants.MaxNumberOfDataToOutputAtATime){

            numberOfDataToOutputAtATime = AppConstants.MaxNumberOfDataToOutputAtATime;

        } else if(numberOfDataToOutputAtATime < AppConstants.MinNumberOfDataToOutputAtATime){

            numberOfDataToOutputAtATime = AppConstants.MinNumberOfDataToOutputAtATime;

        }

        return numberOfDataToOutputAtATime;
    }

    private String getFormattedData(int index, LocationData locationData) {

        String outputLine = "";

        if(locationData != null) {

            outputLine = index
                    + ";" + locationData.getId()
                    + ";" + locationData.getFormattedTime()
                    + ";" + locationData.getLatitude()
                    + ";" + locationData.getLongitude()
                    + ";" + locationData.getAltitude()
                    + ";" + locationData.getAccuracy()
                    + ";" + locationData.getProvider()
                    + ";" + locationData.getSpeed()
                    + ";" + locationData.getBearing()
                    + ";" + locationData.getInformation()
                    + "\n";
        }

        return outputLine;
    }

    private void writeDataToFile(String outputData, FileOutputStream fileOutputStreamLocationDataOutput){

        if(outputData != null && fileOutputStreamLocationDataOutput != null){

            try {

                if (isExternalStorageWritable()) {

                    fileOutputStreamLocationDataOutput.write(outputData.getBytes());
                }

            } catch (IOException e) {
                //e.printStackTrace();
            }

        }

    }

    private FileOutputStream initializeLocationDataOutputFile(){

        FileOutputStream fileOutputStreamLocationDataOutput = null;
        if(isExternalStorageWritable()){

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_for_data_export));
            String formattedTime = simpleDateFormat.format(date);
            String filenameOfLocationDataOutput
                    = getString(R.string.file_name_for_data_export) + formattedTime + getString(R.string.file_extension_for_data_export);

            pathOfOutputFile = getApplicationContext().getExternalFilesDir(null) + "/" + filenameOfLocationDataOutput;
            File fileLocationDataOutput = new File(pathOfOutputFile);

            try {
                fileOutputStreamLocationDataOutput = new FileOutputStream(fileLocationDataOutput, true);

                try {
                    fileOutputStreamLocationDataOutput.write(getString(R.string.data_export_first_line).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }

        }

        return fileOutputStreamLocationDataOutput;
    }


    private void closeLocationDataOutputFile(FileOutputStream fileOutputStreamLocationDataOutput){

        if(fileOutputStreamLocationDataOutput != null) {
            try {
                fileOutputStreamLocationDataOutput.close();
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

        //Second
        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setMaxValue(Integer.parseInt(getString(R.string.controls_second_max)));

        ((NumberPicker) viewById.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setMinValue(Integer.parseInt(getString(R.string.controls_second_min)));

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

        //Second
        int startSecond = Integer.parseInt(oldestDateFormattedSplitted[5]);
        ((NumberPicker) viewByIdStartTime.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setValue(startSecond);
        startDate.setSecond(startSecond);

        int endSecond = Integer.parseInt(newestDateFormattedSplitted[5]);
        ((NumberPicker) viewByIdEndTime.findViewById(R.id.appLocationNumberPickerSecond)
                .findViewById(R.id.numberPicker)).setValue(endSecond);
        endDate.setSecond(endSecond);

        startDate.setMillisecond(0);
        endDate.setMillisecond(999);

        setTextViewNumberOfSelectedData();

    }


    private void doOnValueChange(){

        setTextViewNumberOfSelectedData();
    }


}
