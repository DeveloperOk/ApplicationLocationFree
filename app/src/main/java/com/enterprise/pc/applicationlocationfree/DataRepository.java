package com.enterprise.pc.applicationlocationfree;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.enterprise.pc.applicationlocationfree.db.AppDatabase;
import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;

import java.util.List;

/**
 * Created by PC on 2018-03-30.
 */

public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<LocationData>> mLocationData;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mLocationData = new MediatorLiveData<>();

        mLocationData.addSource(mDatabase.locationDataDao().loadAllLocationData(),
                locationDataList -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mLocationData.postValue(locationDataList);
                    }
                });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<LocationData>> getLocationData() {
        return mLocationData;
    }

    public LiveData<LocationData> loadLocationData(final int productId) {
        return mDatabase.locationDataDao().loadLocationData(productId);
    }

    public long getOldestTimeMsSync(){
        return  mDatabase.locationDataDao().getOldestTimeMsSync();
    }

    public long getNewestTimeMsSync(){
        return  mDatabase.locationDataDao().getNewestTimeMsSync();
    }

    public List<LocationData> getLocationDataBetweenStartAndEndTimeSync(long startId, long endId){
        return  mDatabase.locationDataDao().getLocationDataBetweenStartAndEndTimeSync(startId, endId);
    }

    public LocationData loadLocationDataHavingNewestTimeSync(){
        return  mDatabase.locationDataDao().loadLocationDataHavingNewestTimeSync();
    }

    public List<LocationData> getLocationDataHavingInformationSync(){
        return  mDatabase.locationDataDao().getLocationDataHavingInformationSync();
    }

    public long getTotalNumberOfData(){
        return  mDatabase.locationDataDao().getTotalNumberOfData();
    }

    public long getNumberOfDataBetweenStartTimeMsAndEndDateMs(long startDateMs, long endDateMs){
        return  mDatabase.locationDataDao().getNumberOfDataBetweenStartTimeMsAndEndDateMs(startDateMs, endDateMs);
    }

}
