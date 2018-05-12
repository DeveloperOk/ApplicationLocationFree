package com.enterprise.pc.applicationlocation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.enterprise.pc.applicationlocation.adapter.DataPropertiesAdapter;
import com.enterprise.pc.applicationlocation.db.AppDatabase;
import com.enterprise.pc.applicationlocation.db.entity.LocationData;

import java.util.List;


public class ListDataActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLocationData;
    private DataPropertiesAdapter dataPropertiesAdapter;
    private RecyclerView.LayoutManager layoutManager;

    AppExecutors executors;
    DataRepository dataRepository;
    AppDatabase appDatabase;

    List<LocationData> locationDataList;

    DataPropertiesAdapter.ClickListener clickListener;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        setTitle(R.string.list_data_activity_title);

        executors = new AppExecutors();

        recyclerViewLocationData = (RecyclerView) findViewById(R.id.recyclerViewLocationData);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerViewLocationData.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        getDataAndSpecifyAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();

        getDataAndSpecifyAdapter();

    }

    private void getDataAndSpecifyAdapter() {

        executors.storageIO().execute(() -> {

            if(dataRepository == null){

                dataRepository = ((BasicApp) getApplication()).getRepository();
            }

            locationDataList = dataRepository.getLocationDataHavingInformationSync();

            generateAndSetAdapter();

        });

    }

    private void generateAndSetAdapter() {

        executors.mainThread().execute(() -> {

            if(locationDataList!=null){

                dataPropertiesAdapter = new DataPropertiesAdapter(this, locationDataList);

                clickListener = new DataPropertiesAdapter.ClickListener(){

                    @Override
                    public void onItemClick(View view, int position){

                        setVisibilityOfButtonsDeleteAndUpdate(view, position);

                    }

                };

                dataPropertiesAdapter.setItemClickListener(clickListener);

            }

            if(recyclerViewLocationData != null && dataPropertiesAdapter != null){

                recyclerViewLocationData.setAdapter(dataPropertiesAdapter);

            }
        });
    }

    private void setVisibilityOfButtonsDeleteAndUpdate(View view, int position) {

        if(view != null){

            int visibilityOfButtonDelete = view.findViewById(R.id.buttonDelete).getVisibility();
            if(visibilityOfButtonDelete == View.GONE){
                view.findViewById(R.id.buttonDelete).setVisibility(View.VISIBLE);
                view.findViewById(R.id.spaceBottom).setVisibility(View.VISIBLE);

                setDeleteButtonListener(view, position);

            } else if(visibilityOfButtonDelete == View.VISIBLE) {
                view.findViewById(R.id.buttonDelete).setVisibility(View.GONE);
                view.findViewById(R.id.spaceBottom).setVisibility(View.GONE);
            }

            int visibilityOfButtonUpdate = view.findViewById(R.id.buttonUpdate).getVisibility();
            if(visibilityOfButtonUpdate == View.GONE) {
                view.findViewById(R.id.buttonUpdate).setVisibility(View.VISIBLE);

                setUpdateButtonListener(view, position);

            } else if(visibilityOfButtonUpdate == View.VISIBLE) {
                view.findViewById(R.id.buttonUpdate).setVisibility(View.GONE);
            }

        }

    }


    private void setDeleteButtonListener(View view, int inputPosition) {

        if(view != null){

            ((Button) view.findViewById(R.id.buttonDelete)).setOnClickListener(new View.OnClickListener() {

                int position = inputPosition;

                @Override
                public void onClick(View vw) {

                    executors.mainThread().execute(() -> {
                        instantiateAndShowAlertDialogForDeleteStart(position);
                    });

                }
            });

        }

    }

    private void setUpdateButtonListener(View view, int inputPosition) {

        if(view != null){

            ((Button) view.findViewById(R.id.buttonUpdate)).setOnClickListener(new View.OnClickListener() {

                int position = inputPosition;

                @Override
                public void onClick(View vw) {

                    if(locationDataList != null){

                        LocationData locationData = new LocationData(locationDataList.get(position));

                        if(locationData != null){

                            Intent intent = new Intent(getApplicationContext(), DataUpdateInformationActivity.class);
                            intent.putExtra(AppConstants.LocationDataToUpdate, locationData);
                            startActivity(intent);

                        }

                    }

                }
            });

        }

    }


    private void instantiateAndShowAlertDialogForDeleteCompleted() {

        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.list_data_delete_completed_dialog_title);
        alertDialogBuilder.setMessage(R.string.list_data_delete_completed_dialog_message);
        alertDialogBuilder.setNeutralButton(R.string.list_data_delete_completed_dialog_neutral_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    private void instantiateAndShowAlertDialogForDeleteStart(int position) {

        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.list_data_delete_started_dialog_title);
        alertDialogBuilder.setMessage(R.string.list_data_delete_started_dialog_message);
        alertDialogBuilder.setPositiveButton(R.string.list_data_delete_started_dialog_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteLocationDataElement(position);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.list_data_delete_started_dialog_negative_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    private void deleteLocationDataElement(int position) {

        if(locationDataList != null){

            LocationData locationData = locationDataList.get(position);

            if(locationData != null){

                LocationData tempLocationData = new LocationData(locationData);

                if(tempLocationData != null){

                    executors.storageIO().execute(() -> {

                        if(appDatabase == null){

                            appDatabase = ((BasicApp) getApplication()).getDatabase();
                        }

                        AppDatabase.delete(appDatabase, tempLocationData);

                    });

                    executors.mainThread().execute(() -> {

                        locationDataList.remove(position);
                        generateAndSetAdapter();
                        instantiateAndShowAlertDialogForDeleteCompleted();

                    });

                }

            }

        }

    }


}
