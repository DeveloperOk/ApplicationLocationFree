package com.enterprise.pc.applicationlocationfree.vm;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;

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
