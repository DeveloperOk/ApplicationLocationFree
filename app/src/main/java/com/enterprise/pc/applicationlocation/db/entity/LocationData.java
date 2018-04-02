package com.enterprise.pc.applicationlocation.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by PC on 2018-03-30.
 */

@Entity(tableName = "locationdata")
public class LocationData {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "info")
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public LocationData(){
    }

    public LocationData(int id, String info){
        this.id = id;
        this.info = info;
    }

    public LocationData(LocationData locationData){

        this.id = locationData.getId();
        this.info = locationData.getInfo();
    }

}
