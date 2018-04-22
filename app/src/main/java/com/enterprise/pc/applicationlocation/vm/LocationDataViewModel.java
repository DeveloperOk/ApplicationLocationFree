package com.enterprise.pc.applicationlocation.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.enterprise.pc.applicationlocation.db.entity.LocationData;

import java.util.List;


/**
 * Created by PC on 2018-04-01.
 */

public class LocationDataViewModel extends ViewModel {

    private LiveData<List<LocationData>> locationData = new MutableLiveData<List<LocationData>>();

    public LocationDataViewModel(){

    }

    public LocationDataViewModel(LiveData<List<LocationData>> locationData){

        this.locationData = locationData;
    }

    public LiveData<List<LocationData>> getLocationData(){

        return this.locationData;
    }

    public void setLocationData(LiveData<List<LocationData>> locationData){

        this.locationData = locationData;
    }

}
