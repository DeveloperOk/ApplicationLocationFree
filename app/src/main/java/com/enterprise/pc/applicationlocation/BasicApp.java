package com.enterprise.pc.applicationlocation;

import android.app.Activity;
import android.app.Application;

import com.enterprise.pc.applicationlocation.db.AppDatabase;

/**
 * Created by PC on 2018-03-30.
 */

//Used for accessing singletons
public class BasicApp extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() { return DataRepository.getInstance(getDatabase()); }

    public AppLocationManager getAppLocationManager(Activity activity) {
        return AppLocationManager.getInstance(this, activity);
    }


}
