package com.enterprise.pc.applicationlocation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.enterprise.pc.applicationlocation.db.AppDatabase;
import com.enterprise.pc.applicationlocation.db.entity.LocationData;

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


}
