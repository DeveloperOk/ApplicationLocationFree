package com.enterprise.pc.applicationlocation.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.enterprise.pc.applicationlocation.db.entity.LocationData;

import java.util.List;

/**
 * Created by PC on 2018-03-30.
 */

@Dao
public interface LocationDataDao {

    @Query("SELECT * FROM locationdata")
    LiveData<List<LocationData>> loadAllLocationData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LocationData> locationData);

    @Query("select * from locationdata where id = :entityId")
    LiveData<LocationData> loadLocationData(int entityId);

    @Query("select * from locationdata where id = :entityId")
    LocationData loadLocationDataSync(int entityId);

}
