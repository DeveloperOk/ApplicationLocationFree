package com.enterprise.pc.applicationlocationfree.db;


import android.content.Context;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.enterprise.pc.applicationlocationfree.AppExecutors;
import com.enterprise.pc.applicationlocationfree.db.converter.DateConverter;
import com.enterprise.pc.applicationlocationfree.db.dao.LocationDataDao;
import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;

import java.util.List;

/**
 * Created by PC on 2018-03-29.
 */


@Database(entities = {LocationData.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    private static final String DATABASE_NAME = "app_loc_db";

    public abstract LocationDataDao locationDataDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.storageIO().execute(() -> {

                            AppDatabase database = AppDatabase.getInstance(appContext, executors);

                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                }).build();
    }


    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    public static void insertAll(final AppDatabase database, final List<LocationData> locationDataList) {
        database.runInTransaction(() -> {
            database.locationDataDao().insertAll(locationDataList);

        });
    }

    public static void insert(final AppDatabase database, final LocationData locationData) {

        if(database != null && locationData != null){

            database.runInTransaction(() -> {
                database.locationDataDao().insert(locationData);

            });

        }

    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }


    public static void delete(final AppDatabase database, final LocationData locationData) {

        if(database != null && locationData != null){

            database.runInTransaction(() -> {
                database.locationDataDao().delete(locationData);

            });

        }

    }


    public static void update(final AppDatabase database, final LocationData locationData) {

        if(database != null && locationData != null){

            database.runInTransaction(() -> {
                database.locationDataDao().update(locationData);

            });

        }

    }

}
