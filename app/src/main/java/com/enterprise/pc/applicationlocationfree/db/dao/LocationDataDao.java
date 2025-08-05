package com.enterprise.pc.applicationlocationfree.db.dao;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;

import java.util.List;

/**
 * Created by PC on 2018-03-30.
 */

@Dao
public interface LocationDataDao {

    @Query("SELECT * FROM locationdata")
    LiveData<List<LocationData>> loadAllLocationData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LocationData> locationDataList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocationData locationData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(LocationData locationData);

    @Query("select * from locationdata where id = :entityId")
    LiveData<LocationData> loadLocationData(long entityId);

    @Query("select * from locationdata where id = :entityId")
    LocationData loadLocationDataSync(long entityId);

    @Query("select * from locationdata where id = (select max(id) from locationdata)")
    LocationData loadLocationDataHavingNewestTimeSync();

    @Query("select min(id) from locationdata")
    long getOldestTimeMsSync();

    @Query("select max(id) from locationdata")
    long getNewestTimeMsSync();

    @Query("select * from locationdata where id >= :startId and id <= :endId")
    List<LocationData> getLocationDataBetweenStartAndEndTimeSync(long startId, long endId);

    @Query("select * from locationdata where information != '' ")
    List<LocationData> getLocationDataHavingInformationSync();

    @Delete
    void delete(LocationData locationData);

    @Query("select count(*) from locationdata")
    long getTotalNumberOfData();

    @Query("select count(*) from locationdata where id >= :startDateMs and id <= :endDateMs")
    long getNumberOfDataBetweenStartTimeMsAndEndDateMs(long startDateMs, long endDateMs);

}
