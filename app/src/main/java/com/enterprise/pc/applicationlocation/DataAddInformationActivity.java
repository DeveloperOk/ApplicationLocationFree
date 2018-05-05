package com.enterprise.pc.applicationlocation;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.enterprise.pc.applicationlocation.db.AppDatabase;
import com.enterprise.pc.applicationlocation.db.entity.LocationData;

public class DataAddInformationActivity extends AppCompatActivity {

    Button buttonClear;
    Button buttonAdd;

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

    LocationData currentLocationData;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_add_information);

        setTitle(R.string.data_add_information_activity_title);

        executors = new AppExecutors();

        currentLocationData = getIntent().getParcelableExtra(AppConstants.CurrentLocationData);

        initializeTextViews();
        setTextsOfTextViews(currentLocationData);

        editTextInformation = (EditText) findViewById(R.id.editTextInformation);

        appDatabase = ((BasicApp) getApplication()).getDatabase();;

        buttonClear = (Button)findViewById(R.id.buttonClear);
        buttonAdd = (Button)findViewById(R.id.buttonAdd);

        addButtonListeners();

        instantiateAlertDialog();

    }

    private void instantiateAlertDialog() {

        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.add_information_dialog_title);
        alertDialogBuilder.setMessage(R.string.add_information_dialog_message);
        alertDialogBuilder.setNeutralButton(R.string.add_information_dialog_neutral_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialog = alertDialogBuilder.create();

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
        addAddButtonListener();

    }

    private void addClearButtonListener() {

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                editTextInformation.setText("");

            }
        });

    }

    private void addAddButtonListener() {

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                addInformation();

            }
        });

    }

    private void addInformation(){

        if(appDatabase == null){
            appDatabase = ((BasicApp) getApplication()).getDatabase();
        }

        String information = editTextInformation.getText().toString();

        if(currentLocationData != null && information != null){

            currentLocationData.setInformation(information);

            executors.storageIO().execute(() -> {

                AppDatabase.insert(appDatabase, currentLocationData);


                executors.mainThread().execute(() -> {

                    alertDialog.show();
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


}
