package com.enterprise.pc.applicationlocationfree;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.enterprise.pc.applicationlocationfree.db.AppDatabase;
import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;

public class DataUpdateInformationActivity extends AppCompatActivity {

    Button buttonClear;
    Button buttonDataUpdate;

    TextView textViewTimeValue;
    TextView textViewLatitudeValue;
    TextView textViewLongitudeValue;
    TextView textViewAltitudeValue;
    TextView textViewAccuracyValue;
    TextView textViewSpeedValue;
    TextView textViewBearingValue;
    TextView textViewProviderValue;

    EditText editTextInformation;

    AppExecutors executors;
    AppDatabase appDatabase;

    LocationData locationDataToUpdate;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update_information);

        setTitle(R.string.activity_data_update_information_title);

        executors = new AppExecutors();

        locationDataToUpdate = getIntent().getParcelableExtra(AppConstants.LocationDataToUpdate);

        initializeTextViews();
        setTextsOfTextViews(locationDataToUpdate);

        initializeAndSetEditText();

        buttonClear = (Button)findViewById(R.id.buttonClear);
        buttonDataUpdate = (Button)findViewById(R.id.buttonDataUpdate);

        addButtonListeners();

    }

    private void initializeAndSetEditText() {

        editTextInformation = (EditText) findViewById(R.id.editTextInformation);

        if(locationDataToUpdate != null){

            String information = locationDataToUpdate.getInformation();
            if(information != null){
                editTextInformation.setText(information);
            }else{
                editTextInformation.setText("");
            }

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


    private void addButtonListeners() {

        addClearButtonListener();
        addUpdateButtonListener();

    }

    private void addClearButtonListener() {

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                editTextInformation.setText("");

            }
        });

    }

    private void addUpdateButtonListener() {

        buttonDataUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                updateInformation();

            }
        });

    }

    private void updateInformation(){

        String information = editTextInformation.getText().toString();

        if(locationDataToUpdate != null && information != null){

            locationDataToUpdate.setInformation(information);

            executors.storageIO().execute(() -> {

                if(appDatabase == null){
                    appDatabase = ((BasicApp) getApplication()).getDatabase();
                }

                AppDatabase.update(appDatabase, locationDataToUpdate);

                executors.mainThread().execute(() -> {

                    instantiateAlertDialogAndShow();
                });

            });
        }

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

    private void instantiateAlertDialogAndShow() {

        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.activity_data_update_information_dialog_title);
        alertDialogBuilder.setMessage(R.string.activity_data_update_information_dialog_message);
        alertDialogBuilder.setNeutralButton(R.string.activity_data_update_information_dialog_neutral_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }


}
