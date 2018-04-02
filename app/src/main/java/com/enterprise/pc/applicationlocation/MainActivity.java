package com.enterprise.pc.applicationlocation;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.enterprise.pc.applicationlocation.db.AppDatabase;
import com.enterprise.pc.applicationlocation.db.entity.LocationData;
import com.enterprise.pc.applicationlocation.vm.LocationDataViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button buttonStart;
    TextView textViewTextToDisplayLine;
    LocationDataViewModel locationDataViewModel;
    int count = 1;

    AppExecutors executors;
    AppDatabase appDatabase;
    DataRepository dataRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executors = new AppExecutors();

        textViewTextToDisplayLine = (TextView) findViewById(R.id.textViewTextToDisplayLine);
        buttonStart = (Button)findViewById(R.id.buttonStart);

        locationDataViewModel = ViewModelProviders.of(this).get(LocationDataViewModel.class);

        addListeners();

    }

    private void addListeners() {

        setDataListeners();
        setButtonListeners();

    }

    private void setButtonListeners() {

        setStartButtonListener();

    }

    private void setDataListeners() {

        dataRepository = ((BasicApp) getApplication()).getRepository();

        locationDataViewModel.setLocationData(dataRepository.getLocationData());

        final Observer<List<LocationData>> locationDataListObserver = new Observer<List<LocationData>>() {

            @Override
            public void onChanged(@Nullable final List<LocationData> locationDataList) {

                if(locationDataList != null && !locationDataList.isEmpty()){

                    LocationData locationDataElement = locationDataList.get(locationDataList.size()-1);

                    if(locationDataElement != null){

                        String textToDisplay = locationDataElement.getInfo();

                        if(textToDisplay != null){

                            textViewTextToDisplayLine.setText(textToDisplay + " " + count);
                        }

                    }

                }

            }
        };

        locationDataViewModel.getLocationData().observe( this, locationDataListObserver);

    }



    private void setStartButtonListener() {


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {


                count++;
                LocationData locationDataElement = new LocationData(count, "Test Successful! " + count);
                List<LocationData> listOfLocationData = new ArrayList<LocationData>();
                listOfLocationData.add(locationDataElement);

                appDatabase = ((BasicApp) getApplication()).getDatabase();

                executors.storageIO().execute(() -> {

                    AppDatabase.insertData(appDatabase, listOfLocationData);

                });

            }
        });

    }


}
