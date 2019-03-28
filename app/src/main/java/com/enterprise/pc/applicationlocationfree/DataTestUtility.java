package com.enterprise.pc.applicationlocationfree;

import android.app.Application;
import android.content.Context;

import com.enterprise.pc.applicationlocationfree.db.AppDatabase;
import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by PC on 2018-10-14.
 */

public class DataTestUtility {

    Application mApplication;
    Context mContext;
    AppDatabase mAppDatabase;
    AppExecutors mExecutors;

    String InputDataFileName = "inputData.txt";
    String PathOfInputDataFile = "";

    public DataTestUtility(Application application, Context context, AppDatabase appDatabase){

        mApplication = application;
        mContext = context;
        mAppDatabase = appDatabase;
        mExecutors = new AppExecutors();

        PathOfInputDataFile = context.getExternalFilesDir(null) + "/" + InputDataFileName;

    }

    public void insertDataFromInputDataFileToDatabase(){

        try {

            FileInputStream fileInputStream = new FileInputStream(PathOfInputDataFile);

            BufferedReader bufferedReader
                    = new BufferedReader(new InputStreamReader(fileInputStream));


            //Skipping first line which contains labels
            String line = bufferedReader.readLine();

            while((line = bufferedReader.readLine()) != null){


                String[] parts = line.trim().split(";");

                if(parts != null) {

                    int lengthOfPart = parts.length;
                    if (lengthOfPart >= 10) {

                        String information = "";

                        if (information == null || lengthOfPart == 10) {

                            information = "";
                        } else {

                            information = parts[10].replace("\n", "");
                        }

                        LocationData locationData = new LocationData(
                                Long.parseLong(parts[1]),
                                parts[2],
                                Double.parseDouble(parts[3]),
                                Double.parseDouble(parts[4]),
                                Double.parseDouble(parts[5]),
                                Float.parseFloat(parts[6]),
                                parts[7],
                                Float.parseFloat(parts[8]),
                                Float.parseFloat(parts[9]),
                                0,
                                0,
                                0,
                                information);


                        mExecutors.storageIO().execute(() -> {

                            if (mAppDatabase == null) {

                                mAppDatabase = ((BasicApp) mApplication).getDatabase();
                            }

                            AppDatabase.insert(mAppDatabase, locationData);

                        });

                    }

                }
            }



        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
