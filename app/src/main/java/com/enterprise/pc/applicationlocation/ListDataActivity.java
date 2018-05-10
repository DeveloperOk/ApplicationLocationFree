package com.enterprise.pc.applicationlocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.enterprise.pc.applicationlocation.adapter.DataPropertiesAdapter;
import com.enterprise.pc.applicationlocation.db.entity.LocationData;

import java.util.List;


public class ListDataActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLocationData;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    AppExecutors executors;
    DataRepository dataRepository;

    List<LocationData> locationDataList;

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

    private void getDataAndSpecifyAdapter() {

        executors.storageIO().execute(() -> {

            if(dataRepository == null){

                dataRepository = ((BasicApp) getApplication()).getRepository();
            }

            locationDataList = dataRepository.getLocationDataHavingInformationSync();


            executors.mainThread().execute(() -> {
                if(locationDataList!=null){
                    adapter = new DataPropertiesAdapter(this, locationDataList);
                }

                if(recyclerViewLocationData != null && adapter != null){

                    recyclerViewLocationData.setAdapter(adapter);
                }
            });

        });

    }


}
